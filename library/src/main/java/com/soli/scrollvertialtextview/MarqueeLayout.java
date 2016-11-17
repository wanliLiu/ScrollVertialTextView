package com.soli.scrollvertialtextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * Created by SoLi on 2016/5/25.
 */
public class MarqueeLayout extends LinearLayout {

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
        setOrientation(HORIZONTAL);
//        mScroller = new Scroller(ctx);
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
        setRightItemSizeWidth();
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
//        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        params.gravity = Gravity.CENTER_HORIZONTAL;
//        ScrollView mScrollView = new ScrollView(getContext());
//        mScrollView.addView(View.inflate(getContext(), itemLayoutResourcesId, null), new ScrollView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//        mScrollView.setLayoutParams(params);

//        return mScrollView;

        return View.inflate(getContext(), itemLayoutResourcesId, null);
    }

    /**
     * @param view
     * @return
     */
    private int getViewWidth(View view) {
        if (view instanceof ViewGroup) {
            return dealTextView(view);
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

                ViewGroup.LayoutParams lp = text.getLayoutParams();
                lp.width = (int) mTextWidth;
                text.setLayoutParams(lp);
                return (int) mTextWidth;
            }
        }
        return view.getWidth();
    }


    /**
     * @param view
     */
    private int dealTextView(View view) {
        int width = 0;
        if (view instanceof ViewGroup) {
            ViewGroup chil = (ViewGroup) view;
            for (int i = 0; i < chil.getChildCount(); i++) {
                width += getViewWidth(chil.getChildAt(i));
            }
        } else {
            width = getViewWidth(view);
        }

        return width;
    }

    /**
     *
     */
    private void setRightItemSizeWidth() {
        scrollWidth = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int width = dealTextView(view);
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            params.width = width;
            scrollWidth += width;
            view.setLayoutParams(params);
        }
        Log.e("scrollWidth",scrollWidth + "");
//        LayoutParams params = (LayoutParams) this.getLayoutParams();
//        params.width = scrollWidth;
//        this.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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

        setRightItemSizeWidth();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        //        int right = 0;
//        for (int i = 0; i < getChildCount(); i++) {
//            final View child = getChildAt(i);
//            final int left = getPaddingLeft() + right;
//            final int top = getPaddingTop();
//            int childWidth = dealTextView(child);
//            right += (childWidth > getMeasuredWidth() ? childWidth : getMeasuredWidth()) + getPaddingRight();
//            final int bottom = top + child.getMeasuredHeight();
//            child.layout(left, top, right, bottom);
//        }
//        scrollWidth = right;

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
//        return getScroollDistance() * 10;
        return getScroollDistance() * 5;
    }

    /**
     *
     */
    private void schedul() {

        if (mAdapter == null)
            throw new IllegalArgumentException("You must set ScrollVertialAdapter before startSchedul");

        isScrolling = true;
        refreshHandler.sendEmptyMessageDelayed(0,  getDuration());//dividerIime +
        mScroller.startScroll(0, 0, getScroollDistance(), 0, getDuration());
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