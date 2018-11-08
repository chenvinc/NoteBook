package com.example.chenm.notebook.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenhongyu
 * @Date 2018/10/4
 * @Time 17:40
 * @Version 1.0
 * @Description 结算保存数据
 */
public class Settlement{

    /**
     * 总结算金额
     */
    public double allAmount;
    /**
     * 被删除用户已付金额
     */
    public double deleteUserAmount;
    /**
     * 被删除用户应付金额
     */
    public double deleteUserPayment;
    /**
     * 被删除用户结算金额
     */
    public double deleteUserSettlement;

    public List<SettlementItem> dataShouldPay = new ArrayList<>();
    public List<SettlementItem> dataPayment = new ArrayList<>();
    public List<SettlementItem> dataSettlementAll = new ArrayList<>();
}
