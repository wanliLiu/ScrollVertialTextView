package com.soli.scrollvertialtextview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by SoLi on 2016/5/25.
 */
public class ScrollVertialListView extends ViewGroup {

    public ScrollVertialListView(Context context) {
        super(context);
    }

    public ScrollVertialListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollVertialListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public void computeScroll() {

    }
}
