package com.yuyou.yiyuanduobao.main;

/**
 * @author FengTing
 * @date 2017/04/24 16:00:40
 */
public interface MainPreInterface {
     void destory();

    void pay();

    void login(String phone);

    void isBuy(int position);

    void getCourseList();
}
