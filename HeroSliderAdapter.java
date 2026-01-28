package com.example.streamingapp;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class HeroSliderAdapter extends RecyclerView.Adapter<HeroSliderAdapter.HeroViewHolder> {

    private final List<Movie> movies;
    private final ViewPager2 viewPager2;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable autoSlideRunnable;

    public HeroSliderAdapter(List<Movie> movies, ViewPager2 viewPager2) {
        this.movies = movies != null ? movies : new ArrayList<>();
        this.viewPager2 = viewPager2;
        setupAutoSlide();
    }

    @NonNull
    @Override
    public HeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hero_slider_item, parent, false);
        return new HeroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeroViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    private void setupAutoSlide() {
        if (viewPager2 == null || movies.isEmpty()) return;

        autoSlideRunnable = new Runnable() {
            @Override
            public void run() {
                int next = (viewPager2.getCurrentItem() + 1) % movies.size();
                viewPager2.setCurrentItem(next, true);
                handler.postDelayed(this, 4000);
            }
        };

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                handler.removeCallbacks(autoSlideRunnable);
                handler.postDelayed(autoSlideRunnable, 4000);
            }
        });

        handler.postDelayed(autoSlideRunnable, 4000);
    }

    public void stopAutoSlide() {
        handler.removeCallbacks(autoSlideRunnable);
    }

    static class HeroViewHolder extends RecyclerView.ViewHolder {

        ImageView bannerImageView;

        HeroViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImageView = itemView.findViewById(R.id.bannerImageView);
        }

        void bind(Movie movie) {
            bannerImageView.setImageResource(movie.getPosterResourceId());

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(
                        itemView.getContext(),
                        MovieDetailsActivity.class
                );
                intent.putExtra("movie", movie);
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
