package soli.scrollvertialtextview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.soli.scrollvertialtextview.MarqueeLayout;
import com.soli.scrollvertialtextview.ScrollVertialAdapter;
import com.soli.scrollvertialtextview.ScrollVertialListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ScrollVertialListView scrollView;

    private MarqueeLayout marqueeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollView = (ScrollVertialListView) findViewById(R.id.scrollView);
        scrollView.setAdapter(new sampleAdapter(getData()));
//        scrollView.setItemLayoutResourcesId(R.layout.scrooller_item);

        ((ScrollVertialListView) findViewById(R.id.scrollView1)).setAdapter(new sampleAdapter(getData()));
        ((ScrollVertialListView) findViewById(R.id.scrollView2)).setAdapter(new sampleAdapter(getData()));

        marqueeLayout = (MarqueeLayout)findViewById(R.id.marqueeLayout);
        marqueeLayout.setAdapter(new sampleAdapter(getData()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        scrollView.startSchedul();
        ((ScrollVertialListView) findViewById(R.id.scrollView1)).startSchedulDelay();
        ((ScrollVertialListView) findViewById(R.id.scrollView2)).startSchedulDelay(5000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        scrollView.stopSchedul();
    }

    private List<String> getData() {
        List<String> data = new ArrayList<String>();
//        for (int i = 0; i < 10; i++) {
//            data.add("--------------------------" + i + "--------------------------");
//        }
//        data.add("关于售后");
//        data.add("购买了保税区的商品，什么时候发货？");
//        data.add("关于返现");
//        data.add("购买了荷兰仓的商品，什么时候发货？");
//        data.add("我购买的成都仓商品，什么时候发货？");
//        data.add("用产品出现过敏，怎么办？");
        data.add("1----有时候用护肤品时，会感觉皮肤刺痛，这是怎么回事？----------------------------结束");
        data.add("2----购买的护肤品和之前同款的味道不同（或者和专柜的  --------味道不同）味道不同）味道不同）------结束");
        data.add("3----为什么我买的商品上没有生产日期或者到期日期？-----------------味道不同）味道不同）味道不同）味道不同）味道不同）味道不同）-----------结束");
//        data.add("为什么有些产品没有塑封呢？");
//        data.add("为何有时扫条形码，无法扫出来？");
//        data.add("任务的奖励何时发放？");
        return data;
    }

    public void Test(View view) {
        if (!marqueeLayout.isScrolling())
            marqueeLayout.startSchedul();
        else
            marqueeLayout.stopSchedul();
    }

    public void onViewClick(View view) {
        if (!scrollView.isScrolling())
            scrollView.startSchedulDelay();
        else
            scrollView.stopSchedul();
    }

    /**
     *
     */
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
}
