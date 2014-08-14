package toby.han.todo.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import toby.han.todo.R;
import toby.han.todo.model.TodoData;
import toby.han.todo.vm.LoadingTodoDataHandler;
import toby.han.todo.vm.LoadingTodoDataHandler.LoadingDataObserver;
import toby.han.todo.vm.LoadingTodoDataRunnable;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TodoListView extends ListView implements LoadingDataObserver {
    private TodoListAdapter mAdapter;

    private int mMemoCount = 0;

    private ArrayList<TodoData> mData = new ArrayList<TodoData>();

    public TodoListView(Context context) {
        super(context);
    }

    public TodoListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TodoListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void construct() {
        mAdapter = new TodoListAdapter();
        setAdapter(mAdapter);

        ExecutorService execService = Executors.newSingleThreadExecutor();
        LoadingTodoDataHandler handler = new LoadingTodoDataHandler(this);
        LoadingTodoDataRunnable run = new LoadingTodoDataRunnable(getContext(), handler);
        execService.execute(run);
    }

    @Override
    public void onLoadingFinished(int memoCount, List<TodoData> todoList) {
        mMemoCount = memoCount;
        if (mMemoCount > 0) {
            mData.add(new TodoData());
        }
        mData.addAll(todoList);
        mAdapter.notifyDataSetChanged();
    }

    void newTodo(TodoData todo) {
        if(todo.notifyTime > 0) {
            if(mData.size() == 0) {
                mData.add(todo);
            }
            else {
                ArrayList<TodoData> tempList1 = new ArrayList<TodoData>();
                ArrayList<TodoData> tempList2 = new ArrayList<TodoData>();
                for(TodoData todoItem : mData) {
                    if(todoItem.notifyTime <= todo.notifyTime) {
                        tempList1.add(todoItem);
                    }
                    else {
                        tempList2.add(todoItem);
                    }
                }
                mData.clear();
                mData.addAll(tempList1);
                mData.add(todo);
                mData.addAll(tempList2);
            }
        }
        else {
            mMemoCount += 1;
        }
        mAdapter.notifyDataSetChanged();
    }

    private static class MemoEntryViewHolder {
        TextView memoEntryTv;
    }

    private static class TodoViewHolder {
        TextView txtBody;
        TextView reminderTime;
    }

    private class TodoListAdapter extends BaseAdapter {
        private final int VIEW_TYPE_COUNT = 2;
        
        private final int VIEW_TYPE_MEMO = 0;

        private final int VIEW_TYPE_TODO = 1;

        private LayoutInflater mInflater;

        public TodoListAdapter() {
            mInflater = LayoutInflater.from(getContext());
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public TodoData getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            if (mMemoCount > 0 && position == 0) {
                return VIEW_TYPE_MEMO;
            } else {
                return VIEW_TYPE_TODO;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int viewType = getItemViewType(position);
            MemoEntryViewHolder memoEntryViewHolder = null;
            TodoViewHolder viewHolder = null;
            if (convertView == null) {
                switch (viewType) {
                case VIEW_TYPE_MEMO: {
                    convertView = mInflater.inflate(R.layout.list_item_memo_entry, null);
                    memoEntryViewHolder = new MemoEntryViewHolder();
                    memoEntryViewHolder.memoEntryTv = (TextView) convertView.findViewById(R.id.memo_entry_tv);
                    convertView.setTag(memoEntryViewHolder);
                    break;
                }
                case VIEW_TYPE_TODO: {
                    convertView = mInflater.inflate(R.layout.list_item_todo, null);
                    viewHolder = new TodoViewHolder();
                    viewHolder.txtBody = (TextView) convertView.findViewById(R.id.todo_txt_body);
                    viewHolder.reminderTime = (TextView) convertView.findViewById(R.id.todo_reminder_time);
                    convertView.setTag(viewHolder);
                    break;
                }
                }
            } else {
                switch (viewType) {
                case VIEW_TYPE_MEMO: {
                    memoEntryViewHolder = (MemoEntryViewHolder) convertView.getTag();
                    break;
                }
                case VIEW_TYPE_TODO: {
                    viewHolder = (TodoViewHolder) convertView.getTag();
                    break;
                }
                }
            }
            
            switch (viewType) {
            case VIEW_TYPE_MEMO: {
                String txt = getContext().getString(R.string.memo_entry);
                memoEntryViewHolder.memoEntryTv.setText(String.format(txt, mMemoCount));
                break;
            }
            case VIEW_TYPE_TODO: {
                TodoData todo = mData.get(position);
                viewHolder.txtBody.setText(todo.body);
                if (todo.notifyTime > 0) {
                    viewHolder.reminderTime.setVisibility(View.VISIBLE);
                    viewHolder.reminderTime.setText(getDateTimeAsStr(todo.notifyTime));
                } else {
                    viewHolder.reminderTime.setVisibility(View.GONE);
                }
                break;
            }
            }
            
            return convertView;
        }

    }

    private String getDateTimeAsStr(long timeInMillis) {
        final long curTimeInMillis = System.currentTimeMillis();
        Calendar curCal = Calendar.getInstance(Locale.CHINA);
        curCal.setTimeInMillis(curTimeInMillis);
        final int curYear = curCal.get(Calendar.YEAR);

        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.setTimeInMillis(timeInMillis);
        final int year = cal.get(Calendar.YEAR);

        String format = "yyyy年M月d日HH:mm";
        if (curYear == year) {
            format = "M月d日HH:mm";
        }
        DateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        return dateFormat.format(new Date(timeInMillis));
    }
}
