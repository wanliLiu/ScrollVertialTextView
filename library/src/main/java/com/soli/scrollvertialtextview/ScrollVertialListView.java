package com.soli.scrollvertialtextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by SoLi on 2016/5/25.
 */
public class ScrollVertialListView extends ViewGroup {

    private ScrollVertialAdapter mAdapter;

    private Scroller mScroller;

    private int itemLayoutResourcesId = 0;
    private int dividerIime = 2000;

    private boolean isScrolling = false;

    private Timer mTimer;

    public ScrollVertialListView(Context context) {
        super(context);
    }

    public ScrollVertialListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScrollVertialListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 运动速度控制
     */
    private class EaseSineInOutInterpolator implements Interpolator {
        public float getInterpolation(float input) {
            return (float) (-1 * 0.5f * (Math.cos(Math.PI * input) - 1));
        }
    }

    /**
     * @param ctx
     */
    private void init(Context ctx, AttributeSet attrs) {
        mTimer = new Timer();
        mScroller = new Scroller(ctx, new EaseSineInOutInterpolator());
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.VertialScrooll);
        try {
            itemLayoutResourcesId = a.getResourceId(R.styleable.VertialScrooll_itemLayout, 0);
            dividerIime = a.getInteger(R.styleable.VertialScrooll_dividerTime, dividerIime);
        } finally {
            a.recycle();
        }

        addChildView();
    }

    /**
     *
     */
    private void addChildView() {
        if (itemLayoutResourcesId > 0) {
            removeAllViews();
            addView(getItemView());
            addView(getItemView());
            addView(getItemView());
            addView(getItemView());
            setViewData();
        }
    }

    /**
     * @return
     */
    private View getItemView() {
        return View.inflate(getContext(), itemLayoutResourcesId, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        int width = 0;

        for (int i = 0; i < getChildCount(); i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
            height += getChildAt(i).getMeasuredHeight();
//            height = getChildAt(i).getMeasuredHeight();
            width = getChildAt(i).getMeasuredWidth();
        }

        final int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int viewMode = MeasureSpec.getMode(widthMeasureSpec);

        if (viewMode == MeasureSpec.EXACTLY) {
            width = viewWidth;
        } else {
            width += getPaddingLeft() + getPaddingRight();
        }

        setMeasuredDimension(width, height + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            final int left = getPaddingLeft();
            final int top = getPaddingTop() + i * getItemHeight();
            final int right = left + child.getMeasuredWidth();
            final int bottom = top + child.getMeasuredHeight();
            child.layout(left, top, right, bottom);
        }
    }

    /**
     * @return
     */
    private int getItemHeight() {
        return getChildCount() > 0 ? getChildAt(0).getMeasuredHeight() : 0;
    }


    /**
     *
     */
    private void setViewData() {
        if (mAdapter != null) {
            for (int i = 0; i < getChildCount(); i++) {
                mAdapter.setView(i, getChildAt(i));
            }
        }
    }

    /**
     * 更新视图内容
     */
    private Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            schedul();
            mAdapter.resetData();
            setViewData();
        }
    };

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
            Log.e("mScroller.getCurrY():", mScroller.getCurrY() + "");

            if (mScroller.getCurrY() == mScroller.getFinalY()) {
                if (isScrolling) {
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            refreshHandler.sendEmptyMessage(0);
                        }
                    }, dividerIime);
                }
            }
        }
    }

    /**
     * @param resId
     */
    public void setItemLayoutResourcesId(int resId) {
        itemLayoutResourcesId = resId;
        addChildView();
    }

    /**
     * @param adapter
     */
    public void setAdapter(ScrollVertialAdapter adapter) {
        mAdapter = adapter;
        addChildView();
    }

    /**
     * @param mdividerIime
     */
    public void startSchedul(int mdividerIime) {
        dividerIime = mdividerIime;
        startSchedul();
    }

    /**
     *
     */
    public void startSchedul() {
        if (!isScrolling) {
            schedul();
        }
    }

    /**
     * @return
     */
    private int getDuration() {
        int duration = getItemHeight();

        if (duration < 1000)
            duration = 1000;

        return duration;
    }

    /**
     *
     */
    private void schedul() {
        isScrolling = true;
        mScroller.startScroll(0, 0, 0, getItemHeight(), getDuration());
        postInvalidate();
    }

    /**
     *
     */
    public void stopSchedul() {
        isScrolling = false;
    }
}
