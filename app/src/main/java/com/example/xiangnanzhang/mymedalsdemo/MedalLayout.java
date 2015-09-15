package com.example.xiangnanzhang.mymedalsdemo;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.ScrollView;

/**
 * Created by XiangnanZhang on 9/09/15.
 */
public class MedalLayout extends LinearLayout {

    /*text部分是否到最大*/
    private boolean isTop;
    /*上面的viewpager*/
    private ViewPager mTopViewPager;
    /*文字描述部分*/
    private LinearLayout mBottomTextContainer;
    /*Y轴坐标*/
    private float mLastY;
    private float mLastX;
    /*文字描述部分的高度*/
    private float mBottonHeight;
    /*scroller*/
    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mMaxVelocity, mMiniVelocity;
    private int mTouchSlop;
    private static int originalHeight;
    private boolean isDragging = false;

    private MedalLayout mMedalLayout;

    public MedalLayout(Context context){
        super(context);
        init(context);
    }

    public MedalLayout(Context context, AttributeSet attSet){
        super(context,attSet);
        init(context);
    }

    private void init(Context context){
        mScroller = new OverScroller(context);
        mMaxVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mMiniVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();


    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        mTopViewPager = (ViewPager)findViewById(R.id.vp_medals);
        mBottomTextContainer = (LinearLayout)findViewById(R.id.ll_textcontainer);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w,h,oldw,oldh);
        mBottonHeight = mBottomTextContainer.getMeasuredHeight();
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev){
//        int action = ev.getAction();
//        float y = ev.getY();
//        Log.i("ev.getY()", Float.toString(y));
//        switch(action){
//            case MotionEvent.ACTION_DOWN:
//                mLastY = y;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float dy = y - mLastY;
//                Log.i("dy",Float.toString(dy));
//                if(Math.abs(dy) < 15 || isTop) {
////                    MotionEvent ev1 = MotionEvent.obtain(ev);
////                    ev1.setAction(MotionEvent.ACTION_DOWN);
////                    dispatchTouchEvent(ev1);
//                }
//                break;
//        }
//        return super.dispatchTouchEvent(ev);
//    }

    public void initVelocityTracker(){
        if(mVelocityTracker == null){
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    public void recycleVelocityTracker(){
        if(mVelocityTracker != null){
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float y = ev.getY();
        float x = ev.getX();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                mLastX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                float dx = x - mLastX;
                if(Math.abs(dy) > 15 && Math.abs(dy) > Math.abs(dx)){
                    mLastX = x;
                    mLastY = y;
                    return true;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        initVelocityTracker();
        mVelocityTracker.addMovement(ev);
        int action = ev.getAction();
        float y = ev.getY();
        float x = ev.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                mLastX = x;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                float dx = x - mLastX;
                if(!isDragging && Math.abs(dy) > mTouchSlop && Math.abs(dy) > Math.abs(dx))
                { isDragging = true;}
                if(isDragging){
                    scrollBy(0, (int) -dy);
//                    invalidate();
                    mLastY = y;
                    mLastX = x;
//                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000,mMaxVelocity);
                int height = getHeight();
                int scrollY = getScrollY();
                int velocityY = (int)mVelocityTracker.getYVelocity();
                Log.i("velocityY", Integer.toString(velocityY));
                if(Math.abs(velocityY) >= mMiniVelocity) {
                    //velocity小于0，向上滑动
                    Log.i("velocity gt mini","greater");
                    boolean toTop = velocityY < 0;
                    scrollToTop(toTop,height,scrollY);
                }
                else {
                    Log.i("velocity lt mini","less");
                    boolean toTop = (height - scrollY) < scrollY;
                    scrollToTop(toTop,height,scrollY);
                }
                recycleVelocityTracker();
                break;


        }
        return super.onTouchEvent(ev);
    }


    private void scrollToTop(final boolean toTop,final int height, final int scrollY) {
        int nIntOffset;
        int h = height, s = scrollY;
        Log.i("height & scrollY",Integer.toString(h)+" , "+Integer.toString(s));
        if (toTop) {
            Log.i("toTop","top");
            nIntOffset=h - s;
        } else {
            Log.i("toBottom","down");
            nIntOffset= -s;
        }
        Log.i("nIntOffset",Integer.toString(nIntOffset));
        int nIntDuration= Math.abs(nIntOffset / 2);
        mScroller.startScroll(0, s, 0, nIntOffset, nIntDuration);
//        invalidate();
//        postInvalidateOnAnimation();
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            scrollTo(0, mScroller.getCurrY());
//            invalidate();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > getHeight()) {
            y = getHeight();
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }
    }

}
