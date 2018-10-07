package com.example.chenm.notebook.activity;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.example.chenm.notebook.R;
import com.example.chenm.notebook.adapter.RecordRecyclerAdapter;
import com.example.chenm.notebook.model.RecordsForShow;
import com.example.chenm.notebook.model.Settlement;
import com.example.chenm.notebook.utils.CommonUtils;
import com.example.chenm.notebook.utils.DataBaseUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author chenm
 */
public class MainActivity extends Activity implements OnItemSwipeListener {

    @BindView(R.id.tv_available_amount)
    TextView tvAvailableAmount;
    @BindView(R.id.settlement_date)
    TextView settlementDate;
    @BindView(R.id.please_choose_time)
    TextView pleaseChooseTime;
    @BindView(R.id.line_no_records)
    LinearLayout lineNoRecords;
    @BindView(R.id.record_list)
    RecyclerView recordList;
    private List<RecordsForShow> records = new ArrayList<>();
    private TextView start;
    private TextView end;
    private ImageView startView;
    private ImageView endView;

    private TimePickerView pvCustomTime;
    private int a = 1;
    private String time = "";
    private String startTime = "";
    private String endTime = "";

    private RecordRecyclerAdapter recordRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Utils.init(getApplication());
        recordRecyclerAdapter = new RecordRecyclerAdapter(records);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        initData();
        setAdapter(recordRecyclerAdapter);
    }

    private void initView() {
        tvAvailableAmount.setText(SPUtils.getInstance().getString("settlement_price"));
        settlementDate.setText(SPUtils.getInstance().getString("settlement_time"));
    }

    private void initData() {
        if (records == null) {
            records = new ArrayList<>();
        }
        records.clear();
        records.addAll(DataBaseUtils.selectAllRecord());
        if (records.size() == 0) {
            lineNoRecords.setVisibility(View.VISIBLE);
        } else {
            lineNoRecords.setVisibility(View.GONE);
        }
        recordRecyclerAdapter.setNewData(records);
    }

    private void getListData() {
        if (records == null) {
            initData();
        }
        records.clear();
        records.addAll(DataBaseUtils.selectLimitTimeRecord(startTime,endTime));
        if (records.size() == 0) {
            lineNoRecords.setVisibility(View.VISIBLE);
        } else {
            lineNoRecords.setVisibility(View.GONE);
        }
        recordRecyclerAdapter.setNewData(records);
    }

    private void setAdapter(RecordRecyclerAdapter adapter) {
        ItemDragAndSwipeCallback callback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recordList);
        adapter.enableSwipeItem();
        adapter.setOnItemSwipeListener(this);
        recordList.setAdapter(adapter);
    }




    @OnClick({R.id.users, R.id.btn_settlement, R.id.btn_new_record, R.id.please_choose_time, R.id.tv_available_amount})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.users:
                UsersActivity.launch(MainActivity.this);
                break;
            case R.id.btn_settlement:
                SettlementActivity.launch(MainActivity.this,SettlementActivity.SETTLEMENT);
                break;
            case R.id.btn_new_record:
                NewRecordActivity.launch(MainActivity.this);
                break;
            case R.id.please_choose_time:
                initCustomTimePicker();
                pvCustomTime.show();
                break;
            case R.id.tv_available_amount:
                SettlementActivity.launch(MainActivity.this,SettlementActivity.CHECK_FOR_VIEW);
                break;
            default:
                break;
        }
    }

    private void initCustomTimePicker() {

        //系统当前时间
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        String year = CommonUtils.getYear();
        String mouth = CommonUtils.getMouth();
        String day = CommonUtils.getDay();

        int y = Integer.parseInt(year);
        int m = Integer.parseInt(mouth);
        int d = Integer.parseInt(day);

        startDate.set(y - 5, m - 1, d);
        endDate.set(y, m - 1, d);
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            //选中事件回调
            @Override
            public void onTimeSelect(Date date, View v) {
                if (a == 1) {
                    startTime = CommonUtils.getDate3(date.getTime());
                    start.setText(startTime);
                    if ("".equals(time)) {
                        time = time + startTime + "  --  ";
                    } else {
                        time = startTime + "  --  " + time;
                    }
                } else if (a == 2) {
                    endTime = CommonUtils.getDate3(date.getTime());
                    end.setText(endTime);
                    time = time + endTime;
                }
            }
        })
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.choose_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        start = (TextView) v.findViewById(R.id.startTime);
                        end = (TextView) v.findViewById(R.id.endTime);
                        startView = (ImageView) v.findViewById(R.id.startImage);
                        endView = (ImageView) v.findViewById(R.id.endImage);
                        TextView sure = (TextView) v.findViewById(R.id.sure);
                        TextView noSure = (TextView) v.findViewById(R.id.nosure);
                        start.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                a = 1;
                                start.setTextColor(0xff006699);
                                end.setTextColor(0xffd3d3d3);
                                startView.setBackground(getResources().getDrawable(R.drawable.blue_5dp_line));
                                endView.setBackground(getResources().getDrawable(R.drawable.gray_5dp_line));
                            }
                        });
                        end.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                a = 2;
                                end.setTextColor(0xff006699);
                                start.setTextColor(0xffd3d3d3);
                                endView.setBackground(getResources().getDrawable(R.drawable.blue_5dp_line));
                                startView.setBackground(getResources().getDrawable(R.drawable.gray_5dp_line));
                            }
                        });
                        startView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                a = 1;
                                start.setTextColor(0xff006699);
                                end.setTextColor(0xffd3d3d3);
                                startView.setBackground(getResources().getDrawable(R.drawable.blue_5dp_line));
                                endView.setBackground(getResources().getDrawable(R.drawable.gray_5dp_line));
                            }
                        });
                        endView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                a = 2;
                                end.setTextColor(0xff006699);
                                start.setTextColor(0xffd3d3d3);
                                endView.setBackground(getResources().getDrawable(R.drawable.blue_5dp_line));
                                startView.setBackground(getResources().getDrawable(R.drawable.gray_5dp_line));
                            }
                        });
                        sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (a == 1) {
                                    if (!"".equals(startTime)) {
                                        time = "";
                                    }
                                    pvCustomTime.returnData();
                                    end.setTextColor(0xff006699);
                                    start.setTextColor(0xffd3d3d3);
                                    startView.setBackground(getResources().getDrawable(R.drawable.gray_5dp_line));
                                    endView.setBackground(getResources().getDrawable(R.drawable.blue_5dp_line));
                                    if (!"".equals(endTime)) {
                                        start.setTextColor(0xff006699);
                                        end.setTextColor(0xff006699);
                                        startView.setBackground(getResources().getDrawable(R.drawable.blue_5dp_line));
                                        endView.setBackground(getResources().getDrawable(R.drawable.blue_5dp_line));
                                        pvCustomTime.dismiss();
                                        pleaseChooseTime.setText(time);
                                        getListData();
                                    } else {
                                        a = 2;
                                    }
                                } else if (a == 2) {
                                    if (!"".equals(endTime)) {
                                        time = "";
                                    }
                                    pvCustomTime.returnData();
                                    start.setTextColor(0xff006699);
                                    end.setTextColor(0xffd3d3d3);
                                    startView.setBackground(getResources().getDrawable(R.drawable.blue_5dp_line));
                                    endView.setBackground(getResources().getDrawable(R.drawable.gray_5dp_line));
                                    if (!"".equals(startTime)) {
                                        start.setTextColor(0xff006699);
                                        end.setTextColor(0xff006699);
                                        startView.setBackground(getResources().getDrawable(R.drawable.blue_5dp_line));
                                        endView.setBackground(getResources().getDrawable(R.drawable.blue_5dp_line));
                                        pvCustomTime.dismiss();
                                        pleaseChooseTime.setText(time);
                                        getListData();
                                    } else {
                                        a = 1;
                                    }
                                }
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

    @Override
    public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
    }

    @Override
    public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
    }

    @Override
    public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
        DataBaseUtils.deleteRecord(recordRecyclerAdapter.getData().get(pos).getId());
    }

    @Override
    public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
    }
}
