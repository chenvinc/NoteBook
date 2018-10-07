package com.example.chenm.notebook.model;

/**
 * @author chenhongyu
 * @Date 2018/9/15
 * @Time 20:39
 * @Version 1.0
 * @Description ${DESCRIPTION}
 */
public class SelectUser {
    private boolean isCheck;
    private User user;

    public boolean getCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
