package com.xd.customviewpagerindicator.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xd.customviewpagerindicator.R;

import java.util.List;

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
    private static final int DEFAULR_TAB_COUNT = 3;

    private int mInitTranslationX;
    private int mMoveTranslationX;
    //用户不设置的话，默认的是三个
    private int count = DEFAULR_TAB_COUNT;

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

        if (count != 1) {
            mTriangleWidth = (int) ((w / count) * RADIO_TRIANGLE_WIDTH);
            //默认角度为45度
            mTriangleHeight = mTriangleWidth / 2;
            mInitTranslationX = w / count / 2 - mTriangleWidth / 2;
        } else {
            mTriangleWidth = (int) ((w / 3) * RADIO_TRIANGLE_WIDTH);
            mTriangleHeight = mTriangleWidth / 2;
            mInitTranslationX = w / count / 2 - mTriangleWidth / 2;
        }

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

        //处理容器移动出当前屏幕的逻辑
        if (position >= count - 2 && positionOffset > 0 && getChildCount() > count) {
            if (count != 1) {
                int moveDistance = (int) ((position - (count - 2)) * tabWidth + positionOffset * tabWidth);
                this.scrollTo(moveDistance, 0);
            } else {
                int moveDistance = (int) (position * tabWidth + tabWidth * positionOffset);
                this.scrollTo(moveDistance, 0);
            }
        }

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

    //向外界提供的方法 用于动态的设置tab的数量
    public void setTabCounts(int count) {
        if (count > 0)
            this.count = count;
    }

    //向外界提供的方法 用于动态设置tab
    public void setItemTabs(List<String> titles) {
        if (titles != null && titles.size() > 0) {
            //先移除所有布局文件中的views
            this.removeAllViews();
            for (String title : titles)
                addView(generateTextView(title));
        }
    }

    private static final int COLOR_NORMAL_TEXT = 0x77FFFFFF;

    private View generateTextView(String title) {
        TextView textView = new TextView(getContext());
        textView.setText(title);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setTextColor(COLOR_NORMAL_TEXT);
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.width = getScreenWidth() / count;
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    private ViewPager mViewPager;

    //把处理代码的逻辑封装到自定义View中
    public void setViewPager(ViewPager viewPager, int pos) {
        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //偏移量为position * width + positionOffset * width
                scroll(position, positionOffset);
                if (myOnPagerChangeListener != null)
                    myOnPagerChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                if (myOnPagerChangeListener != null)
                    myOnPagerChangeListener.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (myOnPagerChangeListener != null)
                    myOnPagerChangeListener.onPageScrollStateChanged(state);
            }
        });
        mViewPager.setCurrentItem(pos);
    }

    //自己处理了这个监听滚动的逻辑，所以需要向用户提供一个接口，让用户也可以用上监听方法
    //在自定义view时，自己处理逻辑时占用了某个接口，一定要自定义一个接口，把原来的回调提供给用户
    public interface MyOnPagerChangeListener {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);
    }

    private MyOnPagerChangeListener myOnPagerChangeListener;

    public void setMyOnPagerChangeListener(MyOnPagerChangeListener myOnPagerChangeListener) {
        this.myOnPagerChangeListener = myOnPagerChangeListener;
    }
}
