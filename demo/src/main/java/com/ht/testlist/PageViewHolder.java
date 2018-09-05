package com.ht.testlist;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2018/8/29.
 * Description : PageViewHolder
 */

public class PageViewHolder extends RecyclerView.ViewHolder {

    public ViewPager mViewPager;
    public RelativeLayout rlVpContainer;

    public PageViewHolder(View view) {
        super(view);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        rlVpContainer = view.findViewById(R.id.rl_vp_container);
    }
}