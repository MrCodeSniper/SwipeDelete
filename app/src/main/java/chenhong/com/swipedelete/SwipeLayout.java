package chenhong.com.swipedelete;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2016/6/12.
 */
public class SwipeLayout extends FrameLayout {
    private ViewDragHelper viewDragHelper;
    private View content_view;
    private View delete_view;
    private int content_height;
    private int delete_width;
    private int content_width;
    private int delete_height;
    private float downX;
    private float downY;

    public SwipeLayout(Context context) {
        super(context);
        initview();
    }

    private void initview() {
         viewDragHelper=ViewDragHelper.create(this,callback);

    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview();
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initview();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
           content_view.layout(0,0,content_width,content_height);
           delete_view.layout(content_view.getRight(),0,content_view.getRight()+delete_width,content_height);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        content_view = getChildAt(0);
        delete_view = getChildAt(1);


    }
    //onMeasure已经完了
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        content_width = content_view.getMeasuredWidth();
        content_height = content_view.getMeasuredHeight();
        delete_width = delete_view.getMeasuredWidth();
        delete_height = delete_view.getMeasuredHeight();
    }





    private ViewDragHelper.Callback callback=new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child==content_view||child==delete_view;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if(child==content_view){
                if(left>0){
                    left=0;
                }else if(left<-delete_width){
                    left=-delete_width;
                }
            }else if(child==delete_view){
                   if(left<content_width-delete_width) left=content_width-delete_width;
                   if(left>content_width) left=content_width;
            }
            return left;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return delete_width;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if(content_view.getLeft()<-delete_width/2){
                open();
            }else {
                close();
            }


        }





        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if(changedView==content_view){
                delete_view.layout(delete_view.getLeft()+dx,delete_view.getTop()+dy,
                        delete_view.getRight()+dx,delete_view.getBottom()+dy);
            }else if(changedView==delete_view){
                content_view.layout(content_view.getLeft()+dx,content_view.getTop()+dy,
                        content_view.getRight()+dx,content_view.getBottom()+dy);
            }
            //判断什么时候是开和关
             if(content_view.getLeft()==0&&currentState!=SwipeState.Close){
                 currentState=SwipeState.Close;
                 //关闭了之后清空
                 if(swipeStateChangedListener!=null){
                     swipeStateChangedListener.onClose(getTag());
                 }

                 SwipeLayoutManager.getInstance().clearCurrentLayout();
             }else if(content_view.getLeft()==-delete_width&&currentState!=SwipeState.Open){
                 currentState=SwipeState.Open;
                 if(swipeStateChangedListener!=null){
                     swipeStateChangedListener.onOpen(getTag());
                 }
                 //让打开的布局成为单例的布局
                 SwipeLayoutManager.getInstance().setSwipeLayout(SwipeLayout.this);
             }


        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }


    };

    public void close() {
        viewDragHelper.smoothSlideViewTo(content_view,0,content_view.getTop());
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);//刷新动画
    }

    public void open() {
        viewDragHelper.smoothSlideViewTo(content_view,-delete_width,content_view.getTop());
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);//刷新动画
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result=viewDragHelper.shouldInterceptTouchEvent(ev);
        //如果当前有打开的，则需要直接拦截，交给onTouch处理
        if(!SwipeLayoutManager.getInstance().isShouldSwipe(this)){
            //先关闭打开的layout
            SwipeLayoutManager.getInstance().closeCurrentLayout();//不要放在ontouch里因为会一直调用卡顿
            return true;//交给onTouch
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //如果就是那个打开的布局，且与当前触摸的不是同一个
         if(!SwipeLayoutManager.getInstance().isShouldSwipe(this)){

             requestDisallowInterceptTouchEvent(true);//请求父类不拦截，父类就不能滑动了
             return true;//不执行下面代码
         }
        // 在拉动的过程中listview不滑动
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_MOVE:
                float movex=event.getX();
                float movey=event.getY();
                float delatx=movex- downX;
                float delaty=movey- downY;
                if(Math.abs(delatx)>Math.abs(delaty)){//移动偏向水平方向,应该是swipelayout处理，listview不拦截
                    requestDisallowInterceptTouchEvent(true);//请求父类不拦截
                }
                downX=movex;
                downY=movey;
                //更新最新的落下坐标
                break;
        }

        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if(viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
        }
    }


    //记录状态
    enum SwipeState{
        Open,Close;
    }

    private SwipeState currentState=SwipeState.Close;


    public void setOnSwipeStateChangedListener(OnSwipeStateChangedListener swipeStateChangedListener) {
        this.swipeStateChangedListener = swipeStateChangedListener;
    }
    private OnSwipeStateChangedListener swipeStateChangedListener;
    public interface OnSwipeStateChangedListener{
        void onOpen(Object tag);
        void onClose(Object tag);
        //还可能拖拽百分比
    }






}
