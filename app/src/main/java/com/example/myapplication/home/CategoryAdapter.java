package com.example.myapplication.home;

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
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Category> categoryList;
    private Context context;

    public CategoryAdapter(List<Category> categoryList, Context context){
        this.categoryList = categoryList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageCategory;
        TextView nameCategory;

        public ViewHolder(View itemView){
            super(itemView);
            imageCategory = itemView.findViewById(R.id.imgCategoria);
            nameCategory = itemView.findViewById(R.id.txtNombreCategoria);
        }
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_category, parent, false); // tu layout aquí
        return new CategoryAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder,int position){
        Category category = categoryList.get(position);
        holder.nameCategory.setText(category.getName());

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, "Seleccionaste: " + category.getName(), Toast.LENGTH_SHORT).show()
        );

        Category cat = categoryList.get(position);


        Picasso.get()
                .load(category.getImage()) // 🔥 URL desde Firestore
                .into(holder.imageCategory);
    }

    @Override
    public int getItemCount(){
        return categoryList.size();
    }
}


