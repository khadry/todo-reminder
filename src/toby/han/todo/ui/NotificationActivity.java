package toby.han.todo.ui;

import java.io.IOException;

import toby.han.todo.R;
import toby.han.todo.notify.TodoAlarm;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class NotificationActivity extends Activity {
    public static boolean IS_SHOWING = false;
    
    private long mTodoId;
    
//    private long mNotifyTime;
    
    private String mContent;
    
    private MediaPlayer mMediaPlayer;
    
    private boolean mSoundPlayed;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | 
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | 
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        /*new Handler().postDelayed(new Runnable(){  
            @Override  
            public void run(){
                Window win = getWindow();
                if(win != null) {
                    win.clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                }
            }  
        }, 30000);*/
        
        setContentView(R.layout.dlg_todo_notification);
        
        Intent intent = getIntent();
        mTodoId = intent.getLongExtra(TodoAlarm.INTENT_TODO_ID, 0);
//        mNotifyTime = intent.getLongExtra(TodoAlarm.INTENT_TODO_TIME, 0);
        mContent = intent.getStringExtra(TodoAlarm.INTENT_TODO_CONTENT);
        
        TextView contentTv = (TextView)findViewById(R.id.todo_notify_dlg_msg);
        contentTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        contentTv.setText(mContent);
        
        mSoundPlayed = false;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        if(!mSoundPlayed) {
            playSound();
        }
        mSoundPlayed = true;
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        
        // 锁屏界面时弹窗：onResume -> onStop -> onResume
        asyncStopSound();
        
        IS_SHOWING = false;
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        
        final long id = intent.getLongExtra(TodoAlarm.INTENT_TODO_ID, 0);
        String content = intent.getStringExtra(TodoAlarm.INTENT_TODO_CONTENT);
        if(id > 0 && content != null) {
            final long time = System.currentTimeMillis() + 30 * 1000;
            TodoAlarm.startAlarm(this, id, time, content);
        }
    }
    
    public void onClickBtn1(View v) {
        final long newNotifyTime = System.currentTimeMillis() + 10 * 60 * 1000;
        
        // 不必取消
//        TodoAlarm.cancelAlarm(this, mTodoId);
        TodoAlarm.startAlarm(this, mTodoId, newNotifyTime, mContent);
        
        finish();
    }
    
    public void onClickBtn2(View v) {
        finish();
    }
    
    private void playSound() {
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE); 
        final int current = audioManager.getStreamVolume(AudioManager.STREAM_RING );
        if (current != 0) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.reset();
            
            try {
                mMediaPlayer.setDataSource(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                mMediaPlayer.prepare();
            }
            catch (IllegalArgumentException e) {
            }
            catch (SecurityException e) {
            }
            catch (IllegalStateException e) {
            }
            catch (IOException e) {
            }
            
            mMediaPlayer.start();
        }
    }
    
    private void asyncStopSound() {
        new Handler().postDelayed(new Runnable(){  
            @Override  
            public void run(){
                if(mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            }  
        }, 30000);
    }
}
