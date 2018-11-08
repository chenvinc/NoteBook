package com.example.chenm.notebook.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.SPUtils;
import com.example.chenm.notebook.R;
import com.example.chenm.notebook.utils.CommonUtils;
import com.example.chenm.notebook.utils.DataBaseUtils;
import com.example.chenm.notebook.adapter.UserListAdapter;
import com.example.chenm.notebook.model.User;
import com.example.chenm.notebook.utils.DialogDef;
import com.example.chenm.notebook.utils.DialogWithEditText;
import com.example.chenm.notebook.utils.RemindDialog;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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
    SwipeMenuRecyclerView userList;
    @BindView(R.id.add_relation_btn)
    TextView addRelationBtn;
    @BindView(R.id.add_relation_layout)
    RelativeLayout addRelationLayout;

    Unbinder unbinder;

    private List<User> users = new ArrayList<>();
    private UserListAdapter adapter;

    private RemindDialog remindDialog;

    boolean isFirstDelete;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, UsersActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        unbinder = ButterKnife.bind(this);
        tvTitle.setText(R.string.relations);
        ivRight.setVisibility(View.VISIBLE);
        adapter = new UserListAdapter();
        isFirstDelete = SPUtils.getInstance().getBoolean("is_first_delete");
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(UsersActivity.this);
                deleteItem.setBackground(R.color.red1)
                        .setWidth(170)
                        .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                        .setText("删除")
                        .setTextColor(Color.WHITE);
                swipeRightMenu.addMenuItem(deleteItem);
            }
        };
        userList.setSwipeMenuCreator(creator);
        userList.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                menuBridge.closeMenu();
                User user = adapter.getData().get(menuBridge.getAdapterPosition());
                if (!isFirstDelete) {
                    initRemindDialog(user);
                    remindDialog.show();
                } else {
                    LitePal.delete(User.class,user.getId());
                    refreshUserList();
                }
            }
        });
        DefaultItemDecoration decoration = new DefaultItemDecoration(getResources().getColor(R.color.text_gray1));
        userList.addItemDecoration(decoration);
        userList.setLayoutManager(new LinearLayoutManager(this));
        userList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUserList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
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

    private void initRemindDialog(final User user) {
        remindDialog = new RemindDialog(this);
        remindDialog.setCancelButtonOnclickListener(new RemindDialog.OnCancelButtonOnclickListener() {
            @Override
            public void onCancelButtonClick() {
                remindDialog.dismiss();
            }
        });
        remindDialog.setConfirmButtonOnclickListener(new RemindDialog.OnConfirmButtonOnclickListener() {
            @Override
            public void onConfirmButtonClick() {
                remindDialog.dismiss();
                LitePal.delete(User.class,user.getId());
                refreshUserList();
                isFirstDelete = true;
                SPUtils.getInstance().put("is_first_delete",true);
            }
        });
    }

    private void refreshUserList() {
        users.clear();
        users.addAll(DataBaseUtils.getInstance().selectAllUser());
        if (users.size() == 0) {
            if (addRelationLayout.getVisibility() == View.GONE) {
                addRelationLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (addRelationLayout.getVisibility() == View.VISIBLE) {
                addRelationLayout.setVisibility(View.GONE);
            }
        }
        adapter.setNewData(users);
    }

    private void showAddUserDialog(){
        final DialogWithEditText dialogWithEditText = new DialogWithEditText(this);
        dialogWithEditText.setConfirmButtonOnclickListener(new DialogWithEditText.OnConfirmButtonOnclickListener() {
            @Override
            public void onConfirmButtonClick(String username) {
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(UsersActivity.this,"请填写关系人名称",Toast.LENGTH_SHORT).show();
                    return;
                }
                User user = new User();
                user.setUserName(username);
                if (user.save()){
                    dialogWithEditText.dismiss();
                    refreshUserList();
                }else {
                    Toast.makeText(UsersActivity.this,"添加失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogWithEditText.show();
    }
}
