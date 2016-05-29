ScrollVertialListView
---------------------



and now i inprove the Widget
ScrollVertialListView

![](https://github.com/wanliLiu/ScrollVertialTextView/raw/master/Images/demo1.gif)

you can custom your scrooller view throw attrs like app:itemLayout="@layout/scrooller_item" or

    /**
     * 设置视图的另一种方式
     * @param resId
     */
    public void setItemLayoutResourcesId(int resId) {
        itemLayoutResourcesId = resId;
        addChildView();
    }


use the widget is every simple like


    <com.soli.scrollvertialtextview.ScrollVertialListView
        android:id="@+id/scrollView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/color_gray"
        android:layout_marginTop="20dp"
        app:dividerTime="2000"
        app:itemLayout="@layout/scrooller_item" />

you must set adapter before schedule


-ScrollVertialTextView
not recommend

![](https://github.com/wanliLiu/ScrollVertialTextView/raw/master/Images/demo.gif)

custom viewgroup to layout two textviews to scroll in vetial direction,you can easy add to your project,just add a couple of lines.

you mast implements ScrollVertialAdapter as the data soucre,as my sample code display below:

         private class sampleAdapter implements ScrollVertialAdapter {

                private List<String> data;

                public sampleAdapter(List<String> temp) {
                    data = temp;
                }

                @Override
                public int getCount() {
                    return data.size();
                }

                @Override
                public String getTextString(int position) {
                    return data.get(position);
                }

                @Override
                public void onItemClick(int position) {
                    Toast.makeText(MainActivity.this, "You click:\n" + getTextString(position), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void setView(int position, View view) {
        //            if (position % 2 == 0)
        //            {
        //                view.setBackgroundColor(getResources().getColor(R.color.color_green));
        //            }else
        //            {
        //                view.setBackgroundColor(getResources().getColor(R.color.color_blue));
        //            }
                    ((TextView) view.findViewById(R.id.title)).setText(getTextString(position));
                }

                @Override
                public void exchangeDataPosition() {
                    String temp = data.get(0);
                    data.remove(0);
                    data.add(temp);
                }
            }

at last you just

        ScrollVertialTextView.setAdapter(new sampleAdapter(getData()));

Done!
