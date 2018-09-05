package com.ht.testlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.StickyLayoutHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by song on 2018/8/22 0022
 * My email : logisong@163.com
 * The role of this :
 */
public class MainActivity extends AppCompatActivity implements ProductPagerAdapter.PageChageListener, ProductTabAdapter.TabChangeListener{

    private List<String> data = new ArrayList<>();
    public boolean innerCanScroll = true;
    private VirtualLayoutManager virtualLayoutManager;
    private ProductPagerAdapter productPagerAdapter;
    private ProductTabAdapter productTabAdapter;
    private MainAdapter mainAdapter;
    private StickyLayoutHelper stickyLayoutHelper;
    private float downX ;    //按下时 的X坐标
    private float downY ;    //按下时 的Y坐标
    public OutRecyclerView rv;
    private DelegateAdapter delegateAdapter;
    public boolean isStick=false;
    private RelativeLayout rootView;
    private StickStatusListener stickStatusListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data.add("推荐");
        data.add("男装");
        data.add("女装");
        data.add("童装");
        data.add("鞋子");
        rv = findViewById(R.id.rv);
        rootView = findViewById(R.id.rl_root);
        virtualLayoutManager = new VirtualLayoutManager(this);
        rv.setLayoutManager(virtualLayoutManager);
        delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        rv.setAdapter(delegateAdapter);
        setStickStatusListener(rv);
        mainAdapter = new MainAdapter(this);
        delegateAdapter.addAdapter(mainAdapter);
        stickyLayoutHelper = new StickyLayoutHelper();
        productTabAdapter = new ProductTabAdapter(this, data, stickyLayoutHelper);
        productTabAdapter.setTabChangeListener(this);
        delegateAdapter.addAdapter(productTabAdapter);
        //状态栏高度
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            statusBarHeight =getResources().getDimensionPixelSize(resourceId);
        }
        //屏幕高度
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        productPagerAdapter = new ProductPagerAdapter(this,getSupportFragmentManager(),data, dm.heightPixels-statusBarHeight);
        productPagerAdapter.setPageChageListener(this);
        delegateAdapter.addAdapter(productPagerAdapter);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //内外RecyclerView滑动时候刷新isStick的状态
                refreshStick();
            }
        });


    }

    //ViewPager滑动切换回调
    @Override
    public void pageChange(int position) {
        //切换ViewPage,
        if(isStick){
            rv.setNeedIntercept(false);
        }else{
            rv.setNeedIntercept(true);
        }
        if(productTabAdapter!=null){
            productTabAdapter.setSelectPosition(position);
        }
    }
    //Tab点击切换回调
    @Override
    public void tabChage(int position) {
        if(productPagerAdapter!=null){
            productPagerAdapter.setSelectedPosition(position);
        }
    }

    //RecyclerView滑动时,实时刷新isStick的状态
    public void refreshStick(){
        //由于stickyLayoutHelper.isStickyNow()在布局刚展示时默认为true所以这里还取了其View的Y坐标一起判断
        if(stickyLayoutHelper !=null && stickyLayoutHelper.isStickyNow() && stickyLayoutHelper.getFixedView()!=null && stickyLayoutHelper.getFixedView().getY()== rv.getY()){
            isStick=true;
        }else{
            isStick=false;
        }
        if(productPagerAdapter!=null){
            productPagerAdapter.refreshVpLayout(isStick);
        }
        if (stickStatusListener != null) {
            stickStatusListener.updateStick(isStick);
        }
    }

    public interface  StickStatusListener{
        void updateStick(boolean isStick);
    }


    //这里是告诉外层的RecyclerView当前的吸顶状态
    public void setStickStatusListener(StickStatusListener stickStatusListener) {
        this.stickStatusListener=stickStatusListener;
    }

    //
    public void adjustScroll(boolean needIntercept){
        rv.setNeedIntercept(needIntercept);
    }
}
