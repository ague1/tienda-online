package com.example.myapplication.features.client.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.features.cart.CartUseCase;
import com.example.myapplication.features.client.adapter.CategoryAdapter;
import com.example.myapplication.features.product.model.Category;
import com.example.myapplication.features.product.model.Product;
import com.example.myapplication.features.client.adapter.ClientProductsAdapter;
import com.example.myapplication.features.product.usecase.GetCategoriesUseCase;
import com.example.myapplication.features.product.usecase.GetProductsUseCase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ClientHomeFragment extends Fragment {
    @Inject
    GetProductsUseCase getProductsUseCase;
    @Inject
    GetCategoriesUseCase getCategoriesUseCase;
    @Inject
    CartUseCase cartUseCase;
    private RecyclerView recyclerView, recyclerViewFavorite, recyclerCategories;
    private ClientProductsAdapter adapter, favoriteAdapter;
    private ArrayList<Product> productList;
    private ArrayList<Product> favoriteList;
    private ArrayList<Product> allProducts;
    private EditText search;

    private TextView titulo, seeAllProducts;
    private ArrayList<Category> categoryList;
    private CategoryAdapter categoryAdapter;
    private boolean ignoreTextWatcher = false;

    public ClientHomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        productList = new ArrayList<>();
        favoriteList = new ArrayList<>();
        allProducts = new ArrayList<>();
        categoryList = new ArrayList<>();

        adapter = new ClientProductsAdapter(
                requireContext(),
                productList,
                cartUseCase
        );

        favoriteAdapter = new ClientProductsAdapter(
                requireContext(),
                favoriteList,
                cartUseCase
        );

        categoryAdapter = new CategoryAdapter(
                requireContext(),
                categoryList,
                category -> filterProductsByCategory(category.getName())
        );


        recyclerView = view.findViewById(R.id.recycler_products);
        recyclerViewFavorite = view.findViewById(R.id.recycler_producFavorite);
        recyclerCategories = view.findViewById(R.id.recyclerCategories);
        titulo = view.findViewById(R.id.txtEspecialProducts);
        seeAllProducts = view.findViewById(R.id.txtSeeAllProducts);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFavorite.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerCategories.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        recyclerCategories.setAdapter(categoryAdapter);
        recyclerView.setAdapter(adapter);
        recyclerViewFavorite.setAdapter(favoriteAdapter);
        search = view.findViewById(R.id.etSearch);


        loadProducts();
        loadCategory();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ignoreTextWatcher) return;
                if (s.length() == 0) {
                    titulo.setText("Especiales de Hoy");
                }
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });



        seeAllProducts.setOnClickListener(v -> {
            ignoreTextWatcher = true;       // ⬅️ BLOQUEA TextWatcher
            search.setText("");             // dispara onTextChanged, pero ahora está bloqueado
            ignoreTextWatcher = false;
            titulo.setText("Todos los productos");

            // Restaurar todos los productos en el adapter
            adapter.showAll();;

            // Limpiar el campo de búsqueda si quieres

        });

        return view;
    }

    private void filterProductsByCategory(String categoryName) {
        ArrayList<Product> filteredList = new ArrayList<>();

        for (Product product : allProducts) {

            if (product.getCategoria() != null &&
                    product.getCategoria().equalsIgnoreCase(categoryName)) {

                filteredList.add(product);
            }
        }

        titulo.setText(categoryName);
        adapter.updateList(filteredList);
    }

    private void loadProducts() {

        getProductsUseCase.execute()
                .addOnSuccessListener(products -> {

                    allProducts.clear();
                    allProducts.addAll(products);

                    productList.clear();
                    productList.addAll(products);

                    adapter.updateFullList(products);
                    adapter.updateList(products);

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            requireContext(),
                            "No se pudieron cargar los productos",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    private void loadCategory() {
        getCategoriesUseCase.execute(categories -> {

            categoryList.clear();
            categoryList.addAll(categories);

            categoryAdapter.updateList(categories);
        });
    }
}
