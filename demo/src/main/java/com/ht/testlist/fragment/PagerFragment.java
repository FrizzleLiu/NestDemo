package com.ht.testlist.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ht.testlist.adapter.PagerListAdapter;
import com.ht.testlist.R;
import com.ht.testlist.activity.MainActivity;
import com.ht.testlist.weight.InnerRecyclerView1;

/**
 * Created by song on 2018/8/22 0022
 * My email : logisong@163.com
 * The role of this :
 */
public class PagerFragment extends Fragment implements InnerRecyclerView1.NeedIntercepectListener {

    private InnerRecyclerView1 mRv;
    private GridLayoutManager gridLayoutManager;
    private float downX ;    //按下时 的X坐标
    private float downY ;    //按下时 的Y坐标
    private String title;
    private int height;

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
        mRv = (InnerRecyclerView1) view.findViewById(R.id.rv);
        mRv.setNestedScrollingEnabled(true);
        if(getArguments()!=null){
            title = getArguments().getString("title");
        }
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            statusBarHeight =getResources().getDimensionPixelSize(resourceId);
        }
        //屏幕高度
        DisplayMetrics dm = getActivity().getApplicationContext().getResources().getDisplayMetrics();
        final float scale = dm.density;
        int i = (int) (54 * scale + 0.5f);
        height = statusBarHeight+i;
        mRv.setMaxY(height);
        mRv.setNeedIntercepectListener(this);
        initView();
        return view;
    }


    private void initView() {
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRv.setLayoutManager(new GridLayoutManager(getContext(),2));
        PagerListAdapter adapter = new PagerListAdapter(title);
        mRv.setAdapter(adapter);
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


    @Override
    public void needIntercepect(boolean needIntercepect) {
        ((MainActivity)getActivity()).adjustIntercept(!needIntercepect);
    }
}
