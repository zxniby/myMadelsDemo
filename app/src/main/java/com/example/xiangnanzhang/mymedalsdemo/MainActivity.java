package com.example.xiangnanzhang.mymedalsdemo;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

public class MainActivity extends Activity{

    private LinearLayout ll_textcontainer,ll_vpcontainer;
    private ViewPager mViewPager;
    private TextView tv_teamname;

//    private MedalLayout mMedalLayout;
    private DragLinearLayout dragLinearLayout;


    /* image resource id*/
    private int[] mImageIds= new int[]{R.drawable.spurs,R.drawable.celtes,R.drawable.cavaliers,R.drawable.bucks,R.drawable.bulls};

    /*图片数组*/
    private ImageView[] mImageViews;

    private int[] textIds = new int[]{R.string.spurs,R.string.celtes,R.string.cavaliers,R.string.bucks,R.string.bulls};

    private TextView[] textViews = new TextView[textIds.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        tv_teamname = (TextView)findViewById(R.id.tv_teamname);
        tv_teamname.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mViewPager.dispatchTouchEvent(event);
            }
        });
        mViewPager = (ViewPager)findViewById(R.id.vp_medals);
        MedalsAdapter mAdapter = new MedalsAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                tv_teamname.setText("");
//                Log.e("text","null");
            }

            @Override
            public void onPageSelected(int position) {
                tv_teamname.setText(textIds[position]);
                Log.e("text", Integer.toString(textIds[position]));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPageMargin(10);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        ll_textcontainer = (LinearLayout)findViewById(R.id.ll_textcontainer);
//        ll_vpcontainer = (LinearLayout)findViewById(R.id.ll_vpContainer);


        dragLinearLayout = (DragLinearLayout)findViewById(R.id.ll_Medalcontainer);
        dragLinearLayout.setViewPager(mViewPager);
        dragLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                dragLinearLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ll_textcontainer.getLayoutParams().height = dragLinearLayout.getHeight();
            }
        });

//        dragLinearLayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return mViewPager.dispatchTouchEvent(event);
//            }
//        });



    }

    private void init(){
        mImageViews = new ImageView[mImageIds.length];
        for(int i = 0; i < mImageIds.length; i++) {
            mImageViews[i] = new ImageView(this);
            mImageViews[i].setImageResource(mImageIds[i]);
        }

        for(int i = 0; i < textIds.length; i++){
            textViews[i] = new TextView(this);
            textViews[i].setText(textIds[i]);
        }

    }




    public class TextAdapter extends PagerAdapter{

        @Override
        public boolean isViewFromObject(View view, Object object){return view == object;}

        @Override
        public int getCount(){return textIds.length;}

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(textViews[position],0);
            return textViews[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(textViews[position]);
        }


    }


    public class MedalsAdapter extends PagerAdapter {


        @Override
        public boolean isViewFromObject(View view, Object obj){
            return view == obj;
        }

        @Override
        public int getCount(){
            return mImageIds.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mImageViews[position],0);
            return mImageViews[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object){
            container.removeView(mImageViews[position]);
        }

    }

}
