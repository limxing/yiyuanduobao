package com.yuyou.yiyuanduobao.paymore;

import android.content.Intent;
import com.yuyou.yiyuanduobao.BaseActivity;
import com.yuyou.yiyuanduobao.R;

/**
 * @author FengTing
 * @date 2017/04/24 17:36:13
 */
public class PaymoreActivity extends BaseActivity implements PaymoreView {
    private PaymorePresenter presenter;

    @Override
    protected void initData() {
        presenter = new PaymorePresenter(this);
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
        return R.layout.activity_paymore;
    }

    @Override
    protected void doReceive(Intent action) {

    }
}
