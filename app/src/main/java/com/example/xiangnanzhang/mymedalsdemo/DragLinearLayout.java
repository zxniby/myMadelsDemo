package com.example.xiangnanzhang.mymedalsdemo;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.OverScroller;


/**
 * Created by mik_eddy on 15/9/2.
 */
public class DragLinearLayout extends LinearLayout {

    private OverScroller mScroller;
    private float mFloatLastY,mFloatLastX;//最后一次获取到的Y坐标
    private int mTouchSlop;//最小滑动触发阀值
    private boolean mBoolDragging = false;//是否处在拖动状态
    private int mMaximumVelocity;//最大手势速率
    private int mMinimumVelocity = 4000;//最小触发滚屏手势速率
    private VelocityTracker mVelocityTracker;
    private ViewPager mViewPager;
    private boolean isBlockVP = false;
    private int mScrollY;
    private int mHeight;


    public DragLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public DragLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mScroller = new OverScroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();

    }

    public void setViewPager(ViewPager viewPager){
        this.mViewPager = viewPager;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        PalLog.printD("parent==>dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float y = ev.getY();
        float x = ev.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mFloatLastY = y;
                mFloatLastX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mFloatLastY;
                float dx = x - mFloatLastX;
                if (Math.abs(dy) > 15 && Math.abs(dy) > Math.abs(dx)) {
                    mFloatLastX = x;
                    mFloatLastY = y;
                    mBoolDragging = true;
                    return true;
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mBoolDragging = false;
                break;
        }


        return super.onInterceptTouchEvent(ev);
    }

    private void blockViewPager(MotionEvent event){

        MotionEvent ev1  = event.obtain(event);
        ev1.setAction(MotionEvent.ACTION_CANCEL);
        mViewPager.onTouchEvent(ev1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();
        float x = event.getX();


        getVelocityTracker().addMovement(event);
        if(!mBoolDragging){
            mViewPager.dispatchTouchEvent(event);
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                mFloatLastY = y;
                mFloatLastX = x;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mFloatLastY;
                float dx = x - mFloatLastX;
                if (!mBoolDragging && Math.abs(dy) > mTouchSlop && Math.abs(dy) > Math.abs(dx)) {
                    mBoolDragging = true;
                }
                if (mBoolDragging) {
                    int nIntOffset = (int) -dy;
                    scrollBy(0, nIntOffset);
                    invalidate();
                    mFloatLastY = y;
                    mFloatLastX = x;//只有在mBoolDragging==true.即滑动状态时才计算新的偏移
                }
                PalLog.printD("scroll:" + getScrollY());
                mHeight = getHeight();
                mScrollY = getScrollY();
                break;
            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_OUTSIDE:
                PalLog.printD("ACTION_CANCEL");
                mScrollY = getScrollY();
                mHeight = getHeight();
                mBoolDragging = false;

                getVelocityTracker().computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                recycleVelocityTracker();
                PalLog.printI("MaxVelocityY: "+Integer.toString(mMaximumVelocity));
                PalLog.printI("MiniVelocityY: "+Integer.toString(mMinimumVelocity));
                PalLog.printI("velocityY: "+Integer.toString(velocityY));
                PalLog.printI("mHeight: "+Integer.toString(mHeight));
                PalLog.printI("mScrollY: "+Integer.toString(mScrollY));
                //根据速率来判断应该滑到顶部还是底部
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    boolean toTop = velocityY > 0;
                    PalLog.printI("greater than mMinimumVelocity");
                    scrollToTop(toTop);
//                    mScroller.fling(0, mScrollY, 0,-velocityY*2,0,0,0,mHeight);
                    invalidate();
                } else {
                    //根据已经滑动的距离来判断
                    PalLog.printI("less than minivelocity");
                    PalLog.printI("Height-ScrollY: " + (mHeight - mScrollY));
                    //当距离底部距离>距离顶部距离的时候:向顶部滑动,反之向底部滑
//                    scrollToTop((mHeight - mScrollY) > mScrollY);
                    if(mHeight - mScrollY > mScrollY){
                        mScroller.fling(0,mScrollY,0,-mMinimumVelocity,0,0,0,mHeight);
                    }
                    else
                        mScroller.fling(0,mScrollY,0,mMinimumVelocity,0,0,0,mHeight);
                    invalidate();

                }


                return true;

        }
        return super.onTouchEvent(event);
    }

    /**
     * 控制整体滑动到顶部
     *
     * @param nBooltoTop true:滑动到顶部 false:滑动到底部
     */
    private void scrollToTop(boolean nBooltoTop) {
        int nIntOffset;
        PalLog.printI("nBoolToTop: " + nBooltoTop);
        if (nBooltoTop) {
            nIntOffset=0 - mScrollY;
        } else {
            nIntOffset=mHeight - mScrollY;
        }
        PalLog.printI("Height in scrollToTop: " + Integer.toString(mHeight));
        PalLog.printI("mScrollY  in scrollToTop: " + Integer.toString(mScrollY));
        PalLog.printI("offset: " + Integer.toString(nIntOffset));
        int nIntDuration= Math.abs(nIntOffset / 2);
        mScroller.startScroll(0, mScrollY, 0, nIntOffset, nIntDuration);

    }

    /**
     * 重写scrollTo防止滑过头
     *
     * @param x
     * @param y
     */
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


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

    private VelocityTracker getVelocityTracker() {
        if (mVelocityTracker == null) mVelocityTracker = VelocityTracker.obtain();
        return mVelocityTracker;
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}
