package toby.han.todo.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import toby.han.todo.R;
import toby.han.todo.model.DatabaseHelper;
import toby.han.todo.model.TodoData;
import toby.han.todo.notify.TodoAlarm;
import toby.han.todo.widget.DateTimeDlg;
import toby.han.todo.widget.DateTimeDlg.DateTimeDlgCallback;
import toby.han.todo.widget.QueryDialog;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TodoEditActivity extends Activity implements DateTimeDlgCallback {
    private TextView mSaveTv;
    
    private EditText mTodoContentEt;
    
    private ImageView mReminderSwitcher;
    
    private boolean mReminder;
    
    private View mDateTimeLayout;
    
    private TextView mDateTimeTv;
    
    private long mDateTime = 0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_todo_edit);
        
        mSaveTv = (TextView)findViewById(R.id.todo_edit_save);
        mSaveTv.setTextColor(0xff757575);
        mTodoContentEt = (EditText)findViewById(R.id.todo_edit_content);
        mTodoContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                updateSaveBtn();
            }
        });
        
        findViewById(R.id.todo_edit_reminder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                mReminder = !mReminder;
                setSwitcher(mReminderSwitcher, mReminder);
                setDateTimeLayoutVisible();
            }
        });
        mReminderSwitcher = (ImageView)findViewById(R.id.reminder_switcher);
        mReminder = false;
        setSwitcher(mReminderSwitcher, mReminder);
        
        mDateTimeLayout = findViewById(R.id.todo_edit_datetime_layout);
        setDateTimeLayoutVisible();
        mDateTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                DateTimeDlg dlg = new DateTimeDlg(v.getContext(), mDateTime);
                dlg.setCallback(TodoEditActivity.this);
                dlg.show();
            }
        });
        
        mDateTimeTv = (TextView)findViewById(R.id.todo_edit_datetime);
    }    
    
    public void onCancelTodoClick(View v) {
        if(hasSomethingToSave()) {
            QueryDialog dlg = new QueryDialog(this, "是否放弃此次操作?");
            dlg.setConfirm("取消");
            dlg.setCancel("放弃");
            dlg.setCancelListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            dlg.show();
        }
        else {
            finish();
        }
    }
    
    public void onSaveTodoClick(View v) {
        String content = mTodoContentEt.getText().toString();
        if(content.trim().length() == 0) {
            Toast.makeText(this, "待办内容不能为空", Toast.LENGTH_LONG).show();
        }
        else {
            final long curTime = System.currentTimeMillis();
            final long todoId = DatabaseHelper.getInstance(this).insertTodoData(content, curTime, mDateTime);
            if(todoId > 0) {
                Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show();
                if(mDateTime > 0) {
                    TodoAlarm.startAlarm(this, todoId, mDateTime, content);
                }
                TodoData todo = new TodoData();
                todo.id = todoId;
                todo.body = content;
                todo.creTime = curTime;
                todo.notifyTime = mDateTime;
                TodoData.NEW_TODO = todo;
                finish();
            }
            else {
                Toast.makeText(this, "保存失败", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void updateSaveBtn() {
        boolean enabled = hasSomethingToSave();
        if(enabled) {
            mSaveTv.setTextColor(0xffd2d2d2);
        }
        else {
            mSaveTv.setTextColor(0xff757575);
        }
    }
    
    private boolean hasSomethingToSave() {
        boolean res = false;
        if(mReminder && mDateTime > 0) {
            res = true;
        }
        else {
            String content = mTodoContentEt.getText().toString();
            if(content.trim().length() > 0) {
                res = true;
            }
        }
        return res;
    }
    
    @Override
    public void timeInMillis(long time) {
        if(time > 0) {
            if(mDateTime > 0) {
                Toast.makeText(this, "提醒修改成功", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "提醒设置成功", Toast.LENGTH_SHORT).show();
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            mDateTimeTv.setText(dateFormat.format(new Date(time)));
        }
        else {
            mDateTimeTv.setText("设置");
        }
        mDateTime = time;
        
        updateSaveBtn();
    }

    @Override
    public void onCancel() {
        // do nothing
    }
    
    private void setDateTimeLayoutVisible() {
        if(mReminder) {
            mDateTimeLayout.setVisibility(View.VISIBLE);
        }
        else {
            mDateTimeLayout.setVisibility(View.GONE);
        }
    }
    
    private void setSwitcher(View v, boolean flag) {
        if(flag) {
            v.setBackgroundResource(R.drawable.switch_on);
        }
        else {
            v.setBackgroundResource(R.drawable.switch_off);
        }
    }
    
    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTodoContentEt.getApplicationWindowToken(), 0);
    }
}
