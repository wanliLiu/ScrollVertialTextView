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
    }

    @Override
    protected void onResume() {
        super.onResume();
        textView.startSchedul();
        scrollView.startSchedul();
    }

    @Override
    protected void onStop() {
        super.onStop();
        textView.stopSchedul();
        scrollView.stopSchedul();
    }

    private List<String> getData() {
        List<String> data = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            data.add("--------------------------" + i + "--------------------------");
        }
        return data;
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
