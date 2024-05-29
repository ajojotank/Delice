package com.example.delice.utilities;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IsFavoriteRecipe {
    public static boolean getIsFavorite(String userId, String recipeId) {
        // isFavorite=false;
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
        try {
            URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/get_user_favorite.php?user_id="+ userId + "&recipe_id=" + recipeId);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String response = convertStreamToString(in);
            Log.d("setupRecyclerView", "userId: " + userId + " recipeId: " + recipeId);
            Log.d("setupRecyclerView", "Response get favorite status: " + response);

            JSONObject favorite = new JSONObject(response);
            return favorite.getBoolean("is_favorite");

        } catch (Exception e) {
            //Log.e("setupRecyclerView", "Error fetching recipes", e);
        }
        //});
        return false;
    }


    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            Log.e("convertStreamToString", "Error reading stream", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("convertStreamToString", "Error closing stream", e);
            }
        }
        return sb.toString();
    }
}
