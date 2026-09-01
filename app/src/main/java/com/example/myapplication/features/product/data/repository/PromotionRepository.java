package com.example.myapplication.features.product.data.repository;

import com.example.myapplication.features.product.data.firebase.document.ProductDocument;
import com.example.myapplication.features.product.data.firebase.mapper.ProductMapper;
import com.example.myapplication.features.product.domain.model.PromotionPage;
import com.example.myapplication.features.product.domain.model.PromotionProduct;
import com.example.myapplication.features.product.data.firebase.datasource.ProductDataSource;
import com.example.myapplication.features.product.data.firebase.datasource.PromotionDataSource;
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
                .continueWithTask(task ->
                        mapPage(task.getResult())
                );
    }


    public Task<PromotionPage> getNextPage(
            DocumentSnapshot lastDocument
    ) {
        return promotionDataSource
                .getNextPage(lastDocument, PAGE_SIZE)
                .continueWithTask(task ->
                        mapPage(task.getResult())
                );
    }

    private Task<PromotionPage> mapPage(
            QuerySnapshot snapshot
    ) {

        List<DocumentSnapshot> documents =
                snapshot.getDocuments();

        final boolean hasMore = documents.size() > PAGE_SIZE;

        int promotionsToReturn = Math.min(documents.size(), PAGE_SIZE);

        List<Promotion> promotions = new ArrayList<>();

        DocumentSnapshot lastDocument = null;

        for (int i = 0; i < promotionsToReturn; i++) {

            DocumentSnapshot document =
                    documents.get(i);

            Promotion promotion =
                    document.toObject(Promotion.class);

            if (promotion == null) {
                continue;
            }

            promotion.setId(document.getId());

            promotions.add(promotion);

            lastDocument = document;
        }

        final DocumentSnapshot finalLastDocument = lastDocument;


        // No hay promociones
        if (promotions.isEmpty()) {

            return Tasks.forResult(
                    new PromotionPage(
                            new ArrayList<>(),
                            lastDocument,
                            hasMore
                    )
            );
        }

        // Obtener IDs de productos
        Set<String> productIdSet =
                new HashSet<>();

        for (Promotion promotion : promotions) {

            String productId =
                    promotion.getProductId();

            if (productId != null
                    && !productId.isEmpty()) {

                productIdSet.add(productId);
            }
        }

        List<String> productIds =
                new ArrayList<>(productIdSet);


        // No hay IDs de productos
        if (productIds.isEmpty()) {

            return Tasks.forResult(
                    new PromotionPage(
                            new ArrayList<>(),
                            lastDocument,
                            hasMore
                    )
            );
        }

        // Buscar los productos en una sola consulta
        return productDataSource
                .getProductsByIds(productIds)
                .continueWith(productTask -> {

                    if (!productTask.isSuccessful()) {

                        Exception exception = productTask.getException();

                        if (exception != null) {
                            throw exception;
                        }

                        throw new IllegalStateException(
                                "Error retrieving products"
                        );
                    }

                    QuerySnapshot productSnapshot =
                            productTask.getResult();

                    Map<String, Product> productMap =
                            new HashMap<>();

                    for (DocumentSnapshot document :
                            productSnapshot.getDocuments()) {

                        ProductDocument productDocument =
                                document.toObject(
                                        ProductDocument.class
                                );

                        if (productDocument != null) {

                            Product product =
                                    productMapper.toDomain(
                                            document.getId(),
                                            productDocument
                                    );

                            productMap.put(
                                    product.getId(),
                                    product
                            );
                        }

                    }

                    List<PromotionProduct>
                            promotionProducts =
                            new ArrayList<>();

                    for (Promotion promotion :
                            promotions) {

                        Product product =
                                productMap.get(
                                        promotion.getProductId()
                                );

                        if (product != null) {

                            promotionProducts.add(
                                    new PromotionProduct(
                                            product,
                                            promotion.getSpecialPrice()
                                    )
                            );
                        }
                    }

                    return new PromotionPage(
                            promotionProducts,
                            finalLastDocument,
                            hasMore
                    );

                });
    }


}


