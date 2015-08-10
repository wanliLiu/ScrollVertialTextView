ScrollVertialTextView
---------------------

custom viewgroup to layout two textviews to scroll in vetial direction,you can easy add to your project,just add a couple of lines.

you mast implements ScrollVertialAdapter as the data soucre,as my sample code display below:

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

at last you just
```java
ScrollVertialTextView.setAdapter(new sampleAdapter(getData()));
