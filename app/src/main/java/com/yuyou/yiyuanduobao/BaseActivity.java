package com.yuyou.yiyuanduobao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import butterknife.ButterKnife;
import me.leefeng.library.NoTitleBar.StatusBarCompat;
import me.leefeng.library.NoTitleBar.SystemBarTintManager;
import me.leefeng.library.SVProgressHUD.SVProgressHUD;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * @author FengTing
 * @date 2017/04/24 16:00:40
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Context mContext;
    protected PromptDialog promptDialog;
//    protected SVProgressHUD svp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.translucentStatusBar(this);
        setContentView(getContentView());
        View topView = findViewById(R.id.title_topview);
        if (topView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int height = StatusBarCompat.getStatusBarHeight(this);
            topView.getLayoutParams().height = height;
        }
        ButterKnife.bind(this);
        mContext = this;
        initView();
//        svp = new SVProgressHUD(mContext);
        promptDialog = new PromptDialog(this);
        promptDialog.getDefaultBuilder().backAlpha(0).roundAlpha(200).stayDuration(1300l);
        initData();
        registBrodcast();//注册广播

    }

    protected abstract void initData();

    protected abstract void initView();

    protected abstract int getContentView();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        mContext = null;
    }

    /**
     * 关闭键盘
     */
    protected void closeInput() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.siweidg.finish")) {
                finish();
                return;
            }
            doReceive(intent);
        }
    };

    protected abstract void doReceive(Intent action);

    private void registBrodcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.yuyou.account");
        registerReceiver(broadcastReceiver, intentFilter);
    }
}
