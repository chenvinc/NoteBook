package com.example.chenm.notebook.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
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

import org.litepal.LitePal;

import java.math.BigDecimal;
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
    @BindView(R.id.delete_user_paid)
    TextView deleteUserPaid;
    @BindView(R.id.delete_user_should_pay)
    TextView deleteUserShouldPay;
    @BindView(R.id.delete_user_settlement)
    TextView deleteUserSettlement;

    /**
     * 应付金额
     */
    private MyAdapter mShouldPayAdapter;
    /**
     * 已付金额
     */
    private MyAdapter mPaymentAdapter;
    /**
     * 结算金额
     */
    private MyAdapter mSettlementAllAdapter;

    private Settlement settlement = new Settlement();

    public static String INTENT_TYPE = "intent_type";
    private static String IS_CHECK = "isCheck";
    private static String CHECKED = "1";
    public static String SETTLEMENT_TIME = "settlement_time";
    public static String SETTLEMENT_DATE = "settlement_price";
    public static String SETTLEMENT_RESULT_ALL = "settlement_result_all";
    public static int SETTLEMENT = 1;
    public static int CHECK_FOR_VIEW = 2;

    private Gson gson;
    private final int DELETE_USER = 10086;

    public static void launch(Context context, int intentTypeValue) {
        Intent intent = new Intent(context, SettlementActivity.class);
        intent.putExtra(INTENT_TYPE, intentTypeValue);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        tvTitle.setText("结算");
        relBar.setBackgroundColor(getColor(R.color.white));
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        gson = new GsonBuilder().create();

        int intentType = getIntent().getIntExtra(INTENT_TYPE, 1);
        if (intentType == 1) {
            settlementAllRecord();
        } else {
            checkResultWithSave();
        }
    }

    public void settlementAllRecord() {
        settlement.deleteUserAmount = 0;
        settlement.deleteUserPayment = 0;

        //初始化三个list数据
        initListData();

        //判断是否有数据
        List<RecordsForShow> records = DataBaseUtils.getInstance().selectAllUnsettlementRecord();
        if (records == null || records.size() == 0) {
            noRecordsLayout.setVisibility(View.VISIBLE);
            settlementContent.setVisibility(View.GONE);
            return;
        }
        //把取出来的数据check状态置成已结算
        ContentValues values = new ContentValues();
        values.put(IS_CHECK, CHECKED);
        //结算程序
        for (RecordsForShow record : records) {
            settlement.allAmount += record.getPrice();
            if (record.getBuyer() == null) {
                settlement.deleteUserAmount += record.getPrice();
            } else {
                int pos = returnPosition(settlement.dataPayment, record.getBuyer().getId());
                if (pos == DELETE_USER) {
                    settlement.deleteUserAmount += record.getPrice();
                } else {
                    settlement.dataPayment.get(pos).price += record.getPrice();
                }
            }
            double average = record.getPrice() / (record.getUsers().size());
            for (User user : record.getUsers()) {
                if (user == null) {
                    settlement.deleteUserPayment += average;
                } else {
                    int pos = returnPosition(settlement.dataShouldPay, user.getId());
                    if (pos == DELETE_USER) {
                        settlement.deleteUserPayment += average;
                    } else {
                        settlement.dataShouldPay.get(pos).price += average;
                    }
                }
            }
            LitePal.update(Record.class, values, record.getId());
        }

        settlement.deleteUserSettlement = settlement.deleteUserAmount - settlement.deleteUserPayment;

        //修饰数据
        setDataDoubleLay();

        //缓存本次结算结果
        SPUtils.getInstance().put(SETTLEMENT_TIME, records.get(records.size() - 1).getTime());
        SPUtils.getInstance().put(SETTLEMENT_DATE, String.valueOf(settlement.allAmount));

        String settlementStr = gson.toJson(settlement);
        SPUtils.getInstance().put(SETTLEMENT_RESULT_ALL, settlementStr);

        setAdapter();

    }

    private void initListData() {
        List<User> users = DataBaseUtils.getInstance().selectAllUser();
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
    }

    private void setDataDoubleLay(){
        settlement.allAmount = new BigDecimal(settlement.allAmount).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        settlement.deleteUserPayment = new BigDecimal(settlement.deleteUserPayment).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        settlement.deleteUserAmount = new BigDecimal(settlement.deleteUserAmount).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        settlement.deleteUserSettlement = new BigDecimal(settlement.deleteUserSettlement).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();

        double price;
        for (int i = 0; i < settlement.dataPayment.size(); i++) {
            settlement.dataSettlementAll.get(i).price = settlement.dataShouldPay.get(i).price - settlement.dataPayment.get(i).price;
            price = settlement.dataPayment.get(i).price;
            settlement.dataPayment.get(i).price = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
            price = settlement.dataShouldPay.get(i).price;
            settlement.dataShouldPay.get(i).price = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
            price = settlement.dataSettlementAll.get(i).price;
            settlement.dataSettlementAll.get(i).price = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        }
    }
    public void checkResultWithSave() {

        String settlementStr = SPUtils.getInstance().getString(SETTLEMENT_RESULT_ALL);
        if (TextUtils.isEmpty(settlementStr)) {
            noRecordsLayout.setVisibility(View.VISIBLE);
            settlementContent.setVisibility(View.GONE);
            return;
        }
        settlement = gson.fromJson(settlementStr, Settlement.class);

        setAdapter();
    }

    private int returnPosition(List<SettlementItem> itemList, int id) {
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).user.getId() == id) {
                return i;
            }
        }
        return DELETE_USER;
    }

    public void setAdapter() {
        settlementAmountAll.setText("￥ " + settlement.allAmount);
        mPaymentAdapter = new MyAdapter(settlement.dataPayment);
        mShouldPayAdapter = new MyAdapter(settlement.dataShouldPay);
        mSettlementAllAdapter = new MyAdapter(settlement.dataSettlementAll);
        settlementPeruserPayment.setAdapter(mPaymentAdapter);
        settlementPeruserShouldPay.setAdapter(mShouldPayAdapter);
        settlementAll.setAdapter(mSettlementAllAdapter);
        if (settlement.deleteUserPayment != 0 && settlement.deleteUserAmount != 0) {
            String deleteUserStr = "已删除关系人： ";
            deleteUserShouldPay.setVisibility(View.VISIBLE);
            deleteUserPaid.setVisibility(View.VISIBLE);
            deleteUserSettlement.setVisibility(View.VISIBLE);
            deleteUserShouldPay.setText(deleteUserStr + String.valueOf(settlement.deleteUserPayment));
            deleteUserPaid.setText(deleteUserStr + String.valueOf(settlement.deleteUserAmount));
            deleteUserSettlement.setText(deleteUserStr + String.valueOf(settlement.deleteUserSettlement));
        }
        setListHeight(settlementPeruserPayment, mPaymentAdapter);
        setListHeight(settlementPeruserShouldPay, mShouldPayAdapter);
        setListHeight(settlementAll, mSettlementAllAdapter);
    }

    public void setListHeight(RecyclerView recyclerView,MyAdapter adapter) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
        params.height = ConvertUtils.dp2px(50) * adapter.getItemCount();
        recyclerView.setLayoutParams(params);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        List<SettlementItem> dataList;

        public MyAdapter(List<SettlementItem> data) {
            this.dataList = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SettlementActivity.this).inflate(R.layout.item_settlement, null, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.settlementUsername.setText(dataList.get(position).user.getUserName());
            holder.settlementMoney.setText("￥ " + dataList.get(position).price);
        }

        @Override
        public int getItemCount() {
            return dataList == null ? 0 : dataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
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
