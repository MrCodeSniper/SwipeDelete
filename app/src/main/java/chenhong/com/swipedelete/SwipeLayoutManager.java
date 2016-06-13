package chenhong.com.swipedelete;

/**
 * 单例的设计模式-一个类中只有一个实例
 * Created by Administrator on 2016/6/12.
 */
public class SwipeLayoutManager {
    private SwipeLayoutManager(){};//私有化构造函数
    private static SwipeLayoutManager mInstance=new SwipeLayoutManager();
    public static SwipeLayoutManager getInstance(){//得到都是唯一的实例
        return mInstance;
    }
    private SwipeLayout currentLayout;//用来记录当前打开的SwipeLayout
    public  void setSwipeLayout(SwipeLayout layout){//将打开的layout设置进来
        this.currentLayout=layout;
    }

    /**
     * 判断当前是否能够滑动
     * @param swipeLayout
     * @return
     */
    public boolean isShouldSwipe(SwipeLayout swipeLayout){
      if(currentLayout==null){//如果打开的布局没有那么就可以滑
          return  true;
      }else {
          return currentLayout==swipeLayout;//如果不是当前打开的布局，就不能滑动，如果就是这个打开的布局则可以滑动
      }
    }
   //关闭当前打开的layout
    public void  closeCurrentLayout(){
    if(currentLayout!=null){
        currentLayout.close();
    }
    }
    /**
     * 清空当前记录了已经打开的layout
     */
    public void clearCurrentLayout(){
        currentLayout=null;
    }










}
