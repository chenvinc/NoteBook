package com.example.chenm.notebook.adapter;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.chenm.notebook.R;
import com.example.chenm.notebook.model.RecordsForShow;
import com.example.chenm.notebook.model.User;

import java.util.List;

/**
 * @author chenhongyu
 * @Date 2018/9/8
 * @Time 21:39
 * @Version 1.0
 * @Description 首页展示记录的adapter
 */
public class RecordRecyclerAdapter extends BaseItemDraggableAdapter<RecordsForShow,BaseViewHolder>{

    public RecordRecyclerAdapter(List<RecordsForShow> data) {
        super(R.layout.item_record, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RecordsForShow item) {
        helper.setText(R.id.record_time,item.getTime());
        StringBuilder builder = new StringBuilder();
        for (User user : item.getUsers()){
            builder.append(user.getUserName()).append(" ");
        }
        helper.setText(R.id.record_with_peoples,builder.toString());
        helper.setText(R.id.record_price_amount,"￥"+item.getPrice());
        helper.setText(R.id.record_things_name,item.getThing());
        helper.setText(R.id.record_payment_people,item.getBuyer().getUserName());
        if ("1".equals(item.getIsCheck())){
            helper.setVisible(R.id.item_record_checked,true);
        } else {
            helper.setVisible(R.id.item_record_checked,false);
        }
    }
}
