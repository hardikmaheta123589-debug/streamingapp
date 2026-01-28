package com.example.streamingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MovieDetailsActivity extends AppCompatActivity {
    
    private Movie movie;
    private PreferencesHelper preferencesHelper;
    private Button myListButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        
        // Get movie from intent
        movie = (Movie) getIntent().getSerializableExtra("movie");
        if (movie == null) {
            finish();
            return;
        }
        
        preferencesHelper = new PreferencesHelper(this);
        
        setupViews();
        updateMyListButton();
    }
    
    private void setupViews() {
        ImageView bannerImageView = findViewById(R.id.bannerImageView);
        TextView movieTitleTextView = findViewById(R.id.movieTitleTextView);
        TextView categoryTextView = findViewById(R.id.categoryTextView);
        Button playButton = findViewById(R.id.playButton);
        myListButton = findViewById(R.id.myListButton);
        
        bannerImageView.setImageResource(movie.getPosterResourceId());
        movieTitleTextView.setText(movie.getTitle());
        categoryTextView.setText(movie.getCategory());
        
        playButton.setOnClickListener(v -> {
            // Save to continue watching
            preferencesHelper.saveContinueWatching(movie, 0);
            
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("videoResourceId", movie.getVideoResourceId());
            intent.putExtra("movieTitle", movie.getTitle());
            startActivity(intent);
        });
        
        myListButton.setOnClickListener(v -> {
            if (preferencesHelper.isInMyList(movie)) {
                preferencesHelper.removeFromMyList(movie);
            } else {
                preferencesHelper.addToMyList(movie);
            }
            updateMyListButton();
        });
    }
    
    private void updateMyListButton() {
        if (preferencesHelper.isInMyList(movie)) {
            myListButton.setText(getString(R.string.remove_from_my_list));
        } else {
            myListButton.setText(getString(R.string.add_to_my_list));
        }
    }
}
