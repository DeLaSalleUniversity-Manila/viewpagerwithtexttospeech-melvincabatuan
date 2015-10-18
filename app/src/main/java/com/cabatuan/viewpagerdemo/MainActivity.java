package com.cabatuan.viewpagerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{


    public final static String TAG = "MainActivity";
    private ViewPager viewPager;
    private MyPagerAdapter myPagerAdapter;
    private TextToSpeech tts = null;
    private boolean ttsLoaded = false;
    private String[] infotext;
    private int currentSlide;
    private HashMap<String, String> params = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Extract String info array for slides
        infotext = getResources().getStringArray(R.array.infotext);

        // TextToSpeech engine initialization + View pager initialization
        tts = new TextToSpeech(this /* context */, this /* listener */);
    }



    @Override
    public void onInit(int status) {

        if(status == TextToSpeech.SUCCESS) {
            ttsLoaded = true;
            tts.setSpeechRate(0.8f);
        }

        int temp = tts.setLanguage(Locale.US);
        if (temp == TextToSpeech.LANG_MISSING_DATA ||
                temp == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e(TAG, "Language is not available.");
            ttsLoaded = false;
        }

        // Initialize view pager
        initializePager();
    }




    public void initializePager(){
        viewPager = (ViewPager)findViewById(R.id.myviewpager);
        myPagerAdapter = new MyPagerAdapter();
        viewPager.setAdapter(myPagerAdapter);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                readMessage(infotext[position], "page" + position);
            }

            @Override
            public void onPageSelected(int position) {
                readMessage(infotext[position], "page" + position);
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    public void readMessage(String message, String utteranceId) {
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, params);
    }

    @Override
    public void onStop() {
        if (tts != null) {
            tts.stop();
        }
        super.onStop();
    }


    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_previous).setEnabled(viewPager.getCurrentItem() > 0);

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (viewPager.getCurrentItem() == myPagerAdapter.getCount() - 1)
                        ? R.string.action_finish
                        : R.string.action_next);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_previous:
                // Go to the previous step in the wizard. If there is no previous step,
                // setCurrentItem will do nothing.
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                return true;

            case R.id.action_next:
                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
                // will do nothing.
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private class MyPagerAdapter extends PagerAdapter {

        int NumberOfPages = 5;

        int pos;

        int[] res = {
                R.drawable.breastcancer,
                R.drawable.breastmap,
                R.drawable.breastcancerph,
                R.drawable.one_in_13,
                R.drawable.pinkribbon};

        int[] backgroundcolor = {
                0xFF101010,
                0xFFFFFFFF,
                0xFFFFFFFF,
                0xFFFFFFFF,
                0xFF101010};


        @Override
        public int getCount() {
            return NumberOfPages;
        }

        public int getPosition() {
            return pos;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            pos = position;

            TextView textView = new TextView(MainActivity.this);
            textView.setTextColor(getResources().getColor(R.color.pink));
            textView.setTextSize(24);
            textView.setGravity(Gravity.CENTER);
            textView.setText(String.valueOf(position + 1) + ". " + infotext[position]);

            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setImageResource(res[position]);
            LayoutParams imageParams = new LayoutParams(
                    LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(imageParams);

            LinearLayout layout = new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            LayoutParams layoutParams = new LayoutParams(
                    LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            layout.setBackgroundColor(backgroundcolor[position]);
            layout.setVerticalGravity(Gravity.CENTER_VERTICAL);
            layout.setLayoutParams(layoutParams);
            layout.addView(textView);
            layout.addView(imageView);

            final int page = position;

            // Listen for clicks
            layout.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    readMessage(infotext[page],"page" + page);
                }});

            container.addView(layout);
            return layout;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout)object);
        }


    }




}
