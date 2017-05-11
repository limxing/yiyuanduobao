package com.yuyou.yiyuanduobao.bean;

import com.litesuits.orm.db.annotation.Table;

import cn.bmob.v3.BmobObject;

/**
 * Created by FengTing on 2017/4/26.
 */

public class BuyData  extends BmobObject {
    private String courseid;
    private User user;


    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
