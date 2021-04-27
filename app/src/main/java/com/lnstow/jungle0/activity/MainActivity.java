package com.lnstow.jungle0.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.google.android.material.navigation.NavigationView;
import com.lnstow.jungle0.BaseJungle;
import com.lnstow.jungle0.R;
import com.lnstow.jungle0.database.AppDatabase;
import com.lnstow.jungle0.fragment.BaseFragment;
import com.lnstow.jungle0.fragment.CustomDialogFragment;
import com.lnstow.jungle0.fragment.main.ReadFragment;
import com.lnstow.jungle0.util.HttpUtil;
import com.lnstow.jungle0.util.ViewUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    public ArrayDeque<BaseFragment> fragmentStack;
    public BaseFragment[] fragmentMain;
    public byte fragmentMainIndex;
    private ReadFragment readFragment;
    private ListPopupWindow listPopupWindow;
    private PopupListener popupListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.main_drawer);
        navigationView = findViewById(R.id.main_left);
        initView();
        initData(savedInstanceState);

        if (savedInstanceState == null)
            showFragment(BaseJungle.MOVIE_HOME, BaseJungle.JUNGLE_URL,
                    navigationView.getCheckedItem().getTitle().toString(), 0);

        readFragment = (ReadFragment) BaseFragment.newInstance(
                BaseJungle.MOVIE_READ, null, null);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_right, readFragment)
                .commitAllowingStateLoss();

        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setInputMethodMode(ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
        listPopupWindow.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, new String[]{"加入未读", "复制"}));
        popupListener = new PopupListener(this);
        listPopupWindow.setOnItemClickListener(popupListener);
        listPopupWindow.setWidth(ViewUtil.dp2px(120));
        listPopupWindow.setModal(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        singleThread.execute(HttpUtil::saveCookieToFile);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        singleThread.shutdown();
        appDatabase.close();
    }

    private void initData(Bundle savedInstanceState) {
        fragmentStack = new ArrayDeque<>();
        fragmentMain = new BaseFragment[5];
        fragmentMainIndex = 0;

        HttpUtil.initOkHttpClient(getExternalCacheDir(), getExternalFilesDir(null));
        singleThread = Executors.newSingleThreadExecutor();
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, BaseJungle.DATABASE_NAME)
//                .addMigrations()
//                .allowMainThreadQueries()
                .build();
    }

    private void initView() {
        //init toolbar
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
//        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

//        箭头
//        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        三条杠
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        //init statusBar
//        Window window = this.getWindow();
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

//        AppTheme.NoActionBar
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

//        window.setStatusBarColor(Color.TRANSPARENT);
//        window.setStatusBarColor(Color.parseColor("#885555"));
//        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    showFragment(BaseJungle.MOVIE_HOME, BaseJungle.JUNGLE_URL,
                            item.getTitle().toString(), 0);
                    break;
                case R.id.nav_favorite:
                    showFragment(BaseJungle.MOVIE_FAVORITE, BaseJungle.JUNGLE_URL,
                            item.getTitle().toString(), 1);
                    break;
                case R.id.nav_moe:
                    showFragment(BaseJungle.MOVIE_HOME, BaseJungle.JUNGLE_URL_MOE,
                            item.getTitle().toString(), 2);
                    break;
                case R.id.nav_history:
                    showFragment(BaseJungle.MOVIE_HOME, BaseJungle.JUNGLE_URL_HISTORY,
                            item.getTitle().toString(), 3);
                    break;
                case R.id.nav_setting:
                    showFragment(BaseJungle.MOVIE_SETTING, BaseJungle.JUNGLE_URL,
                            item.getTitle().toString(), 4);
                    break;
                case R.id.nav_exit:
                    finish();
                    break;
                default:
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START, true);
            return true;
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if (drawerView.getId() == R.id.main_right &&
                        readFragment.getViewPagerPosition() != 0)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, GravityCompat.END);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (drawerView.getId() == R.id.main_right)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        drawerLayout.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawerLayout.closeDrawer(GravityCompat.END, true);
                    break;
            }
            return false;
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void addFragment(byte type, String link, String toolbarTitle) {
        BaseFragment baseFragment = BaseFragment.newInstance(type, link, toolbarTitle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (!fragmentStack.isEmpty()) {
            ft.hide(fragmentStack.peekFirst());
        } else {
            ft.hide(fragmentMain[fragmentMainIndex]);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
//            findViewById(R.id.frame_base).setVisibility(View.INVISIBLE);
        }
//        ft.add(R.id.frame_base, baseFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.add(R.id.main_content, baseFragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();

        fragmentStack.offerFirst(baseFragment);
//        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        Toast.makeText(this, String.valueOf(fm.getBackStackEntryCount()), Toast.LENGTH_SHORT).show();

//        getSupportFragmentManager().popBackStack(1,"");
    }

    public void showFragment(byte type, String link, String toolbarTitle, int index) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < 5; i++) {
            if (index == i) continue;
            if (fragmentMain[i] == null) continue;
            if (fragmentMain[i].isHidden()) continue;
            ft.hide(fragmentMain[i]);
        }
        if (fragmentMain[index] == null) {
            BaseFragment baseFragment = BaseFragment.newInstance(type, link, toolbarTitle);
            ft.add(R.id.main_content, baseFragment);
            fragmentMain[index] = baseFragment;
        }
        ft.show(fragmentMain[index]);
        ft.commitAllowingStateLoss();
        fragmentMainIndex = (byte) index;
    }


    @Override
    public void onBackPressed() {
//        Toast.makeText(this, String.valueOf(getSupportFragmentManager().getBackStackEntryCount()), Toast.LENGTH_SHORT).show();
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END, true);
        } else if (!fragmentStack.isEmpty()) {
            getSupportFragmentManager().popBackStack();
            fragmentStack.pollFirst();
            readFragment.deleteFromReading();
            if (fragmentStack.isEmpty())
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void requestAddFragment(byte type, String link, String toolbarTitle) {
        addFragment(type, link, toolbarTitle);
        readFragment.insertIntoRead(type, link, toolbarTitle);
        readFragment.insertIntoReading(type, link, toolbarTitle);
    }

    @Override
    public void requestAddDialog(byte dialogType, int checkedItem, int maxPage) {
        CustomDialogFragment.newInstance(dialogType, checkedItem, maxPage)
                .show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void operateRightDrawer(boolean lock, boolean close) {
        if (lock)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, GravityCompat.END);
        else if (!close)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
        else drawerLayout.closeDrawer(GravityCompat.END, true);
    }

    @Override
    public void showPopupWindow(View view, byte type, String link, String title, String copy) {
        popupListener.setData(type, link, title, copy);
        listPopupWindow.setAnchorView(view);
        listPopupWindow.show();
    }

    @Override
    public void dialogNotifyChange(byte dialogType, int index, String textValue) {
        BaseFragment fragment = fragmentStack.peekFirst();
        if (fragment == null) fragment = fragmentMain[fragmentMainIndex];
        fragment.changeFromDialog(dialogType, index, textValue);
    }

    static class PopupListener implements AdapterView.OnItemClickListener {
        WeakReference<MainActivity> weakReference;
        ClipboardManager clipboardManager;
        byte type;
        String link;
        String title;
        String copy;

        public PopupListener(MainActivity activity) {
            this.weakReference = new WeakReference<>(activity);
            clipboardManager = (ClipboardManager)
                    activity.getSystemService(Context.CLIPBOARD_SERVICE);
        }

        public void setData(byte type, String link, String title, String copy) {
            this.type = type;
            this.link = link;
            this.title = title;
            this.copy = copy;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    weakReference.get().readFragment.insertIntoUnread(type, link, title);
                    break;
                case 1:
//                    ClipData clipData = ClipData.newPlainText(copy, copy);
//                    clipboardManager.setPrimaryClip(clipData);
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(copy, copy));
                    break;
                default:
                    break;
            }
            weakReference.get().listPopupWindow.dismiss();
        }
    }

}
