package toby.han.todo.notify;

import toby.han.todo.model.DatabaseHelper;
import toby.han.todo.model.TodoData;
import toby.han.todo.ui.NotificationActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class TodoAlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final long todoId = intent.getLongExtra(TodoAlarm.INTENT_TODO_ID, 0);
        
        TodoData todo = DatabaseHelper.getInstance(context).queryTodo(todoId);
        if(todo == null) {
            return;
        }
        
        final long time = intent.getLongExtra(TodoAlarm.INTENT_TODO_TIME, 0);
        String content = intent.getStringExtra(TodoAlarm.INTENT_TODO_CONTENT);
        
        if(NotificationActivity.IS_SHOWING) {
            TodoAlarm.startAlarm(context, todoId, System.currentTimeMillis()+30*1000, content);
            return;
        }
        
        NotificationActivity.IS_SHOWING = true;
        
        Intent intent1 = new Intent(context, NotificationActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra(TodoAlarm.INTENT_TODO_ID, todoId);
        intent1.putExtra(TodoAlarm.INTENT_TODO_TIME, time);
        intent1.putExtra(TodoAlarm.INTENT_TODO_CONTENT, content);
        context.startActivity(intent1);
    }
}
