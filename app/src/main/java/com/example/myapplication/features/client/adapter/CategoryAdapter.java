package com.example.myapplication.features.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.features.product.model.Category;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter
        extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categoryList;
    private Context context;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }


    public CategoryAdapter(Context context, List<Category> categoryList,
                           OnCategoryClickListener listener


    ) {
        this.categoryList = categoryList;
        this.context = context;
        this.listener = listener;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageCategory;
        TextView nameCategory;

        public ViewHolder(View itemView) {
            super(itemView);

            imageCategory = itemView.findViewById(R.id.imgCategoria);
            nameCategory = itemView.findViewById(R.id.txtNombreCategoria);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_category, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Category category = categoryList.get(position);

        holder.nameCategory.setText(category.getName());

        Picasso.get()
                .load(category.getImage())
                .into(holder.imageCategory);

        holder.itemView.setOnClickListener(v ->{
            listener.onCategoryClick(category);

        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void updateList(List<Category> newList) {

        categoryList.clear();
        categoryList.addAll(newList);

        notifyDataSetChanged();
    }
}


