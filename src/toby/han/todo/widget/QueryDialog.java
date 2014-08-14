package toby.han.todo.widget;

import toby.han.todo.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class QueryDialog extends Dialog {
    private String mMsg, mConfirm, mCancel;
    
    private TextView mMsgTv, mConfirmTv, mCancelTv;
    
    private View.OnClickListener mConfirmListener;
    private View.OnClickListener mCancelListener;

    public QueryDialog(Context context, String msg) {
        super(context, R.style.DlgTransparentBg);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mMsg = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_dlg);
        mMsgTv = (TextView)findViewById(R.id.queryDlg_msg);
        mMsgTv.setText(mMsg);
        mConfirmTv = (TextView)findViewById(R.id.queryDlg_confirm);
        mCancelTv = (TextView)findViewById(R.id.queryDlg_cancel);
    }
    
    @Override
    public void show() {
        super.show();
        if(mConfirm != null) {
            mConfirmTv.setText(mConfirm);
        }
        if(mCancel != null) {
            mCancelTv.setText(mCancel);
        }
        View.OnClickListener defaultListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        };
        if(mConfirmListener == null) {
            mConfirmTv.setOnClickListener(defaultListener);
        }
        else {
            mConfirmTv.setOnClickListener(mConfirmListener);
        }
        if(mCancelListener == null) {
            mCancelTv.setOnClickListener(defaultListener);
        }
        else {
            mCancelTv.setOnClickListener(mCancelListener);
        }
    }
    
    public void setConfirm(String str) {
        mConfirm = str;
    }
    
    public void setConfirmListener(View.OnClickListener listener) {
        mConfirmListener = listener;
    }
    
    public void setCancel(String str) {
        mCancel = str;
    }
    
    public void setCancelListener(View.OnClickListener listener) {
        mCancelListener = listener;
    }
}
