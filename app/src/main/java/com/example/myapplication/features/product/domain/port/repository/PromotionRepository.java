package com.example.myapplication.features.product.domain.port.repository;

import com.example.myapplication.features.product.infrastructure.mapper.ProductMapper;
import com.example.myapplication.features.product.infrastructure.document.ProductDocument;
import com.example.myapplication.features.product.domain.model.PromotionPage;
import com.example.myapplication.features.product.domain.model.PromotionProduct;
import com.example.myapplication.features.product.infrastructure.datasource.ProductDataSource;
import com.example.myapplication.features.product.infrastructure.datasource.PromotionDataSource;
import com.example.myapplication.features.product.domain.model.Product;
import com.example.myapplication.features.product.domain.model.Promotion;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

public class PromotionRepository {

    private static final int PAGE_SIZE = 4;

    private final PromotionDataSource promotionDataSource;
    private final ProductDataSource productDataSource;
    private final ProductMapper productMapper;

    @Inject
    public PromotionRepository(
            PromotionDataSource promotionDataSource,
            ProductDataSource productDataSource,
            ProductMapper productMapper
    ) {
        this.promotionDataSource = promotionDataSource;
        this.productDataSource = productDataSource;
        this.productMapper = productMapper;
    }

    public Task<PromotionPage> getFirstPage() {

        return promotionDataSource
                .getFirstPage(PAGE_SIZE)
                .continueWithTask(task -> {

                    if (!task.isSuccessful()) {

                        Exception exception =
                                task.getException();

                        if (exception != null) {
                            return Tasks.forException(exception);
                        }

                        return Tasks.forException(
                                new IllegalStateException(
                                        "Error cargando promociones"
                                )
                        );
                    }

                    QuerySnapshot snapshot =
                            task.getResult();

                    return mapPage(snapshot);
                });
    }

    public Task<PromotionPage> getNextPage(
            DocumentSnapshot lastDocument
    ) {

        if (lastDocument == null) {

            return Tasks.forException(
                    new IllegalArgumentException(
                            "El cursor de promociones no puede ser null"
                    )
            );
        }

        return promotionDataSource
                .getNextPage(
                        lastDocument,
                        PAGE_SIZE
                )
                .continueWithTask(task -> {

                    if (!task.isSuccessful()) {

                        Exception exception =
                                task.getException();

                        if (exception != null) {
                            return Tasks.forException(exception);
                        }

                        return Tasks.forException(
                                new IllegalStateException(
                                        "Error cargando siguiente página de promociones"
                                )
                        );
                    }

                    QuerySnapshot snapshot =
                            task.getResult();

                    return mapPage(snapshot);
                });
    }

    private Task<PromotionPage> mapPage(
            QuerySnapshot snapshot
    ) {

        if (snapshot == null) {

            return Tasks.forException(
                    new IllegalStateException(
                            "La respuesta de promociones es null"
                    )
            );
        }

        List<DocumentSnapshot> documents =
                snapshot.getDocuments();

        boolean hasMore =
                documents.size() > PAGE_SIZE;

        int promotionsToReturn =
                Math.min(
                        documents.size(),
                        PAGE_SIZE
                );

        DocumentSnapshot lastDocument =
                promotionsToReturn > 0
                        ? documents.get(promotionsToReturn - 1)
                        : null;

        List<Promotion> promotions =
                new ArrayList<>(
                        promotionsToReturn
                );

        for (int i = 0; i < promotionsToReturn; i++) {

            DocumentSnapshot document =
                    documents.get(i);

            if (document == null) {
                continue;
            }

            Promotion promotion =
                    document.toObject(
                            Promotion.class
                    );

            if (promotion == null) {
                continue;
            }

            promotion.setId(
                    document.getId()
            );

            String productId =
                    promotion.getProductId();

            if (productId == null ||
                    productId.trim().isEmpty()) {
                continue;
            }

            long specialPrice =
                    promotion.getSpecialPrice();

            if (specialPrice < 0) {
                continue;
            }

            promotions.add(
                    promotion
            );
        }

        if (promotions.isEmpty()) {

            return Tasks.forResult(
                    new PromotionPage(
                            new ArrayList<>(),
                            lastDocument,
                            hasMore
                    )
            );
        }

        Set<String> productIdSet =
                new HashSet<>();

        for (Promotion promotion :
                promotions) {

            String productId =
                    promotion.getProductId();

            if (productId != null &&
                    !productId.trim().isEmpty()) {

                productIdSet.add(
                        productId
                );
            }
        }

        if (productIdSet.isEmpty()) {

            return Tasks.forResult(
                    new PromotionPage(
                            new ArrayList<>(),
                            lastDocument,
                            hasMore
                    )
            );
        }

        final List<String> productIds =
                new ArrayList<>(
                        productIdSet
                );

        final DocumentSnapshot finalLastDocument =
                lastDocument;

        return productDataSource
                .getProductsByIds(productIds)
                .continueWithTask(productTask -> {

                    if (!productTask.isSuccessful()) {

                        Exception exception =
                                productTask.getException();

                        if (exception != null) {
                            return Tasks.forException(
                                    exception
                            );
                        }

                        return Tasks.forException(
                                new IllegalStateException(
                                        "Error recuperando productos de promociones"
                                )
                        );
                    }

                    QuerySnapshot productSnapshot =
                            productTask.getResult();

                    if (productSnapshot == null) {

                        return Tasks.forException(
                                new IllegalStateException(
                                        "La respuesta de productos es null"
                                )
                        );
                    }

                    Map<String, Product> productMap =
                            new HashMap<>();

                    for (DocumentSnapshot document :
                            productSnapshot.getDocuments()) {

                        if (document == null) {
                            continue;
                        }

                        ProductDocument productDocument =
                                document.toObject(
                                        ProductDocument.class
                                );

                        if (productDocument == null) {
                            continue;
                        }

                        Product product =
                                productMapper.toDomain(
                                        document.getId(),
                                        productDocument
                                );

                        if (product == null ||
                                product.getId() == null) {
                            continue;
                        }

                        productMap.put(
                                product.getId(),
                                product
                        );
                    }

                    List<PromotionProduct>
                            promotionProducts =
                            new ArrayList<>(
                                    promotions.size()
                            );

                    for (Promotion promotion :
                            promotions) {

                        Product product =
                                productMap.get(
                                        promotion.getProductId()
                                );

                        if (product == null) {
                            continue;
                        }

                        long specialPrice =
                                promotion.getSpecialPrice();

                        promotionProducts.add(
                                new PromotionProduct(
                                        product,
                                        specialPrice
                                )
                        );
                    }

                    return Tasks.forResult(
                            new PromotionPage(
                                    promotionProducts,
                                    finalLastDocument,
                                    hasMore
                            )
                    );
                });
    }

    public Task<Map<String, Long>> getActivePricesByProductIds(
            List<String> productIds
    ) {

        if (productIds == null || productIds.isEmpty()) {

            return Tasks.forResult(
                    new HashMap<>()
            );
        }

        return promotionDataSource
                .getActivePromotionsByProductIds(productIds)
                .continueWithTask(task -> {

                    if (!task.isSuccessful()) {

                        Exception exception =
                                task.getException();

                        if (exception != null) {
                            return Tasks.forException(exception);
                        }

                        return Tasks.forException(
                                new IllegalStateException(
                                        "Error cargando promociones activas"
                                )
                        );
                    }

                    QuerySnapshot snapshot =
                            task.getResult();

                    Map<String, Long> prices =
                            new HashMap<>();

                    if (snapshot == null) {
                        return Tasks.forResult(prices);
                    }

                    for (DocumentSnapshot document :
                            snapshot.getDocuments()) {

                        if (document == null) {
                            continue;
                        }

                        Promotion promotion =
                                document.toObject(
                                        Promotion.class
                                );

                        if (promotion == null) {
                            continue;
                        }

                        String productId =
                                promotion.getProductId();

                        if (productId == null ||
                                productId.trim().isEmpty()) {
                            continue;
                        }

                        if (!promotion.isActive()) {
                            continue;
                        }

                        long specialPrice =
                                promotion.getSpecialPrice();

                        if (specialPrice < 0) {
                            continue;
                        }

                        prices.put(
                                productId,
                                specialPrice
                        );
                    }

                    return Tasks.forResult(prices);
                });
    }

}
