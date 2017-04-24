package com.yuyou.yiyuanduobao.main;

import android.content.Intent;
import com.yuyou.yiyuanduobao.BaseActivity;
import com.yuyou.yiyuanduobao.R;

/**
 * @author FengTing
 * @date 2017/04/24 16:00:40
 */
public class MainActivity extends BaseActivity implements MainView {
    private MainPresenter presenter;

    @Override
    protected void initData() {
        presenter = new MainPresenter(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destory();
        presenter = null;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void doReceive(Intent action) {

    }
}
