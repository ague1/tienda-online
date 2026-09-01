package com.example.myapplication.features.product.presentation.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.R;
import com.example.myapplication.features.product.presentation.home.listener.OnCategoryClickListener;
import com.example.myapplication.features.product.domain.model.Category;

import java.util.List;

public class CategoryAdapter
        extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final List<Category> categoryList;
    private final OnCategoryClickListener listener;

    public CategoryAdapter(
            List<Category> categoryList,
            OnCategoryClickListener listener
    ) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imageCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageCategory =
                    itemView.findViewById(R.id.imgCategoria);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.content_category,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Category category =
                categoryList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(category.getImage())
                .placeholder(
                        R.drawable.ic_launcher_foreground
                )
                .error(
                        R.drawable.ic_launcher_foreground
                )
                .diskCacheStrategy(
                        DiskCacheStrategy.ALL
                )
                .centerCrop()
                .into(holder.imageCategory);

        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void updateList(
            List<Category> newList
    ) {

        categoryList.clear();

        if (newList != null) {
            categoryList.addAll(newList);
        }

        notifyItemRangeChanged(
                0,
                categoryList.size()
        );
    }
}