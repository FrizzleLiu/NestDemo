package com.ht.testlist;

import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2018/9/5.
 * Description : ProductTabHolder
 */

public class ProductTabHolder extends RecyclerView.ViewHolder {
    public TabLayout tabLayout;

    public ProductTabHolder(View view) {
        super(view);
        tabLayout = view.findViewById(R.id.tablayout);
    }
}
