package com.example.delice.ui.home;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DayComponentAdapter extends FragmentStateAdapter {
    public DayComponentAdapter(Fragment fragment) {
        super(fragment);
    }

    @Override
    public Fragment createFragment(int position) {
        // This should correctly create a new instance of DayComponent for each position/day
        return DayComponent.newInstance(position);
    }

    @Override
    public int getItemCount() {
        // Assuming you have 7 days
        return 7;
    }
}
