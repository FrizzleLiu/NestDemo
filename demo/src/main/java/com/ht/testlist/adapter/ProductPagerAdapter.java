package com.ht.testlist.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;
import com.ht.testlist.holder.PageViewHolder;
import com.ht.testlist.R;

import java.util.List;

/**
 * Created by Administrator on 2018/8/29.
 * Description : ProductPagerAdapter
 */

public class ProductPagerAdapter extends DelegateAdapter.Adapter {
    private Context context;
    private FragmentManager fragmentManager;
    private List<String> titles;
    private int height;
    private PageChageListener pageChageListener;
    private PageViewHolder pageViewHolder;
    private PagerAdapter adapter;
    //记录上次展示的tab位置
    private int lastItem;
    private boolean isStick=false;
    private int statusBarHeight;

    public ProductPagerAdapter(Context context, FragmentManager fragmentManager, List<String> titles, int height) {
        this.context=context;
        this.titles=titles;
        this.fragmentManager=fragmentManager;
        this.height=height;
        //状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            statusBarHeight =context.getResources().getDimensionPixelSize(resourceId);
        }
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new SingleLayoutHelper();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new PageViewHolder(View.inflate(parent.getContext(), R.layout.rv_item_pager,null));
}

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        pageViewHolder = (PageViewHolder) holder;
        if(adapter==null){
            adapter = new MainPagerAdapter(fragmentManager,titles);
        }
        pageViewHolder.mViewPager.setAdapter(adapter);
        if(lastItem>0){
            pageViewHolder.mViewPager.setCurrentItem(lastItem);
        }
        //RecyclerView嵌套ViewPager会出现高度为0的bug,这里给ViewPager设置的高度为屏幕高度-状态栏高度
        ViewGroup.LayoutParams layoutParams = pageViewHolder.mViewPager.getLayoutParams();
        layoutParams.height=height;
        pageViewHolder.mViewPager.setLayoutParams(layoutParams);
        pageViewHolder.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(pageChageListener!=null){
                    pageChageListener.pageChange(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        lastItem = ((PageViewHolder) holder).mViewPager.getCurrentItem();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public void setSelectedPosition(int position) {
        if(position>=0 && pageViewHolder!=null){
            pageViewHolder.mViewPager.setCurrentItem(position);
        }
    }


    public interface PageChageListener{
        void pageChange(int position);
    }
    public void setPageChageListener(PageChageListener pageChageListener) {
        this.pageChageListener = pageChageListener;
    }



    public void refreshVpLayout(boolean stick) {
        isStick = stick;
        if(pageViewHolder==null || pageViewHolder.mViewPager==null){
            return;
        }
        int[] location={0,0};
        pageViewHolder.mViewPager.getLocationOnScreen(location);
        float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        //54dp是tab的高度,这里动态修改 PaddingTop的值是为了适应Tab吸顶覆盖掉的高度
        int paddingTop = (int) (54 * scale + 0.5f);
        paddingTop-=(location[1]-statusBarHeight);
        if(location[1]>0){
            if(paddingTop>=0){
                pageViewHolder.mViewPager.setPadding(0,paddingTop,0,0);
            }else{
                pageViewHolder.mViewPager.setPadding(0,0,0,0);
            }
        }


    }
}
