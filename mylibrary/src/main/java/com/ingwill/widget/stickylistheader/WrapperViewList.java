package com.ingwill.widget.stickylistheader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.ingwill.utils.AndroidUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class WrapperViewList extends ListView{

	interface LifeCycleListener {
		void onDispatchDrawOccurred(Canvas canvas);
	}

	private LifeCycleListener mLifeCycleListener;
	private List<View> mFooterViews;
	private int mTopClippingLength;
	private Rect mSelectorRect = new Rect();// for if reflection fails
	private Field mSelectorPositionField;
	private boolean mClippingToPadding = true;
    private boolean mBlockLayoutChildren = false;

	public WrapperViewList(Context context) {
		super(context);

		// Use reflection to be able to change the size/position of the list
		// selector so it does not come under/over the header
		try {
			Field selectorRectField = AbsListView.class.getDeclaredField("mSelectorRect");
			selectorRectField.setAccessible(true);
			mSelectorRect = (Rect) selectorRectField.get(this);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				mSelectorPositionField = AbsListView.class.getDeclaredField("mSelectorPosition");
				mSelectorPositionField.setAccessible(true);
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean performItemClick(View view, int position, long id) {
		if (view instanceof WrapperView) {
			view = ((WrapperView) view).mItem;
		}
		return super.performItemClick(view, position, id);
	}

	private void positionSelectorRect() {
		if (!mSelectorRect.isEmpty()) {
			int selectorPosition = getSelectorPosition();
			if (selectorPosition >= 0) {
				int firstVisibleItem = getFixedFirstVisibleItem();
				View v = getChildAt(selectorPosition - firstVisibleItem);
				if (v instanceof WrapperView) {
					WrapperView wrapper = ((WrapperView) v);
					mSelectorRect.top = wrapper.getTop() + wrapper.mItemTop;
				}
			}
		}
	}

	private int getSelectorPosition() {
		if (mSelectorPositionField == null) { // not all supported andorid
			// version have this variable
			for (int i = 0; i < getChildCount(); i++) {
				if (getChildAt(i).getBottom() == mSelectorRect.bottom) {
					return i + getFixedFirstVisibleItem();
				}
			}
		} else {
			try {
				return mSelectorPositionField.getInt(this);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		positionSelectorRect();
		if (mTopClippingLength != 0) {
			canvas.save();
			Rect clipping = canvas.getClipBounds();
			clipping.top = mTopClippingLength;
			canvas.clipRect(clipping);
			super.dispatchDraw(canvas);
			canvas.restore();
		} else {
			super.dispatchDraw(canvas);
		}
		mLifeCycleListener.onDispatchDrawOccurred(canvas);
	}

	void setLifeCycleListener(LifeCycleListener lifeCycleListener) {
		mLifeCycleListener = lifeCycleListener;
	}

	@Override
	public void addFooterView(View v) {
		super.addFooterView(v);
		addInternalFooterView(v);
	}

	@Override
	public void addFooterView(View v, Object data, boolean isSelectable) {
		super.addFooterView(v, data, isSelectable);
		addInternalFooterView(v);
	}

	private void addInternalFooterView(View v) {
		if (mFooterViews == null) {
			mFooterViews = new ArrayList<View>();
		}
		mFooterViews.add(v);
	}

	@Override
	public boolean removeFooterView(View v) {
		if (super.removeFooterView(v)) {
			mFooterViews.remove(v);
			return true;
		}
		return false;
	}

	boolean containsFooterView(View v) {
		if (mFooterViews == null) {
			return false;
		}
		return mFooterViews.contains(v);
	}

	void setTopClippingLength(int topClipping) {
		mTopClippingLength = topClipping;
	}

	public int getFixedFirstVisibleItem() {
		int firstVisibleItem = getFirstVisiblePosition();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			return firstVisibleItem;
		}

		// first getFirstVisiblePosition() reports items
		// outside the view sometimes on old versions of android
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i).getBottom() >= 0) {
				firstVisibleItem += i;
				break;
			}
		}

		// work around to fix bug with firstVisibleItem being to high
		// because list view does not take clipToPadding=false into account
		// on old versions of android
		if (!mClippingToPadding && getPaddingTop() > 0 && firstVisibleItem > 0) {
			if (getChildAt(0).getTop() > 0) {
				firstVisibleItem -= 1;
			}
		}

		return firstVisibleItem;
	}

	@Override
	public void setClipToPadding(boolean clipToPadding) {
		mClippingToPadding = clipToPadding;
		super.setClipToPadding(clipToPadding);
	}

    public void setBlockLayoutChildren(boolean block) {
        mBlockLayoutChildren = block;
    }

    @Override
    protected void layoutChildren() {
        if (!mBlockLayoutChildren) {
            super.layoutChildren();
        }
    }


	public  static final double MAXHEIGHTSCALE=1.565;

	public void addHeaderImageView(View headView, FrameLayout layout_header_container, View mack_cover_imageView, ImageView cover_imageView, int dp){
		setOverScrollMode(OVER_SCROLL_NEVER);
		this.layout_header_container=layout_header_container;
		this.mack_cover_imageView=mack_cover_imageView;
		this.cover_imageView=cover_imageView;
//		layout_header_container=(FrameLayout)headView.findViewById(R.id.layout_header_container);
//		mack_cover_imageView= headView.findViewById(R.id.mack_cover_imageView);
//		cover_imageView= (ImageView) headView.findViewById(R.id.coverBG);

		cover_imageViewheight = AndroidUtils.dip2px(getContext(),dp);

		layout_header_container.getLayoutParams().height=cover_imageViewheight;
		cover_imageViewheightMaxHeight=(int)(cover_imageViewheight*MAXHEIGHTSCALE);
		addHeaderView(headView);

	}
	public void setHeadScaleImage(String file){
		//ImageTools.setImageViewBitmap(getContext(),file,cover_imageView,R.drawable.ic_reppic);
		//mack_cover_imageView.setAlpha(0);

	}


	private ImageView cover_imageView;

	public ImageView getCover_imageView() {
		return cover_imageView;
	}

	private View mack_cover_imageView;

	private FrameLayout layout_header_container;


	private int cover_imageViewheight;
	private int cover_imageViewheightMaxHeight;

	public void setCover_imageViewheightMaxHeight(int cover_imageViewheightMaxHeight) {
		this.cover_imageViewheightMaxHeight = cover_imageViewheightMaxHeight;
	}



	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

		if(layout_header_container!=null) {
			if (layout_header_container.getHeight() <= cover_imageViewheightMaxHeight && isTouchEvent) {
				final int destImageViewHeight = layout_header_container.getHeight() - deltaY / 2;

				layout_header_container.getLayoutParams().height = Math.min(cover_imageViewheightMaxHeight, Math.max(destImageViewHeight, cover_imageViewheight));
				layout_header_container.requestLayout();

			}
		}

		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(layout_header_container!=null) {
			if (ev.getAction() == MotionEvent.ACTION_UP) {
				if (layout_header_container.getHeight() > cover_imageViewheight) {
					ReleaseHandAnimator animator = new ReleaseHandAnimator(layout_header_container, cover_imageViewheight);
					animator.setDuration(300);
					layout_header_container.startAnimation(animator);
				}

			}
		}
		return super.onTouchEvent(ev);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		if(layout_header_container!=null) {
			int containerHeight = layout_header_container.getHeight();
			//向下拉containerHeight会变大
			int diff = containerHeight - cover_imageViewheight;


			if (diff > 0) { //往下拉的时候会渐渐显示出模糊图层
				double factor = (diff + (double) cover_imageViewheight / 10.0) * 2 / (double) (cover_imageViewheightMaxHeight - cover_imageViewheight);

				//mack_cover_imageView.setAlpha(Math.max(0, Math.min((int) (factor * 255), 255)));

			} else if (mack_cover_imageView.getAlpha() != 0) {
				//mack_cover_imageView.setAlpha(0);
			}
		}
	}

	private class ReleaseHandAnimator extends Animation {
		private float targetHeight;
		private View targetView;
		private float  diff;

		public ReleaseHandAnimator(View view , float targetHeight){
			this. targetView = view;
			this.targetHeight = targetHeight;
			this. diff = targetHeight - view.getHeight();

		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			targetView.getLayoutParams().height= (int) (targetHeight-diff*(1-interpolatedTime));
			targetView.requestLayout();
		}
	}


}
