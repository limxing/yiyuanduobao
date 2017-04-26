package com.yuyou.yiyuanduobao.main;


import com.yuyou.yiyuanduobao.bean.User;

/**
 * @author FengTing
 * @date 2017/04/24 16:00:40
 */
public interface MainView {
    void payView(String sOrderId, String sVacCode);

    void showToast(String msg);

    void showLoading(String msg);

    void svpDismiss();

    void loginFail();

    void loginSuccess();

    void updateCourseSuccess();

    void showErrorWithStatus(String msg);

    void setAdapter(VideoAdapter adapter);

    void openPlayer(int position);

    void buySuccess(int position);
}
