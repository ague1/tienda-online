package com.example.myapplication.features.product.data.repository;

import com.example.myapplication.features.product.data.firebase.document.ProductDocument;
import com.example.myapplication.features.product.data.firebase.mapper.ProductMapper;
import com.example.myapplication.features.product.domain.cursor.CategoryProductCursor;
import com.example.myapplication.features.product.domain.model.CategoryProductPage;
import com.example.myapplication.features.product.domain.model.ProductPage;
import com.example.myapplication.features.product.domain.cursor.ProductPageCursor;
import com.example.myapplication.features.product.domain.cursor.SearchCursor;
import com.example.myapplication.features.product.domain.model.Product;
import com.example.myapplication.features.product.data.firebase.datasource.ProductDataSource;
import com.example.myapplication.features.product.domain.model.SearchPage;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
public class ProductRepository {

    private final ProductDataSource dataSource;
    private final ProductMapper mapper;

    @Inject
    public ProductRepository(
            ProductDataSource dataSource,
            ProductMapper mapper
    ) {
        this.dataSource = dataSource;
        this.mapper = mapper;
    }

    public Task<ProductPage> getFirstPage(int pageSize) {

        return dataSource
                .getFirstPage(pageSize)
                .continueWith(task ->
                    mapPage(
                            task.getResult(),
                            pageSize
                    )
                );
    }

    public Task<ProductPage> getNextPage(
            ProductPageCursor cursor,
            int pageSize
    ) {

        return dataSource
                .getNextPage(
                        cursor,
                        pageSize
                )
                .continueWith(task ->
                        mapPage(
                            task.getResult(),
                            pageSize
                    )
                );
    }

    private ProductPage mapPage(
            QuerySnapshot snapshot,
            int pageSize
    ) {

        List<DocumentSnapshot> documents =
                snapshot.getDocuments();

        boolean hasMore =
                documents.size() > pageSize;

        int productsToReturn =
                Math.min(
                        documents.size(),
                        pageSize
                );

        List<Product> products =
                new ArrayList<>(productsToReturn);

        ProductDocument lastProductDocument = null;
        String lastDocumentId = null;

        for (int i = 0; i < productsToReturn; i++) {

            DocumentSnapshot document =
                    documents.get(i);

            ProductDocument productDocument =
                    document.toObject(
                            ProductDocument.class
                    );

            if (productDocument == null) {
                continue;
            }

            products.add(
                    mapper.toDomain(
                            document.getId(),
                            productDocument
                    )
            );

            lastProductDocument = productDocument;
            lastDocumentId = document.getId();
        }

        ProductPageCursor nextCursor = null;

        if (hasMore && lastProductDocument != null) {

            nextCursor = new ProductPageCursor(
                    lastProductDocument.getNombre(),
                    lastDocumentId
            );
        }

        return new ProductPage(
                products,
                nextCursor,
                hasMore
        );
    }



    public Task<SearchPage> searchProducts(
            String query,
            int pageSize
    ) {

        return dataSource
                .searchProducts(
                        query,
                        pageSize
                )
                .continueWith(task ->
                        mapSearchPage(
                            task.getResult(),
                            pageSize
                    )
                );
    }

    public Task<SearchPage> searchProductsNextPage(
            String query,
            SearchCursor cursor,
            int pageSize
    ) {

        return dataSource
                .searchProductsNextPage(
                        query,
                        cursor,
                        pageSize
                )
                .continueWith(task ->
                        mapSearchPage(
                            task.getResult(),
                            pageSize
                    )
                );
    }

    private SearchPage mapSearchPage(
            QuerySnapshot snapshot,
            int pageSize
    ) {

        List<DocumentSnapshot> documents =
                snapshot.getDocuments();

        boolean hasMore =
                documents.size() > pageSize;

        int productsToReturn =
                Math.min(
                        documents.size(),
                        pageSize
                );

        List<Product> products =
                new ArrayList<>(productsToReturn);

        for (int i = 0; i < productsToReturn; i++) {

            DocumentSnapshot document =
                    documents.get(i);

            ProductDocument productDocument =
                    document.toObject(
                            ProductDocument.class
                    );

            if (productDocument == null) {
                continue;
            }

            products.add(
                    mapper.toDomain(
                            document.getId(),
                            productDocument
                    )
            );
        }

        SearchCursor nextCursor =
                getSearchNextCursor(
                        documents,
                        productsToReturn,
                        hasMore
                );

        return new SearchPage(
                products,
                nextCursor,
                hasMore
        );
    }

    private SearchCursor getSearchNextCursor(
            List<DocumentSnapshot> documents,
            int productsToReturn,
            boolean hasMore
    ) {

        if (!hasMore || productsToReturn <= 0) {
            return null;
        }

        DocumentSnapshot lastDocument =
                documents.get(productsToReturn - 1);

        ProductDocument lastProduct =
                lastDocument.toObject(
                        ProductDocument.class
                );

        if (lastProduct == null) {
            return null;
        }

        return new SearchCursor(
                lastProduct.getKeySearch(),
                lastDocument.getId()
        );
    }


    public Task<CategoryProductPage> getProductsByCategory(
            String category,
            int pageSize
    ) {

        return dataSource
                .getProductsByCategory(
                        category,
                        pageSize
                )
                .continueWith(task ->
                        mapCategoryPage(
                            task.getResult(),
                            pageSize
                    )
                );
    }

    public Task<CategoryProductPage> getProductsByCategoryNextPage(
            String category,
            CategoryProductCursor cursor,
            int pageSize
    ) {

        return dataSource
                .getProductsByCategoryNextPage(
                        category,
                        cursor,
                        pageSize
                )
                .continueWith(task ->
                        mapCategoryPage(
                            task.getResult(),
                            pageSize
                    )
                );
    }

    private CategoryProductPage mapCategoryPage(
            QuerySnapshot snapshot,
            int pageSize
    ) {

        List<DocumentSnapshot> documents =
                snapshot.getDocuments();

        boolean hasMore =
                documents.size() > pageSize;

        int productsToReturn =
                Math.min(
                        documents.size(),
                        pageSize
                );

        List<Product> products =
                new ArrayList<>(productsToReturn);

        for (int i = 0; i < productsToReturn; i++) {

            DocumentSnapshot document =
                    documents.get(i);

            ProductDocument productDocument =
                    document.toObject(
                            ProductDocument.class
                    );

            if (productDocument == null) {
                continue;
            }

            products.add(
                    mapper.toDomain(
                            document.getId(),
                            productDocument
                    )
            );
        }

        CategoryProductCursor nextCursor =
                getCategoryNextCursor(
                        documents,
                        productsToReturn,
                        hasMore
                );

        return new CategoryProductPage(
                products,
                nextCursor,
                hasMore
        );
    }

    private CategoryProductCursor getCategoryNextCursor(
            List<DocumentSnapshot> documents,
            int productsToReturn,
            boolean hasMore
    ) {

        if (!hasMore || productsToReturn <= 0) {
            return null;
        }

        DocumentSnapshot lastDocument =
                documents.get(productsToReturn - 1);

        ProductDocument lastProduct =
                lastDocument.toObject(
                        ProductDocument.class
                );

        if (lastProduct == null) {
            return null;
        }

        return new CategoryProductCursor(
                lastProduct.getKeySearch(),
                lastDocument.getId()
        );
    }


}
