package com.example.delice.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.delice.databinding.FragmentFriendsBinding;
import com.example.delice.utilities.LoginController;
import com.google.android.material.snackbar.Snackbar;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FriendsFragment extends Fragment {

    private FragmentFriendsBinding binding;
    private List<Friend> friendsList;
    private FriendsAdapter adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFriendsBinding.inflate(inflater, container, false);

        setupFriendsRecyclerView();
        binding.addFriendButton.setOnClickListener(v -> addFriend());

        fetchFriendsFromServer();


        return binding.getRoot();
    }

    private void setupFriendsRecyclerView() {
        friendsList = new ArrayList<>();
        adapter = new FriendsAdapter(getContext(), friendsList, this::navigateToFriendCookbook);
        binding.friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.friendsRecyclerView.setAdapter(adapter);
    }

    private void addFriend() {
        String username = binding.searchFriendInput.getText().toString().trim();
        if (username.isEmpty()) {
            Snackbar.make(binding.getRoot(), "Please enter a username", Snackbar.LENGTH_SHORT).show();
            return;
        }

        searchUser(username);
    }
    private void searchUser(String username) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        LoginController appController = (LoginController) getActivity().getApplicationContext();
        String userId = appController.getUserId();

        executorService.execute(() -> {
            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/get_user.php?username=" + username);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in);
                Log.d("searchUser", "Response: " + response);

                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if (jsonObject.has("user_id")) {
                    String friendId = jsonObject.getString("user_id");
                    String currentUserId = appController.getUserId();
                    addFriendToDatabase(currentUserId, friendId);
                } else {
                    handler.post(() -> Snackbar.make(binding.getRoot(), "User does not exist", Snackbar.LENGTH_SHORT).show());
                }

                urlConnection.disconnect();
            } catch (Exception e) {
                Log.e("searchUser", "Error searching user", e);
            }
        });
    }

    private void addFriendToDatabase(String userId, String friendId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            try {
                // Replace with your server URL
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/add_friend.php?user_id=" + userId + "&friend_id=" + friendId);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in);
                Log.d("addFriendToDatabase", "Response: " + response);

                // Assuming the response indicates success
                handler.post(() -> {
                    Snackbar.make(binding.getRoot(), "Friend added successfully", Snackbar.LENGTH_SHORT).show();
                    fetchFriendsFromServer(); // Refresh the friends list
                });

                urlConnection.disconnect();
            } catch (Exception e) {
                Log.e("addFriendToDatabase", "Error adding friend", e);
            }
        });
    }

    private void navigateToFriendCookbook(Friend friend) {
        Intent intent = new Intent(getActivity(), FriendRecipeBookActivity.class);
        intent.putExtra("friend_username", friend.getUsername()); // Pass friend's username as an extra
        startActivity(intent);
    }

    private void fetchFriendsFromServer() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        LoginController appController = (LoginController) getActivity().getApplicationContext();

        executorService.execute(() -> {
            try {
                // Replace with your server URL
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/get_friends_list.php?user_id=" + appController.getUserId());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in);
                Log.d("fetchFriendsFromServer", "Response: " + response);

                JSONArray jsonArray = new JSONArray(response);
                List<Friend> friends = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String friendId = jsonObject.getString("friend_id");
                    String friendUsername = jsonObject.getString("friend_username");
                    String friendName = jsonObject.getString("friend_name");
                    Friend friend = new Friend(friendId,friendUsername,friendName);
                    friends.add(friend);

                }

                if (!friends.isEmpty()) {
                    handler.post(() -> {
                        friendsList.clear();
                        friendsList.addAll(friends);
                        adapter.notifyDataSetChanged();
                    });
                }

                urlConnection.disconnect();
            } catch (Exception e) {
                Log.e("fetchFriendsFromServer", "Error fetching friends", e);
            }
        });
    }

//    private Friend getFriendDetailsById(int friendId) {
//        // Dummy method to simulate fetching friend details
//        // In reality, you might fetch this data from another API or local database
//        Map<Integer, Friend> dummyFriends = new HashMap<>();
//        dummyFriends.put(1, new Friend("John Doe", "johndoe123"));
//        dummyFriends.put(2, new Friend("Jane Smith", "janesmith456"));
//        dummyFriends.put(3, new Friend("Emily Johnson", "emilyjohnson789"));
//
//        return dummyFriends.get(friendId);
//    }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clean up references to avoid memory leaks
    }
}