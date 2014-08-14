package toby.han.todo.vm;

import java.util.List;

import toby.han.todo.model.TodoData;
import android.os.Handler;
import android.os.Message;

public class LoadingTodoDataHandler extends Handler {
    private LoadingDataObserver mObserver;
    
    public LoadingTodoDataHandler(LoadingDataObserver obs) {
        mObserver = obs;
    }
    
    @Override
    public void handleMessage(Message msg) {
        @SuppressWarnings("unchecked")
        List<TodoData> todoList = (List<TodoData>)msg.obj;
        mObserver.onLoadingFinished(msg.arg1, todoList);
    }
    
    public static interface LoadingDataObserver {
        public void onLoadingFinished(int memoCount, List<TodoData> todoList);
    }
}
