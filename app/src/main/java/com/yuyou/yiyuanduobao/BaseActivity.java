package com.yuyou.yiyuanduobao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * @author FengTing
 * @date 2017/04/24 16:00:40
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        mContext = this;
        initView();
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
        intentFilter.addAction("com.siweidg.finish");
        registerReceiver(broadcastReceiver, intentFilter);
    }
}
