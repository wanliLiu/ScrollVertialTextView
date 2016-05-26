package com.soli.scrollvertialtextview;

import android.content.Context;
import android.content.res.TypedArray;
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

    private final String TAG = ScrollVertialListView.class.getSimpleName();

    private ScrollVertialAdapter mAdapter;
    private View viewTop, viewBottom;
    private Scroller mScroller;
    private int itemLayoutResourcesId = 0;
    private int dividerIime = 2000;
    private int currentPosition = 0;
    private int scrollOffset = 0;

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
     *运动速度控制
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
            addView(viewTop = getItemView());
            addView(viewBottom = getItemView());
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
//        for (int i = 0; i < getChildCount(); i++) {
//            final View child = getChildAt(i);
//            final int left = getPaddingLeft();
//            final int top = getPaddingTop() + i * getItemHeight();
//            final int right = left + child.getMeasuredWidth();
//            final int bottom = top + child.getMeasuredHeight();
//            child.layout(left, top, right, bottom);
//        }
        if (getChildCount() > 0) {
            final int left = getPaddingLeft();
            final int top = getPaddingTop() - scrollOffset;
            viewTop.layout(left, top, left + viewTop.getMeasuredWidth(), top + viewTop.getMeasuredHeight());
            viewBottom.layout(left, viewTop.getBottom(), left + viewBottom.getMeasuredWidth(), viewTop.getBottom() + viewBottom.getMeasuredHeight());
        }
    }

    /**
     * @return
     */
    private int getItemHeight() {
        return getChildCount() > 0 ? getChildAt(0).getMeasuredHeight() : 0;
    }

    /**
     * 获取需要同时滑动的items个数
     *
     * @return
     */
    private int getDisplayItems() {
        return getChildCount();
    }

    /**
     *
     */
    private void exchangeViewPosition() {
        scrollOffset = 0;
        View temp = viewTop;
        viewTop = viewBottom;
        viewTop = temp;
        requestLayout();
    }

    private int lastViewTop, lastViewBottom;
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {

            Log.e(TAG, "mScroller.getCurrY:" + mScroller.getCurrY() + "");
            Log.e(TAG, "viewTop.getTop:" + viewTop.getTop() + "");
            //use offsetTopAndBottom
            lastViewTop = viewTop.getTop();
            lastViewBottom = viewBottom.getTop();
            viewTop.offsetTopAndBottom(-mScroller.getCurrY());
            viewBottom.offsetTopAndBottom(-mScroller.getCurrY());
            viewTop.offsetTopAndBottom(lastViewTop);
            viewBottom.offsetTopAndBottom(lastViewBottom);
            postInvalidate();

            //use scrollto
//            scrollTo(0, mScroller.getCurrY());
//            postInvalidate();
//            Log.e(TAG, "getScrollY:" + getScrollY());


            //user layout
//            scrollOffset = mScroller.getCurrY();
//            requestLayout();
//            Log.e(TAG, "scrollOffset:" + scrollOffset);

            if (mScroller.getCurrY() == mScroller.getFinalY()) {
//                exchangeViewPosition();
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
//        removeAllViews();
//
//        addView(viewTop = mAdapter.getItemView());
//        addView(viewBottom = mAdapter.getItemView());
    }

    public void setOnItemClickListener() {

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
        mTimer = new Timer();
//        mTimer.scheduleAtFixedRate(new schedulTask(), 0, dividerIime);

        mScroller.startScroll(0, 0, 0, getItemHeight(), 2000);
        postInvalidate();

    }

    /**
     *
     */
    public void stopSchedul() {
        viewTop.offsetTopAndBottom(-viewTop.getTop() + getItemHeight());
        viewBottom.offsetTopAndBottom(-viewBottom.getTop() + getItemHeight());
    }
    /**
     *
     */
    private class schedulTask extends TimerTask {
        @Override
        public void run() {
            Log.e(TAG, "开始滑动");
        }
    }
}
