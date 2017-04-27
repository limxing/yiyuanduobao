package com.yuyou.yiyuanduobao.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.yuyou.yiyuanduobao.BaseActivity;
import com.yuyou.yiyuanduobao.R;
import com.yuyou.yiyuanduobao.main.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.smssdk.SMSSDK;
import me.leefeng.library.utils.SharedPreferencesUtil;
import me.leefeng.library.utils.StringUtils;
import me.leefeng.library.utils.ToastUtils;

/**
 * @author FengTing
 * @date 2017/04/25 10:52:05
 */
public class LoginActivity extends BaseActivity implements LoginView {
    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.title_tv_right)
    TextView titleTvRight;
    @BindView(R.id.login_id)
    MaterialEditText loginId;
    @BindView(R.id.login_pw)
    MaterialEditText loginPw;
    @BindView(R.id.login_phone)
    Button loginPhone;
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout2;
    private LoginPresenter presenter;
    private TimeCount timer;
    private boolean getPhoneConfirmNumSuccess;
    private String num;
    private boolean isBack;

    @Override
    protected void initData() {
        isBack = getIntent().getBooleanExtra("isBack", false);
        presenter = new LoginPresenter(this);
        timer =new TimeCount(60000, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
        presenter.destory();
        presenter = null;
    }

    @Override
    protected void initView() {
        titleName.setText("登录");
        titleBack.setVisibility(View.GONE);
        titleTvRight.setVisibility(View.INVISIBLE);
        if (isBack) {
            titleBack.setVisibility(View.VISIBLE);
            titleBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            });
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void doReceive(Intent action) {

    }

    /**
     * 点击登录
     *
     * @param view
     */
    public void login(View view) {
        //测试，直接登录
//        num="18514528236";
//        presenter.getUserInfo(num);

        if (StringUtils.isEmpty(num)) {
            ToastUtils.showShort(mContext, "请输入手机号");
            return;
        }
        if (!getPhoneConfirmNumSuccess) {
            ToastUtils.showShort(mContext, "请获取验证码");
            return;
        }
        String confirmNUm = loginPw.getText().toString().trim();
        if (StringUtils.isEmpty(confirmNUm)) {
            ToastUtils.showShort(mContext, "请输入验证码");
            return;
        }else{
            SMSSDK.submitVerificationCode("86", num, confirmNUm);
        }
        closeInput();
        svp.showLoading("正在登录");

    }

    /**
     * 获取验证码按钮
     *
     * @param view
     */
    public void getPhoneConfirmNum(View view) {
        num = loginId.getText().toString().trim();
        String telRegex = "[1][34578]\\d{9}";
        if (num.length() == 11 && num.matches(telRegex)) {
            closeInput();
            svp.showLoading("正在获取验证码");
            presenter.getPhoneConfirmNum(num);
        } else {
            ToastUtils.showLong(mContext, "手机格式错误");
        }
    }

    @Override
    public void showSvp(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                svp.showInfoWithStatus(msg);
                getPhoneConfirmNumSuccess = true;
                timer.start();
            }
        });
    }

    @Override
    public void getPhoneConfirmFail(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getPhoneConfirmNumSuccess = false;
                svp.showInfoWithStatus(message);
                loginPhone.setEnabled(true);
                loginPhone.setText("获取验证码");
//                task.cancel();
            }
        });


    }

    @Override
    public void loginFail() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                svp.showInfoWithStatus("登录失败，请稍候重试");
                loginPhone.setEnabled(true);
                loginPhone.setText("获取验证码");
//                task.cancel();
            }
        });
    }

    @Override
    public void loginSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SharedPreferencesUtil.saveStringData(mContext, "phone", num);
                if (isBack) {
                    setResult(RESULT_OK);
                } else {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            loginPhone.setEnabled(false);
            loginPhone.setText(millisUntilFinished / 1000 + "(s)");
        }

        @Override
        public void onFinish() {
            loginPhone.setText("获取验证码");
            loginPhone.setEnabled(true);

        }
    }
}
