package com.example.myapplication.features.product.presentation.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.core.scheduler.DebounceScheduler;
import com.example.myapplication.features.cart.presentation.viewmodel.CartViewModel;
import com.example.myapplication.features.product.domain.model.PricedProduct;
import com.example.myapplication.features.product.domain.model.Category;
import com.example.myapplication.features.product.domain.model.PromotionProduct;
import com.example.myapplication.features.product.presentation.home.adapter.CategoryAdapter;
import com.example.myapplication.features.cart.presentation.listener.OnCartClickListener;
import com.example.myapplication.features.product.presentation.home.adapter.PromotionAdapter;
import com.example.myapplication.features.product.presentation.promotion.PromotionViewModel;
import com.example.myapplication.features.product.domain.model.Product;
import com.example.myapplication.features.product.presentation.home.adapter.ClientProductsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class ClientHomeFragment extends Fragment {
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

    private List<Product> allProducts = Collections.emptyList();


    @Inject
    DebounceScheduler debounceScheduler;


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
        observeCategories();
        observeCart();

        loadProducts();
        loadPromotions();
        loadCategories();
        return view;
    }




    private void observeProducts() {
        viewModel.getProductsState().observe(
                getViewLifecycleOwner(),
                state -> {

                    if (state == null) return;

                    switch (state.getStatus()) {

                        case LOADING:
                            // Opcional: mostrar indicador de carga.
                            break;

                        case SUCCESS:

                            List<Product> products = state.getData();

                            allProducts = products != null
                                    ? products
                                    : Collections.emptyList();

                            String query =
                                    search.getText()
                                            .toString()
                                            .trim();

                            if (!query.isEmpty()) return;

                            if (!viewModel.getCurrentCategory().isEmpty()) {
                                return;
                            }

                            titulo.setText(
                                    R.string.title_today_specials
                            );

                            favoriteAdapter.submitList(allProducts);
                            break;

                        case ERROR:
                            Toast.makeText(
                                    requireContext(),
                                    "No se pudieron cargar los productos",
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;

                        case IDLE:
                            break;
                    }
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
                            PricedProduct pricedProduct
                    ) {

                        if (pricedProduct == null
                                || pricedProduct.getProduct() == null
                                || pricedProduct.getProduct().getId() == null) {
                            return;
                        }

                        cartViewModel.addProduct(
                                pricedProduct
                        );
                    }

                    @Override
                    public void onIncrease(
                            PricedProduct pricedProduct
                    ) {

                        if (pricedProduct == null
                                || pricedProduct.getProduct() == null
                                || pricedProduct.getProduct().getId() == null) {
                            return;
                        }

                        cartViewModel.increase(
                                pricedProduct.getProduct().getId()
                        );
                    }

                    @Override
                    public void onDecrease(
                            PricedProduct pricedProduct
                    ) {

                        if (pricedProduct == null
                                || pricedProduct.getProduct() == null
                                || pricedProduct.getProduct().getId() == null) {
                            return;
                        }

                        cartViewModel.decrease(
                                pricedProduct.getProduct().getId()
                        );
                    }
                };

        favoriteAdapter =
                new ClientProductsAdapter(
                        cartClickListener
                );

        promotionAdapter =
                new PromotionAdapter(
                        cartClickListener
                );

        categoryAdapter =
                new CategoryAdapter(
                        new ArrayList<>(),
                        category -> {

                            cancelPendingSearch();
                            search.setText("");
                            layoutPromotions.setVisibility(
                                    View.GONE
                            );

                            txtLookProducts.setVisibility(
                                    View.GONE
                            );

                            viewModel.clearSearch();
                            viewModel.loadProductsByCategory(
                                    category.getName()
                            );
                        }
                );

        recyclerView.setAdapter(
                promotionAdapter
        );

        recyclerViewAllProducts.setAdapter(
                favoriteAdapter
        );

        recyclerCategories.setAdapter(
                categoryAdapter
        );
    }



    private void observeCategoryProducts() {

        viewModel.getCategoryProductsState().observe(
                getViewLifecycleOwner(),
                state -> {

                    if (state == null) return;

                    switch (state.getStatus()) {

                        case SUCCESS:

                            if (viewModel.getCurrentCategory().isEmpty()) {
                                return;
                            }

                            List<Product> products = state.getData();

                            titulo.setText(
                                    viewModel.getCurrentCategory()
                            );

                            favoriteAdapter.submitList(
                                    products != null
                                            ? products
                                            : Collections.emptyList()
                            );
                            break;

                        case ERROR:
                            Toast.makeText(
                                    requireContext(),
                                    "No se pudieron cargar los productos de la categoría",
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;

                        case LOADING:
                        case IDLE:
                            break;
                    }
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
                    debounceScheduler.removeCallbacks(searchRunnable);

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

                searchRunnable = () -> {
                    searchRunnable = null;
                    viewModel.search(query);
                };

                debounceScheduler.postDelayed(
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
        viewModel.loadCategories();
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
                .getPromotionsState()
                .observe(
                        getViewLifecycleOwner(),
                        state -> {

                            if (state == null) {
                                return;
                            }

                            switch (state.getStatus()) {

                                case SUCCESS:

                                    List<PromotionProduct> promotions =
                                            state.getData();

                                    if (promotions == null) {
                                        promotions =
                                                Collections.emptyList();
                                    }

                                    promotionAdapter.submitList(
                                            promotions
                                    );

                                    favoriteAdapter.updatePromotions(
                                            promotions
                                    );

                                    updateCartPrices(
                                            promotions
                                    );

                                    break;

                                case ERROR:

                                    Toast.makeText(
                                            requireContext(),
                                            "No se pudieron cargar las promociones",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    break;

                                case LOADING:
                                case IDLE:
                                    break;
                            }
                        }
                );
    }


    private void loadPromotions() {
        promotionViewModel.loadFirstPage();
    }

    private void observeSearchProducts() {

        viewModel.getSearchProductsState().observe(
                getViewLifecycleOwner(),
                state -> {

                    if (state == null) return;

                    switch (state.getStatus()) {

                        case SUCCESS:

                            String query =
                                    search.getText()
                                            .toString()
                                            .trim();

                            if (query.isEmpty()) return;

                            List<Product> products = state.getData();

                            favoriteAdapter.submitList(
                                    products != null
                                            ? products
                                            : Collections.emptyList()
                            );
                            break;

                        case ERROR:
                            Toast.makeText(
                                    requireContext(),
                                    "No se pudieron buscar los productos",
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;

                        case LOADING:
                        case IDLE:
                            break;
                    }
                }
        );
    }

    @Override
    public void onDestroyView() {

        if (searchRunnable != null) {
            debounceScheduler.removeCallbacks(searchRunnable);
            searchRunnable = null;
        }

        super.onDestroyView();
    }

    private void updateCartPrices(
            List<PromotionProduct> promotions
    ) {

        if (promotions == null) {
            return;
        }

        for (PromotionProduct promotion : promotions) {

            if (promotion == null ||
                    promotion.getProduct() == null) {
                continue;
            }

            String productId =
                    promotion.getProduct().getId();

            if (productId == null ||
                    productId.trim().isEmpty()) {
                continue;
            }

            long specialPrice =
                    promotion.getSpecialPrice();

            cartViewModel.updateProductPrice(
                    productId,
                    specialPrice
            );
        }
    }

    private void cancelPendingSearch() {

        if (searchRunnable != null) {
            debounceScheduler.removeCallbacks(searchRunnable);
            searchRunnable = null;
        }
    }

    private void observeCategories() {

        viewModel.getCategoriesState().observe(
                getViewLifecycleOwner(),
                state -> {

                    if (state == null) {
                        return;
                    }

                    switch (state.getStatus()) {

                        case SUCCESS:

                            List<Category> categories =
                                    state.getData();

                            categoryAdapter.updateList(
                                    categories != null
                                            ? categories
                                            : Collections.emptyList()
                            );

                            break;

                        case ERROR:

                            Toast.makeText(
                                    requireContext(),
                                    "No se pudieron cargar las categorías",
                                    Toast.LENGTH_SHORT
                            ).show();

                            break;

                        case LOADING:
                        case IDLE:
                            break;
                    }
                }
        );
    }

}