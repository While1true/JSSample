/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.myapplication.View;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
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
 * 子View必须是实现了NestScroll的View
 * <p>
 * 功能包括 下拉刷新  上啦加载 只预估数值实现自定义添加在布局  overscroll itemtouchhelper
 * 出列快速滑动时的处理不是很完美，但能用
 */

public class SAllView extends LinearLayout implements NestedScrollingParent {
    public static final String TAG = "SRecyclerView";
    private NestedScrollingParentHelper scrollingParentHelper;
    private LinearLayout headLayout;
    private LinearLayout footLayout;
    private ViewGroup myScrollView;
    static final Interpolator sQuinticInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };
    ScrollerCompat scrollerCompat = ScrollerCompat.create(getContext(), sQuinticInterpolator);
    private OnRefreshListener listener;

    private int headerHeight = dp2px(50), footHeight = dp2px(40);

    private int pullRate = 3;

    private boolean actruallyHead = true, actruallyFoot = true;

    private int scrolls;

    private long beginRefreshing;

    private boolean isLoading = false;

    private int maxTime = 200;

    private int maxFastOverScroll = dp2px(100);

    private boolean canheader = true, canfooter = false;
    private ValueAnimator animator;
    private ValueAnimator animator1;
    private boolean canOverLoadingHeader = true, canOverLoadingfooter = true, canLoadingHeader = true, canLoadingFooter = false;
    private boolean canOverscroll = false;
    private boolean isusingDefault;
    private ImageView headerprogress;
    private TextView headTitle;
    private RotateAnimation animation;
    private View child;

    public SAllView(Context context) {
        this(context, null);
    }

    public SAllView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SAllView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public View getHeaderView() {

        return headLayout;
    }

    private void init() {
        setOrientation(VERTICAL);
        scrollingParentHelper = new NestedScrollingParentHelper(this);

        headLayout = new LinearLayout(getContext());
        headLayout.setOrientation(VERTICAL);
        myScrollView = new LinearLayout(getContext());
        ViewCompat.setNestedScrollingEnabled(myScrollView, true);
        footLayout = new LinearLayout(getContext());
        footLayout.setOrientation(VERTICAL);
        addView(headLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(myScrollView, new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(footLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount > 4) {
            throw new IllegalArgumentException("只能有一个直接子布局");
        }
        child = getChildAt(3);
        removeView(child);
        ViewCompat.setNestedScrollingEnabled(child, true);
        myScrollView.addView(child);
    }

    public SAllView setRefreshMode(boolean head, boolean foot, boolean canLoadingHeader, boolean canLoadingFooter) {
        this.canheader = head;
        this.canfooter = foot;
        this.canLoadingHeader = canLoadingHeader;
        this.canLoadingFooter = canLoadingFooter;
        return this;
    }

    public SAllView addHeader(View headView, int headerHeight) {
        this.headerHeight = headerHeight;
        headLayout.removeAllViews();
        headLayout.addView(headView);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight);
        params.topMargin = -headerHeight;
        headLayout.setLayoutParams(params);
        return this;
    }

    public SAllView addFooter(View footView, int footHeight) {
        this.footHeight = footHeight;
        footLayout.removeAllViews();
        footLayout.addView(footView);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, footHeight);
        footLayout.setLayoutParams(layoutParams);
        return this;
    }

    public SAllView setBackgdColor(int color) {
        myScrollView.setBackgroundColor(color);
        return this;
    }

    public SAllView setView(int res) {
        myScrollView.removeAllViews();
        View view = LayoutInflater.from(getContext()).inflate(res, myScrollView, false);
        myScrollView.addView(view);
        return this;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public SAllView setRefreshing() {
        headLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrolls = headerHeight;
                smoothScroll(0, -headerHeight);
                isLoading = true;
                if (listener != null) {
                    listener.Refreshing();
                }
            }
        }, 300);
        return this;
    }

    public SAllView addDefaultHeaderFooter() {
        isusingDefault = true;
        this.headerHeight = dp2px(65);
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.addRule(RelativeLayout.CENTER_IN_PARENT);
        relativeLayout.setLayoutParams(params1);
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.mipmap.prf_background);
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
        headerprogress.setImageResource(R.mipmap.net_progressbar);

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
        Drawable drawable = getResources().getDrawable(R.mipmap.net_progressbar);
        drawable.setBounds(0, 0, dp2px(30), dp2px(30));
        progressBar.setIndeterminateDrawable(drawable);
//        progressBar.setProgressDrawable(drawable);
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


    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return true;
    }

    public SAllView setRefreshingListener(OnRefreshListener listener) {
        this.listener = listener;
        return this;
    }

    public SAllView setPullRate(int rate) {
        if (rate >= 1) {
            pullRate = rate;
        }
        return this;
    }

    @TargetApi(23)
    public SAllView setScrollChangeListener(OnScrollChangeListener listener) {
        myScrollView.setOnScrollChangeListener(listener);
        return this;
    }


    public abstract static class OnRefreshListener {
        public void pullDown(int height) {
        }

        public void pullUp(int height) {
        }

        public abstract void Refreshing();

        public void Loading() {
        }

        public void PreLoading(int pre) {
        }

        public void fling(int height) {
        }
    }

    /**
     * @param //loading  快速滑动加载？
     * @param overscroll 超出范围？
     * @return
     */
    public SAllView setOverScrollEnable(boolean overscroll, boolean loadingheader, boolean loadingfooter) {
        canOverLoadingHeader = loadingheader;
        canOverLoadingfooter = loadingfooter;
        canOverscroll = overscroll;
        return this;
    }

    /**
     * 是否真实滚动头部
     *
     * @return
     */
    public SAllView setActrullyScrollMode(boolean actruallyHead, boolean actrullyFoot) {
        this.actruallyHead = actruallyHead;
        this.actruallyFoot = actrullyFoot;
        return this;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        //TODO回拉时的处理
        if (isLoading || !scrollerCompat.isFinished())
            return;
        if (canheader) {
            //下拉回拉时
            if (!child.canScrollVertically(-1) && dy > 0 && scrolls < 0) {
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

                } else if (child.canScrollVertically(1)) {
                    scrollTo(0, 0);
                    scrolls = 0;
                    consumed[1] = 0;
                }
                return;
            }

        }


        if (canfooter) {
            if (!child.canScrollVertically(1) && dy < 0 && scrolls > 0) {
                scrolls += dy;
                if (scrolls >= 0) {
                    Log.i(TAG, "onNestedPreScroll:上拉回拉时 ");
                    if (actruallyFoot)
                        scrollTo(0, scrolls / pullRate);
                    consumed[1] = dy;

                    if (listener != null)
                        listener.pullUp(scrolls / pullRate);

                } else if (child.canScrollVertically(-1)) {
                    scrollTo(0, 0);
                    scrolls = 0;
                    consumed[1] = 0;
                }
            }
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (isLoading || !scrollerCompat.isFinished())
            return;
        //TODO拉动的处理
        if ((!child.canScrollVertically(-1) && dyUnconsumed < 0 && canheader) || (!child.canScrollVertically(1) && dyUnconsumed > 0 && canfooter)) {
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
        Log.i(TAG, "onNestedFling: " + scrolls + "==" + velocityY);
        if ((!child.canScrollVertically(1) && !child.canScrollVertically(-1)) || !scrollerCompat.isFinished())
            return false;
        if (canOverscroll && scrolls == 0 && Math.abs(velocityY) > 1000) {
            if (!scrollerCompat.isFinished())
                scrollerCompat.abortAnimation();
            if (animator1 != null)
                animator1.cancel();
            Log.i(TAG, "onNestedFling: " + velocityY);
            scrollerCompat.fling(0, myScrollView.getScrollY(), 0, (int) velocityY, 0, 0, -10 * maxFastOverScroll, myScrollView.getBottom() - myScrollView.getTop() + 10 * maxFastOverScroll);
            ViewCompat.postInvalidateOnAnimation(this);
        }
        return canOverscroll;
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return scrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void computeScroll() {

        if (scrollerCompat.computeScrollOffset()) {
            if (scrolls != 0)
                scrollerCompat.abortAnimation();
            if ((!child.canScrollVertically(-1) && scrollerCompat.getCurrY() < 0)) {
                int i = scrollerCompat.getFinalY() - scrollerCompat.getCurrY();
                Log.i(TAG + "a", "computeScroll: xiala" + i);
                if (i < -headerHeight && canOverLoadingHeader) {
                    smoothScroll(0, -headerHeight);
                    scrolls = -headerHeight;
                    if (listener != null) {
                        listener.Refreshing();
                        beginRefreshing = System.currentTimeMillis();
                    }
                } else {
                    if (i > 0)
                        return;
                    if (!actruallyHead) {
                        if (listener != null)
                            listener.pullDown(i < -maxFastOverScroll ? -maxFastOverScroll : i);
                    } else
                        smoothScrollRepeat(i < -maxFastOverScroll ? -maxFastOverScroll : i);
                    scrolls = 0;
                }
                scrollerCompat.abortAnimation();
            } else if ((!child.canScrollVertically(1) && scrollerCompat.getCurrY() > 0)) {
                int i2 = scrollerCompat.getFinalY() - scrollerCompat.getCurrY();
                Log.i(TAG + "a", "computeScroll: shangla" + i2);
                if (i2 > headerHeight && canOverLoadingfooter) {
                    smoothScroll(0, footHeight);
                    if (listener != null)
                        listener.Loading();
                    scrolls = footHeight;
                } else {
                    if (i2 < 0)
                        return;
                    if (!actruallyFoot) {
                        if (listener != null)
                            listener.pullUp(i2 > maxFastOverScroll ? maxFastOverScroll : i2);
                    } else
                        smoothScrollRepeat(i2 > maxFastOverScroll ? maxFastOverScroll : i2);
                    scrolls = 0;
                }
                scrollerCompat.abortAnimation();
            }
        } else {
//            Log.i(TAG, "computeScroll: finish");
        }

    }

    private void smoothScrollRepeat(final int max) {
        if (animator1 != null) {
            animator1.cancel();
            animator1.setIntValues(0, max, 0);
            animator1.setDuration(400);
        } else {
            animator1 = ValueAnimator.ofInt(0, max, 0);
            animator1.setDuration(400);
            animator1.setInterpolator(new AccelerateDecelerateInterpolator());
        }
        animator1.removeAllUpdateListeners();
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                scrollTo(0, value);
                scrolls = pullRate * value;

                if (listener != null) {
                    if (max >= 0)
                        listener.pullUp(value);
                    else if (max <= 0)
                        listener.pullDown(value);
                }

            }
        });
        animator1.start();
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
                smoothScroll(scrolls / pullRate, 0);
            }
        }, current > 500 ? 100 : 500);
    }

    @Override
    public void onStopNestedScroll(View child) {
        scrollingParentHelper.onStopNestedScroll(child);
        Log.i(TAG, "onStopNestedScroll: ");
        if (isLoading || !scrollerCompat.isFinished()) {
            return;
        }
        if (scrolls / pullRate <= -headerHeight) {
            if (listener != null && !isLoading && canLoadingHeader) {
                listener.Refreshing();
                beginRefreshing = System.currentTimeMillis();
            }

            if (isusingDefault) {
                headTitle.setText("正在刷新");
                headerprogress.setRotation(scrolls / pullRate);
                startRotate();
            }
            isLoading = true;
            smoothScroll(scrolls / pullRate, -headerHeight);
        } else if (scrolls / pullRate >= footHeight && canLoadingFooter) {

            if (listener != null && !isLoading)
                listener.Loading();

            isLoading = true;
            smoothScroll(scrolls / pullRate, footHeight);
        } else {
            smoothScroll(scrolls / pullRate, 0);
        }
    }

    private void smoothScroll(final int from, final int to) {
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

                if (listener != null) {
                    if (from == 0) {
                        if (to >= 0) {
                            if (actruallyFoot)
                                scrollTo(0, value);
                            listener.pullUp(value);
                        } else if (to <= 0) {
                            if (actruallyHead)
                                scrollTo(0, value);
                            listener.pullDown(value);
                        }
                    } else {
                        if (from >= 0) {
                            if (actruallyFoot)
                                scrollTo(0, value);
                            listener.pullUp(value);
                        } else if (from <= 0) {
                            if (actruallyHead)
                                scrollTo(0, value);
                            listener.pullDown(value);
                        }
                    }
                }

            }
        });
        animator.start();
    }

    private long getAnimatorDuring(int from, int to) {
        return (long) (maxTime * ((float) Math.abs(from - to) / (float) headerHeight));
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
//        if(animator!=null)
//            animator.cancel();
//        if(animator1!=null)
//            animator1.cancel();
        scrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }

}
