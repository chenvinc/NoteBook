package com.example.chenm.notebook.activity;

import android.Manifest;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.example.chenm.notebook.R;
import com.example.chenm.notebook.adapter.RecordRecyclerAdapter;
import com.example.chenm.notebook.model.RecordsForShow;
import com.example.chenm.notebook.utils.DataBaseUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * @author chenm
 */
public class MainActivity extends FragmentActivity implements OnItemSwipeListener,BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.tv_available_amount)
    TextView tvAvailableAmount;
    @BindView(R.id.settlement_date)
    TextView settlementDate;
    @BindView(R.id.line_no_records)
    LinearLayout lineNoRecords;
    @BindView(R.id.record_list)
    RecyclerView recordList;
    @BindView(R.id.last_settlement_tipcontent)
    TextView lastSettlementTipContent;

    private List<RecordsForShow> records = new ArrayList<>();

    private RecordRecyclerAdapter recordRecyclerAdapter;
    private int singlePageItemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Utils.init(getApplication());
        recordRecyclerAdapter = new RecordRecyclerAdapter(records);
        new RxPermissions(this).requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(Permission permission) throws Exception {
                if (!permission.granted){
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        initData();
        setAdapter(recordRecyclerAdapter);
        cleanOldFileDir();
    }

    private void cleanOldFileDir(){
        String filePath = Environment.getExternalStorageDirectory() + "/notebook";
        if (FileUtils.isDir(filePath)){
            FileUtils.deleteDir(filePath);
        }
    }

    private void initView() {
        String lastSettlementPrice = SPUtils.getInstance().getString("settlement_price");
        String lastSettlementTime = SPUtils.getInstance().getString("settlement_time");
        if (!TextUtils.isEmpty(lastSettlementPrice) && !TextUtils.isEmpty(lastSettlementTime)) {
            tvAvailableAmount.setText(lastSettlementPrice);
            settlementDate.setText(lastSettlementTime);
            lastSettlementTipContent.setVisibility(View.VISIBLE);
        } else {
            lastSettlementTipContent.setVisibility(View.GONE);
        }
    }

    private void initData() {
        if (records == null) {
            records = new ArrayList<>();
        }
        records.clear();
        records.addAll(DataBaseUtils.getInstance().select10Records(0));
        if (records.size() == 0) {
            lineNoRecords.setVisibility(View.VISIBLE);
        } else {
            lineNoRecords.setVisibility(View.GONE);
            recordRecyclerAdapter.setNewData(records);
        }
    }

    private void setAdapter(RecordRecyclerAdapter adapter) {
        ItemDragAndSwipeCallback callback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recordList);
        adapter.enableSwipeItem();
        adapter.setOnItemSwipeListener(this);
        adapter.setOnLoadMoreListener(this,recordList);
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

    @Override
    public void onLoadMoreRequested() {
        singlePageItemCount+=10;
        List<RecordsForShow> recordsForShowList = DataBaseUtils.getInstance().select10Records(singlePageItemCount);
        if (recordsForShowList != null) {
            recordRecyclerAdapter.addData(recordsForShowList);
            if (recordsForShowList.size() < singlePageItemCount) {
                recordRecyclerAdapter.loadMoreEnd();
            } else {
                recordRecyclerAdapter.loadMoreComplete();
            }
        } else {
            recordRecyclerAdapter.loadMoreFail();
        }
    }
}
