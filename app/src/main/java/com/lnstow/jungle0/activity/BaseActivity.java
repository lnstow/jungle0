package com.lnstow.jungle0.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lnstow.jungle0.database.AppDatabase;
import com.lnstow.jungle0.fragment.BaseFragment;
import com.lnstow.jungle0.fragment.CustomDialogFragment;

import java.util.concurrent.ExecutorService;

public class BaseActivity extends AppCompatActivity implements
        BaseFragment.OnFragmentInteractionListener,
        CustomDialogFragment.OnFragmentInteractionListener {
    private static final String TAG = "BaseActivity";
    //    public
    public ExecutorService singleThread;
    public AppDatabase appDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d("BaseActivity", "onCreate: " + getClass().getSimpleName());
    }

    @Override
    public void requestAddFragment(byte type, String link, String toolbarTitle) {

    }

    @Override
    public void requestAddDialog(byte dialogType, int checkedItem, int maxPage) {

    }

    @Override
    public void operateRightDrawer(boolean lock, boolean close) {

    }

    @Override
    public void showPopupWindow(View view, byte type, String link, String title, String copy) {

    }

    @Override
    public void dialogNotifyChange(byte dialogType, int index, String textValue) {

    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d(TAG, "onStart: ");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d(TAG, "onResume: ");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d(TAG, "onPause: ");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.d(TAG, "onStop: ");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "onDestroy: ");
//    }
//
//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        Log.d(TAG, "onSaveInstanceState: ");
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.d(TAG, "onRestart: ");
//    }
}
