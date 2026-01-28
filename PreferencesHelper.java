package com.example.streamingapp;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreferencesHelper {
    
    private static final String PREFS_NAME = "StreamingAppPrefs";
    private static final String KEY_CONTINUE_WATCHING = "continue_watching";
    private static final String KEY_MY_LIST = "my_list";
    private static final String KEY_LAST_POSITION = "last_position_";
    
    private SharedPreferences sharedPreferences;
    
    public PreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    // Continue Watching methods
    public void saveContinueWatching(Movie movie, long position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_CONTINUE_WATCHING + "_video", movie.getVideoResourceId());
        editor.putString(KEY_CONTINUE_WATCHING + "_title", movie.getTitle());
        editor.putInt(KEY_CONTINUE_WATCHING + "_poster", movie.getPosterResourceId());
        editor.putString(KEY_CONTINUE_WATCHING + "_category", movie.getCategory());
        editor.putString(KEY_CONTINUE_WATCHING + "_resource", movie.getResourceName());
        editor.putLong(KEY_CONTINUE_WATCHING + "_position", position);
        editor.apply();
    }
    
    public Movie getContinueWatchingMovie() {
        int videoId = sharedPreferences.getInt(KEY_CONTINUE_WATCHING + "_video", 0);
        if (videoId == 0) return null;
        
        String title = sharedPreferences.getString(KEY_CONTINUE_WATCHING + "_title", "");
        int posterId = sharedPreferences.getInt(KEY_CONTINUE_WATCHING + "_poster", 0);
        String category = sharedPreferences.getString(KEY_CONTINUE_WATCHING + "_category", "");
        String resourceName = sharedPreferences.getString(KEY_CONTINUE_WATCHING + "_resource", "");
        
        return new Movie(title, category, posterId, videoId, resourceName);
    }
    
    public long getContinueWatchingPosition() {
        return sharedPreferences.getLong(KEY_CONTINUE_WATCHING + "_position", 0);
    }
    
    public void clearContinueWatching() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_CONTINUE_WATCHING + "_video");
        editor.remove(KEY_CONTINUE_WATCHING + "_title");
        editor.remove(KEY_CONTINUE_WATCHING + "_poster");
        editor.remove(KEY_CONTINUE_WATCHING + "_category");
        editor.remove(KEY_CONTINUE_WATCHING + "_resource");
        editor.remove(KEY_CONTINUE_WATCHING + "_position");
        editor.apply();
    }
    
    // My List methods
    public void addToMyList(Movie movie) {
        Set<String> myListSet = sharedPreferences.getStringSet(KEY_MY_LIST, new HashSet<>());
        Set<String> newSet = new HashSet<>(myListSet);
        
        // Store movie as JSON string
        JSONObject movieJson = new JSONObject();
        try {
            movieJson.put("title", movie.getTitle());
            movieJson.put("category", movie.getCategory());
            movieJson.put("posterId", movie.getPosterResourceId());
            movieJson.put("videoId", movie.getVideoResourceId());
            movieJson.put("resourceName", movie.getResourceName());
            newSet.add(movieJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(KEY_MY_LIST, newSet);
        editor.apply();
    }
    
    public void removeFromMyList(Movie movie) {
        Set<String> myListSet = sharedPreferences.getStringSet(KEY_MY_LIST, new HashSet<>());
        Set<String> newSet = new HashSet<>();
        
        for (String movieJsonStr : myListSet) {
            try {
                JSONObject movieJson = new JSONObject(movieJsonStr);
                if (!movieJson.getString("resourceName").equals(movie.getResourceName())) {
                    newSet.add(movieJsonStr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(KEY_MY_LIST, newSet);
        editor.apply();
    }
    
    public boolean isInMyList(Movie movie) {
        Set<String> myListSet = sharedPreferences.getStringSet(KEY_MY_LIST, new HashSet<>());
        
        for (String movieJsonStr : myListSet) {
            try {
                JSONObject movieJson = new JSONObject(movieJsonStr);
                if (movieJson.getString("resourceName").equals(movie.getResourceName())) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }
    
    public List<Movie> getMyList() {
        List<Movie> movies = new ArrayList<>();
        Set<String> myListSet = sharedPreferences.getStringSet(KEY_MY_LIST, new HashSet<>());
        
        for (String movieJsonStr : myListSet) {
            try {
                JSONObject movieJson = new JSONObject(movieJsonStr);
                String title = movieJson.getString("title");
                String category = movieJson.getString("category");
                int posterId = movieJson.getInt("posterId");
                int videoId = movieJson.getInt("videoId");
                String resourceName = movieJson.getString("resourceName");
                
                movies.add(new Movie(title, category, posterId, videoId, resourceName));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        return movies;
    }
}
