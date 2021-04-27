package com.lnstow.jungle0.adapter;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lnstow.jungle0.BaseJungle;
import com.lnstow.jungle0.R;
import com.lnstow.jungle0.bean.MovieListItemBean;
import com.lnstow.jungle0.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.WeakHashMap;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {
    private ArrayList<MovieListItemBean> list;
    private BaseFragment fragment;
    private ItemClickListener clickListener;
    private static ForegroundColorSpan span = new ForegroundColorSpan(0xff7C4DFF);
//private static ForegroundColorSpan span = new ForegroundColorSpan(0xff4db6ac);

    public void setList(ArrayList<MovieListItemBean> list) {
        this.list = list;
        clickListener.list = list;
        if (clickListener.weakHashMap == null)
            clickListener.weakHashMap = new WeakHashMap<>(list.size(), 1);
    }

    public MovieListAdapter(ArrayList<MovieListItemBean> list, BaseFragment fragment) {
        this.list = list;
        this.fragment = fragment;
        clickListener = new ItemClickListener(list, fragment);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item_small, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(clickListener);
        holder.cardView.setOnLongClickListener(clickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieListItemBean itemBean = list.get(position);
//        Glide.get(this).clearMemory();//清理内存缓存  可以在UI主线程中进行
//        Glide.get(this).clearDiskCache();//清理磁盘缓存 需要在子线程中执行
        CharSequence charSequence;
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder()
                .append(itemBean.getSid())
                .append("  ")
                .append(itemBean.getTitle());
        stringBuilder.setSpan(span, 0, itemBean.getSid().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.itemTitle.setText(stringBuilder);
        Glide.with(fragment)
                .asBitmap()
                .load(itemBean.getImageLink())
                .dontAnimate()
//                .placeholder(R.drawable.ic_image_black_24dp)
                .error(R.drawable.ic_image_black_24dp)
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(holder.itemImage);
        clickListener.put(holder.cardView, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        View cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.movie_list_item_image);
            itemTitle = itemView.findViewById(R.id.movie_list_item_title);
            cardView = itemView;
        }
    }

    static class ItemClickListener implements View.OnClickListener, View.OnLongClickListener {
        ArrayList<MovieListItemBean> list;
        BaseFragment fragment;
        WeakHashMap<View, Integer> weakHashMap;

        public ItemClickListener(ArrayList<MovieListItemBean> list, BaseFragment fragment) {
            this.list = list;
            this.fragment = fragment;
        }

        public void put(View view, int index) {
            weakHashMap.put(view, index);
        }

        @Override
        public void onClick(View v) {
            MovieListItemBean itemBean = list.get(weakHashMap.get(v));
            fragment.clickTo(BaseJungle.MOVIE_DETAIL, itemBean.getNextLink(),
                    itemBean.getSid() + "  " + itemBean.getTitle());
        }

        @Override
        public boolean onLongClick(View v) {
            MovieListItemBean itemBean = list.get(weakHashMap.get(v));
            fragment.longClickTo(v, BaseJungle.MOVIE_DETAIL, itemBean.getNextLink(),
                    itemBean.getSid() + "  " + itemBean.getTitle(), itemBean.getSid());
            return true;
        }
    }
}
