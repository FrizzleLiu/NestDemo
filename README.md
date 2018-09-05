###最近看到京东,淘宝都有RecyclerView嵌套ViewPager嵌套RecyclerView商品展示的效果,效果挺好,废话不多说先看效果图:
![GIF.gif](https://upload-images.jianshu.io/upload_images/11952059-4111ae6af8632647.gif?imageMogr2/auto-orient/strip)

>技能点:
1.Android事件分发机制等

> 需求点:
1.Tab需要吸顶
 2.ViewPager可以点击和滑动切换

####最近在淘宝京东看到类似的效果,有时间就写了一下,效果实现了,但是感觉解决问题的思路和代码有很多瑕疵,写出来抛砖引玉,希望大佬们不吝赐教,写的不好不喜勿喷!
####下面进入正题,先看下布局结构:![screen.png](https://upload-images.jianshu.io/upload_images/11952059-050ac900ca4dbe19.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

####就是标题所说的布局结构 *RecyclerView+ViewPager+RecyclerView*`
#####很多同学看到这里肯定想到要处理滑动冲突,没错,我们简单分析一下好撸代码(虽然是撸好的代码)
1. 横向滑动
- 横向滑动很简单,RecyclerView不需要处理,ViewPager处理
---
2. 纵向滑动
- 纵向滑动就稍微复杂点,本文的解决滑动冲突主要就就是解决外层RecyclerView以及内层RecyclerView的滑动冲突,仔细看下交互效果,不难发现我们需要用Tab是否吸顶作为判断的节点来将滑动事件交给外层或内层RecyclerView处理.  即: 1.Tab未吸顶时外层RecyclerView处理滑动事件,2.Tab吸顶时内层RecyclerView处理滑动事件.

####大概就是这样,思路很清晰,这里先提几个接下来遇到的问题:
- RecyclerView嵌套ViewPager时ViewPager的高度为0
- Tab吸顶之后切换ViewPager时候,吸顶的Tab会覆盖掉一部分内层RecyclerView的内容,让人感觉不是从第一条开始展示的
- 操作步骤:滑动到Tab吸顶->滑动内层RecyclerView至中间->切换一个Tab(内层RecyclerView的状态已经滑动到顶部,就是初始状态)->这时候将Tab滑动到非吸顶->切换到最初内层RecyClerView滑动到中间的Tab,这时候展示的就是Tab未吸顶,内层RecyclerView不在顶部的尴尬局面.说了这么多应该需要一张gif解释一下上图:![GIF1.gif](https://upload-images.jianshu.io/upload_images/11952059-5c1a525a620f70a3.gif?imageMogr2/auto-orient/strip)

#####对于上图所提到的情况,这个时候用户手指纵向滑动红色区域,滑动事件交给谁都不合适
.那先说下淘宝和京东采取的方式:
1. 淘宝和京东部分页面切换ViewPager时候重新拉取数据(可能没有重新拉数据,只是notify了一下)将RecyclerView直接展示到初始状态
2. 京东的部分界面(京东->我的->下拉->为你推荐)处理方式为:当Tab为非吸顶状态时候切换ViewPager,外层RecyclerView滑动到Tab吸顶
3. demo因为用的是假数据,所以没做处理,但是代码中有在tab非吸顶状态时候,外层RecyclerView优先处理滑动事件的代码
#####个人感觉第一种处理方式比较好一点,demo的代码如下(需要请自行修改,PagerFragment.java)

``` 
                        if(! ((MainActivity)getActivity()).isStick){
                            ((MainActivity)getActivity()).adjustScroll(true);
                            return false;
                        }
```
####下面说下实现方式,以及问题的解决(布局等细节就不贴出来了,详情见demo):
1. 外部的RecyclerView为自定义的View继承自RecyclerView重写onInterceptTouchEvent方法
处理滑动事件:
```

   private float downX ;    //按下时 的X坐标
    private float downY ;    //按下时 的Y坐标
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
                    //外层RecyclerView滑动到底部将滑动不拦截在拦截滑动事件,注意:滑动到底部的时候,可以拿到触摸时间但是接着上滑已经不会回调onScrolled
                    //来回切换的过程中可能出现没吸顶,但是外部的RecyclerView已经滑动到底部的情况
                    case 't':
                        if(!canScrollVertically(1) && isStick){
                            setNeedIntercept(false);
                        }else{
                            setNeedIntercept(true);
                        }
                        break;
                        //左右滑动交给ViewPager处理
                    case 'r':
                        setNeedIntercept(false);
                        break;
                    //左右滑动交给ViewPager处理
                    case 'l':
                        setNeedIntercept(false);
                        break;
                }
                break;
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                return isNeedIntercept;
        }
        return super.onInterceptTouchEvent(e);
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
```
isNeedIntercept为是否拦截滑动事件,自己处理.并提供了一个setNeedIntercept方法供外部调用.代码可以看出,横向的滑动直接放行,让ViewPager处理,上上滑动时候如果tab吸顶了且不已经滑动到底部,交给内部的RecyclerView处理,否则自己处理.

##### 以下2-5条为MainActivity里的代码
2. 外部的RecyclerView设置了一个滚动监听,实时刷新Tab的是否吸顶,告诉需要用到的地方

```  rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //内外RecyclerView滑动时候刷新isStick的状态
                refreshStick();
            }
        });
```
####这里说明一下外面的RecyclerView填充数据时候用的是阿里开源的vLayout(需要了解的同学请github,百度上有很多讲解这个框架用法的),如果用原生的话自己处理下吸顶的效果(吸顶的实现方式网上一大堆),只要能拿到是否吸顶的状态就OK.

下面继续:
3.   计算了屏幕高度-状态栏高度为后面ViewPager设置高度做准备
```
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

```

4. 判断是否Tab吸顶在外部RecyclerView的滑动监听中调用
```
 //由于stickyLayoutHelper.isStickyNow()在布局刚展示时默认为true所以这里还取了其View的Y坐标一起判断
        if(stickyLayoutHelper !=null && stickyLayoutHelper.isStickyNow() && stickyLayoutHelper.getFixedView()!=null && stickyLayoutHelper.getFixedView().getY()== rv.getY()){
            isStick=true;
        }else{
            isStick=false;
        }
```

5. 设置Tab的点击监听和ViewPagerde 滑动切换监听,彼此同步选中item

```
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
```


####以上代码为部分代码,详细请见demo,处理完外面的剩下只有里面了,我们在Fragment里面给内层RecyclerView设置触摸监听,代码如下:
```
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
                                    ((MainActivity)getActivity()).rv.setNeedIntercept(true);
                                }
                                break;
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
```

这里处理了两种需要将事件交给外层RecyclerView的情况,第一种会影响到上面提到的,Tab未吸顶,内层RecyclerView滑动到中间的情况,需要请自行修改.


####滑动冲突到这里基本上处理完了,处理下吸顶的Tab会遮住部分Fragment(即:RecyclerView展示的内容)内容的问题
在包含ViewPager的adapter中定义一个方法,在外层RecyclerView的addOnScrollListener回调中调用实时的给ViewPagershe设置padingTop来解决显示问题,远离就是当Tab刚好吸顶时,向上的滑动距离等于ViewPager的paddingTop,这样就解决了Tab吸顶的覆盖问题.

```
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
            }
        }


    }
```

---
#####简单总结一下,写的不好,可能是一开始思路问题,直接撸了代码,遇到什么问题解决什么问题,希望大佬们有好的想法多多指教,多多评论,我会跟进,优化更新这个demo
项目地址:[简书](https://www.jianshu.com/p/a5100ac471ae)
       

            

