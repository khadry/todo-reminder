package toby.han.todo.widget;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import toby.han.todo.R;
import toby.han.todo.wheel.AbstractWheelTextAdapter;
import toby.han.todo.wheel.NumericWheelAdapter;
import toby.han.todo.wheel.OnWheelScrollListener;
import toby.han.todo.wheel.WheelView;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class DateTimeDlg extends Dialog {
//    private static final String Tag = "DateTimeDlg";
    
    private TextView mYearTv;
    
    private final long mDate;
    
    private Calendar mCalendar;
    
    private int mDayWheelIndex;
    
    private DateTimeDlgCallback mCallback;
    
    public DateTimeDlg(Context context, long date) {
        super(context, R.style.DlgTransparentBg);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        mDate = date;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_date_time_);
        
        mYearTv = (TextView)findViewById(R.id.date_time_dlg_title_tv);
        
        final Context cxt = getContext();
        final WheelView hours = (WheelView) findViewById(R.id.hourWheelView);
        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(cxt, 0, 23);
        hourAdapter.setItemResource(R.layout.wheel_text_item);
        hourAdapter.setItemTextResource(R.id.wheel_text);
        hours.setViewAdapter(hourAdapter);
    
        final WheelView mins = (WheelView) findViewById(R.id.minuteWheelView);
        NumericWheelAdapter minAdapter = new NumericWheelAdapter(cxt, 0, 59, "%02d");
        minAdapter.setItemResource(R.layout.wheel_text_item);
        minAdapter.setItemTextResource(R.id.wheel_text);
        mins.setViewAdapter(minAdapter);
//        mins.setCyclic(true);
    
        mCalendar = Calendar.getInstance(Locale.CHINA);
        final long curTimeInMillis = System.currentTimeMillis();
        if(mDate >= curTimeInMillis) {
            mCalendar.setTimeInMillis(mDate);
        }
        else {
            mCalendar.setTimeInMillis(curTimeInMillis);
        }
        mCalendar.set(Calendar.SECOND, 0);
        hours.setCurrentItem(mCalendar.get(Calendar.HOUR_OF_DAY));
        mins.setCurrentItem(mCalendar.get(Calendar.MINUTE));
        
        String year = mCalendar.get(Calendar.YEAR) + "年";
        mYearTv.setText(year);
        
        final WheelView days = (WheelView) findViewById(R.id.dayWheelView);
        final DayArrayAdapter daysAdapter = new DayArrayAdapter(cxt, mCalendar);
        days.setViewAdapter(daysAdapter);
        days.setCurrentItem(mDayWheelIndex);
        
        /*OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                
            }
        };
        hours.addChangingListener(wheelListener);
        mins.addChangingListener(wheelListener);
        days.addChangingListener(wheelListener);
        */
        final OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
            public void onScrollingStarted(WheelView wheel) {
            }
            public void onScrollingFinished(WheelView wheel) {
                if(wheel == days) {
                    final int index = days.getCurrentItem();
                    final long time = mCalendar.getTimeInMillis() + 
                                      (index - mDayWheelIndex) * (24L * 60 * 60 * 1000);
                    mCalendar.setTimeInMillis(time);
                    mDayWheelIndex = index;
                }
                else if(wheel == hours) {
                    mCalendar.set(Calendar.HOUR_OF_DAY, hours.getCurrentItem());
                }
                else if(wheel == mins) {
                    mCalendar.set(Calendar.MINUTE, mins.getCurrentItem());
                }
                String year = mCalendar.get(Calendar.YEAR) + "年";
                mYearTv.setText(year);
            }
        };
        days.addScrollingListener(scrollListener);
        hours.addScrollingListener(scrollListener);
        mins.addScrollingListener(scrollListener);
        
        findViewById(R.id.date_time_dlg_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context cxt = v.getContext();
                final long time = mCalendar.getTimeInMillis();
                if(time > System.currentTimeMillis()) {
                    if(mCallback != null) {
                        mCallback.timeInMillis(time);
                    }
                    DateTimeDlg.this.dismiss();
                }
                else {
                    Toast.makeText(cxt, "不能设置过去的时间", Toast.LENGTH_LONG).show();
                }
            }
        });
        
        findViewById(R.id.date_time_dlg_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onCancel();
                DateTimeDlg.this.dismiss();
            }
        });
    }
    
    public void setCallback(DateTimeDlgCallback callback) {
        this.mCallback = callback;
    }
    
    private static class DayArrayAdapter extends AbstractWheelTextAdapter {
        // Count of days to be shown
        private final int DaysCount = 1000;
        
        // Calendar
        private Calendar calendar;
        
        private final int thisYear, thisDayOfYear;
        
        protected DayArrayAdapter(Context context, Calendar calendar) {
            super(context, R.layout.wheel_text_item, NO_RESOURCE);
            setItemTextResource(R.id.wheel_text);
            // 必须clone，否则外部calendar的修改会影响this.calendar
            this.calendar = (Calendar)calendar.clone();
            
            Calendar nowCal = Calendar.getInstance(Locale.CHINA);
            thisYear = nowCal.get(Calendar.YEAR);
            thisDayOfYear = nowCal.get(Calendar.DAY_OF_YEAR);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            final int day = index;
            Calendar newCalendar = (Calendar) calendar.clone();
            final long newTime = newCalendar.getTimeInMillis() + day * (24L * 60 * 60 * 1000);
            newCalendar.setTimeInMillis(newTime);
            
            boolean isToday = false;
            if(thisYear == newCalendar.get(Calendar.YEAR) &&
                    thisDayOfYear == newCalendar.get(Calendar.DAY_OF_YEAR)) {
                isToday = true;
            }
            
            View view = super.getItem(index, cachedView, parent);
            /*TextView weekday = (TextView) view.findViewById(R.id.time_weekday);
            TextView monthday = (TextView) view.findViewById(R.id.time_monthday);
            if (isToday) {
                weekday.setText("");
                monthday.setText("今天");
            } else {
                DateFormat weekDayFormat = new SimpleDateFormat("EEE", Locale.CHINA);
                weekday.setText(weekDayFormat.format(newCalendar.getTime()));
                DateFormat monthDayFormat = new SimpleDateFormat("MMMd", Locale.CHINA);
                monthday.setText(monthDayFormat.format(newCalendar.getTime()));
            }*/
            
            if (view instanceof TextView) {
                TextView textView = (TextView)view;
                if (isToday) {
                    textView.setText("今天");
                } else {
                    StringBuilder sb = new StringBuilder();
                    DateFormat weekDayFormat = new SimpleDateFormat("EEE", Locale.CHINA);
                    sb.append(weekDayFormat.format(newCalendar.getTime()));
                    sb.append(" ");
                    DateFormat monthDayFormat = new SimpleDateFormat("MM月dd日", Locale.CHINA);
                    sb.append(monthDayFormat.format(newCalendar.getTime()));
                    textView.setText(sb.toString());
                }
            }

            return view;
        }
        
        @Override
        public int getItemsCount() {
            return DaysCount;
        }
        
        @Override
        protected CharSequence getItemText(int index) {
            return "";
        }
    }
    
    public static interface DateTimeDlgCallback {
//        public static final int RESULT_CANCELED = 0;
        
//        public static final int RESULT_OK = 1;
        
        public void timeInMillis(long time);
        
        public void onCancel();
        
//        public void onDateTimeResult(int resultCode, boolean remindFlag, long timeInMillis);
    }
}
