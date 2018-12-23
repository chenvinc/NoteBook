package com.example.chenm.notebook.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.chenm.notebook.R;

/**
 * @author chenhongyu
 * @Date 2018/11/5
 * @Time 22:01
 * @Version 1.0
 * @Description 提示弹窗
 */
public class RemindDialog extends Dialog {

    private static final int resId = R.layout.dialog_remind;

    private TextView cancelButton;
    private TextView confirmButton;
    private TextView remindStr1;
    private TextView remindStr2;
    private String remindStr;

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
         */
        void onConfirmButtonClick();
    }

    public interface OnCancelButtonOnclickListener {
        /**
         * onCancelButtonClick
         */
        void onCancelButtonClick();
    }

    public RemindDialog(Context context) {
        super(context, R.style.AlertDialog);
    }

    public RemindDialog(Context context, String remindStr) {
        super(context, R.style.AlertDialog);
        this.remindStr = remindStr;
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
        setContentView(resId);
        setCanceledOnTouchOutside(false);
        init();
        initEvent();
    }

    private void init(){
        cancelButton = findViewById(R.id.tv_center_cancle);
        confirmButton = findViewById(R.id.tv_center_sure);
        remindStr1 = findViewById(R.id.remind_str1);
        remindStr2 = findViewById(R.id.remind_str2);
        if (!TextUtils.isEmpty(remindStr)) {
            remindStr1.setText(remindStr);
            remindStr2.setVisibility(View.GONE);
        }
    }

    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmButtonOnclickListener != null) {
                    confirmButtonOnclickListener.onConfirmButtonClick();
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
}