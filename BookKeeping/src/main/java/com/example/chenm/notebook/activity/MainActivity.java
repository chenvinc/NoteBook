package com.example.chenm.notebook.activity;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.example.chenm.notebook.R;
import com.example.chenm.notebook.adapter.RecordRecyclerAdapter;
import com.example.chenm.notebook.model.Record;
import com.example.chenm.notebook.model.RecordsForShow;
import com.example.chenm.notebook.utils.CommonUtils;
import com.example.chenm.notebook.utils.DataBaseUtils;

import org.litepal.LitePal;

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
public class MainActivity extends Activity implements OnItemSwipeListener{

    @BindView(R.id.tv_available_amount)
    TextView tvAvailableAmount;
    @BindView(R.id.settlement_date)
    TextView settlementDate;
    @BindView(R.id.line_no_records)
    LinearLayout lineNoRecords;
    @BindView(R.id.record_list)
    RecyclerView recordList;
    private List<RecordsForShow> records = new ArrayList<>();

    private RecordRecyclerAdapter recordRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Utils.init(getApplication());
        LitePal.deleteDatabase("bookkeeping");
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
        records.addAll(DataBaseUtils.getInstance().select10Records());
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




    @OnClick({R.id.users, R.id.btn_settlement, R.id.btn_new_record, R.id.tv_available_amount})
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
            case R.id.tv_available_amount:
                SettlementActivity.launch(MainActivity.this,SettlementActivity.CHECK_FOR_VIEW);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
    }

    @Override
    public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
    }

    @Override
    public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
        DataBaseUtils.getInstance().deleteRecord(recordRecyclerAdapter.getData().get(pos).getId());
    }

    @Override
    public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
    }
}
