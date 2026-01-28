package com.example.streamingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    
    private List<Category> categories;
    
    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }
    
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if (categories != null && position >= 0 && position < categories.size()) {
            Category category = categories.get(position);
            holder.bind(category);
        }
    }
    
    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }
    
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitleTextView;
        RecyclerView moviesRecyclerView;
        MovieAdapter movieAdapter;
        PagerSnapHelper snapHelper;
        boolean snapHelperAttached = false;
        
        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitleTextView = itemView.findViewById(R.id.categoryTitleTextView);
            moviesRecyclerView = itemView.findViewById(R.id.moviesRecyclerView);
            
            // Setup horizontal RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    itemView.getContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
            );
            moviesRecyclerView.setLayoutManager(layoutManager);
            
            // Add snap behavior for smooth scrolling (only once)
            if (!snapHelperAttached) {
                try {
                    snapHelper = new PagerSnapHelper();
                    snapHelper.attachToRecyclerView(moviesRecyclerView);
                    snapHelperAttached = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    // Continue without snap helper if it fails
                }
            }
            
            movieAdapter = new MovieAdapter(null);
            moviesRecyclerView.setAdapter(movieAdapter);
        }
        
        void bind(Category category) {
            if (category != null) {
                categoryTitleTextView.setText(category.getName() != null ? category.getName() : "");
                List<Movie> movies = category.getMovies();
                movieAdapter = new MovieAdapter(movies != null ? movies : new ArrayList<>());
                moviesRecyclerView.setAdapter(movieAdapter);
            }
        }
    }
}
