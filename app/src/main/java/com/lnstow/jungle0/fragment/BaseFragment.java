package com.lnstow.jungle0.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import com.lnstow.jungle0.BaseJungle;
import com.lnstow.jungle0.R;
import com.lnstow.jungle0.activity.BaseActivity;
import com.lnstow.jungle0.activity.MainActivity;
import com.lnstow.jungle0.fragment.main.FavoriteFragment;
import com.lnstow.jungle0.fragment.main.HomeFragment;
import com.lnstow.jungle0.fragment.main.ReadFragment;
import com.lnstow.jungle0.fragment.main.SettingFragment;
import com.lnstow.jungle0.util.HttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BaseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BaseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TYPE = "param1";
    private static final String ARG_LINK = "param2";
    private static final String ARG_TOOLBAR_TITLE = "param3";

    // TODO: Rename and change types of parameters
    protected byte mType;
    protected String mLink;
    protected String mToolbarTitle;

    protected WeakReference<BaseActivity> weakActivity;
    protected OnFragmentInteractionListener mListener;

    public BaseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mType Parameter 1.
     * @param mLink Parameter 2.
     * @return A new instance of fragment BaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BaseFragment newInstance(byte mType, String mLink, String mToolbarTitle) {
        BaseFragment fragment;
        Bundle args = new Bundle();
        args.putByte(ARG_TYPE, mType);
        args.putString(ARG_LINK, mLink);
        args.putString(ARG_TOOLBAR_TITLE, mToolbarTitle);

        switch (mType) {
            case BaseJungle.MOVIE_LIST:
                fragment = new MovieListFragment();
                break;
            case BaseJungle.MOVIE_DETAIL:
                fragment = new MovieDetailFragment();
                break;
            case BaseJungle.MOVIE_HOME:
                fragment = new HomeFragment();
                break;
            case BaseJungle.MOVIE_FAVORITE:
                fragment = new FavoriteFragment();
                break;
            case BaseJungle.MOVIE_SETTING:
                fragment = new SettingFragment();
                break;
            case BaseJungle.MOVIE_READ:
                fragment = new ReadFragment();
                break;
            default:
                fragment = new BaseFragment();
                break;
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getByte(ARG_TYPE);
            mLink = getArguments().getString(ARG_LINK);
            mToolbarTitle = getArguments().getString(ARG_TOOLBAR_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("baseFragment", "onActivityCreated: " + getActivity() + "  " + getContext());
        weakActivity = new WeakReference<>((BaseActivity) getActivity());
        initView();
    }

    protected void initView() {
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void clickTo(byte type, String link, String toolbarTitle) {
        if (mListener != null) mListener.requestAddFragment(type, link, toolbarTitle);
    }

    public void operateRightDrawer(boolean lock, boolean close) {
        if (mListener != null) mListener.operateRightDrawer(lock, close);
    }

    public void longClickTo(View view, byte type, String link, String title, String copy) {
        if (mListener != null) mListener.showPopupWindow(view, type, link, title, copy);
    }

    public void changeFromDialog(byte dialogType, int index, String textValue) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void requestAddFragment(byte type, String link, String toolbarTitle);

        void requestAddDialog(byte dialogType, int checkedItem, int maxPage);

        void operateRightDrawer(boolean lock, boolean close);

        void showPopupWindow(View view, byte type, String link, String title, String copy);

    }


    protected void getContentFromLink() {
        HttpUtil.sendOkHttpRequest(mLink, new HttpBack(this), mType);
    }

    protected void getContentFromLink(String link) {
        HttpUtil.sendOkHttpRequest(link, new HttpBack(this), mType);
    }

    protected void getContentSuccess(String htmlResult) {
    }

    protected void getContentFailure() {
        weakActivity.get().runOnUiThread(() -> Toast.makeText(weakActivity.get(),
                "获取失败，请重试", Toast.LENGTH_LONG).show());
    }

    public static class HttpBack implements Callback {
        WeakReference<BaseFragment> weakFragment;

        HttpBack(BaseFragment fragment) {
            this.weakFragment = new WeakReference<>(fragment);
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                e.printStackTrace();
            BaseFragment fragment = weakFragment.get();
            if (fragment != null && fragment.getContext() != null) {
                fragment.getContentFailure();
            }
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            try {
                BaseFragment fragment = weakFragment.get();
                Log.d("okhttp_onResponse", "response cache :" + response.cacheControl());
                Log.d("okhttp_onResponse", "response cache :" + response.cacheResponse());
                Log.d("okhttp_onResponse", "response network :" + response.networkResponse());
                if (fragment != null && fragment.getContext() != null)
                    fragment.getContentSuccess(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
                onFailure(call, e);
            }
        }
    }

    public static class SwipeBack implements SlidingPaneLayout.PanelSlideListener {
        private WeakReference<BaseFragment> thisFragment;
        private boolean isSliding;
        private WeakReference<BaseFragment> previousFragment;

        public SwipeBack(BaseFragment fragment) {
            thisFragment = new WeakReference<>(fragment);
            isSliding = false;

            MainActivity mainActivity = (MainActivity) fragment.weakActivity.get();
            Iterator<BaseFragment> iterator = mainActivity.fragmentStack.iterator();
            previousFragment = new WeakReference<>(null);
            if (!iterator.hasNext()) return;
            iterator.next();
            if (!iterator.hasNext()) {
                previousFragment = new WeakReference<>(
                        mainActivity.fragmentMain[mainActivity.fragmentMainIndex]);
            } else {
                previousFragment = new WeakReference<>(iterator.next());
            }
        }

        @Override
        public void onPanelSlide(@NonNull View panel, float slideOffset) {
            if (isSliding || previousFragment.get() == null) return;
            BaseFragment fragment = thisFragment.get();
            BaseActivity activity = fragment.weakActivity.get();
            if (fragment.mType == BaseJungle.MOVIE_LIST) {
                ((MovieListFragment) fragment).refreshLayout.setBackgroundColor(Color.WHITE);
            }
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .show(previousFragment.get())
                    .commitNowAllowingStateLoss();
//            Log.d("tag", "onPanelSlide: " + previousFragment.get());
//            Log.d("tag", "onPanelSlide: " + thisFragment.get());
//            previousFragment.get().setHasOptionsMenu(false);
            isSliding = true;
        }

        @Override
        public void onPanelOpened(@NonNull View panel) {
            isSliding = false;
            thisFragment.get().weakActivity.get().onBackPressed();
//            previousFragment.get().setHasOptionsMenu(true);
//            this.overridePendingTransition(0, R.anim.out_to_right);
        }

        @Override
        public void onPanelClosed(@NonNull View panel) {
            isSliding = false;
            if (previousFragment.get() == null) return;
            BaseFragment fragment = thisFragment.get();
            BaseActivity activity = fragment.weakActivity.get();
            if (fragment.mType == BaseJungle.MOVIE_LIST) {
                ((MovieListFragment) fragment).refreshLayout.setBackgroundColor(Color.TRANSPARENT);
            }
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .hide(previousFragment.get())
                    .commitNowAllowingStateLoss();
        }
    }

    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
//        return super.onCreateAnimation(transit, enter, nextAnim);
//        if (transit == FragmentTransaction.TRANSIT_FRAGMENT_OPEN) {//表示是一个进入动作，比如add.show等
//            if (enter) {//普通的进入的动作
//                return AnimationUtils.loadAnimation(getContext(), R.anim.fragment_right_in);
//            } else {//比如一个已经Fragmen被另一个replace，是一个进入动作，被replace的那个就是false
//                return AnimationUtils.loadAnimation(getContext(), R.anim.fragment_no_out);
//            }
//        } else if (transit == FragmentTransaction.TRANSIT_FRAGMENT_CLOSE) {//表示一个退出动作，比如出栈，hide，detach等
//            if (enter) {//之前被replace的重新进入到界面或者Fragment回到栈顶
//                return AnimationUtils.loadAnimation(getContext(), R.anim.fragment_no_out);
//            } else {//Fragment退出，出栈
//                return AnimationUtils.loadAnimation(getContext(), R.anim.fragment_right_out);
//            }
//        }
//        return null;
        TranslateAnimation animation = null;
        if (transit == FragmentTransaction.TRANSIT_FRAGMENT_OPEN) {
            if (enter) {
                animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0,
                        Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
            } else {
                animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                        Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
            }
        } else if (transit == FragmentTransaction.TRANSIT_FRAGMENT_CLOSE) {
            if (enter) {
                animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                        Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
            } else {
                animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 1,
                        Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
            }
        }
        if (animation != null)
            animation.setDuration(200);
        return animation;
    }

}
