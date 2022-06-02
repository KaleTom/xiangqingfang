package com.zg.xqf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import im.zego.zegoexpress.ZegoExpressEngine;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.jaeger.library.StatusBarUtil;
import com.zg.xqf.controller.MasterController;
import com.zg.xqf.controller.UserController;
import com.zg.xqf.entity.UserInfo;
import com.zg.xqf.fragments.CreateJoinRoomFragment;
import com.zg.xqf.fragments.RoomFragment;
import com.zg.xqf.zego.MyEventHandler;
import com.zg.xqf.zego.Zego;

public class MainActivity extends AppCompatActivity {
    private final static int MAX_USER_COUNT = 2; //最大上麦用户数量
    private String[] permissionNeeded = {
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO"};
    private final static String TAG = "MainActivity";
    private final static int MY_PERMISSION_REQUEST_CODE = 10001;
    private ZegoExpressEngine mEngine;
    private CreateJoinRoomFragment mCJRFragment;
    private RoomFragment mRoomFragment;
    private MyEventHandler mMyEventHandler;
    private int mCurIdx = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        translucent();
//        setContentView(R.layout.fragment_create_join_room);
//        StatusBarUtil.setTranslucent(this, 0);
//        translucent();
        setContentView(R.layout.activity_main);
        translucent();
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        mEngine = Zego.createEngine(getApplication(), null);
        int navHeight = getNavHeight();
        Log.e(TAG, "navH:" + navHeight);
        mCJRFragment = new CreateJoinRoomFragment();
        mRoomFragment = new RoomFragment(navHeight);
        mMyEventHandler = new MyEventHandler();
        mEngine.setEventHandler(mMyEventHandler);
        // 设置默认的Fragment
        switchFragment(0);

    }

    public LoadingDailog showLoading(String msg) {
        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage(msg)
                .setCancelable(true)
                .setCancelOutside(true);
        LoadingDailog dialog = loadBuilder.create();
        dialog.show();
        return dialog;
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void switchFragment(int idx) {
        if (idx == mCurIdx) return;
        mCurIdx = idx;
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (idx == 0) {
            transaction.replace(R.id.id_content, mCJRFragment);

        } else {
            transaction.replace(R.id.id_content, mRoomFragment);
        }
        transaction.commit();
    }

    private boolean checkPermission() {
        for (String permission : permissionNeeded) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, permissionNeeded, MY_PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE: {
                boolean isAllGranted = true;
                // 判断是否所有的权限都已经授予了
                for (int grant : grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        isAllGranted = false;
                        break;
                    }
                }
                if (isAllGranted) {
                    // 如果所有的权限都授予了, 则执行备份代码

                    switchFragment(1);

                } else {
                    // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                    toast("请先允许音视频权限");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void createRoom(UserInfo userInfo, String roomName, String roomId) {

        MasterController controller = new MasterController(mEngine, MAX_USER_COUNT, roomId, roomName, userInfo);
        mRoomFragment.setController(controller);
        mMyEventHandler.setMyEventListener(controller);

        if (checkPermission()) {
            switchFragment(1);
        } else {
            requestPermission();
        }

    }

    public void joinRoom(UserInfo userInfo, String roomId) {
        UserController controller = new UserController(mEngine, MAX_USER_COUNT, roomId, null, userInfo);
        mRoomFragment.setController(controller);
        mMyEventHandler.setMyEventListener(controller);

        if (checkPermission()) {
            switchFragment(1);
        } else {
            requestPermission();
        }

    }

    public boolean checkHasNavigationBar() {
        WindowManager windowManager = getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    private int getNavHeight() {
        int result = 0;
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && checkHasNavigationBar()) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void translucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 实现透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    public void closeRoom() {

        if (mCurIdx == 1) {
            switchFragment(0);
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBack");
        if (mCurIdx == 1) {
            mRoomFragment.loginOut(1);
        }
    }
}