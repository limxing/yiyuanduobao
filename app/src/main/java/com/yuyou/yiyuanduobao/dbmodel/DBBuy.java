package com.yuyou.yiyuanduobao.dbmodel;

import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by FengTing on 2017/5/11.
 * https://www.github.com/limxing
 */
@Table("db_buy")
public class DBBuy {
    // 指定自增，每个对象需要有一个主键
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;
    // 非空字段
    @NotNull
    private String username;

    @NotNull
    private String courseid;

    public DBBuy(String username, String courseid) {
        this.username = username;
        this.courseid = courseid;
    }

    public DBBuy() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }
}
