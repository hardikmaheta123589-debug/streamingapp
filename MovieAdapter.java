package com.example.streamingapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    
    private List<Movie> movies;
    private boolean openDetailsOnClick;
    
    public MovieAdapter(List<Movie> movies) {
        this.movies = movies;
        this.openDetailsOnClick = false;
    }
    
    public MovieAdapter(List<Movie> movies, boolean openDetailsOnClick) {
        this.movies = movies;
        this.openDetailsOnClick = openDetailsOnClick;
    }
    
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }
    
    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }
    
    class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;
        
        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.posterImageView);
        }
        
        void bind(Movie movie) {
            posterImageView.setImageResource(movie.getPosterResourceId());
            
            itemView.setOnClickListener(v -> {
                if (openDetailsOnClick) {
                    Intent intent = new Intent(itemView.getContext(), MovieDetailsActivity.class);
                    intent.putExtra("movie", movie);
                    itemView.getContext().startActivity(intent);
                } else {
                    // Save to continue watching
                    Context context = itemView.getContext();
                    PreferencesHelper preferencesHelper = new PreferencesHelper(context);
                    preferencesHelper.saveContinueWatching(movie, 0);
                    
                    Intent intent = new Intent(context, PlayerActivity.class);
                    intent.putExtra("videoResourceId", movie.getVideoResourceId());
                    intent.putExtra("movieTitle", movie.getTitle());
                    context.startActivity(intent);
                }
            });
        }
    }
}
