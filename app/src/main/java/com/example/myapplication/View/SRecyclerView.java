/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.myapplication.View;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.R;


/**
 * Created by s0005 on 2017/4/8.
 * <p>
 * 功能包括 下拉刷新  上啦加载 只预估数值实现自定义添加在布局  overscroll itemtouchhelper
 * 出列快速滑动时的处理不是很完美，但能用
 */

public class SRecyclerView extends LinearLayout implements NestedScrollingParent {
    public static final String TAG = "SRecyclerView";
    private NestedScrollingParentHelper scrollingParentHelper;
    private LinearLayout headLayout;
    private LinearLayout footLayout;
    private MyRecyclerView myRecyclerView;
    static final Interpolator sQuinticInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };
    private OnRefreshListener listener;

    private int headerHeight = dp2px(50), footHeight = dp2px(40);

    //滚动比率
    private int pullRate = 3;

    //滚动距离
    private int scrolls;

    //开始刷新时间
    private long beginRefreshing;

    private boolean isLoading = false;

    private int maxTime = 200;

    private int maxFastOverScroll = dp2px(100);

    private boolean canheader = true, canfooter = false;
    private ValueAnimator animator;
    private ValueAnimator animator1;
    //超出距离是否加载
    private boolean  canLoadingHeader = true, canLoadingFooter = false;
    //是否默认头尾布局
    private boolean isusingDefault;
    //旋转的进度
    private ImageView headerprogress;
    //文字Textview
    private TextView headTitle;
    private RotateAnimation animation;

    //执行模拟值，但实际不移动
//    private boolean onlypreData;
    private boolean actruallyHead = true, actruallyFoot = true;
    private boolean prenomore = true;


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        if (animator1 != null) {
            animator1.cancel();
            animator1 = null;
        }
    }


    public SRecyclerView(Context context) {
        this(context, null);
    }

    public SRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        scrollingParentHelper = new NestedScrollingParentHelper(this);

        headLayout = new LinearLayout(getContext());
        headLayout.setOrientation(VERTICAL);
        myRecyclerView = new MyRecyclerView(getContext());
        myRecyclerView.setNestedScrollingEnabled(true);
        footLayout = new LinearLayout(getContext());
        footLayout.setOrientation(VERTICAL);

        addView(headLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(myRecyclerView, new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(footLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public SRecyclerView setRefreshMode(boolean head, boolean foot, boolean canLoadingHeader, boolean canloadingFooter) {
        this.canheader = head;
        this.canfooter = foot;
        this.canLoadingHeader = canLoadingHeader;
        this.canLoadingFooter = canloadingFooter;
        return this;
    }

    public int getScrolls() {
        return scrolls;
    }

    public SRecyclerView setBackgdColor(int color) {
        myRecyclerView.setBackgroundColor(color);
        return this;
    }

    public SRecyclerView addHeader(View headView, int headerHeight) {
        this.headerHeight = headerHeight;
        headLayout.removeAllViews();
        headLayout.addView(headView);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight);
        params.topMargin = -headerHeight;
        headLayout.setLayoutParams(params);
        return this;
    }

    public SRecyclerView addFooter(View footView, int footHeight) {
        this.footHeight = footHeight;
        footLayout.removeAllViews();
        footLayout.addView(footView);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, footHeight);
        footLayout.setLayoutParams(layoutParams);
        return this;
    }

    public SRecyclerView setAdapter(RecyclerView.LayoutManager manager, RecyclerView.Adapter adapter) {
        myRecyclerView.setLayoutManager(manager);
        myRecyclerView.setAdapter(adapter);
        return this;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public SRecyclerView setRefreshing() {
        headLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrolls = headerHeight;
                smoothScroll(0, -headerHeight, SCROLLTYPE.PULLDOWN);
                isLoading = true;
                if (listener != null) {
                    listener.Refreshing();
                }
            }
        }, 300);
        return this;
    }

    public SRecyclerView addDefaultItemTouchCallBack(MyTouchCallBack.OnChangedListener listener) {
        MyTouchCallBack callBack = new MyTouchCallBack(listener);
        new ItemTouchHelper(callBack).attachToRecyclerView(myRecyclerView);
        return this;
    }

    public SRecyclerView addItemTouchHelper(ItemTouchHelper helper) {
        helper.attachToRecyclerView(myRecyclerView);
        return this;
    }

    /**
     * 是否真实滚动头部
     *
     * @return
     */
    public SRecyclerView setActrullyScrollMode(boolean actruallyHead, boolean actrullyFoot) {
        this.actruallyHead = actruallyHead;
        this.actruallyFoot = actrullyFoot;
        return this;
    }

    public SRecyclerView addDefaultHeaderFooter() {
        isusingDefault = true;
        this.headerHeight = dp2px(65);
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.addRule(RelativeLayout.CENTER_IN_PARENT);
        relativeLayout.setLayoutParams(params1);
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.ptr_background);
//        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bg, 0, bg.length));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        RelativeLayout.LayoutParams paramsimageView = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsimageView.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageView.setId(12);
        imageView.setPadding(0, dp2px(12), 0, 0);
        imageView.setLayoutParams(paramsimageView);
        relativeLayout.addView(imageView);

        LinearLayout linearLayout1 = new LinearLayout(getContext());
        RelativeLayout.LayoutParams linearLayout1params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout1params1.addRule(RelativeLayout.BELOW, 12);
        linearLayout1params1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        linearLayout1.setLayoutParams(linearLayout1params1);


        headerprogress = new ImageView(getContext());
//        headerprogress.setImageBitmap(BitmapFactory.decodeByteArray(pro, 0, pro.length));
        headerprogress.setImageResource(R.drawable.ptr_loading);

        headTitle = new TextView(getContext());
        headTitle.setText("下拉刷新");
        LayoutParams headTitleparams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headTitleparams.gravity = Gravity.CENTER_VERTICAL;
        headTitle.setPadding(dp2px(5), 0, 0, 0);
        headTitle.setLayoutParams(headTitleparams);

        linearLayout1.addView(headerprogress);
        linearLayout1.addView(headTitle);

        relativeLayout.addView(linearLayout1);

        headLayout.removeAllViews();
        LayoutParams params2 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight);
        params2.gravity = Gravity.CENTER_VERTICAL;
        headLayout.setOrientation(HORIZONTAL);
        params2.topMargin = -headerHeight;
        headLayout.setLayoutParams(params2);
        headLayout.addView(relativeLayout);


        this.footHeight = dp2px(45);
        footLayout.removeAllViews();
        LinearLayout linearLayout2 = new LinearLayout(getContext());
        linearLayout2.setOrientation(HORIZONTAL);
        LayoutParams layoutParams2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, footHeight);
        layoutParams2.gravity = Gravity.CENTER_VERTICAL;
        linearLayout2.setLayoutParams(layoutParams2);
        ProgressBar progressBar = new ProgressBar(getContext());
        LayoutParams params3 = new LayoutParams(dp2px(30), dp2px(30));
        params3.gravity = Gravity.CENTER_VERTICAL;
        progressBar.setLayoutParams(params3);
        Drawable drawable = getResources().getDrawable(R.drawable.ptr_loading);
        drawable.setBounds(0, 0, dp2px(30), dp2px(30));
        progressBar.setProgressDrawable(drawable);

        TextView footText = new TextView(getContext());
        footText.setText("正在加载...");
        LayoutParams params4 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params4.gravity = Gravity.CENTER_VERTICAL;
        footText.setLayoutParams(params4);

        linearLayout2.addView(progressBar);
        linearLayout2.addView(footText);
        footLayout.addView(linearLayout2);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, footHeight);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        footLayout.setLayoutParams(layoutParams);

        return this;
    }

    /**
     * 下拉progress的动画
     */
    private void startRotate() {
        if (animation == null) {
            animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(300);
            animation.setRepeatCount(-1);
        } else {
            animation.cancel();
        }
        headerprogress.startAnimation(animation);
    }

    public SRecyclerView addItemDecorate(RecyclerView.ItemDecoration decoration) {
        myRecyclerView.addItemDecoration(decoration);
        return this;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return true;
    }

    public SRecyclerView setRefreshingListener(OnRefreshListener listener) {
        this.listener = listener;
        return this;
    }

    public SRecyclerView setPullRate(int rate) {
        if (rate >= 1) {
            pullRate = rate;
        }
        return this;
    }

    public SRecyclerView setPreLoadingCount(final int count) {
        prenomore = false;
        myRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (listener != null)
                    RefreshLoading(recyclerView, count);
            }
        });
        return this;
    }

    public SRecyclerView setScrollListener(RecyclerView.OnScrollListener listener) {
        myRecyclerView.addOnScrollListener(listener);
        return this;
    }

    private void RefreshLoading(RecyclerView recyclerView, int count) {
        if (isLoading || prenomore) {
            return;
        }
        int lastPosition = -1;
        //当前状态为停止滑动状态SCROLL_STATE_IDLE时
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
            //得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
            int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
            lastPosition = findMax(lastPositions);
        } else if (layoutManager instanceof LinearLayoutManager) {
            lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            //通过LayoutManager找到当前显示的最后的item的position
            lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        }

        //时判断界面显示的最后item的position是否等于itemCount总数-1也就是最后一个item的position
        //如果相等则说明已经滑动到最后了
        if (recyclerView.getLayoutManager().getItemCount() - count < 0) {
            return;
        }
        if (lastPosition >= recyclerView.getLayoutManager().getItemCount() - count) {
            if (!isLoading) {
                isLoading = true;
                listener.PreLoading();
            }
        }

    }

    //找到数组中的最大值
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public abstract static class OnRefreshListener {
        public void pullDown(int height) {
        }

        public void pullUp(int height) {
        }

        public abstract void Refreshing();

        public void Loading() {
        }

        public void PreLoading() {
        }
    }


    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        //TODO回拉时的处理
        if (isLoading )
            return;
        if (canheader) {
            //下拉回拉时
            if (myRecyclerView.canPull(-1) && dy > 0 && scrolls < 0) {
                scrolls += dy;
                if (scrolls <= 0) {
                    Log.i(TAG, "onNestedPreScroll:下拉回拉时 ");
                    if (actruallyHead)
                        scrollTo(0, scrolls / pullRate);

                    if (isusingDefault) {
                        if (Math.abs(scrolls / pullRate) >= headerHeight)
                            headTitle.setText("释放刷新");
                        else
                            headTitle.setText("下拉刷新");
                        headerprogress.setRotation(scrolls / pullRate);
                    }

                    consumed[1] = dy;

                    if (listener != null)
                        listener.pullDown(scrolls / pullRate);

                } else if (!myRecyclerView.canPull(1)) {
                    scrollTo(0, 0);
                    scrolls = 0;
                    consumed[1] = 0;
                }
                return;
            }

        }


        if (canfooter) {
            if (myRecyclerView.canPull(1) && dy < 0 && scrolls > 0) {
                scrolls += dy;
                if (scrolls >= 0) {
                    Log.i(TAG, "onNestedPreScroll:上拉回拉时 ");
                    if (actruallyFoot)
                        scrollTo(0, scrolls / pullRate);
                    consumed[1] = dy;

                    if (listener != null)
                        listener.pullUp(scrolls / pullRate);

                } else if (!myRecyclerView.canPull(-1)) {
                    scrollTo(0, 0);
                    scrolls = 0;
                    consumed[1] = 0;
                }
            }
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (isLoading )
            return;
        //TODO拉动的处理
        if ((myRecyclerView.canPull(-1) && dyUnconsumed < 0 && canheader) || (myRecyclerView.canPull(1) && dyUnconsumed > 0 && canfooter)) {
            scrolls += dyUnconsumed;
            Log.i(TAG, "onNestedScroll: " + dyUnconsumed);
            if (scrolls < 0 && actruallyHead)
                scrollTo(0, scrolls / pullRate);
            else if (scrolls > 0 && actruallyFoot)
                scrollTo(0, scrolls / pullRate);

            if (listener != null) {
                if (scrolls > 0)
                    listener.pullUp(scrolls / pullRate);
                else {
                    listener.pullDown(scrolls / pullRate);
                    if (isusingDefault) {
                        if (Math.abs(scrolls / pullRate) >= headerHeight)
                            headTitle.setText("释放刷新");
                        else
                            headTitle.setText("下拉刷新");
                        headerprogress.setRotation(scrolls / pullRate);
                    }
                }
            }
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return scrollingParentHelper.getNestedScrollAxes();
    }


    public enum SCROLLTYPE {
        PULLUP, PULLDOWN, FLINGDOWN, FLINGUP
    }

    public class DecelerateAccelerateInterpolator implements TimeInterpolator {

        @Override
        public float getInterpolation(float input) {
            float result;
            if (input <= 0.5) {
                result = (float) (Math.sin(Math.PI * input)) / 2;
            } else {
                result = (float) (2 - Math.sin(Math.PI * input)) / 2;
            }
            return result;
        }
    }


    public void notifyRefreshComplete() {
        Log.i(TAG, "notifyRefreshComplete: smoothScroll");
        if (animation != null)
            animation.cancel();
        long current = System.currentTimeMillis() - beginRefreshing;
        if (isusingDefault)
            headTitle.setText("刷新完成");
        footLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                isLoading = false;
                smoothScroll(scrolls / pullRate, 0, scrolls >= 0 ? SCROLLTYPE.PULLUP : SCROLLTYPE.PULLDOWN);
            }
        }, current > 500 ? 100 : 500);
    }

    public void notifyRefreshComplete(boolean prenomore) {
        this.prenomore = prenomore;
        notifyRefreshComplete();
    }

    @Override
    public void onStopNestedScroll(View child) {
        scrollingParentHelper.onStopNestedScroll(child);
        Log.i(TAG, "onStopNestedScroll: 111");
        if (isLoading ) {
            return;
        }
        Log.i(TAG, "onStopNestedScroll:222 ");
        if (scrolls / pullRate <= -headerHeight && canLoadingHeader) {
            if (listener != null && !isLoading) {
                listener.Refreshing();
                beginRefreshing = System.currentTimeMillis();
            }

            if (isusingDefault) {
                headTitle.setText("正在刷新");
                headerprogress.setRotation(scrolls / pullRate);
                startRotate();
            }
            isLoading = true;
            smoothScroll(scrolls / pullRate, -headerHeight, SCROLLTYPE.PULLDOWN);
        } else if (scrolls / pullRate >= footHeight && canLoadingFooter) {

            if (listener != null && !isLoading)
                listener.Loading();

            isLoading = true;
            smoothScroll(scrolls / pullRate, footHeight, SCROLLTYPE.PULLUP);
        } else {
            Log.i(TAG, "onStopNestedScroll: 3333");
            smoothScroll(scrolls / pullRate, 0, scrolls >= 0 ? SCROLLTYPE.PULLUP : SCROLLTYPE.PULLDOWN);
        }
    }

    private void smoothScroll(final int from, final int to, final SCROLLTYPE type) {
        if (from == to)
            return;
        Log.i(TAG, "smoothScroll: " + from + "-" + to);
        if (animator != null) {
            animator.cancel();
            animator.setIntValues(from, to);
            animator.setDuration(getAnimatorDuring(from, to));
        } else {
            animator = ValueAnimator.ofInt(from, to);
            animator.setDuration(getAnimatorDuring(from, to));
            animator.setInterpolator(new DecelerateInterpolator());
        }
        animator.removeAllUpdateListeners();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();

                scrolls = pullRate * value;

                if (listener == null)
                    scrollTo(0, value);
                else {
                    if (type == SCROLLTYPE.PULLDOWN) {
                        if (actruallyHead)
                            scrollTo(0, value);
                        listener.pullDown(value);
                    } else {
                        if (actruallyFoot)
                            scrollTo(0, value);
                        listener.pullUp(value);
                    }

                }
            }
        });
        animator.start();
    }

    private long getAnimatorDuring(int from, int to) {
        long l = (long) (maxTime * ((float) Math.abs(from - to) / (float) headerHeight));
        if (l < maxTime)
            l = maxTime;
        return l > 550 ? 550 : l;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        scrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    private class MyRecyclerView extends RecyclerView {
        private StaggeredGridLayoutManager staggeredGridLayoutManager = null;
        private LinearLayoutManager linearLayoutManager = null;
        private GridLayoutManager gridLayoutManager = null;
        private boolean canpull = true;

        public MyRecyclerView(Context context) {
            super(context);
//            setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
            setHorizontalFadingEdgeEnabled(false);
            setVerticalFadingEdgeEnabled(false);
            setOverScrollMode(OVER_SCROLL_NEVER);
            setItemAnimator(new DefaultItemAnimator());
            setVerticalScrollBarEnabled(false);
            setHorizontalScrollBarEnabled(false);
        }

        @Override
        public void setLayoutManager(LayoutManager layout) {
            super.setLayoutManager(layout);
            canpull = true;
            if (layout instanceof LinearLayoutManager) {
                linearLayoutManager = (LinearLayoutManager) layout;
                if (linearLayoutManager.getOrientation() != VERTICAL)
                    canpull = false;
            } else if (layout instanceof GridLayoutManager) {
                gridLayoutManager = (GridLayoutManager) layout;
                if (gridLayoutManager.getOrientation() != VERTICAL)
                    canpull = false;
            } else if (layout instanceof StaggeredGridLayoutManager) {
                staggeredGridLayoutManager = (StaggeredGridLayoutManager) layout;
                if (staggeredGridLayoutManager.getOrientation() != VERTICAL)
                    canpull = false;
            }
        }

        /**
         * -1 pulldown 1 pullup
         * recyclerview不能滑动了，外层就可滑动了
         */
        public boolean canPull(int orentation) {
            return canpull && (orentation == 1 || orentation == -1) && !canScrollVertically(orentation);
        }
    }

    public static class MyTouchCallBack extends ItemTouchHelper.Callback {
        OnChangedListener listener;
        private boolean canswitch = true, canswipe;

        public MyTouchCallBack(OnChangedListener listener) {
            this.listener = listener;
        }

        public void setMoveMode(boolean canswitch, boolean swipe) {
            this.canswitch = canswitch;
            this.canswipe = swipe;
        }

        /**
         * 设置移动方向 模式
         *
         * @param recyclerView
         * @param viewHolder
         * @return
         */
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragflag = 0;
            int swipeflag = 0;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                if (canswitch)
                    dragflag = ItemTouchHelper.UP |
                            ItemTouchHelper.DOWN |
                            ItemTouchHelper.LEFT |
                            ItemTouchHelper.RIGHT;
                if (canswipe)
                    swipeflag = ItemTouchHelper.END;
            } else {
                if (canswitch)
                    dragflag = ItemTouchHelper.UP |
                            ItemTouchHelper.DOWN;
                if (canswipe)
                    swipeflag = ItemTouchHelper.END;
            }
            return makeMovementFlags(dragflag, swipeflag);
        }

        /**
         * 移动过程中
         *
         * @param recyclerView
         * @param viewHolder
         * @param target
         * @return
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

            int position = viewHolder.getAdapterPosition();
            int Positionto = target.getAdapterPosition();
            if (position < Positionto) {
                for (int i = position; i < Positionto; i++) {
                    listener.onMove(i, i + 1);
                }
            } else {
                for (int i = position; i < Positionto; i--) {
                    listener.onMove(i, i - 1);
                }
            }
            recyclerView.getAdapter().notifyItemMoved(position, Positionto);
            return true;
        }

        public int dp2px(Context context, float dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                Rect src = new Rect(
                        itemView.getLeft() + dp2px(itemView.getContext(), 10),
                        itemView.getTop() + dp2px(itemView.getContext(), 10),
                        itemView.getRight() - dp2px(itemView.getContext(), 10),
                        itemView.getBottom() - dp2px(itemView.getContext(), 10)
                );
                Paint paint = new Paint();
                paint.setColor(0x88888888);
                c.drawRect(src, paint);
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        /**
         * 绘制高亮
         *
         * @param viewHolder
         * @param actionState
         */
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                viewHolder.itemView.setBackgroundColor(0xA8888888);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setBackgroundColor(0);
        }

        /**
         * 滑动的时候
         *
         * @param viewHolder
         * @param direction
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            listener.swiped(viewHolder.getAdapterPosition(), direction);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return super.isLongPressDragEnabled();
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return super.isItemViewSwipeEnabled();
        }

        /**
         * 交换后
         *
         * @param recyclerView
         * @param viewHolder
         * @param fromPos
         * @param target
         * @param toPos
         * @param x
         * @param y
         */
        @Override
        public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
            super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
            listener.moved(fromPos, toPos);
        }

        public static interface OnChangedListener {
            void moved(int from, int to);

            void onMove(int from, int to);

            void swiped(int position, int direction);
        }
    }

}
