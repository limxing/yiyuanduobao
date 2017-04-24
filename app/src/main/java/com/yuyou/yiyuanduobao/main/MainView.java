package com.yuyou.yiyuanduobao.main;


/**
 * @author FengTing
 * @date 2017/04/24 16:00:40
 */
public interface MainView {
    void payView(String sOrderId, String sVacCode);

    void showToast(String msg);

    void showLoading(String msg);
}
