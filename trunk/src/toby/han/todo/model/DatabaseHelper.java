package toby.han.todo.model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper mInstance = null;

    /** 数据库名称 **/
    public static final String DATABASE_NAME = "todo.db";

    /** 数据库版本号 **/
    private static final int DATABASE_VERSION = 1;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** 单例模式 **/
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TodoTable.SQL_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int version = oldVersion + 1;
        while(version <= newVersion) {
            upgradeByOneStep(db, version);
            ++version;
        }
    }
    
    // 单步升级，比如从1到2， 从2到3，从3到4
    private void upgradeByOneStep(SQLiteDatabase db, int version) {
        if (version == 2) {
            
        }
        else if(version == 3) {
            
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing
    }

    // 待办表
    private static abstract class TodoTable implements BaseColumns {
        public static final String TABLE_NAME = "todolist";
        
        public static final String COLUMN_NAME_BODY = "body";
        
        //创建时间 
        public static final String COLUMN_NAME_CREATION_TIME = "cretime";
        
        // 提醒时间
        public static final String COLUMN_NAME_REMINDER_TIME = "notifytime";

        public static final String SQL_TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + 
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +  
                COLUMN_NAME_BODY + " TEXT," +
                COLUMN_NAME_CREATION_TIME + " INTEGER DEFAULT 0," +
                COLUMN_NAME_REMINDER_TIME + " INTEGER DEFAULT 0" +
                ");";
        @SuppressWarnings("unused")
        public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
    
    public int queryMemoCount() {
        int count = 0;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] projection = new String[] { TodoTable._ID};
            String selecion = TodoTable.COLUMN_NAME_REMINDER_TIME + "=0";
            cursor = db.query(TodoTable.TABLE_NAME, projection, selecion, null, null, null, null);
            if (cursor != null) {
                count = cursor.getCount();
            }
        }
        catch (SQLException e) {
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }
    
    // 带提醒的待办
    public ArrayList<TodoData> queryTodoWithReminding() {
        ArrayList<TodoData> todoList = new ArrayList<TodoData>();
        
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] projection = new String[] { TodoTable._ID, TodoTable.COLUMN_NAME_BODY,
                                                 TodoTable.COLUMN_NAME_CREATION_TIME,
                                                 TodoTable.COLUMN_NAME_REMINDER_TIME};
            String selecion = TodoTable.COLUMN_NAME_REMINDER_TIME + ">0";
            String orderBy = TodoTable.COLUMN_NAME_REMINDER_TIME + " asc";
            cursor = db.query(TodoTable.TABLE_NAME, projection, selecion, null, null, null, orderBy);
            if (cursor != null && cursor.getCount() > 0) {
                while(cursor.moveToNext()) {
                    TodoData todo = new TodoData();
                    todo.id = cursor.getLong(0);
                    todo.body = cursor.getString(1);
                    todo.creTime = cursor.getLong(2);
                    todo.notifyTime = cursor.getLong(3);
                    todoList.add(todo);
                }
            }
        }
        catch (SQLException e) {
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        return todoList;
    }
    
    public TodoData queryTodo(long todoId) {
        TodoData todo = null;
        
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] projection = new String[] { TodoTable._ID, TodoTable.COLUMN_NAME_BODY,
                    TodoTable.COLUMN_NAME_CREATION_TIME,
                    TodoTable.COLUMN_NAME_REMINDER_TIME};
            String selection = TodoTable._ID + "=?";
            String[] selectionArgs = { String.valueOf(todoId) };
            cursor = db.query(TodoTable.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            if (cursor != null) {
                if(cursor.moveToNext()) {
                    todo = new TodoData();
                    todo.id = cursor.getLong(0);
                    todo.body = cursor.getString(1);
                    todo.creTime = cursor.getLong(2);
                    todo.notifyTime = cursor.getLong(3);
                }
            }
        }
        catch (SQLException e) {
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        return todo;
    }
    
    public long insertTodoData(String body, long creTime, long notifyTime) {
        ContentValues values = new ContentValues();
        values.put(TodoTable.COLUMN_NAME_BODY, body);
        values.put(TodoTable.COLUMN_NAME_CREATION_TIME, creTime);
        values.put(TodoTable.COLUMN_NAME_REMINDER_TIME, notifyTime);
        return getWritableDatabase().insert(TodoTable.TABLE_NAME, null, values);
    }
    
    public void saveReminderTime(long todoId, long time) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = TodoTable._ID + "=?";
        String[] selectionArgs = { String.valueOf(todoId) };
        ContentValues cv = new ContentValues();
        cv.put(TodoTable.COLUMN_NAME_REMINDER_TIME, time);
        db.update(TodoTable.TABLE_NAME, cv, selection, selectionArgs);
    }
    
    // @id, 待办id
    public void delTodoById(long todoId) {
        String selection = TodoTable._ID + "=?";
        String[] selectionArgs = { String.valueOf(todoId) };
        getWritableDatabase().delete(TodoTable.TABLE_NAME, selection, selectionArgs);
    }
}
