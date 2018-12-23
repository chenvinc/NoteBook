package com.example.chenm.notebook.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.example.chenm.notebook.R;
import com.example.chenm.notebook.adapter.SelectUserAdapter;
import com.example.chenm.notebook.model.SelectUser;

import java.util.List;


/**
 * @author chenm
 */
public class DialogDef extends Dialog {

    private Context context;
    private static final int RES_ID = R.layout.dialog_userlist;

    private RecyclerView selectUserList;
    private CheckBox chooseAll;
    private LinearLayout layoutChooseAll;

    private TextView cancelButton;
    private TextView confirmButton;
    private List<SelectUser> userList;

    private SelectUserAdapter adapter;

    private boolean canChooseAll = true;

    /**
     * 取消按钮被点击了的监听器
     */
    private OnCancelButtonOnclickListener cancelButtonOnclickListener;
    /**
     * 确定按钮被点击了的监听器
     */
    private OnConfirmButtonOnclickListener confirmButtonOnclickListener;

    public interface OnConfirmButtonOnclickListener {
        /**
         * onConfirmButtonClick
         * @param list
         */
        void onConfirmButtonClick(List<SelectUser> list);
    }

    public interface OnCancelButtonOnclickListener {
        /**
         * onCancelButtonClick
         */
        void onCancelButtonClick();
    }

    public DialogDef(Context context, List<SelectUser> userList) {
        super(context, R.style.AlertDialog);
        this.context = context;
        this.userList = userList;
    }

    public DialogDef(Context context, List<SelectUser> userList, boolean canChooseAll){
        super(context, R.style.AlertDialog);
        this.context = context;
        this.userList = userList;
        this.canChooseAll = canChooseAll;
    }


    /**
     * 设置取消按钮监听
     * @param onCancelButtonOnclickListener
     */
    public void setCancelButtonOnclickListener(OnCancelButtonOnclickListener onCancelButtonOnclickListener) {
        this.cancelButtonOnclickListener = onCancelButtonOnclickListener;
    }

    /**
     * 设置确定按钮的监听
     * @param onConfirmButtonOnclickListener
     */
    public void setConfirmButtonOnclickListener(OnConfirmButtonOnclickListener onConfirmButtonOnclickListener) {
        this.confirmButtonOnclickListener = onConfirmButtonOnclickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(RES_ID);
        setCanceledOnTouchOutside(false);
        adapter = new SelectUserAdapter(context,userList,canChooseAll);
        init();
        initEvent();
        setDialogHeight();
    }

    private void init(){
        selectUserList = findViewById(R.id.select_user_list);
        chooseAll = findViewById(R.id.choose_all);
        layoutChooseAll = findViewById(R.id.layout_choose_all);
        cancelButton = findViewById(R.id.tv_center_cancle);
        confirmButton = findViewById(R.id.tv_center_sure);
        if (canChooseAll){
            layoutChooseAll.setVisibility(View.VISIBLE);
        }
        selectUserList.setLayoutManager(new GridLayoutManager(context,2));
        selectUserList.setAdapter(adapter);
    }

    private void initEvent() {
        layoutChooseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseAll.setChecked(!chooseAll.isChecked());
                chooseEvent();
            }
        });
        chooseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    chooseEvent();
            }
        });
        //设置确定按钮被点击后，向外界提供监听
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmButtonOnclickListener != null) {
                    confirmButtonOnclickListener.onConfirmButtonClick(adapter.getData());
                }
                dismiss();
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelButtonOnclickListener != null) {
                    cancelButtonOnclickListener.onCancelButtonClick();
                }
                dismiss();
            }
        });
    }

    private void setDialogHeight() {
        if (userList.size() < 16) {
            return;
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) selectUserList.getLayoutParams();
            params.height = ConvertUtils.dp2px(50) * 8;
            selectUserList.setLayoutParams(params);
        }
    }

    private void chooseEvent() {
        for (int i =0;i<userList.size();i++) {
            userList.get(i).setCheck(chooseAll.isChecked());
            adapter.setData(userList);
        }
    }
}
