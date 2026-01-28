package com.example.streamingapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ViewPager2 heroViewPager;
    private TabLayout heroDotsIndicator;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout homeLayout;
    private LinearLayout searchLayout;
    private LinearLayout myListLayout;
    private EditText searchEditText;
    private RecyclerView searchResultsRecyclerView;
    private RecyclerView myListRecyclerView;
    
    private List<Category> allCategories;
    private PreferencesHelper preferencesHelper;
    private HeroSliderAdapter heroSliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_main);

            preferencesHelper = new PreferencesHelper(this);
            
            setupViews();
            setupBottomNavigation();
            loadMovies();
            setupHomeScreen();
        } catch (Exception e) {
            e.printStackTrace();
            // Log the error and show a message if possible
            android.util.Log.e("MainActivity", "Error in onCreate: " + e.getMessage(), e);
            // Try to finish gracefully or show error
            finish();
        }
    }

    private void setupViews() {
        try {
            heroViewPager = findViewById(R.id.heroViewPager);
            heroDotsIndicator = findViewById(R.id.heroDotsIndicator);
            categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
            bottomNavigationView = findViewById(R.id.bottomNavigationView);
            homeLayout = findViewById(R.id.homeLayout);
            searchLayout = findViewById(R.id.searchLayout);
            myListLayout = findViewById(R.id.myListLayout);
            searchEditText = findViewById(R.id.searchEditText);
            searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);
            myListRecyclerView = findViewById(R.id.myListRecyclerView);
            
            if (categoriesRecyclerView != null) {
                // Setup vertical RecyclerView for categories with snap behavior
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                categoriesRecyclerView.setLayoutManager(layoutManager);
            }
            
            if (searchResultsRecyclerView != null) {
                // Setup search RecyclerView
                LinearLayoutManager searchLayoutManager = new LinearLayoutManager(this);
                searchResultsRecyclerView.setLayoutManager(searchLayoutManager);
            }
            
            if (myListRecyclerView != null) {
                // Setup My List RecyclerView
                LinearLayoutManager myListLayoutManager = new LinearLayoutManager(this);
                myListRecyclerView.setLayoutManager(myListLayoutManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // If setup fails, the app might still work partially
        }
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    showHome();
                    return true;
                } else if (itemId == R.id.nav_search) {
                    showSearch();
                    return true;
                } else if (itemId == R.id.nav_my_list) {
                    showMyList();
                    return true;
                }
                return false;
            });
        }
    }

    private void showHome() {
        homeLayout.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.GONE);
        myListLayout.setVisibility(View.GONE);
    }

    private void showSearch() {
        homeLayout.setVisibility(View.GONE);
        searchLayout.setVisibility(View.VISIBLE);
        myListLayout.setVisibility(View.GONE);
        setupSearch();
    }

    private void showMyList() {
        homeLayout.setVisibility(View.GONE);
        searchLayout.setVisibility(View.GONE);
        myListLayout.setVisibility(View.VISIBLE);
        loadMyList();
    }

    private void loadMovies() {
        try {
            allCategories = MovieLoader.loadMovies(this);
            if (allCategories == null) {
                allCategories = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            allCategories = new ArrayList<>();
        }
    }

    private void setupHomeScreen() {
        if (allCategories == null || allCategories.isEmpty()) {
            // No movies loaded, show empty state
            return;
        }
        
        // Get first few movies for hero slider (top 5 from all categories)
        List<Movie> heroMovies = new ArrayList<>();
        for (Category category : allCategories) {
            if (category.getMovies() != null) {
                for (Movie movie : category.getMovies()) {
                    if (heroMovies.size() < 5) {
                        heroMovies.add(movie);
                    } else {
                        break;
                    }
                }
            }
            if (heroMovies.size() >= 5) break;
        }
        
        // Setup hero slider only if we have movies
        if (!heroMovies.isEmpty() && heroViewPager != null && heroDotsIndicator != null) {
            try {
                heroSliderAdapter = new HeroSliderAdapter(heroMovies, heroViewPager);
                heroViewPager.setAdapter(heroSliderAdapter);
                
                // Setup dots indicator - must be done after adapter is set
                if (heroSliderAdapter.getItemCount() > 0) {
                    TabLayoutMediator mediator = new TabLayoutMediator(heroDotsIndicator, heroViewPager, (tab, position) -> {
                        // Empty - just for dots
                    });
                    mediator.attach();
                } else {
                    // Hide dots if no items
                    heroDotsIndicator.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                android.util.Log.e("MainActivity", "Error setting up hero slider: " + e.getMessage(), e);
            }
        } else {
            // Hide hero slider if no movies
            if (heroViewPager != null) {
                heroViewPager.setVisibility(View.GONE);
            }
            if (heroDotsIndicator != null) {
                heroDotsIndicator.setVisibility(View.GONE);
            }
        }
        
        // Build categories list with special rows
        List<Category> displayCategories = new ArrayList<>();
        
        // Continue Watching row
        Movie continueWatchingMovie = preferencesHelper.getContinueWatchingMovie();
        if (continueWatchingMovie != null) {
            Category continueWatching = new Category(getString(R.string.continue_watching));
            continueWatching.addMovie(continueWatchingMovie);
            displayCategories.add(continueWatching);
        }
        
        // Trending Now row (first category's movies)
        if (!allCategories.isEmpty() && allCategories.get(0).getMovies() != null && !allCategories.get(0).getMovies().isEmpty()) {
            Category trendingNow = new Category(getString(R.string.trending_now));
            trendingNow.setMovies(allCategories.get(0).getMovies());
            displayCategories.add(trendingNow);
        }
        
        // Add all regular categories
        displayCategories.addAll(allCategories);
        
        // Setup adapter only if we have categories
        if (categoriesRecyclerView != null) {
            if (!displayCategories.isEmpty()) {
                categoryAdapter = new CategoryAdapter(displayCategories);
                categoriesRecyclerView.setAdapter(categoryAdapter);
            } else {
                // Show empty state or hide RecyclerView
                categoriesRecyclerView.setVisibility(View.GONE);
            }
        }
        
        // Add snap behavior to horizontal RecyclerViews
        setupSnapBehavior();
    }

    private void setupSnapBehavior() {
        // Snap behavior is handled in CategoryAdapter's RecyclerView setup
    }

    private void setupSearch() {
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });
        
        // Also search on text change
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().toLowerCase().trim();
        List<Movie> searchResults = new ArrayList<>();
        
        if (!query.isEmpty() && allCategories != null) {
            for (Category category : allCategories) {
                if (category.getMovies() != null) {
                    for (Movie movie : category.getMovies()) {
                        if (movie != null && movie.getTitle() != null && movie.getTitle().toLowerCase().contains(query)) {
                            searchResults.add(movie);
                        }
                    }
                }
            }
        }
        
        MovieAdapter searchAdapter = new MovieAdapter(searchResults, true);
        searchResultsRecyclerView.setAdapter(searchAdapter);
    }

    private void loadMyList() {
        List<Movie> myListMovies = preferencesHelper.getMyList();
        MovieAdapter myListAdapter = new MovieAdapter(myListMovies, true);
        myListRecyclerView.setAdapter(myListAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (heroSliderAdapter != null) {
            heroSliderAdapter.stopAutoSlide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh continue watching if needed
        if (homeLayout != null && homeLayout.getVisibility() == View.VISIBLE) {
            setupHomeScreen();
        }
    }
}
