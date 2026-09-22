package com.example.myapplication.features.cart.infrastructure.persistence;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.core.scheduler.DebounceScheduler;
import com.example.myapplication.features.cart.domain.model.Cart;
import com.example.myapplication.features.cart.domain.model.CartItem;
import com.example.myapplication.features.cart.domain.port.CartRepository;
import com.example.myapplication.features.product.domain.model.PricedProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.Objects;
@Singleton
public class CartRepositoryImpl implements CartRepository {

    private static final long SAVE_DELAY_MS = 350L;

    private final Cart cart;
    private final CartDataSource dataSource;
    private final FirebaseAuth auth;
    private final DebounceScheduler scheduler;

    private final MutableLiveData<List<CartItem>> items =
            new MutableLiveData<>(
                    new ArrayList<>()
            );

    private final MutableLiveData<Long> total =
            new MutableLiveData<>(0L);

    private ListenerRegistration cartListener;

    private FirebaseAuth.AuthStateListener authStateListener;

    private String currentUid;

    private boolean hasPendingLocalChanges = false;

    private long localChangeVersion = 0L;

    private boolean saveInProgress = false;

    private Runnable pendingSave;

    private boolean started = false;


    @Inject
    public CartRepositoryImpl(
            Cart cart,
            CartDataSource dataSource,
            FirebaseAuth auth,
            DebounceScheduler scheduler
    ) {

        this.cart = cart;
        this.dataSource = dataSource;
        this.auth = auth;
        this.scheduler = scheduler;
    }


    @Override
    public void start() {

        if (started) {
            return;
        }

        started = true;

        FirebaseUser user =
                auth.getCurrentUser();

        currentUid =
                user != null
                        ? user.getUid()
                        : null;

        authStateListener =
                firebaseAuth -> {

                    FirebaseUser newUser =
                            firebaseAuth.getCurrentUser();

                    String newUid =
                            newUser != null
                                    ? newUser.getUid()
                                    : null;

                    if (Objects.equals(
                            currentUid,
                            newUid
                    )) {
                        return;
                    }

                    currentUid = newUid;

                    onUserChanged();
                };

        auth.addAuthStateListener(
                authStateListener
        );

        if (currentUid != null) {
            startSync();
        }
    }


    @Override
    public void stop() {

        if (!started) {
            return;
        }

        started = false;

        /*
         * Cancelar cualquier guardado pendiente.
         */
        cancelPendingSave();

        /*
         * Detener listener de Firestore.
         */
        stopSync();

        /*
         * Quitar listener de FirebaseAuth.
         */
        if (authStateListener != null) {

            auth.removeAuthStateListener(
                    authStateListener
            );

            authStateListener = null;
        }

        /*
         * Invalidar cualquier operación
         * lógica pendiente.
         */
        localChangeVersion++;

        currentUid = null;

        hasPendingLocalChanges = false;

        saveInProgress = false;

        /*
         * Limpiar carrito local.
         */
        cart.clear();

        publishCart();
    }

    @Override
    public LiveData<List<CartItem>> getItems() {
        return items;
    }


    @Override
    public LiveData<Long> getTotal() {
        return total;
    }

    private void onCartChanged() {
        publishCart();
        scheduleSave();
    }


    @Override
    public void addProduct(
            PricedProduct pricedProduct
    ) {

        if (!isUserLoggedIn()) {
            return;
        }

        if (pricedProduct == null) {
            return;
        }

        cart.addProduct(
                pricedProduct
        );

        onCartChanged();
    }




    @Override
    public void removeProduct(
            String productId
    ) {

        if (!isUserLoggedIn()) {
            return;
        }

        cart.removeProduct(
                productId
        );

        onCartChanged();
    }

    @Override
    public void clearCart() {

        if (!isUserLoggedIn()) {
            return;
        }

        cart.clear();

        onCartChanged();
    }

    @Override
    public void increase(
            String productId
    ) {

        if (!isUserLoggedIn()) {
            return;
        }

        cart.increase(
                productId
        );

        onCartChanged();
    }


    @Override
    public void decrease(
            String productId
    ) {

        if (!isUserLoggedIn()) {
            return;
        }

        cart.decrease(
                productId
        );

        onCartChanged();
    }

    @Override
    public void setQuantity(
            String productId,
            int quantity
    ) {

        if (!isUserLoggedIn()) {
            return;
        }

        cart.setQuantity(
                productId,
                quantity
        );

        onCartChanged();
    }

    @Override
    public void updateProductPrice(
            String productId,
            long newPrice
    ) {

        if (!isUserLoggedIn()) {
            return;
        }

        boolean changed = cart.updateProductPrice(
                productId,
                newPrice
        );

        if (!changed) {
            return;
        }

        onCartChanged();
    }


    private boolean isUserLoggedIn() {

        return currentUid != null;
    }


    private void publishCart() {

        items.setValue(
                new ArrayList<>(
                        cart.getItems()
                )
        );

        total.setValue(
                cart.getTotal()
        );
    }

    private void scheduleSave() {

        if (!isUserLoggedIn()) {
            return;
        }

        hasPendingLocalChanges = true;

        /*
         * Cada modificación genera una nueva versión.
         */
        final long changeVersion = ++localChangeVersion;

        /*
         * Guardamos el UID actual.
         *
         * Si cambia el usuario antes de completar
         * el guardado, ignoramos el resultado.
         */
        final String uidAtSchedule = currentUid;

        /*
         * Copia del carrito en este momento.
         */
        final List<CartItem> snapshot = new ArrayList<>(
                cart.getItems());

        /*
         * Cancelamos el guardado anterior.
         */
        cancelPendingSave();

        pendingSave = () -> {

            /*
             * El Runnable ya no está pendiente.
             */
            pendingSave = null;

            /*
             * Verificamos que el usuario
             * siga siendo el mismo.
             */
            if (!Objects.equals(
                    currentUid,
                    uidAtSchedule
            )) {
                return;
            }

            saveInProgress = true;

            dataSource
                    .saveCart(snapshot)
                    .addOnSuccessListener(unused -> {

                        /*
                         * El usuario pudo cambiar mientras
                         * se estaba guardando.
                         */
                        if (!Objects.equals(
                                currentUid,
                                uidAtSchedule
                        )) {
                            return;
                        }

                        /*
                         * Solamente consideramos que
                         * estamos sincronizados si no hubo
                         * otro cambio después de este save.
                         */
                        if (changeVersion ==
                                localChangeVersion) {

                            hasPendingLocalChanges = false;
                        }

                        saveInProgress = false;
                    })
                    .addOnFailureListener(e -> {

                        if (!Objects.equals(
                                currentUid,
                                uidAtSchedule
                        )) {
                            return;
                        }

                        /*
                         * Dejamos marcado que todavía
                         * existen cambios locales.
                         */
                        hasPendingLocalChanges = true;

                        saveInProgress = false;
                    });
        };

        scheduler.postDelayed(
                pendingSave,
                SAVE_DELAY_MS
        );
    }


    // =========================================================
    // CANCELAR GUARDADO PENDIENTE
    // =========================================================

    private void cancelPendingSave() {

        if (pendingSave == null) {
            return;
        }

        scheduler.removeCallbacks(
                pendingSave
        );

        pendingSave = null;
    }


    // =========================================================
    // SINCRONIZACIÓN FIRESTORE
    // =========================================================

    @Override
    public void startSync() {

        if (!isUserLoggedIn()) {
            return;
        }

        /*
         * Ya existe un listener.
         */
        if (cartListener != null) {
            return;
        }

        /*
         * Guardamos el UID con el que se creó
         * este listener.
         */
        final String uidAtStart =
                currentUid;

        cartListener =
                dataSource.listenCart(
                        (document, error) -> {

                            /*
                             * El listener pertenece a otro usuario.
                             */
                            if (!Objects.equals(
                                    currentUid,
                                    uidAtStart
                            )) {
                                return;
                            }

                            /*
                             * Error de Firestore.
                             */
                            if (error != null) {
                                return;
                            }

                            /*
                             * No sobrescribir cambios locales
                             * pendientes.
                             */
                            if (hasPendingLocalChanges ||
                                    saveInProgress) {
                                return;
                            }

                            List<CartItem> savedItems =
                                    dataSource.documentToItems(
                                            document
                                    );

                            /*
                             * Reemplazamos el carrito local
                             * con el estado persistido.
                             */
                            cart.replaceItems(
                                    savedItems
                            );

                            publishCart();
                        }
                );
    }


    // =========================================================
    // DETENER SINCRONIZACIÓN FIRESTORE
    // =========================================================

    @Override
    public void stopSync() {

        if (cartListener == null) {
            return;
        }

        cartListener.remove();

        cartListener = null;
    }


    // =========================================================
    // CAMBIO DE USUARIO
    // =========================================================

    private void onUserChanged() {

        /*
         * Cancelamos cualquier guardado pendiente
         * del usuario anterior.
         */
        cancelPendingSave();

        /*
         * Eliminamos el listener del usuario anterior.
         */
        stopSync();

        /*
         * Invalidamos operaciones anteriores.
         */
        localChangeVersion++;

        hasPendingLocalChanges = false;

        saveInProgress = false;

        /*
         * Muy importante:
         * no conservar el carrito del usuario anterior.
         */
        cart.clear();

        publishCart();
        if (!isUserLoggedIn()) {
            return;
        }

        startSync();
    }
}

