package com.yuyou.yiyuanduobao;

import android.content.Intent;


import com.orm.SugarApp;
import com.unicom.shield.UnicomApplicationWrapper;
import com.yuyou.yiyuanduobao.bean.BuyData;
import com.yuyou.yiyuanduobao.bean.Course;
import com.yuyou.yiyuanduobao.bean.User;
import com.yuyou.yiyuanduobao.download.DownLoadService;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.smssdk.SMSSDK;

/**
 * @author FengTing
 * @date 2017/04/24 16:00:40
 */
public class ProjectApplication extends UnicomApplicationWrapper {
    public static User user;
    public static List<Course> cList;
    private static ProjectApplication sugarContext;
    public static List<BuyData> buyList;

    @Override
    public void onCreate() {
        super.onCreate();

        sugarContext=this;
        startService(new Intent(this, DownLoadService.class));
        //后端云
        Bmob.initialize(this, "b8f0593656c47a6539e7f7253ecf4d37");
        //短信验证码
//        SMSSDK.initSDK(this, "1d50a38c893be", "3c3903461ca3c72aa8e8e9d01c42489c");
        SMSSDK.initSDK(this, "189b617b10eb4", "bbdaa1208d663cbc3b8b4f628ef39fd8");
    }
    @Override
    public void onTerminate() {
//        SugarContext.terminate();
        super.onTerminate();
    }
}
