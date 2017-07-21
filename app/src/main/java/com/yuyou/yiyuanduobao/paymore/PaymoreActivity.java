package com.yuyou.yiyuanduobao.paymore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.unicom.xiaowo.Pay;
import com.unicom.dcLoader.Utils;
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
import me.leefeng.promptlibrary.PromptButton;

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
    private PayType currentPay;

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
//        svp.showLoading(msg);
        promptDialog.showLoading(msg);
    }

    @Override
    public void getListSuccess() {
//        svp.dismissImmediately();
        promptDialog.dismissImmediately();


    }

    @Override
    public void setAdapter(PaymoreAdapter adapter) {
        paymoreList.setAdapter(adapter);
    }

    @Override
    public void svpDismiss() {
        promptDialog.dismissImmediately();
//        svp.dismissImmediately();
    }

    @Override
    public void showToast(String msg) {
        ToastUtils.showLong(mContext, msg);
    }
    private  Utils.UnipayPayResultListener OnLineListener = new Utils.UnipayPayResultListener() {

        @Override
        public void PayResult(String arg0, int arg1, int arg2, String arg3) {
            switch (arg1) {
                case 1://success
                    //此处放置支付请求已提交的相关处理代码
//                showPayResultOnLine(cpProductName + " " + "支付请求已提交");
                    presenter.paySuccess(currentPay);

                    break;

                case 2://fail
                    //此处放置支付请求失败的相关处理代码
                    showPayResultOnLine(currentPay.getName() + " " + "支付失败");
                    break;

                case 3://cancel
                    //此处放置支付请求被取消的相关处理代码
//                showPayResultOnLine(cpProductName + " " + "支付取消");
                    promptDialog.showInfo("支付取消");

                    break;

                default:
                    showPayResultOnLine(currentPay.getName() + " " + "支付结果未知");
                    break;
            }
        }
    };
    @Override
    public void payView(String sOrderId, String sVacCode, final PayType payType) {
//        svp.dismissImmediately();
//        promptDialog.dismiss();
        currentPay=payType;
        promptDialog.dismissImmediately();
//        String s1 = payType.getPrice() / 100 + " 金币";
//        String s2 = payType.getPrice() / 100 + ".00";
//        String code="";

//      String i=  String.valueOf(payType.getPrice().longValue()/100);
//        if (i.length()==1){
//            i="00"+i;
//        }else if (i.length()==2){
//            i="0"+i;
//        }

        String i = payType.getCode();
        if (sOrderId.length() > 24){
            Utils.getInstances().payOnline(mContext, i, "170522582109",sOrderId.substring(0,24),OnLineListener);
        }else {

            Utils.getInstances().payOnline(mContext, i, "170522582109", sOrderId, OnLineListener);
        }

//        Utils.getInstances().payOnline(mContext, i, "0",sOrderId,OnLineListener);


    }
    protected  void showPayResultOnLine(final String result) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {

                promptDialog.getAlertDefaultBuilder().touchAble(false);
                promptDialog.showWarnAlert(result,new PromptButton("确定",null));

            }
        });
    }

    @Override
    public void paySuccess() {
//        svp.showSuccessWithStatus("充值成功");
        promptDialog.showSuccess("充值成功");
        paymoreAccount.setText(ProjectApplication.user.getAccount() + "");
        Intent intent = new Intent("com.yuyou.account");
        sendBroadcast(intent);
    }

    @Override
    public void payFail() {
//        svp.showSuccessWithStatus("充值失败，请稍候重试");
        promptDialog.showError("充值失败，请稍候重试");
    }

    @Override
    public void getListFail() {
//        svp.showErrorWithStatus("获取失败，请稍后重试");
        promptDialog.showError("获取失败，请稍后重试");
        titleBack.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1500);
    }


    @OnClick(R.id.title_back)
    public void onViewClicked() {
        finish();
    }
}
