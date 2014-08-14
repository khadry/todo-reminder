package toby.han.todo.vm;

import java.util.ArrayList;

import toby.han.todo.model.DatabaseHelper;
import toby.han.todo.model.TodoData;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class LoadingTodoDataRunnable implements Runnable {
    private final Context mCxt;
    private final Handler mHandler;
    
    public LoadingTodoDataRunnable(Context cxt, Handler hand){
        this.mCxt = cxt;
        this.mHandler = hand;
    }

    @Override
    public void run() {
        DatabaseHelper dbh = DatabaseHelper.getInstance(mCxt);
        ArrayList<TodoData> todoList = dbh.queryTodoWithReminding();
        Message msg = mHandler.obtainMessage();
        msg.arg1 = dbh.queryMemoCount();
        msg.obj = todoList;
        mHandler.sendMessage(msg);
    }

}
