package com.example.chenm.notebook.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chenm.notebook.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author chenm
 */
public class DialogWithEditText extends Dialog {

    @BindView(R.id.et_user_name_fill)
    EditText etUserNameFill;
    @BindView(R.id.tv_center_cancel)
    TextView cancelButton;
    @BindView(R.id.tv_center_confirm)
    TextView confirmButton;

    private Context context;
    private static final int resId = R.layout.view_edit_dialog;

    /**
     * 确定按钮被点击了的监听器
     */
    private OnConfirmButtonOnclickListener confirmButtonOnclickListener;

    public interface OnConfirmButtonOnclickListener {
        /**
         * onConfirmButtonClick
         * @param username
         */
        void onConfirmButtonClick(String username);
    }

    /**
     * 设置确定按钮的监听
     * @param onConfirmButtonOnclickListener
     */
    public void setConfirmButtonOnclickListener(OnConfirmButtonOnclickListener onConfirmButtonOnclickListener) {
        this.confirmButtonOnclickListener = onConfirmButtonOnclickListener;
    }

    public DialogWithEditText(final Context context) {
        super(context, R.style.AlertDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(resId);
        setCanceledOnTouchOutside(true);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_center_cancel, R.id.tv_center_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_center_cancel:
                dismiss();
                break;
            case R.id.tv_center_confirm:
                confirmButtonOnclickListener.onConfirmButtonClick(etUserNameFill.getText().toString());
                break;
            default:
                break;
        }
    }
}
