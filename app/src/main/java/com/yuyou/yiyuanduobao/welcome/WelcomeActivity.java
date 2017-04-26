package com.yuyou.yiyuanduobao.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.yuyou.yiyuanduobao.BaseActivity;
import com.yuyou.yiyuanduobao.BuildConfig;
import com.yuyou.yiyuanduobao.R;
import com.yuyou.yiyuanduobao.login.LoginActivity;
import com.yuyou.yiyuanduobao.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.leefeng.library.utils.SharedPreferencesUtil;
import me.leefeng.library.utils.StringUtils;

/**
 * @author FengTing
 * @date 2017/04/25 10:39:52
 */
public class WelcomeActivity extends BaseActivity implements WelcomeView {
    @BindView(R.id.welcome_bottom)
    TextView welcomeBottom;
    private WelcomePresenter presenter;
    private String phone;

    @Override
    protected void initData() {
       phone= SharedPreferencesUtil.getStringData(mContext,"phone",null);
        presenter = new WelcomePresenter(this);
        welcomeBottom.setText(getResources().getString(R.string.app_name)+" V "+ BuildConfig.VERSION_NAME);
        welcomeBottom.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=null;
                if (StringUtils.isEmpty(phone)){
                    intent =new Intent(mContext, LoginActivity.class);
                }else{
                    intent =new Intent(mContext, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        },3000);
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
        return R.layout.activity_welcome;
    }

    @Override
    protected void doReceive(Intent action) {

    }
}
