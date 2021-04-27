package com.lnstow.jungle0.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.lnstow.jungle0.BaseJungle;
import com.lnstow.jungle0.R;
import com.lnstow.jungle0.activity.MainActivity;
import com.lnstow.jungle0.adapter.MovieListAdapter;
import com.lnstow.jungle0.bean.MovieListItemBean;
import com.lnstow.jungle0.util.JsoupUtil;
import com.lnstow.jungle0.util.ViewUtil;
import com.scwang.smartrefresh.header.StoreHouseHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends BaseFragment {
    protected RecyclerView recyclerView;
    protected ArrayList<MovieListItemBean> list;
    protected MovieListAdapter adapter;
    protected Toolbar toolbar;
    protected SlidingPaneLayout slidingPaneLayout;
    protected SmartRefreshLayout refreshLayout;
    protected int peopleNumIndex = 0;
    protected int page = 0;
    protected int maxPage = 0;
    protected byte refreshState = 0;//1:refresh,2:loadMore,3:changeNum,4:changePage,5:finish


    public MovieListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_list, container, false);
    }

    @Override
    protected void initView() {
        findView();
        initRecyclerView();
        initToolbar();
        initSwipeBack();
        initRefreshLayout();
        getContentFromLink();
    }

    protected void findView() {
        View view = getView();
        recyclerView = view.findViewById(R.id.movie_list_recycler_view);
        slidingPaneLayout = view.findViewById(R.id.swipe_back_layout);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        toolbar = view.findViewById(R.id.movie_list_toolbar);
        int len = BaseJungle.JUNGLE_LENGTH;
        if (mLink.length() > len + 6 && mLink.charAt(len + 1) == 'h' &&
                mLink.charAt(len + 2) == 'm' && mLink.charAt(len + 3) == '=') {
            String value = mLink.substring(len + 4, mLink.indexOf('&', len + 5));
            for (int i = 0; i < 6; i++) {
                if (BaseJungle.PEOPLE_NUM[i].equals(value)) {
                    peopleNumIndex = i;
                    break;
                }
            }
        }
    }

    protected void initRecyclerView() {
        list = new ArrayList<>();
        adapter = new MovieListAdapter(list, this);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setJustifyContent(JustifyContent.SPACE_EVENLY);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setItemAnimator(new ScaleInAnimator());
        ScaleInAnimationAdapter scale = new ScaleInAnimationAdapter(adapter);
        scale.setDuration(1000);
        scale.setFirstOnly(false);
        scale.setInterpolator(new OvershootInterpolator());
        recyclerView.setAdapter(scale);
        recyclerView.setItemViewCacheSize(3);
    }

    protected void initToolbar() {
        setHasOptionsMenu(true);
        weakActivity.get().setSupportActionBar(toolbar);
        ActionBar actionBar = weakActivity.get().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(ViewUtil.dp2px(4));
            actionBar.setTitle(mToolbarTitle);
        }
    }

    protected void initSwipeBack() {
        //通过反射改变mOverhangSize的值为0，
        //这个mOverhangSize值为菜单到右边屏幕的最短距离，
        //默认是32dp，现在给它改成0
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
//        refreshLayout.setPrimaryColorsId(R.color.primary);
        refreshLayout.setHeaderHeight(70);
//        refreshLayout.setFooterHeight(70);
        refreshLayout.setRefreshHeader(new StoreHouseHeader(getContext()).initWithString("345").setDropHeight(300));
//        refreshLayout.setRefreshHeader(new BezierRadarHeader(getContext()));
//        refreshLayout.setRefreshFooter(new BallPulseFooter(getContext())
//                .setAnimatingColor(Color.parseColor("#009688"))
//                .setNormalColor(Color.parseColor("#557700")));
//        refreshLayout.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale));
        //设置 Header 为 贝塞尔雷达 样式
//        refreshLayout.setRefreshHeader(new BezierRadarHeader(this).setEnableHorizontalDrag(true));

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                Log.d("listFragment", "onLoadMore: " + refreshState);
                if (refreshState == 0) return;
                refreshState = 2;
                getContentFromLink(BaseJungle.addQueryParam(mLink, peopleNumIndex, page + 100));
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (refreshState == 0) return;
                refreshState = 1;
                getContentFromLink(BaseJungle.addQueryParam(mLink, peopleNumIndex, 0));
            }
        });
//        refreshLayout.setOnRefreshLoadMoreListener(new RefreshListener(this));
//        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        refreshLayout.autoRefreshAnimationOnly();
    }

    @Override
    protected void getContentSuccess(String htmlResult) {
        ArrayList<MovieListItemBean> newList = JsoupUtil.handleListWithJsoup(htmlResult);
//        final byte refreshState = this.refreshState;
        weakActivity.get().runOnUiThread(() -> {
            int size = list.size();
            switch (refreshState) {
                case 0:
                    try {
                        Field f_offset = SlidingPaneLayout.class.getDeclaredField("mSlideOffset");
                        f_offset.setAccessible(true);
                        if (slidingPaneLayout == null || f_offset.getFloat(slidingPaneLayout) == 0) {
                            refreshLayout.setBackgroundColor(Color.TRANSPARENT);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                case 1:
                    list = newList;
                    if (list.isEmpty())
                        maxPage = 0;
                    else
                        maxPage = JsoupUtil.getMaxPage(htmlResult);
                    page = 0;
                    break;
                case 2:
                    list.addAll(newList);
                    page += 100;
                    break;
                case 3:
                    list = newList;
                    if (list.isEmpty())
                        maxPage = 0;
                    else
                        maxPage = JsoupUtil.getMaxPage(htmlResult);
                    page = 0;
                    break;
                case 4:
                    list = newList;
                    break;
                default:
                    break;
            }
            if (page + 100 >= maxPage) {
                if (refreshState == 2) refreshLayout.finishLoadMoreWithNoMoreData();
                else refreshLayout.finishRefreshWithNoMoreData();
            } else {
                if (refreshState == 2) refreshLayout.finishLoadMore(true);
                else refreshLayout.finishRefresh(true);
            }
            Log.d("listFragment", "getContentSuccess: " + refreshState);
            if (refreshState == 2) {
//                adapter.setList(list);
                adapter.notifyItemRangeChanged(size, list.size() - size);
            } else {
                adapter.setList(list);
//                adapter.notifyDataSetChanged();
                adapter.notifyItemRangeChanged(0, list.size() == 0 ? 1 : list.size());
            }

            refreshState = 5;
        });
    }

    @Override
    protected void getContentFailure() {
        super.getContentFailure();
        weakActivity.get().runOnUiThread(() -> {
            if (refreshState == 2) refreshLayout.finishLoadMore(false);
            else refreshLayout.finishRefresh(false);
            refreshState = 5;
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        Log.d("tag", "listMenu: " + mToolbarTitle);
        if (((MainActivity) weakActivity.get()).fragmentStack.peekFirst() == this) {
            menu.clear();
            inflater.inflate(R.menu.toolbar_list_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
//        Log.d("tag", "onOptionsItemSelected: "+this);
//        Log.d("tag", "click: " + this);
//        System.out.println(mType+"  "+mToolbarTitle);
//        if (((MainActivity) weakActivity.get()).fragmentStack.peekFirst() == this) {
        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                Toast.makeText(this, "home!", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.actress_num:
                mListener.requestAddDialog(CustomDialogFragment.DIALOG_PEOPLE_NUM
                        , peopleNumIndex, 0);
                break;
            case R.id.search_action:
                mListener.requestAddDialog(CustomDialogFragment.DIALOG_SEARCH
                        , 0, 0);
                break;
            case R.id.view_size:
                mListener.requestAddDialog(CustomDialogFragment.DIALOG_VIEW_SIZE,
                        0, 0);
                break;
            case R.id.page:
                mListener.requestAddDialog(CustomDialogFragment.DIALOG_PAGE,
                        page, maxPage);
                break;
            case R.id.favorite:
                break;
            default:
                break;
//            }
//            return true;
        }
        return false;
    }

    @Override
    public void changeFromDialog(byte dialogType, int index, String textValue) {
        switch (dialogType) {
            case CustomDialogFragment.DIALOG_PEOPLE_NUM:
                peopleNumIndex = index;
                refreshState = 3;
                refreshLayout.autoRefreshAnimationOnly();
                getContentFromLink(BaseJungle.addQueryParam(mLink, peopleNumIndex, 0));
                break;
            case CustomDialogFragment.DIALOG_SEARCH:
                mListener.requestAddFragment(BaseJungle.MOVIE_LIST,
                        BaseJungle.JUNGLE_URL_SEARCH + textValue, "search: " + textValue);
                break;
            case CustomDialogFragment.DIALOG_PAGE:
                page = index * 100;
                refreshState = 4;
                refreshLayout.autoRefreshAnimationOnly();
                getContentFromLink(BaseJungle.addQueryParam(mLink, peopleNumIndex, page));
                break;
            default:
                break;
        }
    }

}

