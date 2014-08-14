package toby.han.todo.notify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

public class TodoAlarm {
    public static final String INTENT_TODO_ID = "INTENT_EXTRAS_TODO_ID";
    
    public static final String INTENT_TODO_TIME = "INTENT_EXTRAS_TODO_TIME";
    
    public static final String INTENT_TODO_CONTENT = "INTENT_EXTRAS_TODO_CONTENT";
    
    private TodoAlarm() {
        
    }
    
    /*public static void startBlockedAlarm(Context cxt, long todoId, long timeInMillis) {
        DatabaseHelper db = DatabaseHelper.getInstance(cxt);
        TodoData todo = db.queryTodo(todoId);
        if(todo != null) {
            startAlarm(cxt, todoId, timeInMillis, todo.body);
        }
    }*/
    
    public static void startAlarm(Context cxt, long todoId, long timeInMillis, String content) {
        Intent intent = new Intent(cxt, TodoAlarmReceiver.class);
        // 使用待办的id作为intent的action；这样，当cancel alarm时, AlarmManager.cancel(PendingIntent),
        // 系统会根据action等内容来确定取消哪个intent
        intent.setAction(String.valueOf(todoId));
        intent.putExtra(INTENT_TODO_ID, todoId);
        intent.putExtra(INTENT_TODO_TIME, timeInMillis);
        intent.putExtra(INTENT_TODO_CONTENT, content);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(cxt, 0, intent, 0);
        
        AlarmManager alarmMgr = (AlarmManager)cxt.getSystemService(Context.ALARM_SERVICE);
        if(VERSION.SDK_INT < VERSION_CODES.KITKAT) {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, timeInMillis, alarmIntent);
        }
        else {
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, alarmIntent);
        }
        
        ComponentName receiver = new ComponentName(cxt, TodoBootReceiver.class);
        PackageManager pm = cxt.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
    
    public static void cancelAlarm(Context cxt, long todoId) {
        Intent intent = new Intent(cxt, TodoAlarmReceiver.class);
        intent.setAction(String.valueOf(todoId));
        PendingIntent alarmIntent = PendingIntent.getBroadcast(cxt, 0, intent, 0);
        
        AlarmManager alarmMgr = (AlarmManager)cxt.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(alarmIntent);
    }
}
