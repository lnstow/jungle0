package com.lnstow.jungle0.fragment.main;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.lnstow.jungle0.R;
import com.lnstow.jungle0.fragment.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends BaseFragment {


    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

}
