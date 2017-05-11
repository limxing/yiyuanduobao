package com.yuyou.yiyuanduobao.main;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.unicom.xiaowo.Pay;
import com.yuyou.yiyuanduobao.BaseActivity;
import com.yuyou.yiyuanduobao.ProjectApplication;
import com.yuyou.yiyuanduobao.R;
import com.yuyou.yiyuanduobao.bean.Course;
import com.yuyou.yiyuanduobao.login.LoginActivity;
import com.yuyou.yiyuanduobao.newplay.NewPlayerActivity;
import com.yuyou.yiyuanduobao.paymore.PaymoreActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.leefeng.lfrecyclerview.LFRecyclerView;
import me.leefeng.lfrecyclerview.OnItemClickListener;
import me.leefeng.library.utils.LogUtils;
import me.leefeng.library.utils.SharedPreferencesUtil;
import me.leefeng.library.utils.ToastUtils;
import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;
import me.leefeng.publicc.alertview.AlertView;

/**
 * @author FengTing
 * @date 2017/04/24 16:00:40
 */
public class MainActivity extends BaseActivity implements MainView, OnItemClickListener, LFRecyclerView.LFRecyclerViewListener, View.OnClickListener {
    private static final int LOGIN_REQUEST_CODE = 1000;
    @BindView(R.id.main_bt)
    Button mainBt;
    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.title_tv_right)
    TextView titleTvRight;
    @BindView(R.id.mian_list)
    LFRecyclerView mianList;
    @BindView(R.id.title_back)
    ImageView titleBack;
    private MainPresenter presenter;
    private String phone;
    private final String course = "course.json";
    private boolean isFirst;

    @Override
    protected void initData() {
        phone = SharedPreferencesUtil.getStringData(mContext, "phone", null);
        presenter = new MainPresenter(this);
        if (!new File(getCacheDir(), course).exists()) {
            isFirst = true;
            presenter.initUpdata();
            return;
        }

        if (ProjectApplication.user == null) {
//            svp.showLoading("正在登录");
            promptDialog.showLoading("正在登录");
            presenter.login(phone);
        } else {
            initUser();
        }
        presenter.initUpdata();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destory();
        presenter = null;
        ProjectApplication.user = null;
    }

    @Override
    protected void initView() {
        mianList.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL));
        mianList.setLoadMore(false);
        mianList.setLFRecyclerViewListener(this);
        mianList.setOnItemClickListener(this);
        titleTvRight.setOnClickListener(this);
        titleBack.setVisibility(View.GONE);

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void doReceive(Intent action) {
        if (action.getAction().equals("com.yuyou.account")) {
            Long account = ProjectApplication.user.getAccount();
            if (account == null) {
                titleTvRight.setText(0 + "");
            } else {
                titleTvRight.setText(account.intValue() + "");
            }
        }
    }


//    @OnClick(R.id.main_bt)
//    public void onViewClicked() {
//        Course course = new Course();
//        course.setName("C语言设计");
//        presenter.pay(course);
//    }

    @Override
    public void payView(final String sOrderId, final String sVacCode, final Course course) {
        LogUtils.i("orderId:" + sOrderId + "==vacCode:" + sVacCode);
//        svp.dismissImmediately();
        promptDialog.dismiss();
        Pay.getInstance().payChannel(mContext, "支付网络课程《" + course.getName() + "》", getString(R.string.company), sVacCode,
                "1 金币", "1.00", sOrderId, new Pay.UnipayPayResultListener() {

                    @Override
                    public void PayResult(String arg0, int arg1, int arg2, String arg3) {
                        LogUtils.i("联通返回结果：" + arg0 + "==" + arg1 + "==" + arg2 + "==" + arg3);
                        if (arg1 == 1) {
                            Toast.makeText(mContext, "支付请求已提交", Toast.LENGTH_LONG).show();
                            presenter.paySuccess(course);
                        } else if (arg1 == 2) {
                            if (arg2 == 2) {
                                new AlertDialog.Builder(mContext).setTitle("支付环境不安全")
                                        .setCancelable(false)
                                        .setMessage("请点击确定后，重新打开APP")
                                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = getBaseContext().getPackageManager()
                                                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                                PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                                                AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, restartIntent); // 1秒钟后重启应用
                                                System.exit(0);
                                            }
                                        })
                                        .show();
                            } else {
                                presenter.paySuccess(course);
//                                Toast.makeText(mContext, "支付成功", Toast.LENGTH_LONG).show();
                            }
                        } else if (arg1 == 3) {
                            promptDialog.showInfo("用户取消支付");
//                            Toast.makeText(mContext, "用户取消支付", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    @Override
    public void showToast(final String msg) {

        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showLoading(String msg) {
//        svp.showLoading(msg);
        promptDialog.showLoading(msg);
    }

    @Override
    public void svpDismiss() {
//        svp.dismiss();
        promptDialog.dismiss();
    }

    @Override
    public void loginFail() {
//        svp.showInfoWithStatus("登录失败，进入离线模式");
        promptDialog.showInfo("登录失败，进入离线模式");
    }

    @Override
    public void loginSuccess() {
//        svp.dismiss();
        initUser();
    }

    @Override
    public void updateCourseSuccess() {
//        svp.showSuccessWithStatus("课程更新完成");
        promptDialog.showSuccess("课程更新完成");
        if (isFirst) {
            isFirst = false;
            if (ProjectApplication.user == null) {
//                svp.showLoading("正在登录");
                promptDialog.showLoading("正在登录");
                presenter.login(phone);
            } else {
                initUser();
            }
        }
    }

    @Override
    public void showErrorWithStatus(String msg) {
//        svp.showErrorWithStatus(msg);
        promptDialog.showError(msg);

    }

    @Override
    public void setAdapter(VideoAdapter adapter) {
        mianList.setAdapter(adapter);

    }

    @Override
    public void openPlayer(int position) {
        Intent intent = new Intent(mContext, NewPlayerActivity.class);
        intent.putExtra("course", position);
        startActivity(intent);
    }

    /**
     * 购买成功
     *
     * @param
     */
    @Override
    public void buySuccess() {
        initUser();
    }

    @Override
    public void stopFresh(boolean b) {
        mianList.stopRefresh(b);
    }

    @Override
    public void showGoldDialog(final Course course, final int money) {
//        new AlertView("", "您将消费" + money + "金币购买" + course.getName(), "取消", new String[]{"确定"}, null, mContext, AlertView.Style.Alert,
//                new me.leefeng.publicc.alertview.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(Object o, int position) {
//                        if (position == 0) {
//                            svp.showLoading("正在支付");
//                            presenter.buyWithGold(course, money);
//                        }
//                    }
//                }).show();
        PromptButton confirm = new PromptButton("支付", new PromptButtonListener() {
            @Override
            public void onClick(PromptButton promptButton) {
//                svp.showLoading("正在支付");

                promptDialog.showLoading("正在支付");
                presenter.buyWithGold(course, money);
            }
        });
        confirm.setTextColor(getResources().getColor(R.color.colorAccent));
        confirm.setFocusBacColor(Color.parseColor("#FFEFD5"));
        confirm.setDismissAfterClick(false);
        promptDialog.showWarnAlert("您将消费" + money + "金币购买" + course.getName(),
                new PromptButton("取消", null), confirm);
//        new AlertDialog.Builder(mContext).setTitle("金币购买").setMessage( "您将消费" + money + "金币购买" + course.getName())
//                .setNegativeButton("取消",null)
//                .setIcon(R.mipmap.ic_launcher)
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        svp.showLoading("正在支付");
//                        presenter.buyWithGold(course, money);
//                    }
//                })
//                .show();
    }

    private void initUser() {

        Long account = ProjectApplication.user.getAccount();
        if (account == null) {
            titleTvRight.setText(0 + "");
        } else {
            titleTvRight.setText(account.intValue() + "");
        }
//        svp.showLoading("正在获取购买信息");
        promptDialog.showLoading("正在获取购买信息");
        presenter.getCourseList();
    }


    /**
     * RecycleView的点击
     *
     * @param position
     */
    @Override
    public void onClick(int position) {
        LogUtils.i("list click:" + position);
        if (ProjectApplication.user == null) {
            //去登陆
            goLogin();
        } else {
            presenter.isBuy(position);
        }
    }

    private void goLogin() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra("isBack", true);
        startActivityForResult(intent, LOGIN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.i("onActivityResult:" + requestCode + "==" + resultCode + "==");
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case LOGIN_REQUEST_CODE:
                    initUser();
                    break;
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * RecycleView的长安
     *
     * @param
     */
    @Override
    public void onLongClick(int po) {

    }

    /**
     * RecycleView的下拉刷新
     *
     * @param
     */
    @Override
    public void onRefresh() {
        if (isFirst) {
            presenter.initUpdata();
        } else if (ProjectApplication.user == null) {
            ToastUtils.showLong(mContext, "请先登录");
            goLogin();
            mianList.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mianList.stopRefresh(false);
                }
            }, 1000);
        } else {
            initUser();
        }

    }

    /**
     * RecycleView的上拉加载
     *
     * @param
     */
    @Override
    public void onLoadMore() {

    }


    /**
     * 按钮的点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_tv_right:
                if (ProjectApplication.user == null) {
                    ToastUtils.showLong(mContext, "请先登录");
                    goLogin();
                    return;
                }
                Intent intent = new Intent(mContext, PaymoreActivity.class);
                startActivity(intent);
                break;
        }
    }


    private long exitTime = 0;

    @Override
    public void onBackPressed() {
     boolean b=   promptDialog.onBackPressed();
        if (b)
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }

    }
}
