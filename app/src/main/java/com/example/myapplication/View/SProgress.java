/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.myapplication.View;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.myapplication.R;

import java.util.IllegalFormatException;


/**
 * Created by S0005 on 2017/4/19.
 */

public class SProgress extends View {
    public static final String TAG = "SProgress";
    private int progressColor = 0xffff3030, progressendcolor = 0xffff6969, backColor = 0xffff8787, textColor = 0xffffffff;
    private Paint bgpaint;
    private Paint textPaint;
    private Paint proPaint;
    private float textsize = dp2px(9);
    private float progress = 0;

    private int maxDuring = 1600, minDuring = 500;

    private int width = dp2px(40), height = dp2px(10);
    private float textbaseline;

    private float minProgress;
    private RectF rectbg;
    private RectF rectprogress;

    private Rect padding = new Rect();
    private Path path;


    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public SProgress(Context context) {
        this(context, null);
    }

    public SProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainData(attrs);
        init();
    }

    private void obtainData(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SProgress);
        progressColor = a.getColor(R.styleable.SProgress_progressColor, progressColor);
        progressendcolor = a.getColor(R.styleable.SProgress_progressendcolor, progressendcolor);
        backColor = a.getColor(R.styleable.SProgress_backColor, backColor);
        textColor = a.getColor(R.styleable.SProgress_textColor, textColor);
        textsize = a.getDimension(R.styleable.SProgress_textsize, textsize);
        minProgress = a.getFloat(R.styleable.SProgress_minProgress, 0);
        float startprogress = a.getFloat(R.styleable.SProgress_startprogress, 0);
        a.recycle();
        Log.i(TAG, "obtainData: "+startprogress);
        if(startprogress>0){
            animatorToProgress(startprogress);
        }
    }

    public float getProgress() {
        return progress;
    }

    public SProgress setColor(int progressColor, int backColor, int textColor) {
        setgradialColor(progressColor,progressColor,backColor,textColor);
        return this;
    }

    public SProgress setgradialColor(int startprogressColor, int endprogresscolor, int backColor, int textColor) {
        this.progressColor = startprogressColor;
        this.progressendcolor = endprogresscolor;
        this.backColor = backColor;
        this.textColor = textColor;

        textPaint.setColor(textColor);

        proPaint.setColor(progressColor);
        LinearGradient linearGradient = new LinearGradient(0, 0, width, height, progressColor, progressendcolor, Shader.TileMode.CLAMP);
        proPaint.setShader(linearGradient);
        bgpaint.setColor(backColor);
        return this;
    }

    public SProgress setTextSize(float size) {
        textsize = dp2px(size);
        return this;
    }

    public SProgress setminprogress(float minprogress) {
        this.minProgress = minprogress;
        return this;
    }

    public SProgress setProgress(final float progress) {
        if (progress > 100||progress<0)
            throw new NumberFormatException("进度0-100");
        SProgress.this.progress = progress < minProgress ? minProgress : progress;
        invalidate();
        return this;
    }

    public SProgress animatorToProgress(final float progress) {

        Log.i(TAG, "setProgress: " + progress);
        //最小位置不执行动画
        if (progress > 100||progress<0)
            throw new NumberFormatException("进度0-100");
        post(new Runnable() {
            @Override
            public void run() {
                if (SProgress.this.progress <= minProgress && progress <= minProgress) {
                    SProgress.this.progress = progress;
                    invalidate();
                } else {
                    startAnimator(progress);
                }
            }
        });
        return this;
    }

    private void startAnimator(float pors) {
        ValueAnimator animator = ValueAnimator.ofFloat((progress < minProgress ? minProgress : progress), pors < minProgress ? minProgress : pors);
        int i = (int) (pors - progress) * maxDuring / 100;
        animator.setDuration(i < minDuring ? minDuring : i);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                //结束时显示原值带小数，中间变换显示不带小数

                if (animation.getAnimatedFraction() == 1)
                    progress = (float) animation.getAnimatedValue();
                else
                    progress = (int) ((float) animation.getAnimatedValue());
                invalidate();
            }
        });
        animator.start();
    }

    private void init() {
       setLayerType(LAYER_TYPE_SOFTWARE,null);
        bgpaint = new Paint();
        bgpaint.setColor(backColor);
        bgpaint.setAntiAlias(true);

        proPaint = new Paint();
        proPaint.setColor(progressColor);
        LinearGradient linearGradient = new LinearGradient(0, 0, width, height, progressColor, progressendcolor, Shader.TileMode.CLAMP);
        proPaint.setShader(linearGradient);
        proPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textsize);
        textbaseline = getFontstart(textPaint);
        rectbg = new RectF();
        rectprogress = new RectF();
    }

    public float getFontstart(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (fm.descent + fm.ascent) / 2 - fm.leading;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        rectbg.set(padding.left, padding.top, (float) width - padding.right, (float) height - padding.bottom);
        canvas.drawRoundRect(rectbg, (height - padding.top-padding.bottom) / 2, (height - padding.top-padding.bottom) / 2, bgpaint);



        if(path==null) {
            path = new Path();
            path.addRoundRect(rectbg,(height - padding.top-padding.bottom) / 2, (height - padding.top-padding.bottom) / 2,Path.Direction.CW);
        }
        int save = canvas.save();
        canvas.clipPath(path);
        rectprogress.set(padding.left, padding.top, padding.left + ((width - padding.left-padding.right) * (progress < minProgress ? minProgress : progress) / 100), height - padding.bottom);
        canvas.drawRoundRect(rectprogress, (float) ((height - padding.top-padding.bottom) / 2), (float) ((height - padding.top-padding.bottom) / 2), proPaint);
        canvas.restoreToCount(save);


        float length = textPaint.measureText(getText(progress));

        canvas.drawText(getText(progress), width / 2 - length / 2, height / 2 - textbaseline, textPaint);

    }

    private String getText(float progress) {
        String progresstext = String.format(getResources().getString(R.string.sprogress_string), progress);
        return progresstext.replace(".0", "");
//        "已领" + progresstext.replace(".0", "") + "%"
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
            height = Math.min(heightSize, height);

        }
        padding.set(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        setMeasuredDimension(width, height);
        path=null;

    }
}
