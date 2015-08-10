package com.soli.scrollvertialtextview;

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
     * @param position
     * @return
     */
     String getTextString(int position);

    /**
     * item click listener
     * @param position
     */
     void onItemClick(int position);
}
