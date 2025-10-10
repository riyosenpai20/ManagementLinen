package com.example.management_linen;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainPagerAdapter extends FragmentStateAdapter {
    
    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the appropriate fragment based on position
        switch (position) {
            case 0:
                return new ScanModeFragment();
            case 1:
                return new ReadWriteFragment();
            case 2:
                return new ScanViewFragment();
            default:
                return new ScanModeFragment();
        }
    }
    
    @Override
    public int getItemCount() {
        // Return the number of tabs
        return 3;
    }
}