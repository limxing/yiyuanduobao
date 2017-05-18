package com.yuyou.yiyuanduobao.welcome;

import android.app.Activity;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import me.leefeng.library.utils.ToastUtils;

/**
 * @author FengTing
 * @date 2017/04/25 10:39:52
 */
public class WelcomePresenter implements WelcomePreInterface {
    private WelcomeView welcomeView;


    private final String course = "course.json";
    public WelcomePresenter(WelcomeView welcomeView) {
        this.welcomeView = welcomeView;
//        File couseFile = new File(((Activity) welcomeView).getCacheDir(), course);
//        welcomeView.showToast(couseFile.exists()+"");
//        try {
//            FileOutputStream out = new FileOutputStream(
//                    new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "course.json"));
//            FileInputStream in = new FileInputStream(couseFile);
//            int byteread = 0; // 读取的字节数
//            byte[] buffer = new byte[1024];
//            while ((byteread = in.read(buffer)) != -1) {
//                out.write(buffer, 0, byteread);
//            }
//            out.flush();
//            out.close();
//            in.close();
//        } catch (FileNotFoundException e) {
//            welcomeView.showToast(e.getMessage());
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void destory() {
        welcomeView = null;
    }
}
