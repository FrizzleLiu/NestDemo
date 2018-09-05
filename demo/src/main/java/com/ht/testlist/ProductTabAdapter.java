package com.ht.testlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.android.vlayout.layout.StickyLayoutHelper;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Administrator on 2018/8/29.
 * Description : ProductPagerAdapter
 */

public class ProductTabAdapter extends DelegateAdapter.Adapter {
    private Context context;
    private List<String> titles;
    private StickyLayoutHelper stickyLayoutHelper;
    private ProductTabHolder pageViewHolder;


    private TabChangeListener tabChangeListener;

    public ProductTabAdapter(Context context,List<String> titles,StickyLayoutHelper stickyLayoutHelper) {
        this.context=context;
        this.titles=titles;
        this.stickyLayoutHelper=stickyLayoutHelper;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return this.stickyLayoutHelper;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ProductTabHolder(View.inflate(parent.getContext(),R.layout.layout_tab_view,null));
}

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        pageViewHolder = (ProductTabHolder) holder;
        pageViewHolder.tabLayout.removeAllTabs();
          for (int i = 0; i <titles.size() ; i++) {
              pageViewHolder.tabLayout.addTab(pageViewHolder.tabLayout.newTab().setText(titles.get(i)));
              pageViewHolder.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                  @Override
                  public void onTabSelected(TabLayout.Tab tab) {
                      if(tabChangeListener!=null){
                          tabChangeListener.tabChage(pageViewHolder.tabLayout.getSelectedTabPosition());
                      }
                  }

                  @Override
                  public void onTabUnselected(TabLayout.Tab tab) {

                  }

                  @Override
                  public void onTabReselected(TabLayout.Tab tab) {

                  }
              });
                      }

//        pageViewHolder.mTabLayout.setupWithViewPager(pageViewHolder.mViewPager);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public void setSelectPosition(int position) {
        if(position>=0 && pageViewHolder!=null){
            pageViewHolder.tabLayout.getTabAt(position).select();
        }
    }
    public void setTabChangeListener(TabChangeListener tabChangeListener) {
        this.tabChangeListener = tabChangeListener;
    }

    public interface TabChangeListener{
        void tabChage(int position);
    }
}
