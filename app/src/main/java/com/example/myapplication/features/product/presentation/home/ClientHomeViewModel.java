package com.example.myapplication.features.product.presentation.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.core.ui.UiState;
import com.example.myapplication.features.product.application.usecase.GetCategoriesUseCase;
import com.example.myapplication.features.product.application.usecase.GetProductsUseCase;
import com.example.myapplication.features.product.domain.cursor.CategoryProductCursor;
import com.example.myapplication.features.product.domain.cursor.ProductPageCursor;
import com.example.myapplication.features.product.domain.cursor.SearchCursor;
import com.example.myapplication.features.product.domain.model.Category;
import com.example.myapplication.features.product.domain.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;


@HiltViewModel
public class ClientHomeViewModel extends ViewModel {

    private final GetProductsUseCase getProductsUseCase;

    private final GetCategoriesUseCase getCategoriesUseCase;

    private final MutableLiveData<UiState<List<Product>>> productsState =
            new MutableLiveData<>(UiState.idle());

    private ProductPageCursor nextCursor;
    private boolean loading = false;
    private boolean hasMore = true;

    private final MutableLiveData<UiState<List<Product>>> searchProductsState =
            new MutableLiveData<>(UiState.idle());

    private SearchCursor searchNextCursor;
    private boolean searchLoading = false;
    private boolean searchHasMore = true;

    private String currentSearch = "";
    private long searchRequestId = 0;

    private final MutableLiveData<UiState<List<Product>>> categoryProductsState =
            new MutableLiveData<>(UiState.idle());

    public LiveData<UiState<List<Product>>> getCategoryProductsState() {
        return categoryProductsState;
    }
    private CategoryProductCursor categoryNextCursor;
    private boolean categoryLoading = false;
    private boolean categoryHasMore = true;

    private String currentCategory = "";
    private long categoryRequestId = 0;

    @Inject
    public ClientHomeViewModel(
            GetProductsUseCase getProductsUseCase,
            GetCategoriesUseCase getCategoriesUseCase
    ) {
        this.getProductsUseCase = getProductsUseCase;
        this.getCategoriesUseCase = getCategoriesUseCase;
    }

    public LiveData<UiState<List<Product>>> getProductsState() {
        return productsState;
    }

    private final MutableLiveData<UiState<List<Category>>> categoriesState =
            new MutableLiveData<>(UiState.idle());
    public LiveData<UiState<List<Category>>> getCategoriesState() {
        return categoriesState;
    }

    public void loadFirstPage() {
        if (loading) return;

        loading = true;
        nextCursor = null;
        hasMore = true;

        productsState.setValue(UiState.loading());

        getProductsUseCase.getFirstPage()
                .addOnSuccessListener(page -> {
                    List<Product> result =
                            new ArrayList<>(page.getProducts());

                    productsState.setValue(
                            UiState.success(result)
                    );

                    nextCursor = page.getNextCursor();
                    hasMore = page.hasMore();
                    loading = false;
                })
                .addOnFailureListener(e -> {
                    loading = false;

                    productsState.setValue(
                            UiState.error("PRODUCTS_LOAD_ERROR")
                    );
                });
    }

    public void loadNextPage() {
        if (loading || nextCursor == null || !hasMore) {
            return;
        }

        loading = true;

        getProductsUseCase.getNextPage(nextCursor)
                .addOnSuccessListener(page -> {

                    List<Product> current =
                            productsState.getValue() != null
                                    ? productsState.getValue().getData()
                                    : null;

                    List<Product> updated;

                    if (current == null) {
                        updated = new ArrayList<>();
                    } else {
                        updated = new ArrayList<>(current);
                    }

                    updated.addAll(page.getProducts());

                    productsState.setValue(
                            UiState.success(updated)
                    );

                    nextCursor = page.getNextCursor();
                    hasMore = page.hasMore();
                    loading = false;
                })
                .addOnFailureListener(e -> {
                    loading = false;

                    /*
                     * No cambiamos el estado a ERROR aquí porque
                     * ya tenemos productos visibles y no queremos
                     * borrar la lista actual por un fallo de paginación.
                     */
                });
    }

    public LiveData<UiState<List<Product>>> getSearchProductsState() {
        return searchProductsState;
    }

    public void search(String query) {
        if (query == null) return;

        final String normalizedQuery =
                query.trim().toLowerCase(Locale.ROOT);

        if (normalizedQuery.isEmpty()) {
            clearSearch();
            return;
        }

        currentSearch = normalizedQuery;
        searchNextCursor = null;
        searchHasMore = true;

        final long requestId = ++searchRequestId;

        searchLoading = true;

        searchProductsState.setValue(
                UiState.loading()
        );

        getProductsUseCase
                .searchProducts(normalizedQuery)
                .addOnSuccessListener(page -> {

                    if (requestId != searchRequestId) {
                        return;
                    }

                    List<Product> result =
                            new ArrayList<>(page.getProducts());

                    searchProductsState.setValue(
                            UiState.success(result)
                    );

                    searchNextCursor = page.getNextCursor();
                    searchHasMore = page.hasMore();
                    searchLoading = false;
                })
                .addOnFailureListener(e -> {

                    if (requestId != searchRequestId) {
                        return;
                    }

                    searchLoading = false;

                    searchProductsState.setValue(
                            UiState.error("PRODUCT_SEARCH_ERROR")
                    );
                });
    }

    public void loadNextSearchPage() {
        if (searchLoading
                || searchNextCursor == null
                || !searchHasMore
                || currentSearch.isEmpty()) {
            return;
        }

        final String queryAtRequest = currentSearch;
        final long requestId = searchRequestId;

        searchLoading = true;

        getProductsUseCase
                .searchProductsNextPage(
                        queryAtRequest,
                        searchNextCursor
                )
                .addOnSuccessListener(page -> {

                    if (requestId != searchRequestId) {
                        return;
                    }

                    if (!queryAtRequest.equals(currentSearch)) {
                        return;
                    }

                    List<Product> current =
                            searchProductsState.getValue() != null
                                    ? searchProductsState
                                    .getValue()
                                    .getData()
                                    : null;

                    List<Product> updated;

                    if (current == null) {
                        updated = new ArrayList<>();
                    } else {
                        updated = new ArrayList<>(current);
                    }

                    updated.addAll(page.getProducts());

                    searchProductsState.setValue(
                            UiState.success(updated)
                    );

                    searchNextCursor = page.getNextCursor();
                    searchHasMore = page.hasMore();
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
        if (category == null) return;

        final String normalizedCategory =
                category.trim();

        if (normalizedCategory.isEmpty()) {
            return;
        }

        searchRequestId++;
        currentSearch = "";
        searchNextCursor = null;
        searchHasMore = true;
        searchLoading = false;

        currentCategory = normalizedCategory;
        categoryNextCursor = null;
        categoryHasMore = true;

        final long requestId = ++categoryRequestId;

        categoryLoading = true;

        categoryProductsState.setValue(
                UiState.loading()
        );

        getProductsUseCase
                .getProductsByCategory(normalizedCategory)
                .addOnSuccessListener(page -> {

                    if (requestId != categoryRequestId) {
                        return;
                    }

                    if (!normalizedCategory.equals(currentCategory)) {
                        return;
                    }

                    List<Product> result =
                            new ArrayList<>(page.getProducts());

                    categoryProductsState.setValue(
                            UiState.success(result)
                    );

                    categoryNextCursor = page.getNextCursor();
                    categoryHasMore = page.hasMore();
                    categoryLoading = false;
                })
                .addOnFailureListener(e -> {

                    if (requestId != categoryRequestId) {
                        return;
                    }

                    categoryLoading = false;

                    categoryProductsState.setValue(
                            UiState.error("PRODUCT_CATEGORY_ERROR")
                    );
                });
    }

    public void loadNextCategoryPage() {
        if (categoryLoading
                || categoryNextCursor == null
                || !categoryHasMore
                || currentCategory.isEmpty()) {
            return;
        }

        final String categoryAtRequest = currentCategory;
        final long requestId = categoryRequestId;

        categoryLoading = true;

        getProductsUseCase
                .getProductsByCategoryNextPage(
                        categoryAtRequest,
                        categoryNextCursor
                )
                .addOnSuccessListener(page -> {

                    if (requestId != categoryRequestId) {
                        return;
                    }

                    if (!categoryAtRequest.equals(currentCategory)) {
                        return;
                    }

                    List<Product> current =
                            categoryProductsState.getValue() != null
                                    ? categoryProductsState
                                    .getValue()
                                    .getData()
                                    : null;

                    List<Product> updated;

                    if (current == null) {
                        updated = new ArrayList<>();
                    } else {
                        updated = new ArrayList<>(current);
                    }

                    updated.addAll(page.getProducts());

                    categoryProductsState.setValue(
                            UiState.success(updated)
                    );

                    categoryNextCursor = page.getNextCursor();
                    categoryHasMore = page.hasMore();
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

        categoryProductsState.setValue(
                UiState.success(Collections.emptyList())
        );
    }

    public void clearSearch() {
        searchRequestId++;

        currentSearch = "";
        searchNextCursor = null;
        searchHasMore = true;
        searchLoading = false;

        searchProductsState.setValue(
                UiState.success(Collections.emptyList())
        );
    }

    public String getCurrentCategory() {
        return currentCategory;
    }

    public void loadCategories() {

        categoriesState.setValue(
                UiState.loading()
        );

        getCategoriesUseCase
                .execute()
                .addOnSuccessListener(categories -> {

                    categoriesState.setValue(
                            UiState.success(
                                    categories != null
                                            ? new ArrayList<>(categories)
                                            : Collections.emptyList()
                            )
                    );
                })
                .addOnFailureListener(e -> {

                    categoriesState.setValue(
                            UiState.error("CATEGORIES_LOAD_ERROR")
                    );
                });
    }
}