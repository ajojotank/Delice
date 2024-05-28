package com.example.delice.ui.groceryList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delice.R;
import com.example.delice.utilities.LoginController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroceryListFragment extends Fragment {

    private static final String TAG = "GroceryListFragment";
    private RecyclerView recyclerView;
    private GroceryListAdapter adapter;
    private List<String> groceryList = new ArrayList<>();  // Initialize with empty list
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocerylist, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewGroceryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GroceryListAdapter(groceryList);
        recyclerView.setAdapter(adapter);

        // Initialize the handler
        handler = new Handler(Looper.getMainLooper());

        // Fetch grocery list from endpoint
        fetchGroceryList();

        return view;
    }

    private void fetchGroceryList() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            LoginController appController = (LoginController) getActivity().getApplicationContext();
            String userId = appController.getUserId();

            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/get_all_ingredients_for_user.php?user_id=" + userId);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in);
                Log.d(TAG, "Response: " + response);

                parseGroceryList(response);

                urlConnection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching grocery list", e);
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
            Log.e(TAG, "Error reading stream", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing stream", e);
            }
        }
        return sb.toString();
    }

    private void parseGroceryList(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.getBoolean("success")) {
                JSONArray ingredientsArray = jsonResponse.getJSONArray("ingredients");
                List<String> fetchedGroceryList = new ArrayList<>();
                for (int i = 0; i < ingredientsArray.length(); i++) {
                    fetchedGroceryList.add(ingredientsArray.getString(i));
                }
                // Update the adapter with the fetched grocery list
                handler.post(() -> {
                    groceryList.clear();
                    groceryList.addAll(fetchedGroceryList);
                    adapter.notifyDataSetChanged();
                });
            } else {
                Log.e(TAG, "Fetching grocery list failed: " + jsonResponse.getString("message"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing grocery list JSON", e);
        }
    }
}
