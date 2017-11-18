package com.xk.kson.bean;

/**
 * Created by Administrator on 2017/2/19 0019.
 */

public class User {
    private int id;
    private String name;
    private String pwd;
    private boolean isBoy;

    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                ", isBoy=" + isBoy +
                '}';
    }
}