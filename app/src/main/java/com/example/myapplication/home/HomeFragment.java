package com.example.myapplication.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView, recyclerViewFavorite, recyclerCategories;
    private AdapterProducts adapter, favoriteAdapter;
    private ArrayList<ProductsList> productList;
    private ArrayList<ProductsList> favoriteList;
    private ArrayList<ProductsList> productListFull;
    private FirebaseFirestore db;
    private LinearLayout linearCategory;
    private EditText search;

    private TextView titulo, seeAllProducts;
    private boolean ignoreTextWatcher = false;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        productList = new ArrayList<>();
        favoriteList = new ArrayList<>();
        productListFull = new ArrayList<>();

        adapter = new AdapterProducts(getContext(), productList);
        favoriteAdapter = new AdapterProducts(getContext(), favoriteList);

        recyclerView = view.findViewById(R.id.recycler_products);
        recyclerViewFavorite = view.findViewById(R.id.recycler_producFavorite);
        recyclerCategories = view.findViewById(R.id.recyclerCategories);
        titulo = view.findViewById(R.id.txtEspecialProducts);
        seeAllProducts = view.findViewById(R.id.txtSeeAllProducts);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFavorite.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerViewFavorite.setAdapter(favoriteAdapter);

        search = view.findViewById(R.id.etSearch);


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

        recyclerCategories.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );


        cargarProductos();
        cargarCategorias();

        seeAllProducts.setOnClickListener(v -> {
            ignoreTextWatcher = true;       // ⬅️ BLOQUEA TextWatcher
            search.setText("");             // dispara onTextChanged, pero ahora está bloqueado
            ignoreTextWatcher = false;
            titulo.setText("Todos los productos");

            // Restaurar todos los productos en el adapter
            adapter.updateList(productListFull);

            // Limpiar el campo de búsqueda si quieres

        });

        return view;
    }

    private void cargarProductos() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("productos")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (!isAdded()) return;
                    productList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {

                        ProductsList product =
                                doc.toObject(ProductsList.class);

                        product.setCantidad(1);
                        productList.add(product);
                    }
                    productListFull.clear();
                    productListFull.addAll(productList);

                    adapter.updateFullList(productList);

                    if (userId == null) {
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    db.collection("users")
                            .document(userId)
                            .collection("purchases")
                            .get()
                            .addOnSuccessListener(purchaseSnapshot -> {
                                Set<String> purchasedIds = new HashSet<>();
                                for (DocumentSnapshot doc : purchaseSnapshot) {
                                    purchasedIds.add(doc.getId()); // productId comprado
                                }

                                // 3️⃣ Marcar productos comprados como favoritos
                                for (ProductsList p : productList) {
                                    p.setFavorite(purchasedIds.contains(p.getId()));
                                }


                                adapter.notifyDataSetChanged(); // 🔹 Notifica que hay nuevos datos
                            })
                            .addOnFailureListener(e ->
                                    Log.e("Firestore", "Error al obtener productos", e));
                });


    }

    private void cargarCategorias() {

        db.collection("category")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    List<Category> categoryList = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot) {
                        Category category = doc.toObject(Category.class);
                        categoryList.add(category);
                    }

                    CategoryAdapter adapter =
                            new CategoryAdapter(categoryList, requireContext());

                    recyclerCategories.setAdapter(adapter);
                });
    }
}