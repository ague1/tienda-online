package com.example.myapplication.features.cart.infrastructure.persistence;

import com.example.myapplication.features.cart.domain.model.CartItem;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class CartDataSourceImpl implements CartDataSource {

    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_CART = "cart";
    private static final String DOCUMENT_CART = "cart";

    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;

    @Inject
    public CartDataSourceImpl(
            FirebaseFirestore firestore,
            FirebaseAuth auth
    ) {
        this.firestore = firestore;
        this.auth = auth;
    }




    @Override
    public Task<Void> saveCart(List<CartItem> items) {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {
            return Tasks.forException(
                    new IllegalStateException(
                            "No hay usuario autenticado"
                    )
            );
        }

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "items",
                items != null
                        ? items
                        : Collections.emptyList()
        );

        data.put(
                "updatedAt",
                FieldValue.serverTimestamp()
        );

        return getCartDocument(user.getUid())
                .set(data);
    }

    @Override
    public ListenerRegistration listenCart(
            EventListener<DocumentSnapshot> listener
    ) {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {
            return null;
        }

        return getCartDocument(user.getUid())
                .addSnapshotListener(listener);
    }

    private DocumentReference getCartDocument(
            String uid
    ) {

        return firestore
                .collection(COLLECTION_USERS)
                .document(uid)
                .collection(COLLECTION_CART)
                .document(DOCUMENT_CART);
    }

    @Override
    public List<CartItem> documentToItems(
            DocumentSnapshot document
    ) {

        List<CartItem> items =
                new ArrayList<>();

        if (document == null ||
                !document.exists()) {

            return items;
        }

        Object rawItems =
                document.get("items");

        if (!(rawItems instanceof List)) {
            return items;
        }

        List<?> savedItems = (List<?>) rawItems;

        for (Object rawItem : savedItems) {

            if (!(rawItem instanceof Map)) {
                continue;
            }

            Map<?, ?> rawMap =
                    (Map<?, ?>) rawItem;

            Map<String, Object> data =
                    new HashMap<>();

            for (Map.Entry<?, ?> entry :
                    rawMap.entrySet()) {

                if (entry.getKey() instanceof String) {

                    data.put(
                            (String) entry.getKey(),
                            entry.getValue()
                    );
                }
            }

            CartItem item =
                    mapToCartItem(data);

            if (item != null) {
                items.add(item);
            }
        }


        return items;
    }

    private CartItem mapToCartItem(
            Map<String, Object> data
    ) {

        if (data == null) {
            return null;
        }

        String productId =
                (String) data.get("productId");

        if (productId == null ||
                productId.trim().isEmpty()) {

            return null;
        }

        String nombre =
                (String) data.get("nombre");

        String image =
                (String) data.get("image");

        Number precioNumber =
                (Number) data.get("precio");

        Number quantityNumber =
                (Number) data.get("quantity");

        if (precioNumber == null ||
                quantityNumber == null) {
            return null;
        }

        long precio =
                precioNumber.longValue();

        int quantity =
                quantityNumber.intValue();

        if (precio < 0 ||
                quantity <= 0) {
            return null;
        }

        return new CartItem(
                productId,
                nombre,
                image,
                precio,
                quantity
        );
    }
}
