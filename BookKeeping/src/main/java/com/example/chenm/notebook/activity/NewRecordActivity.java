package com.example.chenm.notebook.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.ObjectUtils;
import com.example.chenm.notebook.R;
import com.example.chenm.notebook.model.RecordsForShow;
import com.example.chenm.notebook.utils.CommonUtils;
import com.example.chenm.notebook.utils.DataBaseUtils;
import com.example.chenm.notebook.utils.DialogDef;
import com.example.chenm.notebook.model.Record;
import com.example.chenm.notebook.model.SelectUser;
import com.example.chenm.notebook.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author chenm
 */

public class NewRecordActivity extends Activity implements View.OnClickListener{

    /**
     * 导航条
     */
    public RelativeLayout relBar;
    /**
     * 返回
     */
    public ImageView ivBack;
    /**
     * 标题
     */
    public TextView tvTitle;

    private TextView recordDate;
    private EditText recordThings;
    private EditText recordPaymentAmount;
    private TextView recordPaymentPeople;
    private TextView recordWithPeople;
    private TextView commitBtn;

    private RelativeLayout successLayout;

    private TimePickerView pvCustomTime;
    private String chooseTime = "";

    private List<SelectUser> selectUsers = new ArrayList<>();
    private SelectUser paymentPeople;
    private List<SelectUser> withPeoples = new ArrayList<>();

    public static void launch(Context context){
        context.startActivity(new Intent(context,NewRecordActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);
        initActionBarView();
        initView();
    }

    private void initActionBarView(){
        relBar = findViewById(R.id.rel_bar);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("新增记录");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView(){
        recordDate = findViewById(R.id.time_date);
        recordThings = findViewById(R.id.things_name);
        recordPaymentAmount = findViewById(R.id.payment_price);
        recordPaymentPeople = findViewById(R.id.payment_people);
        recordWithPeople = findViewById(R.id.with_people);
        commitBtn = findViewById(R.id.commit_btn);
        successLayout = findViewById(R.id.insert_new_record_success);

        for (User user : DataBaseUtils.selectAllUser()){
            SelectUser selectUser = new SelectUser();
            selectUser.setCheck(false);
            selectUser.setUser(user);
            selectUsers.add(selectUser);
        }

        recordPaymentPeople.setOnClickListener(this);
        recordWithPeople.setOnClickListener(this);
        recordDate.setOnClickListener(this);
        commitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.time_date:
                initTimePicker();
                pvCustomTime.show();
                break;
            case R.id.payment_people:
                DialogDef dialogChoosePaymentPeople = new DialogDef(this,selectUsers,false);
                dialogChoosePaymentPeople.setConfirmButtonOnclickListener(new DialogDef.OnConfirmButtonOnclickListener() {
                    @Override
                    public void onConfirmButtonClick(List<SelectUser> list) {
                        for (SelectUser user :list){
                            if (user.getCheck()){
                                paymentPeople = user;
                                recordPaymentPeople.setText(user.getUser().getUserName());
                            }
                        }
                    }
                });
                dialogChoosePaymentPeople.show();
                break;
            case R.id.with_people:
                DialogDef dialogChooseWithPeoples = new DialogDef(this,selectUsers);
                dialogChooseWithPeoples.setConfirmButtonOnclickListener(new DialogDef.OnConfirmButtonOnclickListener() {
                    @Override
                    public void onConfirmButtonClick(List<SelectUser> list) {
                        StringBuffer withPeopleName = new StringBuffer();
                        for (SelectUser user : list){
                            if (user.getCheck()){
                                withPeoples.add(user);
                                withPeopleName.append(user.getUser().getUserName()+"; ");
                            }
                        }
                        recordWithPeople.setText(withPeopleName);
                    }
                });
                dialogChooseWithPeoples.show();
                break;
            case R.id.commit_btn:
                commit();
            default:
                break;
        }
    }

    private void commit(){
        RecordsForShow record = new RecordsForShow();
        if (recordDate.getText() == null){
            Toast.makeText(this,"未输入日期",Toast.LENGTH_SHORT).show();
            return;
        }
        if (recordThings.getText() == null){
            Toast.makeText(this,"未输入明细",Toast.LENGTH_SHORT).show();
            return;
        }
        if (ObjectUtils.isEmpty(paymentPeople)){
            Toast.makeText(this,"未输入付款人",Toast.LENGTH_SHORT).show();
            return;
        }
        if (withPeoples.size() == 0){
            Toast.makeText(this,"未选择关系人",Toast.LENGTH_SHORT).show();
            return;
        }if (recordPaymentAmount.getText() == null){
            Toast.makeText(this,"未输入金额",Toast.LENGTH_SHORT).show();
            return;
        }

        List<User> withPeopleList = new ArrayList<>();
        for (SelectUser selectUser : withPeoples){
            withPeopleList.add(selectUser.getUser());
        }
        record.setUsers(withPeopleList);
        record.setBuyer(paymentPeople.getUser());
        record.setTime(recordDate.getText().toString());
        record.setThing(recordThings.getText().toString());
        record.setPrice(Double.parseDouble(recordPaymentAmount.getText().toString()));
        record.setIsCheck("0");
        if (DataBaseUtils.saveRecord(record)){
            finish();
        }else {
            Toast.makeText(this,"保存失败",Toast.LENGTH_SHORT).show();
        }
    }

    private void initTimePicker(){
        //系统当前时间
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        final Calendar chooseDate = Calendar.getInstance();

        String year = CommonUtils.getYear();
        String mouth = CommonUtils.getMouth();
        String day = CommonUtils.getDay();

        int y = Integer.parseInt(year);
        int m = Integer.parseInt(mouth);
        int d = Integer.parseInt(day);

        chooseDate.set(y - 5, m - 1, d);
        startDate.set(y - 5, m - 1, d);
        endDate.set(y, m - 1, d);
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            //选中事件回调
            @Override
            public void onTimeSelect(Date date, View v) {
                chooseTime = CommonUtils.getDate3(date.getTime());
            }
        })
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.choose_time2, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        TextView sure = (TextView) v.findViewById(R.id.sure);
                        TextView noSure = (TextView) v.findViewById(R.id.nosure);
                        sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!"".equals(chooseTime)) {
                                    chooseTime = "";
                                }
                                pvCustomTime.returnData();
                                pvCustomTime.dismiss();
                                recordDate.setText(chooseTime);
                                }
                        });
                        noSure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .setContentTextSize(23)
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "")
                .setLineSpacingMultiplier(1.2f)
                .setTextXOffset(0, 0, 0, 40, 0, -40)
                //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isCenterLabel(false)
                .setDividerColor(0xFF24AD9D)
                .build();
    }
}
