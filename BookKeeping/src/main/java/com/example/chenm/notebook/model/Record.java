package com.example.chenm.notebook.model;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenm
 */
public class Record extends LitePalSupport{

    private int id;
    private double price;
    private String thing;
    private int buyerId;
    private String time;
    private String isCheck;
    private List<WithPeople> withPeopleList = new ArrayList<WithPeople>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getThing() {
        return thing;
    }

    public void setThing(String thing) {
        this.thing = thing;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(String isCheck) {
        this.isCheck = isCheck;
    }

    public List<WithPeople> getWithPeopleList() {
        return withPeopleList;
    }

    public void setWithPeopleList(List<WithPeople> withPeopleList) {
        this.withPeopleList = withPeopleList;
    }
}
