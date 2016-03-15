package com.xd.customviewpagerindicator.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.LinearLayout;

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

    private int mInitTranslationX;
    private int mMoveTranslationX;

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
    }

    //根据一些空间的宽高设置一些宽高时
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mTriangleWidth = (int) ((w / 3) * RADIO_TRIANGLE_WIDTH);
        //默认角度为45度
        mTriangleHeight = mTriangleWidth / 2;
        mInitTranslationX = w / 3 / 2 - mTriangleWidth / 2;

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
        int tabWidth = getWidth() / 3;
        mMoveTranslationX = (int) (position * tabWidth + positionOffset * tabWidth);
        invalidate();
    }
}
