package com.kosmo.nbbangapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kosmo.nbbangapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SideFragment extends Fragment {

    private View view;

    public static SideFragment newInstance(){
        return new SideFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_side,container,false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
