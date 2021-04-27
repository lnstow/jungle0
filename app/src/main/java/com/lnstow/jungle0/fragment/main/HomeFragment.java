package com.lnstow.jungle0.fragment.main;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.lnstow.jungle0.R;
import com.lnstow.jungle0.activity.MainActivity;
import com.lnstow.jungle0.fragment.MovieListFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends MovieListFragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    protected void initSwipeBack() {
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        Log.d("tag", "homeMenu: " + mToolbarTitle);
        MainActivity mainActivity = (MainActivity) weakActivity.get();
        if (mainActivity.fragmentStack.isEmpty() &&
                mainActivity.fragmentMain[mainActivity.fragmentMainIndex] == this) {
            menu.clear();
            inflater.inflate(R.menu.toolbar_list_menu, menu);
        }
    }

}
