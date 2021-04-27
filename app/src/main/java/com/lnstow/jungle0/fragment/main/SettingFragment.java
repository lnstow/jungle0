package com.lnstow.jungle0.fragment.main;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.lnstow.jungle0.R;
import com.lnstow.jungle0.database.entity.Read;
import com.lnstow.jungle0.fragment.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment {
    Read[] reads;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    protected void initView() {
        getView().findViewById(R.id.test_button_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weakActivity.get().appDatabase.readDao().insertReads(new Read((byte) 2, "test1", "test1", 2, (byte) 2)
                        , new Read((byte) 2, "test2", "test2", 2, (byte) 2)
                        , new Read((byte) 2, "test3", "test3", 2, (byte) 2)
                        , new Read((byte) 2, "test4", "test4", 2, (byte) 2));
            }
        });
        getView().findViewById(R.id.test_button_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                reads = weakActivity.get().appDatabase.readDao().loadAllReads();
//                Log.d("sett", "onClick: " + reads);
            }
        });
        getView().findViewById(R.id.test_button_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reads != null && reads.length > 3) {
                    reads[1].setLink("update2");
                    weakActivity.get().appDatabase.readDao().updateReads(reads[1]);
                }
            }
        });
    }
}
