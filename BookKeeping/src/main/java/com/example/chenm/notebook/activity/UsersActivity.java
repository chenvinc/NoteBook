package com.example.chenm.notebook.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ObjectUtils;
import com.example.chenm.notebook.R;
import com.example.chenm.notebook.utils.DataBaseUtils;
import com.example.chenm.notebook.adapter.UserListAdapter;
import com.example.chenm.notebook.model.User;
import com.example.chenm.notebook.utils.DialogDef;
import com.example.chenm.notebook.utils.DialogWithEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author chenm
 */
public class UsersActivity extends Activity{

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.user_list)
    RecyclerView userList;
    @BindView(R.id.add_relation_btn)
    TextView addRelationBtn;
    @BindView(R.id.add_relation_layout)
    RelativeLayout addRelationLayout;

    private List<User> users = new ArrayList<>();
    private UserListAdapter adapter;
    private DialogWithEditText dialogWithEditText;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, UsersActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        ButterKnife.bind(this);
        tvTitle.setText(R.string.relations);
        ivRight.setVisibility(View.VISIBLE);
        users.addAll(DataBaseUtils.selectAllUser());
        adapter = new UserListAdapter(this,users);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ObjectUtils.isEmpty(users)){
            addRelationLayout.setVisibility(View.VISIBLE);
        }else {
            userList.setLayoutManager(new LinearLayoutManager(this));
            userList.setAdapter(adapter);
        }
    }

    @OnClick({R.id.iv_right, R.id.add_relation_btn, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_right:
            case R.id.add_relation_btn:
                showAddUserDialog();
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void showAddUserDialog(){
        if (dialogWithEditText == null){
            dialogWithEditText = new DialogWithEditText(this);
        }
        dialogWithEditText.setConfirmButtonOnclickListener(new DialogWithEditText.OnConfirmButtonOnclickListener() {
            @Override
            public void onConfirmButtonClick(String username) {
                User user = new User();
                user.setUserName(username);
                if (user.save()){
                    dialogWithEditText.dismiss();
                    users.clear();
                    users.addAll(DataBaseUtils.selectAllUser());
                    adapter.setData(users);
                }else {
                    Toast.makeText(UsersActivity.this,"添加失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogWithEditText.show();
    }
}
