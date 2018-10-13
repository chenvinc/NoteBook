package com.example.chenm.notebook.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.chenm.notebook.R;
import com.example.chenm.notebook.model.User;

import java.util.List;

/**
 * @author chenhongyu
 * @Date 2018/9/16
 * @Time 15:19
 * @Version 1.0
 * @Description 联系人列表
 */
public class UserListAdapter extends BaseQuickAdapter<User,BaseViewHolder> {

    public UserListAdapter(@Nullable List<User> data) {
        super(R.layout.item_user,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, User item) {
        helper.setText(R.id.user_id,item.getId() + " ");
        helper.setText(R.id.user_name,item.getUserName());
    }
}
