package com.yuyou.yiyuanduobao.welcome;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyou.yiyuanduobao.BaseActivity;
import com.yuyou.yiyuanduobao.BuildConfig;
import com.yuyou.yiyuanduobao.R;
import com.yuyou.yiyuanduobao.login.LoginActivity;
import com.yuyou.yiyuanduobao.main.MainActivity;

import java.util.List;

import butterknife.BindView;
import me.leefeng.library.Permission.EasyPermissions;
import me.leefeng.library.utils.SharedPreferencesUtil;
import me.leefeng.library.utils.StringUtils;

/**
 * @author FengTing
 * @@date 2017/04/25 10:39:52
 */

public class WelcomeActivity extends BaseActivity implements WelcomeView, EasyPermissions.PermissionCallbacks {
    private static final int RC_PERM = 10000;
    @BindView(R.id.welcome_bottom)
    TextView welcomeBottom;
    @BindView(R.id.welcome_splash)
    ImageView mainSplash;
    private WelcomePresenter presenter;
    private String phone;

    @Override
    protected void initData() {
        phone = SharedPreferencesUtil.getStringData(mContext, "phone", null);
        presenter = new WelcomePresenter(this);
        welcomeBottom.setText(getResources().getString(R.string.app_name) + " V " + BuildConfig.VERSION_NAME);
        welcomeBottom.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPermission(new CheckPermListener() {
                    @Override
                    public void superPermission() {
                        permission();
                    }
                }, "需要权限处理", Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE);

            }
        }, 3000);
    }


    private void permission() {
        if (StringUtils.isEmpty(phone)){
            mainSplash.setVisibility(View.VISIBLE);
            mainSplash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;
                    if (StringUtils.isEmpty(phone)) {
                        intent = new Intent(mContext, LoginActivity.class);
                    } else {
                        intent = new Intent(mContext, MainActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }
            });
        }else {


            Intent intent = null;
            if (StringUtils.isEmpty(phone)) {
                intent = new Intent(mContext, LoginActivity.class);
            } else {
                intent = new Intent(mContext, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }
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


    /**
     * 权限回调接口
     */
    private CheckPermListener mListener;

    /**
     * 检查权限
     *
     * @param listener  全县坚挺
     * @param resString 全县提示
     * @param mPerms    全县内容
     */
    public void checkPermission(CheckPermListener listener, String resString, String... mPerms) {
        mListener = listener;
        if (EasyPermissions.hasPermissions(this, mPerms)) {
            if (mListener != null)
                mListener.superPermission();
        } else {
            EasyPermissions.requestPermissions(this, resString,
                    RC_PERM, mPerms);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (mListener != null) {
            mListener.superPermission();//同意了全部权限的回调


        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        finish();
    }
}
