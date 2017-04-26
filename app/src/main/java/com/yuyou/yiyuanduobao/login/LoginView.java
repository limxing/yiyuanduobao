package com.yuyou.yiyuanduobao.login;


/**
 * @author FengTing
 * @date 2017/04/25 10:52:05
 */
public interface LoginView {
    void showSvp(String msg);

    void getPhoneConfirmFail(String message);

    void loginFail();

    void loginSuccess();
}
