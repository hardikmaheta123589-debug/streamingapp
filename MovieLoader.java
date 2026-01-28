package com.example.streamingapp;

import android.content.Context;
import android.content.res.Resources;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieLoader {
    
    public static List<Category> loadMovies(Context context) {
        List<Category> categories = new ArrayList<>();
        
        try {
            Map<String, Category> categoryMap = new HashMap<>();
            
            // Load posters from drawable
            Map<String, Integer> posterMap = loadDrawableResources(context, "drawable");
            
            // Load videos from raw
            Map<String, Integer> videoMap = loadDrawableResources(context, "raw");
            
            if (posterMap == null || posterMap.isEmpty()) {
                android.util.Log.w("MovieLoader", "No posters found in drawable resources");
                return categories;
            }
            
            if (videoMap == null || videoMap.isEmpty()) {
                android.util.Log.w("MovieLoader", "No videos found in raw resources");
                return categories;
            }
            
            // Match posters with videos and group by category
            for (Map.Entry<String, Integer> posterEntry : posterMap.entrySet()) {
                try {
                    String posterName = posterEntry.getKey();
                    if (posterName == null) continue;
                    
                    int posterId = posterEntry.getValue();
                    if (posterId == 0) continue;
                    
                    // Extract category and base name
                    String[] parts = posterName.split("_", 2);
                    if (parts.length < 2) continue;
                    
                    String categoryName = capitalizeFirst(parts[0]);
                    if (categoryName == null || categoryName.isEmpty()) continue;
                    
                    String baseName = parts[1].replace(".jpg", "").replace(".jpeg", "").replace(".png", "");
                    
                    // Remove common prefixes like "the_" from base name for better matching
                    if (baseName.startsWith("the_")) {
                        baseName = baseName.substring(4);
                    }
                    
                    // Find matching video
                    Integer videoId = findMatchingVideo(videoMap, categoryName.toLowerCase(), baseName);
                    if (videoId == null || videoId == 0) continue;
                    
                    // Create or get category
                    Category category = categoryMap.get(categoryName);
                    if (category == null) {
                        category = new Category(categoryName);
                        categoryMap.put(categoryName, category);
                        categories.add(category);
                    }
                    
                    // Create movie title from base name
                    String title = formatTitle(baseName);
                    
                    // Create and add movie
                    Movie movie = new Movie(title, categoryName, posterId, videoId, baseName);
                    category.addMovie(movie);
                } catch (Exception e) {
                    android.util.Log.e("MovieLoader", "Error processing poster: " + posterEntry.getKey(), e);
                    // Continue with next poster
                }
            }
        } catch (Exception e) {
            android.util.Log.e("MovieLoader", "Error loading movies: " + e.getMessage(), e);
        }
        
        return categories;
    }
    
    private static Map<String, Integer> loadDrawableResources(Context context, String resourceType) {
        Map<String, Integer> resourceMap = new HashMap<>();
        Resources resources = context.getResources();
        String packageName = context.getPackageName();
        
        try {
            Class<?> resClass = Class.forName(packageName + ".R$" + resourceType);
            Field[] fields = resClass.getFields();
            
            for (Field field : fields) {
                String resourceName = field.getName();
                int resourceId = field.getInt(null);
                
                // Skip launcher icons
                if (resourceName.startsWith("ic_launcher")) continue;
                
                resourceMap.put(resourceName, resourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return resourceMap;
    }
    
    private static Integer findMatchingVideo(Map<String, Integer> videoMap, String categoryPrefix, String baseName) {
        // Normalize base name for comparison
        String normalizedBase = normalizeName(baseName);
        
        // Try exact match first
        String exactMatch = categoryPrefix + "_" + baseName;
        if (videoMap.containsKey(exactMatch)) {
            return videoMap.get(exactMatch);
        }
        
        // Try with .mp4 extension
        if (videoMap.containsKey(exactMatch + ".mp4")) {
            return videoMap.get(exactMatch + ".mp4");
        }
        
        // Try fuzzy matching - find video that starts with category prefix and matches base name
        for (Map.Entry<String, Integer> entry : videoMap.entrySet()) {
            String videoName = entry.getKey().toLowerCase();
            
            // Check if video starts with category prefix
            if (!videoName.startsWith(categoryPrefix + "_")) {
                continue;
            }
            
            // Extract video base name (remove category prefix and extension)
            String videoBase = videoName.substring(categoryPrefix.length() + 1);
            videoBase = videoBase.replace(".mp4", "");
            
            // Normalize both names for comparison
            String normalizedVideo = normalizeName(videoBase);
            
            // Check if normalized names match or are similar
            if (normalizedVideo.equals(normalizedBase) || 
                normalizedVideo.contains(normalizedBase) || 
                normalizedBase.contains(normalizedVideo)) {
                return entry.getValue();
            }
        }
        
        return null;
    }
    
    private static String normalizeName(String name) {
        // Remove special characters, underscores, hyphens, and convert to lowercase
        return name.toLowerCase()
                .replace("_", "")
                .replace("-", "")
                .replace(" ", "")
                .replace(".jpg", "")
                .replace(".jpeg", "")
                .replace(".png", "")
                .replace(".mp4", "");
    }
    
    private static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    private static String formatTitle(String baseName) {
        // Replace underscores with spaces and capitalize words
        String[] words = baseName.replace("_", " ").split(" ");
        StringBuilder title = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            if (i > 0) title.append(" ");
            if (!words[i].isEmpty()) {
                title.append(capitalizeFirst(words[i]));
            }
        }
        
        return title.toString();
    }
}
