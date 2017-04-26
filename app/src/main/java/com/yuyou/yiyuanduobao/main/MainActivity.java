package com.yuyou.yiyuanduobao.main;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.unicom.xiaowo.Pay;
import com.yuyou.yiyuanduobao.BaseActivity;
import com.yuyou.yiyuanduobao.ProjectApplication;
import com.yuyou.yiyuanduobao.R;
import com.yuyou.yiyuanduobao.newplay.NewPlayerActivity;

import butterknife.BindView;
import butterknife.OnClick;
import me.leefeng.library.utils.SharedPreferencesUtil;

/**
 * @author FengTing
 * @date 2017/04/24 16:00:40
 */
public class MainActivity extends BaseActivity implements MainView, AdapterView.OnItemClickListener {
    @BindView(R.id.main_bt)
    Button mainBt;
    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.title_tv_right)
    TextView titleTvRight;
    @BindView(R.id.mian_list)
    ListView mianList;
    private MainPresenter presenter;
    private String phone;

    @Override
    protected void initData() {
        phone = SharedPreferencesUtil.getStringData(mContext, "phone", null);
        presenter = new MainPresenter(this);
        if (ProjectApplication.user == null) {
            svp.showLoading("正在登录");
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

    @Override
    public void showToast(final String msg) {

        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showLoading(String msg) {
        svp.showLoading(msg);
    }

    @Override
    public void svpDismiss() {
        svp.dismiss();
    }

    @Override
    public void loginFail() {
        svp.showInfoWithStatus("登录失败，进入离线模式");
    }

    @Override
    public void loginSuccess() {
//        svp.dismiss();
        initUser();
    }

    @Override
    public void updateCourseSuccess() {
        svp.showSuccessWithStatus("课程更新完成");
    }

    @Override
    public void showErrorWithStatus(String msg) {
        svp.showErrorWithStatus(msg);
    }

    @Override
    public void setAdapter(VideoAdapter adapter) {
        mianList.setAdapter(adapter);
        mianList.setOnItemClickListener(this);
    }

    @Override
    public void openPlayer(int position) {
        Intent intent = new Intent(mContext, NewPlayerActivity.class);
        intent.putExtra("course", position);
        startActivity(intent);
    }

    /**
     * 购买成功
     * @param position
     */
    @Override
    public void buySuccess(int position) {
        initUser();
    }

    private void initUser() {

        Long account = ProjectApplication.user.getAccount();
        if (account == null) {
            titleTvRight.setText(0 + "");
        } else {
            titleTvRight.setText(account.intValue() + "");
        }
        svp.showLoading("正在获取购买信息");
        presenter.getCourseList();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
presenter.isBuy(position);
    }
}
