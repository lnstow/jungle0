package com.lnstow.jungle0.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.lnstow.jungle0.R;

public class ReadViewPagerAdapter extends RecyclerView.Adapter<ReadViewPagerAdapter.ViewHolder> {
    private RecyclerView.Adapter unreadAdapter;
    private RecyclerView.Adapter readingAdapter;
    private RecyclerView.Adapter readAdapter;

    public ReadViewPagerAdapter(RecyclerView.Adapter unreadAdapter
            , RecyclerView.Adapter readingAdapter, RecyclerView.Adapter readAdapter) {
        this.unreadAdapter = unreadAdapter;
        this.readingAdapter = readingAdapter;
        this.readAdapter = readAdapter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView recyclerView = new RecyclerView(parent.getContext());
        recyclerView.setLayoutParams(new ViewPager2.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new ViewHolder(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(
                holder.recyclerView.getContext(), RecyclerView.VERTICAL, false));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(
                holder.recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(
                holder.recyclerView.getContext().getDrawable(R.drawable.read_text_divider));
        holder.recyclerView.addItemDecoration(itemDecoration);
        switch (position) {
            case 0:
                holder.recyclerView.setAdapter(unreadAdapter);
                break;
            case 1:
                holder.recyclerView.setAdapter(readingAdapter);
                break;
            case 2:
                holder.recyclerView.setAdapter(readAdapter);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView;
        }
    }
}
