package com.yuyou.yiyuanduobao.welcome;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.litesuits.orm.log.OrmLog;
import com.unicom.shield.unipay;
import com.yuyou.yiyuanduobao.BaseActivity;
import com.yuyou.yiyuanduobao.BuildConfig;
import com.yuyou.yiyuanduobao.ProjectApplication;
import com.yuyou.yiyuanduobao.R;
import com.yuyou.yiyuanduobao.bean.BuyData;
import com.yuyou.yiyuanduobao.bean.User;
import com.yuyou.yiyuanduobao.dbmodel.DBBuy;
import com.yuyou.yiyuanduobao.login.LoginActivity;
import com.yuyou.yiyuanduobao.main.MainActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import me.leefeng.library.utils.LogUtils;
import me.leefeng.library.utils.SharedPreferencesUtil;
import me.leefeng.library.utils.StringUtils;
import me.leefeng.library.utils.ToastUtils;
import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;
import pub.devrel.easypermissions.EasyPermissions;

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
        welcomeBottom.setText(getResources().getString(R.string.welcome_bottom) + " V " + BuildConfig.VERSION_NAME);
        welcomeBottom.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPermission(new CheckPermListener() {
                                    @Override
                                    public void superPermission() {
                                        permission();
                                    }
                                }, "需要权限处理", Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS);

            }
        }, 3000);

        List<DBBuy> list = ProjectApplication.liteOrm.query(DBBuy.class);
        OrmLog.i("leefeng", list);
        List<BuyData> buyDatas = new ArrayList<>();
        for (DBBuy dbBuy : list) {
            BuyData buyData = new BuyData();
            buyData.setCourseid(dbBuy.getCourseid());
            User user = new User();
            user.setPhone(dbBuy.getUsername());
            buyData.setUser(user);
            buyDatas.add(buyData);
        }
        ProjectApplication.buyList = buyDatas;
    }


    private void permission() {
//        if (ProjectApplication.mApplication == null)
//            initUnipay();

//        String s = Build.MODEL + "==" + Build.VERSION.RELEASE + "==" + Build.VERSION.SDK_INT;
        if (Build.VERSION.SDK_INT < 21) {
            PromptDialog promptDialog = new PromptDialog(this);
            promptDialog.getAlertDefaultBuilder().cancleAble(false);
            promptDialog.showWarnAlert("当前Android版本：" + Build.VERSION.RELEASE + "\n" +
                    "最低支持Android 5.0", new PromptButton("确定", new PromptButtonListener() {
                @Override
                public void onClick(PromptButton promptButton) {
                    finish();
                }
            }));
            return;
        }
//        ToastUtils.showLong(mContext, s);
        if (StringUtils.isEmpty(phone)) {
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
        } else {


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
//        mCountdownView.setText("计时");
//        mCountdownView.setTime(5000);
//        mCountdownView.star();
//
//        mCountdownView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                permission();
//            }
//        });
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
            ProjectApplication.mApplication = null;
            LogUtils.i("onPermissionsGranted");
            initUnipay();
//            finish();

//            ToastUtils.showLong(mContext,"正在初始化");
//            Intent intent = getBaseContext().getPackageManager()
//                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
//            PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
//            AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, restartIntent); // 1秒钟后重启应用
//            System.exit(0);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        finish();
    }

    //
    public static String mApplicationName = "com.unicom.xiaowo.inner.UnicomApplication";

    public void loadApplication(Context var1) {
        if (ProjectApplication.mApplication == null) {
            try {
                ClassLoader var2 = var1.getClassLoader();
                Class var3 = var2.loadClass(mApplicationName);
                ProjectApplication.mApplication = (Application) var3.newInstance();
                Method var4 = Application.class.getDeclaredMethod("attach", new Class[]{Context.class});
                var4.setAccessible(true);
                var4.invoke(ProjectApplication.mApplication, new Object[]{var1});
            } catch (Exception var5) {
                var5.printStackTrace();
            }
            ProjectApplication.mApplication.onCreate();
        }
    }

    protected void initUnipay() {
        if (ProjectApplication.mApplication == null) {
            unipay.install(ProjectApplication.application, ProjectApplication.attachContext);
            this.loadApplication(ProjectApplication.attachContext);
        }
    }


    @Override
    public void showToast(String message) {
        ToastUtils.showShort(mContext,message);
    }
}
