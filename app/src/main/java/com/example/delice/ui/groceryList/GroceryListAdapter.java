package com.example.delice.ui.groceryList;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delice.R;
import com.example.delice.utilities.LoginController;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroceryListAdapter extends RecyclerView.Adapter<GroceryListAdapter.GroceryViewHolder> {

    private final List<String> groceryItems;
    private LoginController loginController;

    public GroceryListAdapter(List<String> groceryItems) {
        this.groceryItems = groceryItems;
        this.loginController = loginController;


    }

    @NonNull
    @Override
    public GroceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_groceryitem, parent, false);
        return new GroceryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryViewHolder holder, int position) {
        String item = groceryItems.get(position);
        holder.itemName.setText(item);
        Log.e("item name","The item: "+item);

        holder.itemCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                //removeItem(item, position); ignore this i was trying to remove it but we not doing that anymore
                Log.e("item checked","Item was checked: +"+item);
            }
        });
    }

    private void removeItem(String item, int position) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String userId = loginController.getUserId();
        executorService.execute(() -> {
            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/delete_grocery.php?name=" + item +"&user_id=" + userId);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in);

                urlConnection.disconnect();


            } catch (Exception e) {
                handler.post(() -> {

                });
            }
        });
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

    @Override
    public int getItemCount() {
        return groceryItems.size();
    }

    static class GroceryViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        CheckBox itemCheckbox;

        GroceryViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.textViewItemName);
            itemCheckbox = itemView.findViewById(R.id.checkboxItem);
        }
    }
}
