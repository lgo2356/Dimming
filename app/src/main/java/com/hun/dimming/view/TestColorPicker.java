package com.hun.dimming.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TestColorPicker extends View {

    private RectF outerWheelRect;
    private RectF innerWheelRect;

    private Paint colorViewPaint;
    private Path colorViewPath;

    private float[] colorHSV = new float[]{0f, 0f, 1f};

    private int innerPadding;
    private int outerPadding;

    private int outerWheelRadius;
    private int innerWheelRadius;
    private int colorWheelRadius;

    private int valueSliderWidth;

    private int arrowPointerSize;
    private final int paramOuterPadding = 2; // outer padding of the whole color picker view
    private final int paramInnerPadding = 5; // distance between value slider wheel and inner color wheel
    private final int paramValueSliderWidth = 10; // width of the value slider
    private final int paramArrowPointerSize = 4; // size of the arrow pointer; set to 0 to hide the pointer


    public TestColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TestColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestColorPicker(Context context) {
        super(context);
        init();
    }

    private void init() {
        colorViewPaint = new Paint();
        colorViewPath = new Path();

        outerWheelRect = new RectF();
        innerWheelRect = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(widthSize, heightSize);
        setMeasuredDimension(size, size);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        int centerX = getWidth() / 2;
        int centerY = getWidth() / 2;

        colorViewPath.reset();
        colorViewPath.arcTo(outerWheelRect, 270, -36);
        colorViewPath.arcTo(innerWheelRect, 234, 36);
        colorHSV = new float[]{0f, 1f, 1f};
        colorViewPaint.setColor(Color.HSVToColor(colorHSV));
        canvas.drawPath(colorViewPath, colorViewPaint);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        int centerX = width / 2;
        int centerY = height / 2;

        innerPadding = (int) (paramInnerPadding * width / 100);
        outerPadding = (int) (paramOuterPadding * width / 100);
//        arrowPointerSize = (int) (paramArrowPointerSize * width / 100);
        valueSliderWidth = (int) (paramValueSliderWidth * width / 100);

        outerWheelRadius = width / 2 - outerPadding - arrowPointerSize;
        innerWheelRadius = outerWheelRadius - valueSliderWidth;
        colorWheelRadius = innerWheelRadius - innerPadding;

        outerWheelRect.set(centerX - outerWheelRadius, centerY - outerWheelRadius, centerX + outerWheelRadius, centerY + outerWheelRadius);
        innerWheelRect.set(centerX - innerWheelRadius, centerY - innerWheelRadius, centerX + innerWheelRadius, centerY + innerWheelRadius);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                Log.d("Debug", "Touch!");
                return true;
        }
        return super.onTouchEvent(event);
    }
}
