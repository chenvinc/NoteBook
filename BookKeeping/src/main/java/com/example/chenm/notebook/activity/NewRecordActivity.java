package com.example.chenm.notebook.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.ObjectUtils;
import com.example.chenm.notebook.R;
import com.example.chenm.notebook.model.RecordsForShow;
import com.example.chenm.notebook.model.SelectUser;
import com.example.chenm.notebook.model.User;
import com.example.chenm.notebook.utils.CommonUtils;
import com.example.chenm.notebook.utils.DataBaseUtils;
import com.example.chenm.notebook.utils.DialogDef;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author chenm
 */

public class NewRecordActivity extends Activity {

    /**
     * 返回
     */
    @BindView(R.id.iv_back)
    ImageView ivBack;
    /**
     * 标题
     */
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.time_date)
    TextView recordDate;
    @BindView(R.id.things_name)
    EditText recordThings;
    @BindView(R.id.payment_price)
    EditText recordPaymentAmount;
    @BindView(R.id.payment_people)
    TextView recordPaymentPeople;
    @BindView(R.id.with_people)
    TextView recordWithPeople;

    Unbinder unbinder;

    private TimePickerView pvCustomTime;
    private String chooseTime = "";

    private List<SelectUser> selectUsers = new ArrayList<>();
    private SelectUser paymentPeople;
    private List<SelectUser> withPeoples = new ArrayList<>();

    public static void launch(Context context) {
        context.startActivity(new Intent(context, NewRecordActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);
        unbinder = ButterKnife.bind(this);
        tvTitle.setText("新增记录");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void init() {
        for (User user : DataBaseUtils.selectAllUser()) {
            SelectUser selectUser = new SelectUser();
            selectUser.setCheck(false);
            selectUser.setUser(user);
            selectUsers.add(selectUser);
        }
    }

    @OnClick({R.id.time_date, R.id.commit_btn, R.id.with_people, R.id.payment_people})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.time_date:
                initTimePicker();
                pvCustomTime.show();
                break;
            case R.id.payment_people:
                DialogDef dialogChoosePaymentPeople = new DialogDef(this, selectUsers, false);
                dialogChoosePaymentPeople.setConfirmButtonOnclickListener(new DialogDef.OnConfirmButtonOnclickListener() {
                    @Override
                    public void onConfirmButtonClick(List<SelectUser> list) {
                        for (SelectUser user : list) {
                            if (user.getCheck()) {
                                paymentPeople = user;
                                recordPaymentPeople.setText(user.getUser().getUserName());
                            }
                        }
                    }
                });
                dialogChoosePaymentPeople.show();
                break;
            case R.id.with_people:
                DialogDef dialogChooseWithPeoples = new DialogDef(this, selectUsers);
                dialogChooseWithPeoples.setConfirmButtonOnclickListener(new DialogDef.OnConfirmButtonOnclickListener() {
                    @Override
                    public void onConfirmButtonClick(List<SelectUser> list) {
                        StringBuffer withPeopleName = new StringBuffer();
                        withPeoples.clear();
                        for (SelectUser user : list) {
                            if (user.getCheck()) {
                                withPeoples.add(user);
                                withPeopleName.append(user.getUser().getUserName() + "; ");
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

    private void commit() {
        RecordsForShow record = new RecordsForShow();
        if (recordDate.getText() == null) {
            Toast.makeText(this, "未输入日期", Toast.LENGTH_SHORT).show();
            return;
        }
        if (recordThings.getText() == null) {
            Toast.makeText(this, "未输入明细", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ObjectUtils.isEmpty(paymentPeople)) {
            Toast.makeText(this, "未输入付款人", Toast.LENGTH_SHORT).show();
            return;
        }
        if (withPeoples.size() == 0) {
            Toast.makeText(this, "未选择关系人", Toast.LENGTH_SHORT).show();
            return;
        }
        if (recordPaymentAmount.getText() == null) {
            Toast.makeText(this, "未输入金额", Toast.LENGTH_SHORT).show();
            return;
        }

        List<User> withPeopleList = new ArrayList<>();
        for (SelectUser selectUser : withPeoples) {
            withPeopleList.add(selectUser.getUser());
        }
        record.setUsers(withPeopleList);
        record.setBuyer(paymentPeople.getUser());
        record.setTime(recordDate.getText().toString());
        record.setThing(recordThings.getText().toString());
        record.setPrice(Double.parseDouble(recordPaymentAmount.getText().toString()));
        record.setIsCheck("0");
        if (DataBaseUtils.saveRecord(record)) {
            finish();
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initTimePicker() {
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
