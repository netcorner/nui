package com.ingwill.widget.viewpager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * listview嵌套viewpager的滑动解决方案 Created by netcorner on 16/6/18.
 */
public class DecoratorViewPager extends FixedViewPager {
    public DecoratorViewPager(Context context) {
        super(context);
    }

    public DecoratorViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//
//        getParent().requestDisallowInterceptTouchEvent(true);//这句话的作用 告诉父view，我的单击事件我自行处理，不要阻碍我。
//
//        return super.dispatchTouchEvent(ev);
//    }

    /****************************这一坨是专门用作处理ontouch拦截的**********************************/
    private float downEventX, downEventY;
    private int scrollMode; // 0初始化 1左右滑动 2上下滑动
    private static final int SCROLL_MODE_IDLE = 0;
    private static final int SCROLL_MODE_HORIZONTAL = 1;
    private static final int SCROLL_MODE_VERTICAL = 2;
    private boolean isFullScroll = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        final int pointerCount = ev.getPointerCount();
//        if(pointerCount==2){
//            //拦截listview手势处理
//            getParent().requestDisallowInterceptTouchEvent(true);
//
//
//
//            switch (ev.getAction()) {
//
//                case MotionEvent.ACTION_DOWN:
//                case MotionEvent.ACTION_MOVE:
//
//                    if (mDragAndDropStarted) {
//
//                        mDragAndDropStarted = false;
//
//
//                        return launchDragAndDrop(ev);
//                    }
//
//                    break;
//
//                default:
//                case MotionEvent.ACTION_UP:
//                case MotionEvent.ACTION_CANCEL:
//                    onDrop();
//
//
//                    break;
//            }
//            return true;
//        }
        if(isCanScroll()) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    scrollMode = SCROLL_MODE_IDLE;
                    downEventX = ev.getRawX();
                    downEventY = ev.getRawY();
                    isFullScroll = false;

                    getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (scrollMode == SCROLL_MODE_HORIZONTAL) {

                        if(DecoratorViewPager.this.getCurrentItem()==0){
                            float distanceX = ev.getRawX() - downEventX;
                            if(distanceX>0){
                                //防止侧滑冲突
                                getParent().requestDisallowInterceptTouchEvent(false);
                            }
                        }


                        // 已被定性为左右滑动，只在这个view内部处理即可
                    } else if (scrollMode == SCROLL_MODE_VERTICAL) {
                        // 已被定性为上下滑动，此view可以不必处理，不消费此touch事件
                        return false;
                    } else if (scrollMode == SCROLL_MODE_IDLE) {
                        float distanceX = Math.abs(ev.getRawX() - downEventX);
                        float distanceY = Math.abs(ev.getRawY() - downEventY);
                        if (distanceX > distanceY && distanceX > 5) {
                            if (isFullScroll) {
                                if (ev.getRawX() < downEventX) {
                                    scrollMode = SCROLL_MODE_VERTICAL;
                                    getParent().requestDisallowInterceptTouchEvent(false);
                                    return super.dispatchTouchEvent(ev);
                                }
                            }

                            scrollMode = SCROLL_MODE_HORIZONTAL;
                            getParent().requestDisallowInterceptTouchEvent(true);
                        } else if (distanceY > distanceX && distanceY > 5) {
                            scrollMode = SCROLL_MODE_VERTICAL;
                            getParent().requestDisallowInterceptTouchEvent(false);
                            return false;
                        }
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    scrollMode = SCROLL_MODE_IDLE;
                    getParent().requestDisallowInterceptTouchEvent(false);
                    break;

                case MotionEvent.ACTION_CANCEL:
                    scrollMode = SCROLL_MODE_IDLE;
                    getParent().requestDisallowInterceptTouchEvent(false);
                    break;

                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    private Map<Integer,Integer> heights = new HashMap<>();
    private List<Integer> heightTypes=new ArrayList<>();
    public void putHeight(int position,int height)
    {
        if(!heightTypes.contains(height)) heightTypes.add(height);
        heights.put(position,height);


    }
    public boolean isSameHeight(){
        return heightTypes.size()==1;
    }


    public Integer getHeight(int position)
    {
        if(heights.containsKey(position)) {
            return heights.get(position);
        }else{
            return 0;
        }
    }


    public void resetHeight(int position) {
         if(heights.containsKey(position)) {
             setHeight(heights.get(position));
         }
    }

    public void setHeight(int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        } else {
            layoutParams.height = height;
        }
        setLayoutParams(layoutParams);
        requestLayout();
    }


    /**
     * 以下是放大的处理
     */


//    private boolean launchDragAndDrop(final MotionEvent event) {
//
//        final int x = (int) event.getX();
//        final int y = (int) event.getY();
//
//        mDragOffsetX = (int) (event.getRawX() - x);
//        mDragOffsetY = (int) (event.getRawY() - y);
//
//        startDrag(x, y);
//
//        return true;
//    }
//
//    protected int pointToPosition(final int draggedChild, final int x, final int y) {
//
//        for (int index = 0; index < getChildCount(); index++) {
//
//            if (index == draggedChild) {
//                continue;
//            }
//
//            getChildAt(index).getHitRect(mRect);
//
//            if (mRect.contains(x, y)) {
//                return index;
//            }
//        }
//        return INVALID_POSITION;
//    }
//
//    private void destroyDragImageView() {
//
//        if (mDragImageView != null) {
//
//            mWindowManager.removeView(mDragImageView);
//
//            BitmapDrawable bitmapDrawable = (BitmapDrawable) mDragImageView.getDrawable();
//            if (bitmapDrawable != null) {
//                final Bitmap bitmap = bitmapDrawable.getBitmap();
//                if (bitmap != null && !bitmap.isRecycled()) {
//                    bitmap.recycle();
//                }
//            }
//
//            mDragImageView.setImageDrawable(null);
//            mDragImageView = null;
//        }
//
//    }
//
//
//    private void startDrag(final int x, final int y) {
//
//
//        destroyDragImageView();
//
//        mDragImageView = createDragImageView(this, x, y);
//        this.setVisibility(View.INVISIBLE);
//
//
//
//    }
//    private Rect mRect = new Rect();
//    private static final int INVALID_POSITION = -1;
//    private boolean mDragAndDropStarted = true;
//    private ImageView mDragImageView = null;
//    private int mDragPointX;
//    private int mDragPointY;
//    private int mDragOffsetX;
//    private int mDragOffsetY;
//    WindowManager mWindowManager = null;
//    WindowManager.LayoutParams mWindowParams = null;
//
//    private ImageView createDragImageView(final View v, final int x, final int y) {
//
//        v.destroyDrawingCache();
//        v.setDrawingCacheEnabled(true);
//        Bitmap bm = Bitmap.createBitmap(v.getDrawingCache());
//
//        mDragPointX = x - v.getLeft();
//        mDragPointY = y - v.getTop();
//
//        mWindowParams = new WindowManager.LayoutParams();
//        mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
//
//        mWindowParams.x = x - mDragPointX + mDragOffsetX;
//        mWindowParams.y = y - mDragPointY + mDragOffsetY;
//
//        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//
//        mWindowParams.format = PixelFormat.TRANSLUCENT;
//        //mWindowParams.alpha = 0.7f;
//        mWindowParams.windowAnimations = 0;
//
//        ImageView iv = new ImageView(getContext());
//        iv.setImageBitmap(bm);
//
//        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);// "window"
//        mWindowManager.addView(iv, mWindowParams);
//        return iv;
//
//    }
//
//    private void onDrop() {
//
//        destroyDragImageView();
//
//
//        this.setVisibility(View.VISIBLE);
//
//        this.clearAnimation();
//        mDragAndDropStarted = false;
//    }




//    private int current;
//    private int height = 0;
//    private HashMap<Integer, View> mChildrenViews = new LinkedHashMap<Integer, View>();
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        if (mChildrenViews.size() > current) {
//            View child = mChildrenViews.get(current);
//            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//            height = child.getMeasuredHeight();
//        }
//
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
//
//    public void resetHeight(int current) {
//        this.current = current;
//        if (mChildrenViews.size() > current) {
//
//            ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) getLayoutParams();
//            if (layoutParams == null) {
//                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
//            } else {
//                layoutParams.height = height;
//            }
//            setLayoutParams(layoutParams);
//        }
//    }
//    /**
//     * 保存position与对于的View
//     */
//    public void setObjectForPosition(View view, int position)
//    {
//        mChildrenViews.put(position, view);
//    }
}