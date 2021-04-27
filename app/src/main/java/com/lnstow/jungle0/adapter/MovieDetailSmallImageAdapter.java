package com.lnstow.jungle0.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.lnstow.jungle0.R;
import com.lnstow.jungle0.fragment.BaseFragment;

import java.util.ArrayList;

public class MovieDetailSmallImageAdapter extends
        RecyclerView.Adapter<MovieDetailSmallImageAdapter.ViewHolder> {
    private ArrayList<String> smallImageLink;
    private ArrayList<String> smallToBigImage;
    private BaseFragment fragment;
    private int itemWidth;

    public MovieDetailSmallImageAdapter(ArrayList<String> smallImageLink, ArrayList<String> smallToBigImage,
                                        BaseFragment fragment, int itemWidth) {
        this.smallImageLink = smallImageLink;
        this.smallToBigImage = smallToBigImage;
        this.fragment = fragment;
        this.itemWidth = itemWidth;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new FlexboxLayoutManager.LayoutParams(itemWidth, itemWidth));
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(fragment).load(smallImageLink.get(position))
                .error(R.drawable.ic_image_black_24dp)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(holder.smallImage);
    }

    @Override
    public int getItemCount() {
        return smallImageLink.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView smallImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            smallImage = (ImageView) itemView;
        }
    }
}
