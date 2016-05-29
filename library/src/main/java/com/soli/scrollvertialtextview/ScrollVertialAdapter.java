package com.soli.scrollvertialtextview;

import android.view.View;

/**
 * Created by SoLi on 2015/7/20.
 */
public abstract interface ScrollVertialAdapter {

    /**
     * the total num that you will display
     */
    int getCount();

    /**
     * get textdisplay
     *
     * @param position
     * @return
     */
    String getTextString(int position);

    /**
     * item click listener
     *
     * @param position
     */
    void onItemClick(int position);

    /**
     * 设置视图的内容显示
     *
     * @param position
     * @param view
     */
    void setView(int position, View view);

    /**
     * 改变数据位置
     * 这个很关键，主要是通过改变数据的位置实现循环变化的
     * 思维是，把列表第一个数据放到数据列表最后
     */
    void exchangeDataPosition();
}
