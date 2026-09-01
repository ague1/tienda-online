package com.example.myapplication.features.product.presentation.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.features.product.domain.cursor.CategoryProductCursor;
import com.example.myapplication.features.product.domain.cursor.ProductPageCursor;
import com.example.myapplication.features.product.domain.cursor.SearchCursor;
import com.example.myapplication.features.product.domain.model.Product;
import com.example.myapplication.features.product.application.usecase.GetProductsUseCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;


@HiltViewModel
public class ClientHomeViewModel extends ViewModel {

    private final GetProductsUseCase getProductsUseCase;

    // Productos normales
    private final MutableLiveData<List<Product>> products =
            new MutableLiveData<>(new ArrayList<>());

    private ProductPageCursor nextCursor;
    private boolean loading = false;
    private boolean hasMore = true;



    // Búsqueda
    private final MutableLiveData<List<Product>> searchProducts =
            new MutableLiveData<>(new ArrayList<>());

    private SearchCursor searchNextCursor;
    private boolean searchLoading = false;
    private boolean searchHasMore = true;

    private String currentSearch = "";
    private long searchRequestId = 0;

    private final MutableLiveData<List<Product>> categoryProducts =
            new MutableLiveData<>(new ArrayList<>());

    private CategoryProductCursor categoryNextCursor;
    private boolean categoryLoading = false;
    private boolean categoryHasMore = true;

    private String currentCategory = "";

    private long categoryRequestId = 0;

    public LiveData<List<Product>> getCategoryProducts() {
        return categoryProducts;
    }
    @Inject
    public ClientHomeViewModel(
            GetProductsUseCase getProductsUseCase
    ) {
        this.getProductsUseCase = getProductsUseCase;
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public void loadFirstPage() {

        if (loading) {
            return;
        }

        loading = true;

        getProductsUseCase
                .getFirstPage()
                .addOnSuccessListener(page -> {

                    products.setValue(
                            new ArrayList<>(
                                    page.getProducts()
                            )
                    );

                    nextCursor =
                            page.getNextCursor();

                    hasMore =
                            page.hasMore();

                    loading = false;
                })
                .addOnFailureListener(e -> loading = false);
    }


    public void loadNextPage() {

        if (loading ||
                nextCursor == null ||
                !hasMore) {

            return;
        }

        loading = true;

        getProductsUseCase
                .getNextPage(nextCursor)
                .addOnSuccessListener(page -> {

                    nextCursor =
                            page.getNextCursor();

                    hasMore =
                            page.hasMore();

                    List<Product> current =
                            products.getValue();

                    if (current == null) {

                        current = new ArrayList<>();

                    } else {

                        current =
                                new ArrayList<>(current);
                    }

                    current.addAll(
                            page.getProducts()
                    );

                    products.setValue(current);

                    loading = false;
                })
                .addOnFailureListener(e -> loading = false);
    }

    public LiveData<List<Product>> getSearchProducts() {

        return searchProducts;
    }


    public void search(String query) {

        if (query == null) {
            return;
        }

        final String normalizedQuery =
                query.trim().toLowerCase(Locale.ROOT);

        if (normalizedQuery.isEmpty()) {
            clearSearch();
            return;
        }

        currentSearch = normalizedQuery;

        // Nueva búsqueda = nueva sesión de paginación
        searchNextCursor = null;
        searchHasMore = true;

        // Identifica esta petición
        final long requestId = ++searchRequestId;

        searchLoading = true;

        getProductsUseCase
                .searchProducts(normalizedQuery)
                .addOnSuccessListener(page -> {

                    // La respuesta pertenece a una búsqueda vieja
                    if (requestId != searchRequestId) {
                        return;
                    }

                    searchProducts.setValue(
                            new ArrayList<>(
                                    page.getProducts()
                            )
                    );

                    searchNextCursor =
                            page.getNextCursor();

                    searchHasMore =
                            page.hasMore();

                    searchLoading = false;
                })
                .addOnFailureListener(e -> {

                    // Ignorar errores de búsquedas anteriores
                    if (requestId != searchRequestId) {
                        return;
                    }

                    searchLoading = false;
                });
    }



    public void loadNextSearchPage() {

        if (searchLoading ||
                searchNextCursor == null ||
                !searchHasMore ||
                currentSearch.isEmpty()) {

            return;
        }

        final String queryAtRequest =
                currentSearch;

        final long requestId =
                searchRequestId;

        searchLoading = true;

        getProductsUseCase
                .searchProductsNextPage(
                        queryAtRequest,
                        searchNextCursor
                )
                .addOnSuccessListener(page -> {

                    // La búsqueda cambió mientras cargábamos
                    if (requestId != searchRequestId) {
                        return;
                    }

                    // Seguridad adicional
                    if (!queryAtRequest.equals(currentSearch)) {
                        return;
                    }

                    searchNextCursor =
                            page.getNextCursor();

                    searchHasMore =
                            page.hasMore();

                    List<Product> current =
                            searchProducts.getValue();

                    if (current == null) {
                        current = new ArrayList<>();
                    } else {
                        current = new ArrayList<>(current);
                    }

                    current.addAll(
                            page.getProducts()
                    );

                    searchProducts.setValue(current);

                    searchLoading = false;
                })
                .addOnFailureListener(e -> {

                    if (requestId != searchRequestId) {
                        return;
                    }

                    searchLoading = false;
                });
    }



    public void loadProductsByCategory(String category) {

        if (category == null) {
            return;
        }

        final String normalizedCategory =
                category.trim();

        if (normalizedCategory.isEmpty()) {
            return;
        }

        currentCategory = normalizedCategory;

        // Reiniciamos la paginación
        categoryNextCursor = null;
        categoryHasMore = true;

        // Identificador de esta petición
        final long requestId =
                ++categoryRequestId;

        categoryLoading = true;

        getProductsUseCase
                .getProductsByCategory(
                        normalizedCategory
                )
                .addOnSuccessListener(page -> {

                    // El usuario ya cambió de categoría
                    if (requestId != categoryRequestId) {
                        return;
                    }

                    if (!normalizedCategory.equals(currentCategory)) {
                        return;
                    }

                    categoryProducts.setValue(
                            new ArrayList<>(
                                    page.getProducts()
                            )
                    );

                    categoryNextCursor =
                            page.getNextCursor();

                    categoryHasMore =
                            page.hasMore();

                    categoryLoading = false;
                })
                .addOnFailureListener(e -> {

                    if (requestId != categoryRequestId) {
                        return;
                    }

                    categoryLoading = false;
                });
    }

    public void loadNextCategoryPage() {

        if (categoryLoading ||
                categoryNextCursor == null ||
                !categoryHasMore ||
                currentCategory.isEmpty()) {

            return;
        }

        final String categoryAtRequest =
                currentCategory;

        final long requestId =
                categoryRequestId;

        categoryLoading = true;

        getProductsUseCase
                .getProductsByCategoryNextPage(
                        categoryAtRequest,
                        categoryNextCursor
                )
                .addOnSuccessListener(page -> {

                    // La categoría cambió
                    if (requestId != categoryRequestId) {
                        return;
                    }

                    if (!categoryAtRequest.equals(currentCategory)) {
                        return;
                    }

                    categoryNextCursor =
                            page.getNextCursor();

                    categoryHasMore =
                            page.hasMore();

                    List<Product> current =
                            categoryProducts.getValue();

                    if (current == null) {
                        current = new ArrayList<>();
                    } else {
                        current = new ArrayList<>(current);
                    }

                    current.addAll(
                            page.getProducts()
                    );

                    categoryProducts.setValue(current);

                    categoryLoading = false;
                })
                .addOnFailureListener(e -> {

                    if (requestId != categoryRequestId) {
                        return;
                    }

                    categoryLoading = false;
                });
    }


    public void clearCategory() {

        categoryRequestId++;
        currentCategory = "";
        categoryNextCursor = null;
        categoryHasMore = true;
        categoryLoading = false;

        categoryProducts.setValue(
                Collections.emptyList()
        );
    }

    public void clearSearch() {

        // Invalida cualquier petición anterior
        searchRequestId++;

        currentSearch = "";

        searchNextCursor = null;
        searchHasMore = true;
        searchLoading = false;

        searchProducts.setValue(
                Collections.emptyList()
        );
    }


    public String getCurrentCategory() {
        return currentCategory;
    }
}