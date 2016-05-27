package soli.scrollvertialtextview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.soli.scrollvertialtextview.ScrollVertialAdapter;
import com.soli.scrollvertialtextview.ScrollVertialListView;
import com.soli.scrollvertialtextview.ScrollVertialTextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ScrollVertialTextView textView;
    private ScrollVertialListView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView = (ScrollVertialTextView) findViewById(R.id.sample);
        textView.setAdapter(new sampleAdapter(getData()));

        scrollView = (ScrollVertialListView) findViewById(R.id.scrollView);
        scrollView.setAdapter(new sampleAdapter(getData()));
//        scrollView.setItemLayoutResourcesId(R.layout.scrooller_item_one);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.startSchedul();
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.stopSchedul();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        textView.startSchedul();
    }

    @Override
    protected void onStop() {
        super.onStop();
        textView.stopSchedul();
    }

    private List<String> getData() {
        List<String> data = new ArrayList<String>();
        for (int i = 0; i < 4; i++) {
            data.add("-------------" + i + "-------------");
        }
//        data.add("数据开始了哦");
//        data.add("不是第三代上课了打瞌睡了打开");
//        data.add("你说啥东西都是，不住地哦啊");
//        data.add("你什么时候开始，我知道就会过来");
//        data.add("不晓得哦");

        return data;
    }

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
        public void resetData() {
            String temp = data.get(0);
            data.remove(0);
            data.add(temp);
        }
    }
}
