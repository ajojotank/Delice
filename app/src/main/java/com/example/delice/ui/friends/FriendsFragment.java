package com.example.delice.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.delice.databinding.FragmentFriendsBinding;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    private FragmentFriendsBinding binding;
    private List<Friend> friendsList;
    private FriendsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFriendsBinding.inflate(inflater, container, false);

        setupFriendsRecyclerView();
        binding.addFriendButton.setOnClickListener(v -> addFriend());

        return binding.getRoot();
    }

    private void setupFriendsRecyclerView() {
        friendsList = new ArrayList<>();
        friendsList.add(new Friend("John Doe", "johndoe123"));
        friendsList.add(new Friend("Jane Smith", "janesmith456"));
        friendsList.add(new Friend("Emily Johnson", "emilyjohnson789"));

        adapter = new FriendsAdapter(getContext(), friendsList, this::navigateToFriendCookbook);
        binding.friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.friendsRecyclerView.setAdapter(adapter);
    }

    private void addFriend() {
        // Simulate adding a new friend
        friendsList.add(new Friend("New Friend", "newfriend321"));
        adapter.notifyDataSetChanged(); // Notify the adapter that data has changed

        // Show Snackbar confirmation
        Snackbar.make(binding.getRoot(), "New friend added!", Snackbar.LENGTH_SHORT).show();
    }

    private void navigateToFriendCookbook(Friend friend) {
        Intent intent = new Intent(getActivity(), FriendRecipeBookActivity.class);
        intent.putExtra("friend_username", friend.getUsername()); // Pass friend's username as an extra
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clean up references to avoid memory leaks
    }
}
