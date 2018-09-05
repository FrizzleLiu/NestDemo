package com.ht.testlist.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ht.testlist.adapter.PagerListAdapter;
import com.ht.testlist.R;
import com.ht.testlist.activity.MainActivity;

/**
 * Created by song on 2018/8/22 0022
 * My email : logisong@163.com
 * The role of this :
 */
public class PagerFragment extends Fragment {

    private RecyclerView mRv;
    private GridLayoutManager gridLayoutManager;
    private float downX ;    //按下时 的X坐标
    private float downY ;    //按下时 的Y坐标
    private String title;
    public static PagerFragment newInstance(String title) {
        PagerFragment pagerFragment = new PagerFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        pagerFragment.setArguments(args);
        return pagerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_pager,null);
        mRv = (RecyclerView) view.findViewById(R.id.rv);
        if(getArguments()!=null){
            title = getArguments().getString("title");
        }
        initView();
        return view;
    }


    private void initView() {
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRv.setLayoutManager(new GridLayoutManager(getContext(),2));
        PagerListAdapter adapter = new PagerListAdapter(title);
        mRv.setAdapter(adapter);
        mRv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                float x= motionEvent.getX();
                float y = motionEvent.getY();
                switch (motionEvent.getAction()){
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
                        //未吸顶一律交给外层的RecyclerView处理
                        if(! ((MainActivity)getActivity()).isStick){
                            ((MainActivity)getActivity()).adjustScroll(true);
                            return false;
                        }
                        switch (orientation) {
                            case 'b':
                                //内层RecyclerView下拉到最顶部时候不再处理事件
                                if(!recyclerView.canScrollVertically(-1) && ((MainActivity)getActivity()).isStick){
                                    recyclerView.getParent().requestDisallowInterceptTouchEvent(false);
                                    ((MainActivity)getActivity()).adjustScroll(true);
                                }
                        }
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });
    }

    private int getOrientation(float dx, float dy) {
        if (Math.abs(dx)>Math.abs(dy)){
            //X轴移动
            return dx>0?'r':'l';
        }else{
            //Y轴移动
            return dy>0?'b':'t';
        }
    }
}
