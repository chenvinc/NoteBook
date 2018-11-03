package com.example.chenm.notebook.utils;

import com.example.chenm.notebook.model.Record;
import com.example.chenm.notebook.model.RecordsForShow;
import com.example.chenm.notebook.model.User;
import com.example.chenm.notebook.model.WithPeople;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenhongyu
 * @Date 2018/9/8
 * @Time 13:37
 * @Version 1.0
 * @Description 数据库工具类
 */
public class DataBaseUtils {

    private static DataBaseUtils dataBaseUtils;

    public static DataBaseUtils getInstance() {
        if (dataBaseUtils == null) {
            synchronized (DataBaseUtils.class){
                if (dataBaseUtils == null) {
                    dataBaseUtils = new DataBaseUtils();
                }
            }
        }
        return dataBaseUtils;
    }

    public List<User> selectAllUser(){
        return LitePal.findAll(User.class);
    }

    public List<RecordsForShow> select10Records(){
        List<RecordsForShow> records = new ArrayList<>();
        List<Record> recordList = LitePal.order("id desc").find(Record.class,true);
        for (Record record :recordList){
            RecordsForShow recordsForShow =  new RecordsForShow();
            recordsForShow.setId(record.getId());
            recordsForShow.setBuyer(selectUserById(record.getBuyerId()));
            recordsForShow.setIsCheck(record.getIsCheck());
            recordsForShow.setPrice(record.getPrice());
            recordsForShow.setThing(record.getThing());
            recordsForShow.setTime(record.getTime());
            recordsForShow.setUsers(selectUsersByIds(record.getWithPeopleList()));
            records.add(recordsForShow);
        }
        return records;
    }

    private User selectUserById(int id){
        return LitePal.find(User.class,id);
    }

    private List<User> selectUsersByIds(List<WithPeople> withPeoples){
        List<User> users = new ArrayList<>();
        for (WithPeople withPeople : withPeoples){
            users.add(selectUserById(withPeople.getWithPeopleId()));
        }
        return users;
    }

    public List<RecordsForShow> selectAllUnsettlementRecord(){
        String unCheck = "0";
        List<RecordsForShow> records = new ArrayList<>();
        List<Record> recordList = LitePal.where("isCheck = ?",unCheck).find(Record.class,true);
        for (Record record :recordList){
            RecordsForShow recordsForShow =  new RecordsForShow();
            recordsForShow.setId(record.getId());
            recordsForShow.setBuyer(selectUserById(record.getBuyerId()));
            recordsForShow.setIsCheck(record.getIsCheck());
            recordsForShow.setPrice(record.getPrice());
            recordsForShow.setThing(record.getThing());
            recordsForShow.setTime(record.getTime());
            recordsForShow.setUsers(selectUsersByIds(record.getWithPeopleList()));
            records.add(recordsForShow);
        }
        return records;
    }

    public boolean saveRecord(RecordsForShow recordsForShow){
        Record record = new Record();
        for (User user : recordsForShow.getUsers()){
            WithPeople people = new WithPeople();
            people.setWithPeopleId(user.getId());
            people.save();
            record.getWithPeopleList().add(people);
        }
        record.setBuyerId(recordsForShow.getBuyer().getId());
        record.setThing(recordsForShow.getThing());
        record.setTime(recordsForShow.getTime());
        record.setIsCheck(recordsForShow.getIsCheck());
        record.setPrice(recordsForShow.getPrice());
        return record.save();
    }

    public void deleteRecord(int recordId){
        LitePal.delete(Record.class,recordId);
    }
}
