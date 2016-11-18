package com.soli.scrollvertialtextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * Created by SoLi on 2016/5/25.
 */
public class MarqueeLayout extends HorizontalScrollView {

    private ScrollVertialAdapter mAdapter;

    private Scroller mScroller;

    private int itemLayoutResourcesId = 0;
    private int dividerIime = 2000;

    private int scrollWidth = 0;
    private boolean isScrolling = false;

    public MarqueeLayout(Context context) {
        super(context);
    }

    public MarqueeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MarqueeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
        setHorizontalScrollBarEnabled(false);
        mScroller = new Scroller(ctx, new LinearInterpolator());
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.VertialScrooll);
        try {
            itemLayoutResourcesId = a.getResourceId(R.styleable.VertialScrooll_itemLayout, 0);
            dividerIime = a.getInteger(R.styleable.VertialScrooll_dividerTime, dividerIime);
        } finally {
            a.recycle();
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    /**
     * @return
     */
    private LinearLayout addRootLayout() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        addView(layout);
        return layout;
    }

    /**
     * 设置滚动视图，两个循环
     */
    private void addChildView() {
        if (mAdapter == null) return;

        stopSchedul();
        if (!mScroller.isFinished())
            mScroller.forceFinished(true);

        if (itemLayoutResourcesId > 0) {
            removeAllViews();
            LinearLayout layout = addRootLayout();
            for (int i = 0; i < mAdapter.getCount(); i++) {
                View view = getItemView();
                mAdapter.setView(i, view);
                layout.addView(view);
            }
        }
    }

    /**
     * @return
     */
    private View getItemView() {
        return View.inflate(getContext(), itemLayoutResourcesId, null);
    }

    /**
     * @param view
     * @return
     */
    private int getViewWidth(View view) {
        if (view instanceof ViewGroup) {
            return getItemWidth(view);
        } else if (view instanceof TextView) {
            TextView text = (TextView) view;
            if (!TextUtils.isEmpty(text.getText().toString())) {
                Paint mPaint;
                // init helper
                mPaint = new Paint();
                mPaint.setAntiAlias(true);
                mPaint.setStrokeWidth(1);
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                mPaint.setTextSize(text.getTextSize());
                mPaint.setTypeface(text.getTypeface());
                float mTextWidth = mPaint.measureText(text.getText().toString());
                return (int) mTextWidth + text.getPaddingLeft() + text.getPaddingRight();
            }
        }
        return view.getMeasuredWidth() + view.getPaddingLeft() + view.getPaddingRight();
    }


    /**
     * @param view
     */
    private int getItemWidth(View view) {
        int width = 0;
        if (view instanceof ViewGroup) {
            ViewGroup chil = (ViewGroup) view;
            width += chil.getPaddingLeft() + chil.getPaddingRight();
            for (int i = 0; i < chil.getChildCount(); i++) {
                width += getViewWidth(chil.getChildAt(i));
            }
        } else {
            width += getViewWidth(view);
        }

        return width;
    }

    /**
     *
     */
    private void getScrollItemWidth() {
        scrollWidth = 0;
        ViewGroup child = (ViewGroup) getChildAt(0);
        for (int i = 0; i < child.getChildCount(); i++) {
            View view = child.getChildAt(i);
            int width = getItemWidth(view);
            scrollWidth += width;
        }
        Log.e("scrollWidth", scrollWidth + "");
        Log.e("Width", getMeasuredWidth() + "");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        getScrollItemWidth();
    }


    /**
     * 获取滑动的高度
     */
    private int getScroollDistance() {
        return getChildCount() > 0 ? scrollWidth - getMeasuredWidth() : 0;
    }

    /**
     * 更新视图内容
     */
    private Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            postdelay();
        }
    };

    /**
     *
     */
    private void postdelay() {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollTo(0, 0);
                invalidate();
                if (isScrolling) {
                    schedul();
                }
            }
        }, 500);
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
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
        return getScroollDistance() * 5;
    }

    /**
     *
     */
    private void schedul() {
        if (mAdapter == null)
            throw new IllegalArgumentException("You must set ScrollVertialAdapter before startSchedul");

        if (scrollWidth > getMeasuredWidth()) {
            isScrolling = true;
            refreshHandler.sendEmptyMessageDelayed(0, getDuration());
            mScroller.startScroll(0, 0, getScroollDistance(), 0, getDuration());
            invalidate();
        }
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