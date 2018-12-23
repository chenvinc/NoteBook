package com.example.chenm.notebook.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.chenm.notebook.R;
import com.example.chenm.notebook.model.SelectUser;

import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenhongyu
 * @Date 2018/9/9
 * @Time 21:12
 * @Version 1.0
 * @Description 选择用户
 */

public class SelectUserAdapter extends RecyclerView.Adapter<SelectUserAdapter.SelectUserViewHolder>{
    private List<SelectUser> mSelectUsers = new ArrayList<>();
    private Context mContext;
    private boolean canChooseAll;

    public SelectUserAdapter(Context context, List<SelectUser> users, boolean canChooseAll){
        mSelectUsers.addAll(users);
        mContext = context;
        this.canChooseAll = canChooseAll;
    }

    public void setData(List<SelectUser> userList){
        mSelectUsers.clear();
        mSelectUsers.addAll(userList);
        notifyDataSetChanged();
    }

    public List<SelectUser> getData(){
        return mSelectUsers;
    }
    @NonNull
    @Override
    public SelectUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_select_user,parent,false);
        return new SelectUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectUserViewHolder holder, int position) {
        SelectUser mSelectUser = mSelectUsers.get(position);
        holder.checkBox.setChecked(mSelectUser.getCheck());
        holder.nameText.setText(mSelectUser.getUser().getUserName());
    }

    @Override
    public int getItemCount() {
        return mSelectUsers == null ? 0 :mSelectUsers.size();
    }

    public class SelectUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CheckBox checkBox;
        TextView nameText;

        public SelectUserViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.item_choose);
            nameText = itemView.findViewById(R.id.select_user_name);
            itemView.setOnClickListener(this);
            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (R.id.item_choose == v.getId()){
                onClickAction();
            } else {
                checkBox.setChecked(!checkBox.isChecked());
                onClickAction();
            }
        }

        private void onClickAction() {
            if (!canChooseAll){
                for (SelectUser user : mSelectUsers){
                    if (user.getCheck()) {
                        user.setCheck(false);
                    }
                }
            }
            notifyDataSetChanged();
            mSelectUsers.get(getLayoutPosition()).setCheck(checkBox.isChecked());
        }
    }
}
