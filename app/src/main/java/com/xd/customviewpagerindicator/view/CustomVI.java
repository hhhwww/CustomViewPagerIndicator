package com.xd.customviewpagerindicator.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.xd.customviewpagerindicator.R;

/**
 * Created by hhhhwei on 16/3/15.
 */
public class CustomVI extends LinearLayout {

    private Paint mPaint;
    private Path mPath;

    private int mTriangleWidth;
    private int mTriangleHeight;

    //为了适应不同的屏幕
    private static final float RADIO_TRIANGLE_WIDTH = 1 / 6f;
    private static final int DEFAULR_TAB_COUNT = 4;

    private int mInitTranslationX;
    private int mMoveTranslationX;
    private int count;

    public CustomVI(Context context) {
        this(context, null);
    }

    public CustomVI(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#ffffffff"));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));
        mPaint.setDither(true);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomVI);
        count = typedArray.getInt(R.styleable.CustomVI_visible_tab_count, DEFAULR_TAB_COUNT);
        count = (count < 0) ? DEFAULR_TAB_COUNT : count;
        typedArray.recycle();
    }

    //根据一些空间的宽高设置一些宽高时
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mTriangleWidth = (int) ((w / count) * RADIO_TRIANGLE_WIDTH);
        //默认角度为45度
        mTriangleHeight = mTriangleWidth / 2;
        mInitTranslationX = w / count / 2 - mTriangleWidth / 2;

        initTriangle();
    }

    //用Path类初始化三角形
    private void initTriangle() {
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mPath.close();
    }

    //绘制自己的孩子
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        canvas.translate(mInitTranslationX + mMoveTranslationX, getHeight());
        canvas.drawPath(mPath, mPaint);
        canvas.restore();

    }

    public void scroll(int position, float positionOffset) {
        int tabWidth = getWidth() / count;
        Log.e("fuck", "getWidth()" + getWidth());
        mMoveTranslationX = (int) (position * tabWidth + positionOffset * tabWidth);
        invalidate();
    }

    //加载完xml后回调,设置子View的params
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int childCount = getChildCount();
        if (childCount < 0) return;

        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            LinearLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            //这个一定要设为0，不然xml布局会影响代码的显示效果
            layoutParams.weight = 0;
            //每个屏幕中显示三个控件，剩下的就被挤出去了
            //在这个函数里getWidth为0
            layoutParams.width = getScreenWidth() / count;
            Log.e("fuck", "getScreenWidth()" + getScreenWidth());
            view.setLayoutParams(layoutParams);
        }
    }

    //得到屏幕的绝对宽度
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
