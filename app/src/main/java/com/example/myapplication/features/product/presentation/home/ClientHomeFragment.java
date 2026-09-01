package com.example.myapplication.features.product.presentation.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.features.cart.CartViewModel;
import com.example.myapplication.features.product.presentation.home.adapter.CategoryAdapter;
import com.example.myapplication.features.cart.OnCartClickListener;
import com.example.myapplication.features.product.presentation.home.adapter.PromotionAdapter;
import com.example.myapplication.features.product.presentation.promotion.PromotionViewModel;
import com.example.myapplication.features.product.domain.model.Product;
import com.example.myapplication.features.product.presentation.home.adapter.ClientProductsAdapter;
import com.example.myapplication.features.product.application.usecase.GetCategoriesUseCase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class ClientHomeFragment extends Fragment {

    @Inject
    GetCategoriesUseCase getCategoriesUseCase;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewAllProducts;
    private RecyclerView recyclerCategories;

    private PromotionViewModel promotionViewModel;
    private PromotionAdapter promotionAdapter;
    private ClientProductsAdapter favoriteAdapter;
    private CategoryAdapter categoryAdapter;
    private ClientHomeViewModel viewModel;
    private CartViewModel cartViewModel;

    private EditText search;
    private TextView titulo;
    private LinearLayoutManager allProductsLayoutManager;
    private LinearLayout layoutPromotions;
    private TextView txtLookProducts;

    private final List<Product> allProducts = new ArrayList<>();

    private final Handler searchHandler =
            new Handler(Looper.getMainLooper());

    private Runnable searchRunnable;


    public ClientHomeFragment() {}

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        View view = inflater.inflate(
                R.layout.fragment_home,
                container,
                false
        );

        viewModel = new ViewModelProvider(this)
                .get(ClientHomeViewModel.class);

        cartViewModel = new ViewModelProvider(this)
                .get(CartViewModel.class);

        promotionViewModel = new ViewModelProvider(this)
                .get(PromotionViewModel.class);


        initViews(view);
        initRecyclerViews();
        initAdapters();
        initSearch();

        observeProducts();
        observeSearchProducts();
        observePromotions();
        observeCategoryProducts();
        observeCart();


        loadProducts();
        loadPromotions();
        loadCategories();
        return view;
    }




    private void observeProducts() {

        viewModel.getProducts().observe(
                getViewLifecycleOwner(),
                products -> {

                    allProducts.clear();
                    allProducts.addAll(products);

                    String query =
                            search.getText()
                                    .toString()
                                    .trim();

                    // Si estamos buscando, no tocar la lista
                    if (!query.isEmpty()) {
                        return;
                    }

                    // Si estamos viendo una categoría,
                    // tampoco tocar la lista
                    if (!viewModel.getCurrentCategory().isEmpty()) {
                        return;
                    }

                    titulo.setText(R.string.title_today_specials);

                    favoriteAdapter.submitList(
                            new ArrayList<>(allProducts)
                    );

                }
        );
    }

    private void initViews(View view) {

        recyclerView =
                view.findViewById(R.id.recycler_promotionProducts);

        recyclerViewAllProducts=
                view.findViewById(R.id.recycler_allProducts);

        recyclerCategories =
                view.findViewById(R.id.recyclerCategories);

        titulo =
                view.findViewById(R.id.txtEspecialProducts);

        search =
                view.findViewById(R.id.etSearch);

        layoutPromotions =
                view.findViewById(R.id.layoutPromotions);

        txtLookProducts =
                view.findViewById(R.id.txtLookProducts);
    }

    private void initRecyclerViews() {
        LinearLayoutManager productsLayoutManager =
                new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,
                        false);

        recyclerView.setLayoutManager(
                productsLayoutManager
        );

        recyclerView.setHasFixedSize(true);


        allProductsLayoutManager =
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                );

        recyclerViewAllProducts.setLayoutManager(
                allProductsLayoutManager
        );

        recyclerViewAllProducts.setHasFixedSize(true);


        LinearLayoutManager categoryLayoutManager =
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                );

        recyclerCategories.setLayoutManager(
                categoryLayoutManager
        );

        recyclerCategories.setHasFixedSize(true);

        setupProductsPagination();
    }

    private void setupProductsPagination() {

        recyclerViewAllProducts.addOnScrollListener(
                new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrolled(
                            @NonNull RecyclerView recyclerView,
                            int dx,
                            int dy
                    ) {

                        super.onScrolled(
                                recyclerView,
                                dx,
                                dy
                        );

                        if (dx <= 0) {
                            return;
                        }

                        int visibleItemCount =
                                allProductsLayoutManager
                                        .getChildCount();

                        int totalItemCount =
                                allProductsLayoutManager
                                        .getItemCount();

                        int firstVisibleItemPosition =
                                allProductsLayoutManager
                                        .findFirstVisibleItemPosition();

                        if (firstVisibleItemPosition
                                + visibleItemCount
                                >= totalItemCount - 2) {

                            String query =
                                    search.getText()
                                            .toString()
                                            .trim();

                            if (!query.isEmpty()) {

                                viewModel.loadNextSearchPage();

                            } else if (!viewModel.getCurrentCategory().isEmpty()) {

                                viewModel.loadNextCategoryPage();

                            } else {

                                viewModel.loadNextPage();
                            }
                        }
                    }
                }
        );
    }

    private void initAdapters() {
        OnCartClickListener cartClickListener =
                new OnCartClickListener() {

                    @Override
                    public void onAdd(
                            Product product,
                            double price
                    ) {

                        if (product == null
                                || product.getId() == null) {
                            return;
                        }

                        cartViewModel.addProduct(
                                product,
                                price
                        );
                    }

                    @Override
                    public void onIncrease(Product product) {

                        if (product == null
                                || product.getId() == null) {
                            return;
                        }

                        cartViewModel.increase(
                                product.getId()
                        );
                    }

                    @Override
                    public void onDecrease(Product product) {

                        if (product == null
                                || product.getId() == null) {
                            return;
                        }

                        cartViewModel.decrease(
                                product.getId()
                        );
                    }
                };

        favoriteAdapter = new ClientProductsAdapter(
                cartClickListener
        );

        promotionAdapter = new PromotionAdapter(
                cartClickListener
        );

        categoryAdapter = new CategoryAdapter(
                new ArrayList<>(),
                category -> {

                    layoutPromotions.setVisibility(
                            View.GONE
                    );

                    txtLookProducts.setVisibility(
                            View.GONE
                    );

                    viewModel.loadProductsByCategory(
                            category.getName()
                    );
                }
        );

        recyclerView.setAdapter(promotionAdapter);
        recyclerViewAllProducts.setAdapter(favoriteAdapter);
        recyclerCategories.setAdapter(categoryAdapter);
    }

    private void observeCategoryProducts() {

        viewModel.getCategoryProducts().observe(
                getViewLifecycleOwner(),
                products -> {

                    if (products == null) {
                        return;
                    }

                    String category =
                            viewModel.getCurrentCategory();

                    if (category.isEmpty()) {
                        return;
                    }

                    titulo.setText(category);

                    favoriteAdapter.submitList(products);
                }
        );
    }

    private void initSearch() {

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {

                String query = s.toString().trim();

                // Cancelar búsqueda pendiente
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(
                            searchRunnable
                    );
                }

                if (query.isEmpty()) {

                    layoutPromotions.setVisibility(
                            View.VISIBLE
                    );

                    txtLookProducts.setVisibility(
                            View.VISIBLE
                    );
                    viewModel.clearSearch();
                    viewModel.clearCategory();

                    titulo.setText(R.string.title_today_specials);

                    favoriteAdapter.submitList(allProducts);

                    return;
                }

                titulo.setText(R.string.title_search_result);

                searchRunnable = () -> viewModel.search(query);

                searchHandler.postDelayed(
                        searchRunnable,
                        350
                );
            }

            @Override
            public void afterTextChanged(
                    Editable s
            ) {
            }
        });
    }



    private void loadProducts() {

        viewModel.loadFirstPage();
    }

    private void loadCategories() {

        getCategoriesUseCase.execute()
                .addOnSuccessListener(categories ->
                        categoryAdapter.updateList(categories)
                );
    }


    private void observeCart() {
        cartViewModel.getItems().observe(
                getViewLifecycleOwner(),
                items -> {
                    if (items == null) {return;}
                    favoriteAdapter.updateCartQuantities(items);
                    promotionAdapter.updateCartQuantities(items);

                }
        );
    }

    private void observePromotions() {

        promotionViewModel
                .getPromotions()
                .observe(
                        getViewLifecycleOwner(),
                        promotions -> {

                            if (promotions == null) {
                                return;
                            }

                            promotionAdapter.submitList(promotions);

                            favoriteAdapter.updatePromotions(
                                    promotions
                            );
                        }
                );
    }

    private void loadPromotions() {
        promotionViewModel.loadFirstPage();
    }

    private void observeSearchProducts() {

        viewModel.getSearchProducts().observe(
                getViewLifecycleOwner(),
                products -> {

                    String query =
                            search.getText()
                                    .toString()
                                    .trim();

                    if (query.isEmpty()) {
                        return;
                    }

                    favoriteAdapter.submitList(products);
                }
        );
    }

    @Override
    public void onDestroyView() {

        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
            searchRunnable = null;
        }

        super.onDestroyView();
    }

}