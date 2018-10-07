package com.example.chenm.notebook.model;

import org.litepal.crud.LitePalSupport;

/**
 * @author chenhongyu
 * @Date 2018/9/23
 * @Time 16:08
 * @Version 1.0
 * @Description 账单记录关系人
 */
public class WithPeople extends LitePalSupport{

    private int withPeopleId;
    private Record record;

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public int getWithPeopleId() {
        return withPeopleId;
    }

    public void setWithPeopleId(int withPeopleId) {
        this.withPeopleId = withPeopleId;
    }
}
