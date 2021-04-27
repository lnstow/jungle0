package com.lnstow.jungle0.fragment;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.lnstow.jungle0.BaseJungle;
import com.lnstow.jungle0.R;
import com.lnstow.jungle0.activity.BaseActivity;
import com.lnstow.jungle0.bean.MovieDetailBean;
import com.lnstow.jungle0.util.JsoupUtil;
import com.lnstow.jungle0.util.ViewUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;
import java.util.WeakHashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends BaseFragment {
    protected ImageView bigImage;
    protected LinearLayout linearLayout;
    protected MovieDetailBean detail;
    protected SwipeRefreshLayout refreshLayout;
    protected FlexboxLayout imageFlex;
    protected RecyclerView imageRecyclerView;
    protected FrameLayout videoFrame;
    protected FlexboxLayout textFlex;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_detail, container, false);
    }

    @Override
    protected void initView() {
        findView();
        initToolbar();
        initSwipeBack();
        initRefreshLayout();
        getContentFromLink();
    }

    protected void findView() {
        View view = getView();
        bigImage = view.findViewById(R.id.movie_detail_big_image);
        linearLayout = view.findViewById(R.id.movie_detail_linear_layout);
        refreshLayout = view.findViewById(R.id.refresh_layout);
    }

    protected void initToolbar() {
        setHasOptionsMenu(true);
        weakActivity.get().setSupportActionBar(getView().findViewById(R.id.movie_detail_toolbar));
        ActionBar actionBar = weakActivity.get().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(ViewUtil.dp2px(4));
            actionBar.setTitle(mToolbarTitle);
        }
        TextView textView = new TextView(weakActivity.get());
        textView.setTextIsSelectable(true);
        textView.setTextSize(16);
        textView.setText(mToolbarTitle);
        linearLayout.addView(textView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    protected void initSwipeBack() {
        //通过反射改变mOverhangSize的值为0，
        //这个mOverhangSize值为菜单到右边屏幕的最短距离，
        //默认是32dp，现在给它改成0
        SlidingPaneLayout slidingPaneLayout = getView().findViewById(R.id.swipe_back_layout);
        try {
            //mOverhangSize属性，意思就是左菜单离右边屏幕边缘的距离
            Field f_overHang = SlidingPaneLayout.class.getDeclaredField("mOverhangSize");
            f_overHang.setAccessible(true);
            //设置左菜单离右边屏幕边缘的距离为0，设置全屏
            f_overHang.set(slidingPaneLayout, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        slidingPaneLayout.setPanelSlideListener(new SwipeBack(this));
//        slidingPaneLayout.setSliderFadeColor(getResources().getColor(android.R.color.transparent));
        slidingPaneLayout.setSliderFadeColor(Color.TRANSPARENT);
//        slidingPaneLayout.setCoveredFadeColor(Color.parseColor("#ff009688"));
    }

    protected void initRefreshLayout() {
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new RefreshListener(this));
        refreshLayout.setRefreshing(true);
    }

//    protected void initRecyclerView(int itemWidth) {
//        BaseActivity activity = weakActivity.get();
//        imageRecyclerView = new RecyclerView(activity);
//        linearLayout.addView(imageRecyclerView, new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        imageRecyclerView.setLayoutManager(new FlexboxLayoutManager(activity));
//        imageRecyclerView.setAdapter(new MovieDetailSmallImageAdapter(
//                detail.getSmallImageLink(), detail.getSmallToBigImage(), this, itemWidth));
//        FlexboxItemDecoration itemDecoration = new FlexboxItemDecoration(activity);
//        itemDecoration.setDrawable(activity.getDrawable(R.drawable.small_image_divider));
//        imageRecyclerView.addItemDecoration(itemDecoration);
//        imageRecyclerView.setNestedScrollingEnabled(false);
////        imageRecyclerView.setLayoutManager(new GridLayoutManager(weakActivity.get(), 4));
////        imageRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(
////                4, StaggeredGridLayoutManager.VERTICAL));
//    }

    @Override
    protected void getContentSuccess(String htmlResult) {
        detail = JsoupUtil.handleDetailWithJsoup(htmlResult);
        WeakReference<MovieDetailFragment> weakFragment = new WeakReference<>(this);
        weakActivity.get().runOnUiThread(() -> {
            MovieDetailFragment fragment = weakFragment.get();
            BaseActivity activity = weakActivity.get();
            //判空context
            if (fragment == null || activity == null || fragment.getContext() == null) return;
            refreshLayout.setRefreshing(false);
            int dp2px = ViewUtil.dp2px(1);
            int width = linearLayout.getWidth() - dp2px * 20;
            if (!detail.getBigImageLink().isEmpty()) {
                Glide.with(fragment).load(detail.getBigImageLink())
//                        .error(R.drawable.ic_image_black_24dp)
//                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(bigImage);
            }
            if (!detail.getSmallImageLink().isEmpty()) {
//                fragment.initRecyclerView(width / 4 - dp2px);
                imageFlex = new FlexboxLayout(activity);
                imageFlex.setFlexWrap(FlexWrap.WRAP);
                imageFlex.setDividerDrawable(activity.getDrawable(
                        R.drawable.small_image_divider));
                imageFlex.setShowDivider(FlexboxLayout.SHOW_DIVIDER_MIDDLE);
                linearLayout.addView(imageFlex, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ImageView imageView;
                FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                        (width - dp2px * 4) / 4, (width - dp2px * 4) / 4);
                for (String imageLink : detail.getSmallImageLink()) {
                    imageView = new ImageView(activity);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(fragment).load(imageLink)
                            .error(R.drawable.ic_image_black_24dp)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .into(imageView);
                    imageFlex.addView(imageView, params);
                }
            }
            if (!detail.getVideoLink().isEmpty()) {
                videoFrame = new FrameLayout(activity);
                linearLayout.addView(videoFrame, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, width * 5 / 9 + width / 180));
                VideoView videoView = new VideoView(activity);
                //取消videoView焦点，否则会导致视图位置滚动
                videoView.setVisibility(View.GONE);
                videoFrame.addView(videoView, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                videoView.setVideoPath(detail.getVideoLink());
                MediaController mediaController = new MediaController(activity);
                videoView.setMediaController(mediaController);
                mediaController.setMediaPlayer(videoView);
                videoView.setVisibility(View.VISIBLE);
//                videoView.start();
            }
            if (!detail.getTextKey().isEmpty()) {
                ArrayList<String> textKey = detail.getTextKey();
                ArrayList<String[]> textValue = detail.getTextValue();
//                ArrayList<String[]> textLink = detail.getTextLink();
                int keySize = textKey.size();
                int valueSize;
                textFlex = new FlexboxLayout(activity);
                textFlex.setFlexWrap(FlexWrap.WRAP);
                textFlex.setAlignItems(AlignItems.CENTER);
                textFlex.setDividerDrawable(activity.getDrawable(
                        R.drawable.detail_text_divider));
                textFlex.setShowDivider(FlexboxLayout.SHOW_DIVIDER_MIDDLE);
                textFlex.setPadding(dp2px * 10, dp2px * 10, dp2px * 10, dp2px * 30);
                linearLayout.addView(textFlex, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                FlexboxLayout.LayoutParams newLine = new FlexboxLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                FlexboxLayout.LayoutParams inLine = new FlexboxLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                newLine.setWrapBefore(true);
                ClickListener clickListener = new ClickListener(fragment);
                for (int i = 0, j; i < keySize; i++) {
                    TextView textViewKey = new TextView(activity);
                    textFlex.addView(textViewKey, newLine);
                    textViewKey.setText(textKey.get(i));
                    valueSize = textValue.get(i).length;
                    for (j = 0; j < valueSize; j++) {
                        TextView textViewValue = new TextView(activity);
                        textFlex.addView(textViewValue, inLine);
                        textViewValue.setBackgroundResource(R.drawable.detail_text_background);
                        textViewValue.setTextColor(Color.WHITE);
                        textViewValue.setText(textValue.get(i)[j]);
                        clickListener.put(textViewValue, i, j);
                        textViewValue.setOnClickListener(clickListener);
                        textViewValue.setOnLongClickListener(clickListener);
                    }
                }
            }
            Glide.with(fragment).load(detail.getBigImageLink())
                    .error(R.drawable.ic_image_black_24dp)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("GLIDE", String.format(Locale.ROOT,
                                    "onException(%s, %s, %s, %s)", e, model, target, isFirstResource), e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("GLIDE", String.format(Locale.ROOT,
                                    "onResourceReady(%s, %s, %s, %s, %s)", resource, model, target, dataSource, isFirstResource));
                            return false;
                        }
                    })
//                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(bigImage);
        });
    }

    @Override
    protected void getContentFailure() {
        super.getContentFailure();
        weakActivity.get().runOnUiThread(() -> refreshLayout.setRefreshing(false));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        Log.d("tag", "detailMenu: " + mToolbarTitle);
        inflater.inflate(R.menu.toolbar_detail_menu, menu);
        System.out.println(ViewUtil.px2dp(bigImage.getWidth()));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
        Log.d("tag", "onOptionsItemSelected: " + mToolbarTitle);
        switch (item.getItemId()) {
            case R.id.favorite:
                break;
            case R.id.search_action:
                break;
            default:
                break;
        }
        return true;
    }

    static class RefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        WeakReference<MovieDetailFragment> weakFragment;

        public RefreshListener(MovieDetailFragment fragment) {
            weakFragment = new WeakReference<>(fragment);
        }

        @Override
        public void onRefresh() {
            MovieDetailFragment fragment = weakFragment.get();
            if (fragment.detail != null) {
                ArrayList<String> smallImageLink = fragment.detail.getSmallImageLink();
                int size = smallImageLink.size();
                for (int i = 0; i < size; i++) {
                    Glide.with(fragment).load(smallImageLink.get(i))
                            .error(R.drawable.ic_image_black_24dp)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .into((ImageView) fragment.imageFlex.getChildAt(i));
                }
                Glide.with(fragment).load(fragment.detail.getBigImageLink())
                        .error(R.drawable.ic_image_black_24dp)
//                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(fragment.bigImage);
                fragment.refreshLayout.setRefreshing(false);
            } else fragment.getContentFromLink();
        }
    }

    static class ClickListener implements View.OnClickListener, View.OnLongClickListener {
        WeakReference<MovieDetailFragment> weakFragment;
        WeakHashMap<View, int[]> weakHashMap;

        public ClickListener(MovieDetailFragment fragment) {
            weakFragment = new WeakReference<>(fragment);
            weakHashMap = new WeakHashMap<>(16, 1);
        }

        public void put(View view, int row, int column) {
            weakHashMap.put(view, new int[]{row, column});
        }

        @Override
        public void onClick(View v) {
            MovieDetailFragment fragment = weakFragment.get();
            MovieDetailBean detail = fragment.detail;
            int[] index = weakHashMap.get(v);
            int row = index[0];
            int column = index[1];
            fragment.clickTo(BaseJungle.MOVIE_LIST, detail.getTextLink().get(row)[column],
                    detail.getTextKey().get(row) + ": "
                            + detail.getTextValue().get(row)[column]);
        }

        @Override
        public boolean onLongClick(View v) {
            MovieDetailFragment fragment = weakFragment.get();
            MovieDetailBean detail = fragment.detail;
            int[] index = weakHashMap.get(v);
            int row = index[0];
            int column = index[1];
            fragment.longClickTo(v, BaseJungle.MOVIE_LIST, detail.getTextLink().get(row)[column],
                    detail.getTextKey().get(row) + ": " + detail.getTextValue().get(row)[column],
                    detail.getTextValue().get(row)[column]);
            return true;
        }

    }
}
