package com.example.mdp_team04;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) { super(fragmentActivity); }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new FragmentMessage();
            case 1:
                return new FragmentMap();
            case 2:
                return new FragmentControl();
            default:
                return new FragmentMessage();
        }
    }

    @Override
    public int getItemCount() { return 3; }

}