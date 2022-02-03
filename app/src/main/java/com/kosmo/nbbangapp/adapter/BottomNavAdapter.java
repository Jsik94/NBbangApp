package com.kosmo.nbbangapp.adapter;

import com.kosmo.nbbangapp.fragments.HomeFragment;
import com.kosmo.nbbangapp.fragments.ChartFragment;
import com.kosmo.nbbangapp.fragments.HabitFragment;
import com.kosmo.nbbangapp.fragments.SideFragment;
import com.kosmo.nbbangapp.fragments.ProfileFragment;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class BottomNavAdapter extends FragmentStateAdapter {
    ArrayList<Fragment> frags = new ArrayList<>(Arrays.asList(HomeFragment.newInstance(), ChartFragment.newInstance(), HabitFragment.newInstance(), SideFragment.newInstance(), ProfileFragment.newInstance()));

    public BottomNavAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return frags.get(position);
    }

    @Override
    public int getItemCount() {
        return frags.size();
    }
}
