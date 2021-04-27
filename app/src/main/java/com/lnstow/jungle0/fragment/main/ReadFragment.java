package com.lnstow.jungle0.fragment.main;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.lnstow.jungle0.BaseJungle;
import com.lnstow.jungle0.R;
import com.lnstow.jungle0.adapter.ReadViewPagerAdapter;
import com.lnstow.jungle0.adapter.RecordReadAdapter;
import com.lnstow.jungle0.database.AppDatabase;
import com.lnstow.jungle0.database.entity.Read;
import com.lnstow.jungle0.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReadFragment extends BaseFragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private RecordReadAdapter unreadAdapter;
    private RecordReadAdapter readingAdapter;
    private RecordReadAdapter readAdapter;
    private ArrayList<Read> unreadArrayList;
    private ArrayList<Read> readingArrayList;
    private ArrayList<Read> readArrayList;

    public ReadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_read, container, false);
    }

    @Override
    protected void initView() {
        findView();
        initData();
        initViewPager();
    }

    private void initData() {
        weakActivity.get().singleThread.execute(() -> {
            AppDatabase appDatabase = weakActivity.get().appDatabase;
            unreadArrayList = (ArrayList<Read>) appDatabase.readDao()
                    .loadAllReadsByStatus(Read.STATUS_UNREAD);
            readArrayList = (ArrayList<Read>) appDatabase.readDao()
                    .loadAllReadsByStatus(Read.STATUS_READ);
            Collections.reverse(unreadArrayList);
            Collections.reverse(readArrayList);
            finishInit();
        });
        readingArrayList = new ArrayList<>();
    }

    private void finishInit() {
        weakActivity.get().runOnUiThread(() -> {
            unreadAdapter.setData(unreadArrayList);
            readAdapter.setData(readArrayList);
            unreadAdapter.notifyDataSetChanged();
            readAdapter.notifyDataSetChanged();
            insertIntoRead(BaseJungle.MOVIE_LIST, BaseJungle.JUNGLE_URL, "主页");
        });
    }

    private void findView() {
        View view = getView();
        tabLayout = view.findViewById(R.id.read_tab_layout);
        viewPager = view.findViewById(R.id.read_view_pager);
    }

    private void initViewPager() {
        unreadAdapter = new RecordReadAdapter(new ArrayList<>(), this, 0);
        readingAdapter = new RecordReadAdapter(readingArrayList, this, 1);
        readAdapter = new RecordReadAdapter(new ArrayList<>(), this, 2);

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        viewPager.setAdapter(new ReadViewPagerAdapter(unreadAdapter, readingAdapter, readAdapter));
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("未读");
                    break;
                case 1:
                    tab.setText("当前");
                    break;
                case 2:
                    tab.setText("已读");
                    break;
            }
        }).attach();
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) operateRightDrawer(false, false);
                else operateRightDrawer(true, false);
            }
        });
    }

    public void insertIntoUnread(byte type, String link, String title) {
        weakActivity.get().singleThread.execute(() -> {
            Read read = new Read(type, link, title, System.currentTimeMillis(), Read.STATUS_UNREAD);
            int index = unreadArrayList.indexOf(read);
            if (index >= 0)
                weakActivity.get().appDatabase.readDao().deleteReads(unreadArrayList.remove(index));
            unreadArrayList.add(read);
            read.setId((int) weakActivity.get().appDatabase.readDao().insertReads(read)[0]);
            weakActivity.get().runOnUiThread(() -> unreadAdapter
                    .notifyItemRangeChanged(0, unreadAdapter.getItemCount()));
        });
    }

    public void insertIntoRead(byte type, String link, String title) {
        weakActivity.get().singleThread.execute(() -> {
            Read read = new Read(type, link, title, System.currentTimeMillis(), Read.STATUS_READ);
            readArrayList.add(read);
            read.setId((int) weakActivity.get().appDatabase.readDao().insertReads(read)[0]);
            weakActivity.get().runOnUiThread(() -> readAdapter
                    .notifyItemRangeChanged(0, readAdapter.getItemCount()));
        });
    }

    public void insertIntoReading(byte type, String link, String title) {
        readingArrayList.add(new Read(
                type, link, title, System.currentTimeMillis(), Read.STATUS_READING));
        readingAdapter.notifyItemRangeChanged(0, readingAdapter.getItemCount());
    }

    public void deleteFromReading() {
        int size = readingArrayList.size();
        readingArrayList.remove(size - 1);
        readingAdapter.notifyItemRangeChanged(0, size);
    }

    public void deleteFromUnread(Read read) {
        weakActivity.get().singleThread.execute(() ->
                weakActivity.get().appDatabase.readDao().deleteReads(read));
    }

    public int getViewPagerPosition() {
        return viewPager.getCurrentItem();
    }

}
