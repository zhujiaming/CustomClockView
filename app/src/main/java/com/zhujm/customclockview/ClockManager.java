package com.zhujm.customclockview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.format.DateUtils;
import android.view.View;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by zhujiaming on 2017/12/27.
 */

public class ClockManager {

    private Calendar mTime;
    private ClockListener clockListener;
    private TimeZone mTimeZone;
    private View holdView;

    public ClockManager(View holdView, ClockListener clockListener) {
        this.clockListener = clockListener;
        this.holdView = holdView;
    }

    private final Runnable mClockTick = new Runnable() {
        @Override
        public void run() {
            onTimeChanged();
            final long now = System.currentTimeMillis();
            final long delay = DateUtils.SECOND_IN_MILLIS - now % DateUtils.SECOND_IN_MILLIS;
            holdView.postDelayed(this, delay);
        }
    };

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mTimeZone == null && Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                final String tz = intent.getStringExtra("time-zone");
                mTime = Calendar.getInstance(TimeZone.getTimeZone(tz));
            }
            onTimeChanged();
        }
    };

    private void onTimeChanged() {
        mTime.setTimeInMillis(System.currentTimeMillis());
        int hour = mTime.get(Calendar.HOUR);
        int min = mTime.get(Calendar.MINUTE);
        int sec = mTime.get(Calendar.SECOND);
        if (clockListener != null) {
            clockListener.onTimeChanged(hour, min, sec);
        }
    }

    public void setTimeZone(String timeZoneId) {
        mTimeZone = TimeZone.getTimeZone(timeZoneId);
        mTime.setTimeZone(mTimeZone);
        onTimeChanged();
    }

    public void attachedToWindow() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        holdView.getContext().registerReceiver(mIntentReceiver, filter);
        mTime = Calendar.getInstance(mTimeZone != null ? mTimeZone : TimeZone.getDefault());
        onTimeChanged();
        mClockTick.run();
    }

    public void detachedFromWindow() {
        holdView.getContext().unregisterReceiver(mIntentReceiver);
        holdView.removeCallbacks(mClockTick);
    }

    public interface ClockListener {
        void onTimeChanged(int hour, int min, int sec);
    }

}
