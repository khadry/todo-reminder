package toby.han.todo.notify;

import java.util.ArrayList;

import toby.han.todo.model.DatabaseHelper;
import toby.han.todo.model.TodoData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TodoBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            DatabaseHelper db = DatabaseHelper.getInstance(context);
            ArrayList<TodoData> todoList = db.queryTodoWithReminding();
            for(TodoData todo: todoList) {
                if(todo.notifyTime > 0) {
                    TodoAlarm.startAlarm(context, todo.id, todo.notifyTime, todo.body);
                }
            }
        }
    }

}
