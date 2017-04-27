package com.yuyou.yiyuanduobao.paymore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.unicom.xiaowo.Pay;
import com.yuyou.yiyuanduobao.BaseActivity;
import com.yuyou.yiyuanduobao.ProjectApplication;
import com.yuyou.yiyuanduobao.R;
import com.yuyou.yiyuanduobao.bean.PayType;
import com.yuyou.yiyuanduobao.main.RecycleViewDivider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.leefeng.library.utils.LogUtils;
import me.leefeng.library.utils.ToastUtils;

/**
 * @author FengTing
 * @date 2017/04/24 17:36:13
 */
public class PaymoreActivity extends BaseActivity implements PaymoreView {
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.title_tv_right)
    TextView titleTvRight;
    @BindView(R.id.paymore_account)
    TextView paymoreAccount;
    @BindView(R.id.paymore_list)
    RecyclerView paymoreList;
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
        titleTvRight.setVisibility(View.GONE);
        titleName.setText("充值中心");
        if (ProjectApplication.user.getAccount() == null) {
            paymoreAccount.setText(0 + "");
        } else {
            paymoreAccount.setText(ProjectApplication.user.getAccount() + "");
        }
        paymoreList.setLayoutManager(new GridLayoutManager(mContext, 3));
//        paymoreList.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL,5,R.color.transparent));
//        paymoreList.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL,5,R.color.transparent));
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_paymore;
    }

    @Override
    protected void doReceive(Intent action) {

    }

    @Override
    public void showLoading(String msg) {
        svp.showLoading(msg);
    }

    @Override
    public void getListSuccess() {
        svp.dismissImmediately();


    }

    @Override
    public void setAdapter(PaymoreAdapter adapter) {
        paymoreList.setAdapter(adapter);
    }

    @Override
    public void svpDismiss() {
        svp.dismissImmediately();
    }

    @Override
    public void showToast(String msg) {
        ToastUtils.showLong(mContext, msg);
    }

    @Override
    public void payView(String sOrderId, String sVacCode, final PayType payType) {
        svp.dismissImmediately();
        String s1 = payType.getPrice() / 100 + "元";
        String s2 = payType.getPrice() / 100 + ".00";
        Pay.getInstance().payChannel(mContext, getString(R.string.app_name), getString(R.string.company), sVacCode,
                s1, s2, sOrderId, new Pay.UnipayPayResultListener() {

                    @Override
                    public void PayResult(String arg0, int arg1, int arg2, String arg3) {
                        LogUtils.i("联通返回结果：" + arg0 + "==" + arg1 + "==" + arg2 + "==" + arg3);
                        if (arg1 == 1) {
                            Toast.makeText(mContext, "支付请求已提交", Toast.LENGTH_LONG).show();
                            presenter.paySuccess(payType);
                        } else if (arg1 == 2) {
                            if (arg2 == 2) {
//                                Toast.makeText(mContext, arg3+"，请重试", Toast.LENGTH_LONG).show();
                                new AlertDialog.Builder(mContext).setTitle("首次使用支付系统")
                                        .setMessage("请点击确定后，重新打开APP")
                                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                System.exit(0);
                                            }
                                        })
                                        .show();

                            } else {
                                presenter.paySuccess(payType);
                                Toast.makeText(mContext, "支付成功", Toast.LENGTH_LONG).show();
                            }
                        } else if (arg1 == 3) {
                            Toast.makeText(mContext, "用户取消支付", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    public void paySuccess() {
        svp.showSuccessWithStatus("充值成功");
        paymoreAccount.setText(ProjectApplication.user.getAccount() + "");
    }

    @Override
    public void payFail() {
        svp.showSuccessWithStatus("充值失败，请稍候重试");
    }


    @OnClick(R.id.title_back)
    public void onViewClicked() {
        finish();
    }
}
