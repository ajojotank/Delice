package com.example.delice.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.delice.databinding.FragmentHomeBinding;
import com.example.delice.ui.PreMain.OnboardingActivity;
import com.example.delice.utilities.LoginController;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Setup ViewPager2 with the adapter
        setupViewPager();

        // Setup logout button
        setupLogoutButton();

        return binding.getRoot();
    }

    private void setupViewPager() {
        DayComponentAdapter adapter = new DayComponentAdapter(this);
        binding.viewPager.setAdapter(adapter);

        // Attach the ViewPager2 to the TabLayout
        new TabLayoutMediator(binding.daysOfWeekTabLayout, binding.viewPager,
                (tab, position) -> tab.setText(getDayName(position))).attach();
    }

    private String getDayName(int dayIndex) {
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        return days[dayIndex];
    }

    private void setupLogoutButton() {
        binding.logoutButton.setOnClickListener(view -> logout());
    }

    private void logout() {
        // Assuming that the LoginController is a singleton or accessible statically
        // Modify this according to your actual application structure
        LoginController appController = (LoginController) getActivity().getApplicationContext();
        appController.logout();

        // Navigate to the Onboarding Activity
        Intent intent = new Intent(getActivity(), OnboardingActivity.class);
        startActivity(intent);

        // To prevent returning to the HomeFragment when pressing back, finish the current activity
        getActivity().finish();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up any references to binding to avoid memory leaks
        binding = null;
    }
}