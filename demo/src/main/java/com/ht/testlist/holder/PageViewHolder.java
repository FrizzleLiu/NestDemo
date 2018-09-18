package com.ht.testlist.holder;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.ht.testlist.R;

/**
 * Created by Administrator on 2018/8/29.
 * Description : PageViewHolder
 */

public class PageViewHolder extends RecyclerView.ViewHolder {

    public ViewPager mViewPager;
    public RelativeLayout rlVpContainer;
    public TabLayout tabLayout;

    public PageViewHolder(View view) {
        super(view);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        rlVpContainer = view.findViewById(R.id.rl_vp_container);
        tabLayout = view.findViewById(R.id.tablayout);
    }
}