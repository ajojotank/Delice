package com.example.delice.utilities;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkUtils {

    private static RequestQueue requestQueue;

    private static RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static void registerUser(Context context, String name, String username, String hashedPassword, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "https://lamp.ms.wits.ac.za/home/s2670867/register.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("username", username);
                params.put("password", hashedPassword);
                return params;
            }
        };
        getRequestQueue(context).add(request);
    }

    public static void checkUsernameExists(Context context, String username, Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
        String url = "https://lamp.ms.wits.ac.za/home/s2670867/check_username.php"; // This endpoint needs to be implemented on your server
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Assume the server returns "true" if username exists, "false" otherwise
                    listener.onResponse("true".equals(response.trim()));
                }, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };
        getRequestQueue(context).add(request);
    }

    public static void loginUser(Context context, String username, String password, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "https://lamp.ms.wits.ac.za/home/s2670867/login.php"; // This should be the URL to your PHP login script
        StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password); // Consider sending hashed password
                return params;
            }
        };
        getRequestQueue(context).add(request);
    }

    public static void getMealsForUser(Context context, String userId, String day, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String url = "https://lamp.ms.wits.ac.za/home/s2670867/get_meals_for_user.php?user_id=" + userId + "&day=" + day;
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        queue.add(jsonObjectRequest);
    }

}
