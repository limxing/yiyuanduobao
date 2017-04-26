package com.yuyou.yiyuanduobao.login;

/**
 * @author FengTing
 * @date 2017/04/25 10:52:05
 */
public interface LoginPreInterface {
     void destory();

    void getPhoneConfirmNum(String num);
    void getUserInfo(String num);
}
