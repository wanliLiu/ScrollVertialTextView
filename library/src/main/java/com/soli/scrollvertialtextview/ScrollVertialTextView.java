package com.soli.scrollvertialtextview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by SoLi on 2015/7/17.
 */
public class ScrollVertialTextView extends ViewGroup {

    private countDown down;

    /**
     * default marign top ,unit dip
     */
    private int marTop = 5;

    /**
     * ScrollVertialTextView height
     */
    private int viewHeigth = 0;
    /**
     * textview height
     */
    private int textHeight = 0;
    /**
     * offset index when scroll
     */
    private int offset = 0;
    /**
     * min offset when scroll
     */
    private int Minoffset = 0;
    /**
     * max offset when scroll
     */
    private int Maxoffset = 0;

    /**
     * speed
     */
    private int SPEED = 1;

    private Context ctx;

    private TextView view1, view2;

    private MyTimer mTimer;


    private int ScheduleTime = 2000;

    /**
     * data index
     */
    private int index = 0;

    /**
     * data adapter,just for user custom data
     */
    private ScrollVertialAdapter adapter;

    private boolean isMeasure = false;

    public ScrollVertialTextView(Context context) {
        super(context);
        Init(context);
    }

    public ScrollVertialTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public ScrollVertialTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Init(context);
    }

    /**
     * @param context
     */
    private void Init(Context context) {
        down = new countDown();
        ctx = context;

        mTimer = new MyTimer(handler);

        addView(view1 = ViewInit(context));
        addView(view2 = ViewInit(context));

        offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marTop, getResources().getDisplayMetrics());
    }

    /**
     *
     *
     */
    private class viewClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (adapter != null) {
                adapter.onItemClick((Integer) v.getTag());
            }
        }
    }

    /**
     * @param mctx
     * @return
     */
    private TextView ViewInit(Context mctx) {
        TextView temp = new TextView(mctx);
        temp.setSingleLine(true);
        temp.setEllipsize(TextUtils.TruncateAt.END);
        temp.setTextColor(getResources().getColor(android.R.color.black));
        temp.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        temp.setOnClickListener(new viewClick());

        return temp;
    }

    /**
     * @param color
     */
    public void setTextColor(int color) {
        if (view1 != null) {
            view1.setTextColor(getResources().getColor(color));
        }

        if (view2 != null) {
            view2.setTextColor(getResources().getColor(color));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (!isMeasure || viewHeigth == 0) {
            isMeasure = true;
            measureChildren(widthMeasureSpec, heightMeasureSpec);

            textHeight = getChildAt(0).getMeasuredHeight();

            viewHeigth = textHeight + 2 * offset;

            Minoffset = offset;
            Maxoffset = -(viewHeigth - 2 * Minoffset);
        }
        setMeasuredDimension(widthMeasureSpec, viewHeigth);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        view1.layout(0, offset, view1.getMeasuredWidth(), textHeight + offset);
        view2.layout(0, view1.getBottom() + Minoffset, view2.getMeasuredWidth(), view1.getBottom() + Minoffset + textHeight);
    }

    /**
     *
     */
    private void getData() {
        index = (index + 1) % adapter.getCount();
        view2.setTag(index);
        view2.setText(adapter.getTextString(index));
    }

    /**
     * @param mAdapter
     */
    public void setAdapter(ScrollVertialAdapter mAdapter) {
        adapter = mAdapter;
        index = 0;

        view1.setText(adapter.getTextString(index));
        view1.setTag(index);
    }

    /**
     * @param scheduleTime
     */
    public void startSchedul(int scheduleTime) {
        ScheduleTime = scheduleTime;
        startSchedul();
    }

    /**
     *
     */
    public void startSchedul() {
        if (adapter != null) {
            if (adapter.getCount() > 1) {
                down.setTime(ScheduleTime, ScheduleTime);
            }
        }
    }

    /**
     * @return
     */
    public boolean isOkay() {
        return adapter == null ? false : true;
    }

    /**
     * 停止滑动显示
     */
    public void stopSchedul() {
        down.cancel();
        mTimer.cancel();

        offset = Minoffset;
        requestFocus();
    }

    class MyTimer {
        private Handler handler;
        private Timer timer;
        private MyTask mTask;

        public MyTimer(Handler handler) {
            this.handler = handler;
            timer = new Timer();
        }

        public void schedule(long period) {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
            mTask = new MyTask(handler);
            timer.schedule(mTask, 0, period);
        }

        public void cancel() {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
        }

        class MyTask extends TimerTask {
            private Handler handler;

            public MyTask(Handler handler) {
                this.handler = handler;
            }

            @Override
            public void run() {
                handler.obtainMessage().sendToTarget();
            }
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            offset -= SPEED;
            if (offset < Maxoffset) {
                offset = Minoffset;
                mTimer.cancel();

                TextView temp = view1;
                view1 = view2;
                view2 = temp;

                requestLayout();
                startSchedul();
            } else {
                requestLayout();
            }
        }

    };

    /**
     * 用了倒计时用
     *
     * @author milanoouser
     */
    private class countDown extends CountDownTimer {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            getData();
            mTimer.schedule(10);
        }
    }


    public abstract class CountDownTimer {

        /**
         * Millis since epoch when alarm should stop.
         */
        private long mMillisInFuture;

        /**
         * The interval in millis that the user receives callbacks
         */
        private long mCountdownInterval;

        private long mStopTimeInFuture;

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountDownTimer() {

        }

        public void setTime(long millisInFuture, long countDownInterval) {

            cancel();
            mMillisInFuture = millisInFuture;
            mCountdownInterval = countDownInterval;

            start();
        }

        /**
         * Cancel the countdown.
         */
        public final void cancel() {
            mHandler.removeMessages(MSG);
        }

        /**
         * Start the countdown.
         */
        public synchronized final CountDownTimer start() {
            if (mMillisInFuture <= 0) {
                onFinish();
                return this;
            }
            mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
            mHandler.sendMessage(mHandler.obtainMessage(MSG));
            return this;
        }


        /**
         * Callback fired on regular interval.
         *
         * @param millisUntilFinished The amount of time until finished.
         */
        public abstract void onTick(long millisUntilFinished);

        /**
         * Callback fired when the time is up.
         */
        public abstract void onFinish();


        private static final int MSG = 1;


        // handles counting down
        private Handler mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                synchronized (CountDownTimer.this) {
                    final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

                    if (millisLeft <= 0) {
                        onFinish();
                    } else if (millisLeft < mCountdownInterval) {
                        // no tick, just delay until done
                        sendMessageDelayed(obtainMessage(MSG), millisLeft);
                    } else {
                        long lastTickStart = SystemClock.elapsedRealtime();
                        onTick(millisLeft);

                        // take into account user's onTick taking time to execute
                        long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();

                        // special case: user's onTick took more than interval to
                        // complete, skip to next interval
                        while (delay < 0) delay += mCountdownInterval;

                        sendMessageDelayed(obtainMessage(MSG), delay);
                    }
                }
            }
        };
    }
}
