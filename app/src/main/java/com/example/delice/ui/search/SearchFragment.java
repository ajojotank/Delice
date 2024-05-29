package com.example.delice.ui.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.delice.R;
import com.example.delice.databinding.FragmentSearchBinding;
import com.example.delice.ui.recipe.Recipe;
import com.example.delice.ui.recipe.RecipeCardAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private RecipeCardAdapter recipeCardAdapter;
    private List<Recipe> allRecipes;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        // Setup RecyclerViews and the filter drawer
        setupIngredientFilter();
        setupRecyclerView();
        setupSearch();
        onSearch();

        return binding.getRoot();


    }

    //Kaylas search function
    public void onSearch(){
        EditText searchInput = binding.searchInput;
        searchInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction()==KeyEvent.ACTION_DOWN)&&(keyCode == KeyEvent.KEYCODE_ENTER)){
                    setupSearch();

                    return true;
                }
                return false;
            }
        });

    }

    private void setupIngredientFilter() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/get_ingredients.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in);
                Log.d("setupIngredientFilter", "Response: " + response);

                JSONArray jsonArray = new JSONArray(response);
                ArrayList<String> ingredients = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject ingredientJson = jsonArray.getJSONObject(i);
                    String name = ingredientJson.getString("name");
                    ingredients.add(name);
                }

                handler.post(() -> {
                    IngredientFilterAdapter ingredientFilterAdapter = new IngredientFilterAdapter(ingredients, getLayoutInflater());
                    binding.filterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.filterRecyclerView.setAdapter(ingredientFilterAdapter);
                });

                urlConnection.disconnect();
            } catch (Exception e) {
                Log.e("setupIngredientFilter", "Error fetching ingredients", e);
            }
        });

        binding.filterButton.setOnClickListener(v -> {
            DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
            } else {
                drawer.openDrawer(GravityCompat.END);
            }
        });
    }

    private void setupRecyclerView() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/get_all_recipes.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in);
                Log.d("setupRecyclerView", "Response: " + response);

                JSONArray jsonArray = new JSONArray(response);
                ArrayList<Recipe> recipes = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject recipeJson = jsonArray.getJSONObject(i);
                    String title = recipeJson.getString("title");
                    String description = recipeJson.getString("description");
                    String author = recipeJson.getString("author_name"); // Assuming author_id is sufficient for now
                    String imageUrl = recipeJson.getString("image_path");

                    List<String> ingredients = new ArrayList<>();
                    if (recipeJson.has("ingredients")) {
                        JSONArray ingredientsArray = recipeJson.getJSONArray("ingredients");
                        for (int j = 0; j < ingredientsArray.length(); j++) {
                            ingredients.add(ingredientsArray.getString(j));
                        }
                    }

                    List<String> instructions = new ArrayList<>();
                    if (recipeJson.has("instructions")) {
                        JSONArray instructionsArray = recipeJson.getJSONArray("instructions");
                        for (int j = 0; j < instructionsArray.length(); j++) {
                            instructions.add(instructionsArray.getString(j));
                        }
                    }

                    recipes.add(new Recipe(title, description, false, ingredients, instructions, author, imageUrl));
                }

                handler.post(() -> {
                    recipeCardAdapter = new RecipeCardAdapter(recipes);
                    binding.searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.searchResultsRecyclerView.setAdapter(recipeCardAdapter);
                    allRecipes = recipes;
                });

                urlConnection.disconnect();
            } catch (Exception e) {
                Log.e("setupRecyclerView", "Error fetching recipes", e);
            }
        });
    }

    private String convertStreamToString(InputStream is) {
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

    private void setupSearch() {
        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(binding.searchInput.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void performSearch(String query) {
        List<Recipe> filteredRecipes = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (recipe.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredRecipes.add(recipe);
            }
        }
        recipeCardAdapter.updateData(filteredRecipes);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
