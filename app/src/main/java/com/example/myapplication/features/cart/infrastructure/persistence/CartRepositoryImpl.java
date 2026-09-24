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

        cancelPendingSave();
        stopSync();

        if (authStateListener != null) {

            auth.removeAuthStateListener(
                    authStateListener
            );

            authStateListener = null;
        }

        localChangeVersion++;

        currentUid = null;

        hasPendingLocalChanges = false;

        saveInProgress = false;

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

        final long changeVersion = ++localChangeVersion;

        final String uidAtSchedule = currentUid;

        final List<CartItem> snapshot = new ArrayList<>(
                cart.getItems());

        cancelPendingSave();

        pendingSave = () -> {

            pendingSave = null;

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

                        if (!Objects.equals(
                                currentUid,
                                uidAtSchedule
                        )) {
                            return;
                        }

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

                        hasPendingLocalChanges = true;

                        saveInProgress = false;
                    });
        };

        scheduler.postDelayed(
                pendingSave,
                SAVE_DELAY_MS
        );
    }
    private void cancelPendingSave() {

        if (pendingSave == null) {
            return;
        }

        scheduler.removeCallbacks(
                pendingSave
        );

        pendingSave = null;
    }
    @Override
    public void startSync() {

        if (!isUserLoggedIn()) {
            return;
        }

        if (cartListener != null) {
            return;
        }

        final String uidAtStart =
                currentUid;

        cartListener = dataSource.listenCart(
                (document, error) -> {

                    if (!Objects.equals(
                            currentUid,
                            uidAtStart
                    )) {
                        return;
                    }

                    if (error != null) {

                        return;
                    }

                    if (hasPendingLocalChanges ||
                            saveInProgress) {
                        return;
                    }


                    List<CartItem> savedItems =
                            dataSource.documentToItems(
                                    document
                            );

                    cart.replaceItems(savedItems);

                    publishCart();
                });
    }
    @Override
    public void stopSync() {

        if (cartListener == null) {
            return;
        }

        cartListener.remove();

        cartListener = null;
    }

    private void onUserChanged() {

        cancelPendingSave();
        stopSync();

        localChangeVersion++;

        hasPendingLocalChanges = false;

        saveInProgress = false;

        cart.clear();

        publishCart();
        if (!isUserLoggedIn()) {
            return;
        }

        startSync();
    }
}

