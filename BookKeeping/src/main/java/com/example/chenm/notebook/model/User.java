package com.example.chenm.notebook.model;

import org.litepal.crud.LitePalSupport;

/**
 * @author chenm
 */
public class User extends LitePalSupport{

    private int id;
    private String userName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
