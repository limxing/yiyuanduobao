package com.yuyou.yiyuanduobao;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;


import com.tencent.bugly.crashreport.CrashReport;
import com.unicom.shield.UnicomApplicationWrapper;
import com.unicom.shield.unipay;
import com.yuyou.yiyuanduobao.bean.BuyData;
import com.yuyou.yiyuanduobao.bean.Course;
import com.yuyou.yiyuanduobao.bean.PayType;
import com.yuyou.yiyuanduobao.bean.User;
import com.yuyou.yiyuanduobao.download.DownLoadService;

import java.lang.reflect.Method;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.smssdk.SMSSDK;
import me.leefeng.library.utils.LogUtils;

/**
 * @author FengTing
 * @date 2017/04/24 16:00:40
 */
public class ProjectApplication extends UnicomApplicationWrapper {
    public static User user;
    public static List<Course> cList;
    public static List<BuyData> buyList;
    public static List<PayType> payTypes;

    public static ProjectApplication application;

    public static boolean isDebug = true;
    public static Context attachContext;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.isOnline(!isDebug);
        LogUtils.i("Application===onCreat");

//        mApplication.onCreate();
        application=this;
        startService(new Intent(this, DownLoadService.class));
        //后端云
        Bmob.initialize(this, "b8f0593656c47a6539e7f7253ecf4d37");
        //短信验证码
        SMSSDK.initSDK(this, "1d50a38c893be", "3c3903461ca3c72aa8e8e9d01c42489c");//一元夺宝的
//        SMSSDK.initSDK(this, "189b617b10eb4", "bbdaa1208d663cbc3b8b4f628ef39fd8");//北大的

        CrashReport.initCrashReport(getApplicationContext(), "15da576048", isDebug);
//        if (isDebug)
    }

    @Override
    public void onTerminate() {
//        SugarContext.terminate();
        super.onTerminate();
    }


//
//    public static Application mApplication;

//

//    public ProjectApplication() {
//        LogUtils.i("Application===ProjectApplication()");
//    }
//
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        LogUtils.i("Application===attachBaseContext");
        application=this;
        attachContext=base;
    }
//
//    public void onConfigurationChanged(Configuration var1) {
//        super.onConfigurationChanged(var1);
//        LogUtils.i("Application===onConfigurationChanged");
//        if (mApplication!=null)
//        mApplication.onConfigurationChanged(var1);
//    }
//
//    public void onLowMemory() {
//        super.onLowMemory();
//        LogUtils.i("Application===onLowMemory");
//        if (mApplication!=null)
//        mApplication.onLowMemory();
//    }

}
