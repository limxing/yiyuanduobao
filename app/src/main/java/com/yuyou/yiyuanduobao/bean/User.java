package com.yuyou.yiyuanduobao.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by FengTing on 2017/4/25.
 */

public class User extends BmobObject {
    String username;
    String password;
    String phone;
    Long account;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getAccount() {
        return account;
    }

    public void setAccount(Long account) {
        this.account = account;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
