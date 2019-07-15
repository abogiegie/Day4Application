package com.bytedance.clockapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.bytedance.clockapplication.widget.Clock;

import java.lang.ref.WeakReference;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "Homework";
    private final static int secondAdd = 1;
    private Clock mClockView = null;
    private TimingThread timingThread;

    private static class ClockHandler extends Handler{
        private WeakReference<MainActivity> activityWeakReference;
        private Clock mClock;

        private ClockHandler (MainActivity activity, Clock clock) {
            activityWeakReference = new WeakReference<>(activity);
            mClock = clock;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = activityWeakReference.get();
            if(activity != null){
                if(msg.what == secondAdd){
                    if(mClock != null){
                        mClock.setShowAnalog(mClock.isShowAnalog());
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View mRootView = findViewById(R.id.root);
        mClockView = findViewById(R.id.clock);

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClockView.setShowAnalog(!mClockView.isShowAnalog());
            }
        });
        ClockHandler clockHandler = new ClockHandler(this, mClockView);
        timingThread = new TimingThread(clockHandler);

        timingThread.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timingThread.interrupt();
    }

    public class TimingThread extends Thread {
        ClockHandler clockHandler;

        TimingThread(ClockHandler clockHandler) {
            this.clockHandler = clockHandler;
        }

        @Override
        public void run() {
            int preMoment = Calendar.getInstance().get(Calendar.SECOND);
            int thisMoment;
            while(!isInterrupted()){
                try {
                    thisMoment = Calendar.getInstance().get(Calendar.SECOND);
                    if(preMoment != thisMoment){
                        clockHandler.sendEmptyMessage(secondAdd);
                        Log.d(TAG, "run: " + thisMoment);
                        preMoment = thisMoment;
                        sleep(970);
                    }
                }catch (Exception e){
                    Log.d(TAG, "" + e);
                }
            }
        }
    }

}
