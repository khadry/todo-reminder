package toby.han.todo.ui;

import toby.han.todo.R;
import toby.han.todo.model.TodoData;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TodoListActivity extends Activity {
    private TodoListView mTodoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_todo_list);
        
        mTodoListView = (TodoListView)findViewById(R.id.todo_list_view);
        mTodoListView.construct();
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        
        if(TodoData.NEW_TODO != null) {
            mTodoListView.newTodo(TodoData.NEW_TODO);
            TodoData.NEW_TODO = null;
        }
    }
    
    public void onTodoAddClick(View v) {
        Intent intent = new Intent(this, TodoEditActivity.class);
        startActivity(intent);
    }
}
