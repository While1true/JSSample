package com.example.myapplication.View;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


import com.example.myapplication.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by S0005 on 2017/5/12.
 */

public class SLoading extends View {


    private int color = 0xffff4070;

    private float gap = -1;

    private float radius = -1;

    private int num = 3;

    private Paint paint;

    private int width = 60;
    private int height = 100;
    List<Progress> list = new ArrayList<>();
    private AnimatorSet.Builder play;
    private AnimatorSet set;

    public SLoading(Context context) {
        this(context, null);
    }

    public SLoading(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainData(attrs);
        initialized();
    }

    private void obtainData(AttributeSet attrs) {
        TypedArray a=getContext().obtainStyledAttributes(attrs, R.styleable.SLoading);
        radius=a.getDimension(R.styleable.SLoading_sradius,-1);
        gap=a.getDimension(R.styleable.SLoading_sgap,-1);
        num=a.getInt(R.styleable.SLoading_snum,-1);
        color=a.getColor(R.styleable.SLoading_scolor,0xff4070);
        a.recycle();
    }

    private void initialized() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = Math.min(width, widthSize);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = Math.min(height, heightSize);
        }
        if (gap == -1)
            gap = height / 2;

        if (radius == -1)
            radius = height / 2;

        for (int i = num; i > 0; i--) {
            list.add(new Progress((float) i / (float) num, i == num ? 0 : 1, radius * i / num));
        }

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < num; i++) {
            paint.setAlpha((int) ((0.3+0.7*list.get(i).getPercentage())*255));
            canvas.drawCircle(calculateCenterX(i), height / 2, list.get(i).getCurrent(), paint);
        }
    }

    private float calculateCenterX(int i) {
        return calculateStart() + i * 2 * radius + radius + i * gap;
    }


    private int calculateStart() {
        int contentLength = (int) (2 * num * radius + (num - 1) * gap);

        return width / 2 - contentLength / 2;
    }

    public SLoading setColor(int color) {
        this.color = color;
        return this;
    }

    public SLoading setGap(int gap) {
        this.gap = gap;
        return this;
    }

    public SLoading setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public SLoading setNum(int num) {
        this.num = num;
        return this;
    }

    public SLoading startAnimator() {
        post(new Runnable() {
            @Override
            public void run() {
                setVisibility(VISIBLE);
                goAnimator();
            }
        });
        return this;
    }

    private void goAnimator() {
        if (set != null) {
            set.cancel();
            set.getChildAnimations().clear();
        }
        set = new AnimatorSet();

        for (int i = 0; i < num; i++) {
            final int z = i;
            ValueAnimator animator = ValueAnimator.ofFloat(list.get(i).getType() == 0 ? radius : 0, list.get(i).getType() == 0 ? 0 : radius);
            animator.setDuration(500);
            animator.setRepeatCount(-1);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setCurrentPlayTime((long) (500 * list.get(i).getPercentage()));
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    list.get(z).setPercentage(z==0?1-animation.getAnimatedFraction():animation.getAnimatedFraction());
                    list.get(z).setCurrent((Float) animation.getAnimatedValue());
                    if (z == 0)
                        postInvalidate();
                }
            });
            if (play == null)
                play = set.play(animator);
            else
                play.with(animator);
        }
        set.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnimator();
        super.onDetachedFromWindow();
    }

    public void stopAnimator() {
        if (set != null) {
            set.cancel();
            set.getChildAnimations().clear();
            setVisibility(GONE);
        }
    }

    class Progress implements Serializable {
        float percentage = 0;
        //0 减 1增
        int type;

        public Progress(float percentage, int type, float current) {
            this.percentage = percentage;
            this.type = type;
            this.current = current;
        }

        float current;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public float getCurrent() {
            return current;
        }

        public void setCurrent(float current) {
            this.current = current;
        }

        public float getPercentage() {
            return percentage;
        }

        public void setPercentage(float percentage) {
            this.percentage = percentage;
        }
    }


}
