package com.yuyou.yiyuanduobao.main;

import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import com.unicom.xiaowo.Pay;
import com.yuyou.yiyuanduobao.BaseActivity;
import com.yuyou.yiyuanduobao.R;

import butterknife.BindView;
import butterknife.OnClick;
import me.leefeng.library.SVProgressHUD.SVProgressHUD;

/**
 * @author FengTing
 * @date 2017/04/24 16:00:40
 */
public class MainActivity extends BaseActivity implements MainView {
    @BindView(R.id.main_bt)
    Button mainBt;
    private MainPresenter presenter;
    private SVProgressHUD svp;

    @Override
    protected void initData() {
        svp = new SVProgressHUD(mContext);
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


    @OnClick(R.id.main_bt)
    public void onViewClicked() {
        presenter.pay();
    }

    @Override
    public void payView(final String sOrderId, final String sVacCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                svp.dismiss();
                Pay.getInstance().payChannel(mContext, "支付100金币：", "一元夺宝", sVacCode,
                        "1元", "1", sOrderId, new Pay.UnipayPayResultListener() {

                            @Override
                            public void PayResult(String arg0, int arg1, int arg2, String arg3) {
                                if (arg1 == 1) {
                                    Toast.makeText(mContext, "支付请求已提交", Toast.LENGTH_LONG).show();
                                } else if (arg1 == 2) {
                                    Toast.makeText(mContext, "支付失败", Toast.LENGTH_LONG).show();
                                } else if (arg1 == 3) {
                                    Toast.makeText(mContext, "用户取消支付", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });
    }

    @Override
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void showLoading(String msg) {
        svp.showLoading(msg);
    }
}
