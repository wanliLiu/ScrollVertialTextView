package soli.scrollvertialtextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.soli.scrollvertialtextview.ScrollVertialAdapter;
import com.soli.scrollvertialtextview.ScrollVertialTextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ScrollVertialTextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView = (ScrollVertialTextView)findViewById(R.id.sample);
        textView.setAdapter(new sampleAdapter(getData()));
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

    private List<String> getData()
    {
        List<String> data = new ArrayList<String>();
        for(int i = 0; i < 10; i++)
        {
            data.add("Text content display at " + i + "max display in one line,of course you can custom");
        }

        return data;
    }


    private class sampleAdapter implements ScrollVertialAdapter{

        private List<String> data;

        public sampleAdapter(List<String> temp)
        {
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
            Toast.makeText(MainActivity.this,"You click:\n" + getTextString(position) ,Toast.LENGTH_SHORT).show();
        }
    }
}
