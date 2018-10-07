package com.example.chenm.notebook.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

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
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder>{

    private Context context;
    private List<User> userList;
//    private OnItemLongClickListener longClickListener;

    public UserListAdapter(Context context,List<User> userList){
        this.context = context;
        this.userList = userList;
    }

    public void setData(List<User> list){
        userList=list;
        notifyDataSetChanged();
    }

//    public void setLongClickListener(OnItemLongClickListener longClickListener){
//        this.longClickListener = longClickListener;
//    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.userId.setText(userList.get(position).getId() + " ");
        holder.userName.setText(userList.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{
        TextView userId;
        TextView userName;
        public UserViewHolder(View itemView) {
            super(itemView);
//            itemView.setOnLongClickListener(this);
            userId = itemView.findViewById(R.id.user_id);
            userName = itemView.findViewById(R.id.user_name);
        }

//        @Override
//        public boolean onLongClick(View v) {
//            if (longClickListener != null){
//                longClickListener.onLongClick(getLayoutPosition());
//                return true;
//            }else {
//                return false;
//            }
//        }
    }

    public interface OnItemLongClickListener{
        void onLongClick(int position);
    }

}
