package com.yuyou.yiyuanduobao.main;

import com.yuyou.yiyuanduobao.bean.Course;

/**
 * @author FengTing
 * @date 2017/04/24 16:00:40
 */
public interface MainPreInterface {
     void destory();

    void pay(Course course);

    void login(String phone);

    boolean isBuy(int position);

    void getCourseList();

    void paySuccess(Course course);

    void buyWithGold(Course course, int money);

    void getPayifSuccess(String orderId, Course currentCurse);
}
