package com.soli.scrollvertialtextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by SoLi on 2016/5/25.
 */
public class ScrollVertialListView extends ViewGroup {

    private ScrollVertialAdapter mAdapter;

    private Scroller mScroller;

    private int itemLayoutResourcesId = 0;
    private int dividerIime = 2000;

    private boolean isScrolling = false;

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
     * 设置视图内容
     */
    private void setViewContentData() {
        if (mAdapter == null) return;

        for (int pos = 0; pos < getChildCount(); pos++) {
            View view = getChildAt(pos);
            mAdapter.setView(pos, view);
            view.setTag(pos);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.onItemClick((Integer) v.getTag());
                }
            });
        }
    }

    /**
     * 设置滚动视图，两个循环
     */
    private void addChildView() {
        if (itemLayoutResourcesId > 0) {
            removeAllViews();
            addView(getItemView());
            addView(getItemView());
            setViewContentData();
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
        int width = 0;
        int height = 0;

        for (int i = 0; i < getChildCount(); i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
//            height += getChildAt(i).getMeasuredHeight();
            height = getChildAt(i).getMeasuredHeight();
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
            final int top = getPaddingTop() + i * getScroollItemHeight();
            final int right = getMeasuredWidth() - getPaddingRight();
            final int bottom = top + child.getMeasuredHeight();
            child.layout(left, top, right, bottom);
        }
    }

    /**
     * 获取滑动的高度
     */
    private int getScroollItemHeight() {
        return getChildCount() > 0 ? getChildAt(0).getMeasuredHeight() : 0;
    }

    /**
     * 更新视图内容
     */
    private Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mAdapter.exchangeDataPosition();
            setViewContentData();
            if (isScrolling) {
                schedul();
            } else {
                scrollTo(0, 0);
                invalidate();
            }
        }
    };


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * 设置视图的另一种方式
     *
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
            setViewContentData();
            schedul();
        }
    }

    /**
     *
     */
    public void startSchedulDelay() {
        startSchedulDelay(dividerIime);
    }

    /**
     * @param delayTime
     */
    public void startSchedulDelay(int delayTime) {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSchedul();
            }
        }, delayTime);
    }

    /**
     * @return
     */
    private int getDuration() {
        return getScroollItemHeight() * 10;
    }

    /**
     *
     */
    private void schedul() {

        if (mAdapter == null)
            throw new IllegalArgumentException("You must set ScrollVertialAdapter before startSchedul");

        isScrolling = true;
        refreshHandler.sendEmptyMessageDelayed(0, dividerIime + getDuration());
        mScroller.startScroll(0, 0, 0, getScroollItemHeight(), getDuration());
        invalidate();
    }

    /**
     * @return
     */
    public boolean isScrolling() {
        return isScrolling;
    }

    /**
     *
     */
    public void stopSchedul() {
        isScrolling = false;
    }
}