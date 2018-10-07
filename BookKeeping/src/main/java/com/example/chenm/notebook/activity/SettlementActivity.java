package com.example.chenm.notebook.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.example.chenm.notebook.R;
import com.example.chenm.notebook.model.Record;
import com.example.chenm.notebook.model.RecordsForShow;
import com.example.chenm.notebook.model.Settlement;
import com.example.chenm.notebook.model.SettlementItem;
import com.example.chenm.notebook.model.User;
import com.example.chenm.notebook.utils.DataBaseUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.litepal.LitePal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author chenm
 */
public class SettlementActivity extends Activity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rel_bar)
    RelativeLayout relBar;
    @BindView(R.id.settlement_content)
    ScrollView settlementContent;
    @BindView(R.id.line_no_records)
    LinearLayout noRecordsLayout;
    @BindView(R.id.settlement_amount_all)
    TextView settlementAmountAll;
    @BindView(R.id.settlement_peruser_payment)
    RecyclerView settlementPeruserPayment;
    @BindView(R.id.settlement_peruser_should_pay)
    RecyclerView settlementPeruserShouldPay;
    @BindView(R.id.settlement_all)
    RecyclerView settlementAll;

    //应付金额
    private MyAdapter mShouldPayAdapter;
    //已付金额
    private MyAdapter mPaymentAdapter;
    //结算金额
    private MyAdapter mSettlementAllAdapter;

    private Settlement settlement = new Settlement();

    public static String INTENT_TYPE = "intent_type";
    public static int SETTLEMENT = 1;
    public static int CHECK_FOR_VIEW = 2;

    private Gson gson;


    public static void launch(Context context,int intentTypeValue) {
        Intent intent = new Intent(context, SettlementActivity.class);
        intent.putExtra(INTENT_TYPE,intentTypeValue);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);
        ButterKnife.bind(this);
        init();
    }

    public void init(){
        tvTitle.setText("结算");
        relBar.setBackgroundColor(getColor(R.color.white));
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        gson = new GsonBuilder().create();

        int intentType = getIntent().getIntExtra(INTENT_TYPE,1);
        if (intentType == 1){
            settlementAllRecord();
        } else {
            checkResultWithSave();
        }
    }

    public void settlementAllRecord(){

        List<User> users = DataBaseUtils.selectAllUser();
        for (User user : users) {
            SettlementItem data1 = new SettlementItem();
            data1.price = 0;
            data1.user = user;
            settlement.dataShouldPay.add(data1);
            SettlementItem data2 = new SettlementItem();
            data2.price = 0;
            data2.user = user;
            settlement.dataPayment.add(data2);
            SettlementItem data3 = new SettlementItem();
            data3.price = 0;
            data3.user = user;
            settlement.dataSettlementAll.add(data3);
        }

        List<RecordsForShow> records = DataBaseUtils.selectAllUnsettlementRecord();
        if (records == null || records.size() == 0){
            noRecordsLayout.setVisibility(View.VISIBLE);
            settlementContent.setVisibility(View.GONE);
            return;
        }
        ContentValues values = new ContentValues();
        values.put("isCheck","1");
        for (RecordsForShow record : records){
            settlement.allAmount += record.getPrice();
            settlement.dataPayment.get(record.getBuyer().getId() - 1).price += record.getPrice();
            double average = record.getPrice()/(record.getUsers().size());
            for (User user : record.getUsers()){
                settlement.dataShouldPay.get(user.getId() - 1).price+=average;
            }
            LitePal.update(Record.class,values,record.getId());
        }

        SPUtils.getInstance().put("settlement_time",records.get(records.size()-1).getTime());
        SPUtils.getInstance().put("settlement_price"," "+settlement.allAmount);

        double price;
        for (int i=0;i<settlement.dataPayment.size();i++){
            settlement.dataSettlementAll.get(i).price = settlement.dataShouldPay.get(i).price - settlement.dataPayment.get(i).price;
            price = settlement.dataPayment.get(i).price;
            settlement.dataPayment.get(i).price = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
            price = settlement.dataShouldPay.get(i).price;
            settlement.dataShouldPay.get(i).price = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
            price = settlement.dataSettlementAll.get(i).price;
            settlement.dataSettlementAll.get(i).price = new BigDecimal(price).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
        }

        String settlementStr = gson.toJson(settlement);
        SPUtils.getInstance().put("settlement_result_all",settlementStr);

        setAdapter();

    }

    public void checkResultWithSave(){

        String settlementStr = SPUtils.getInstance().getString("settlement_result_all");
        if (settlementStr == ""){
            noRecordsLayout.setVisibility(View.VISIBLE);
            settlementContent.setVisibility(View.GONE);
            return;
        }
        settlement = gson.fromJson(settlementStr, Settlement.class);

        setAdapter();
    }

    public void setAdapter(){
        settlementAmountAll.setText("￥ "+settlement.allAmount);
        mPaymentAdapter = new MyAdapter(settlement.dataPayment);
        mShouldPayAdapter = new MyAdapter(settlement.dataShouldPay);
        mSettlementAllAdapter = new MyAdapter(settlement.dataSettlementAll);
        settlementPeruserPayment.setAdapter(mPaymentAdapter);
        settlementPeruserShouldPay.setAdapter(mShouldPayAdapter);
        settlementAll.setAdapter(mSettlementAllAdapter);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

        List<SettlementItem> dataList;

        public MyAdapter(List<SettlementItem> data){
            this.dataList = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SettlementActivity.this).inflate(R.layout.item_settlement,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.settlementUsername.setText(dataList.get(position).user.getUserName());
            holder.settlementMoney.setText("￥ "+dataList.get(position).price);
        }

        @Override
        public int getItemCount() {
            return dataList == null ? 0:dataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.settlement_username)
            TextView settlementUsername;
            @BindView(R.id.settlement_money)
            TextView settlementMoney;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
