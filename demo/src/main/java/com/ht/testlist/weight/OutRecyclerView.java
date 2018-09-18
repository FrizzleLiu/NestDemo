package com.ht.testlist.weight;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.ht.testlist.activity.MainActivity;

/**
 * Created by Administrator on 2018/9/3.
 * Description : OutRecyclerView
 */

public class OutRecyclerView extends RecyclerView  {
    private boolean isNeedIntercept = false;
    private float downX ;    //按下时 的X坐标
    private float downY ;    //按下时 的Y坐标

    boolean isStick=false;

    public OutRecyclerView(Context context) {
        super(context);
    }

    public OutRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OutRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        float x= e.getX();
        float y = e.getY();
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                //将按下时的坐标存储
                downX = x;
                downY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                //获取到距离差
                float dx= x-downX;
                float dy = y-downY;
                //通过距离差判断方向
                int orientation = getOrientation(dx, dy);
                switch (orientation) {
                        //左右滑动交给ViewPager处理
                    case 'r':
                        setNeedIntercept(false);
                        break;
                    //左右滑动交给ViewPager处理
                    case 'l':
                        setNeedIntercept(false);
                        break;
                }
                return isNeedIntercept;
        }
        return super.onInterceptTouchEvent(e);
    }

    public void setNeedIntercept(boolean needIntercept) {
        isNeedIntercept = needIntercept;
    }

    private int getOrientation(float dx, float dy) {
        if (Math.abs(dx)>Math.abs(dy)){
            //X轴移动
            return dx>0?'r':'l';//右,左
        }else{
            //Y轴移动
            return dy>0?'b':'t';//下//上
        }
    }




}
