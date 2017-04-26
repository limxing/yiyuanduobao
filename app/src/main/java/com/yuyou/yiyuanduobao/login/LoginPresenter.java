package com.yuyou.yiyuanduobao.login;

import com.alibaba.fastjson.JSON;
import com.yuyou.yiyuanduobao.ProjectApplication;
import com.yuyou.yiyuanduobao.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import me.leefeng.library.utils.LogUtils;

/**
 * @author FengTing
 * @date 2017/04/25 10:52:05
 */
public class LoginPresenter implements LoginPreInterface {
    private LoginView loginView;
    private String phone="18514528236";

    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;
        initMsm();
    }

    private void initMsm() {
        EventHandler eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, final Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        LogUtils.i("提交成功");
                        getUserInfo(phone);
                        //提交验证码成功

                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        LogUtils.i("下发成功");
                        loginView.showSvp("验证码下发成功");
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {
                     String message=null;
                    try {
                        message = JSON.parseObject(((Throwable) data).getMessage()).getString("detail");
                    }catch (Exception e){
                        message="网络异常，请稍后重试";
                    }
                    loginView.getPhoneConfirmFail(message);
                    ((Throwable) data).printStackTrace();
                    LogUtils.i(getClass(), ((Throwable) data).getMessage());

                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调

    }

    /**
     * 根据手机号获取个人信息
     */


    public void getUserInfo(String num) {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("phone", phone);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(final List<User> list, BmobException e) {
                if (e==null) {
                    if (list != null && list.size() > 0) {
                        ProjectApplication.user = list.get(0);
                        loginView.loginSuccess();
                    } else {
                        final User user = new User();
                        user.setPhone(phone);
                        user.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e==null) {
                                    ProjectApplication.user = user;
                                    loginView.loginSuccess();
                                }else{
                                    e.printStackTrace();
                                    loginView.loginFail();
                                }
                            }

                        });
                    }
                }else{
                    e.printStackTrace();
                    loginView.loginFail();
                }
            }
        });
    }

    @Override
    public void destory() {
        SMSSDK.unregisterAllEventHandler();
        loginView = null;
    }

    @Override
    public void getPhoneConfirmNum(String num) {
        phone = num;
        SMSSDK.getVerificationCode("86", num);
    }
}
