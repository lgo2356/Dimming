package com.hun.dimming.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import static java.lang.Math.*;

public class MultiColorPicker extends View {

    /**
     * Customizable display parameters (in percents)
     */
    private final int paramOuterPadding = 2; // outer padding of the whole color picker view
    private final int paramInnerPadding = 3; // distance between value slider wheel and inner color wheel
    private final int paramValueSliderWidth = 25; // width of the value slider
    private final int paramArrowPointerSize = 4; // size of the arrow pointer; set to 0 to hide the pointer

    private final int paramColorCount = 21;
    private final float paramHueSpreadAngle = 30f; // in degrees

    private Paint colorWheelPaint;
    private Paint valueSliderPaint;

    private Paint colorViewPaint;
    private Paint centerColorViewPaint;
    private Paint offButtonPaint;

    private Paint colorPointerPaint;
    private RectF colorPointerCoords;

    private Paint valuePointerPaint;
    private Paint valuePointerArrowPaint;

    private RectF outerWheelRect;
    private RectF innerWheelRect;
    private RectF centerOuterWheelRect;
    private RectF centerInnerWheelRect;
    private RectF offButtonRect;

    private Path colorViewPath;
    private Path centerColorViewPath;
    private Path valueSliderPath;
    private Path arrowPointerPath;

    private Bitmap colorWheelBitmap;

    private int valueSliderWidth;
    private int innerPadding;
    private int outerPadding;

    private int outerWheelRadius;
    private int innerWheelRadius;
    private int colorWheelRadius;
    private float centerOuterRadius;
    private float centerInnerRadius;

    private Matrix gradientRotationMatrix;

    /**
     * Currently selected color
     */
    private float[] colorHSV = new float[]{0f, 0f, 1f};
    private float[] adjacentHue = new float[paramColorCount];

    private float[][] colorPickRanges = new float[21][2];
    private float[][] centerColorPickRanges = new float[3][2];
    private int selectedColorPick = -1;

    public MultiColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MultiColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiColorPicker(Context context) {
        super(context);
        init();
    }

    private void init() {
        colorPointerPaint = new Paint();
        colorPointerPaint.setStyle(Style.STROKE);
        colorPointerPaint.setStrokeWidth(2f);
        colorPointerPaint.setARGB(128, 0, 0, 0);

        valuePointerPaint = new Paint();
        valuePointerPaint.setStyle(Style.STROKE);
        valuePointerPaint.setStrokeWidth(2f);

        valuePointerArrowPaint = new Paint();

        colorWheelPaint = new Paint();
        colorWheelPaint.setAntiAlias(true);
        colorWheelPaint.setDither(true);

        valueSliderPaint = new Paint();
        valueSliderPaint.setAntiAlias(true);
        valueSliderPaint.setDither(true);

        colorViewPaint = new Paint();
        colorViewPaint.setAntiAlias(true);

        centerColorViewPaint = new Paint();

        offButtonPaint = new Paint();
        offButtonPaint.setAntiAlias(true);

        colorViewPath = new Path();
        centerColorViewPath = new Path();
        valueSliderPath = new Path();
        arrowPointerPath = new Path();

        outerWheelRect = new RectF();
        innerWheelRect = new RectF();
        centerOuterWheelRect = new RectF();
        centerInnerWheelRect = new RectF();
        offButtonRect = new RectF();

        colorPointerCoords = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = min(widthSize, heightSize);
        setMeasuredDimension(size, size);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        int[] colors = new int[21];
        float[] hues = new float[]{
                0f, 15f, 30f,
                45f, 60f, 75f,
                90f, 120f, 165f,
                180f, 195f, 210f,
                225f, 240f, 255f,
                270f, 285f, 300f,
                315f, 330f, 345f
        };
        float[] saturations = new float[]{
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f
        };
        float[] values = new float[]{
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f
        };

        for (int i = 0; i < colors.length; i++) {
            float[] hsv = new float[3];
            hsv[0] = hues[i];
            hsv[1] = saturations[i];
            hsv[2] = values[i];
            colors[i] = Color.HSVToColor(hsv);
        }

        float sweepAngleStep = 360f / paramColorCount;

        for (int i = 0; i < paramColorCount; i++) {
            float outerStartAngle = 270 - i * sweepAngleStep;
            float innerStartAngle = (90 + (paramColorCount - i - 11.5f) * sweepAngleStep) + 2;

            colorPickRanges[i][0] = outerStartAngle;
            colorPickRanges[i][1] = innerStartAngle;

            colorViewPath.reset();
            colorViewPath.arcTo(outerWheelRect, outerStartAngle, -sweepAngleStep + 2);
            colorViewPath.arcTo(innerWheelRect, innerStartAngle, sweepAngleStep - 2);

            colorViewPaint.setColor(colors[i]);

            canvas.drawPath(colorViewPath, colorViewPaint);
        }

        float centerSweepAngleStep = 360f / 3;
        int[] centerColors = new int[3];
        float[][] colorHSV = new float[][]{{30f, 0.58f, 1f}, {29f, 0.271f, 1f}, {15f, 0.063f, 1f}};

        for (int i = 0; i < centerColors.length; i++) {
            centerColors[i] = Color.HSVToColor(colorHSV[i]);
        }

        for (int i = 0; i < centerColors.length; i++) {
            centerColorPickRanges[i][0] = 210 - i * centerSweepAngleStep;
            centerColorPickRanges[i][1] = 90 - i * centerSweepAngleStep;

            centerColorViewPath.reset();
            centerColorViewPath.arcTo(centerOuterWheelRect, 210 - i * centerSweepAngleStep, -centerSweepAngleStep);
            centerColorViewPath.arcTo(centerInnerWheelRect, 90 - i * centerSweepAngleStep, centerSweepAngleStep);

            centerColorViewPaint.setColor(centerColors[i]);

            canvas.drawPath(centerColorViewPath, centerColorViewPaint);
        }

        // Black button
//        int centerX = getWidth() / 2;
//        int centerY = getHeight() / 2;

        canvas.drawArc(offButtonRect, 270, 360, false, offButtonPaint);

        offButtonPaint.setColor(Color.parseColor("#ffffff"));
        offButtonPaint.setTextSize(dpToPx(getContext(), 14));
        offButtonPaint.setTextAlign(Paint.Align.CENTER);

        int xPos = getWidth() / 2;
        int yPos = (int) (getHeight() / 2 - (offButtonPaint.descent() + offButtonPaint.ascent() / 2));

        canvas.drawText("OFF", xPos, yPos, offButtonPaint);
    }

    private void drawColorWheelPointer(Canvas canvas, float hueAngle) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int colorPointX = (int) (-cos(hueAngle) * colorHSV[1] * colorWheelRadius) + centerX;
        int colorPointY = (int) (-sin(hueAngle) * colorHSV[1] * colorWheelRadius) + centerY;

        float pointerRadius = 0.075f * colorWheelRadius;
        int pointerX = (int) (colorPointX - pointerRadius / 2);
        int pointerY = (int) (colorPointY - pointerRadius / 2);

        colorPointerCoords.set(pointerX, pointerY, pointerX + pointerRadius, pointerY + pointerRadius);
        canvas.drawOval(colorPointerCoords, colorPointerPaint);
    }

    private void drawPointerArrow(Canvas canvas) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        double tipAngle = (colorHSV[2] - 0.5f) * PI;
        double leftAngle = tipAngle + PI / 96;
        double rightAngle = tipAngle - PI / 96;

        double tipAngleX = cos(tipAngle) * outerWheelRadius;
        double tipAngleY = sin(tipAngle) * outerWheelRadius;
//        double leftAngleX = Math.cos(leftAngle) * (outerWheelRadius + arrowPointerSize);
//        double leftAngleY = Math.sin(leftAngle) * (outerWheelRadius + arrowPointerSize);
//        double rightAngleX = Math.cos(rightAngle) * (outerWheelRadius + arrowPointerSize);
//        double rightAngleY = Math.sin(rightAngle) * (outerWheelRadius + arrowPointerSize);

        arrowPointerPath.reset();
        arrowPointerPath.moveTo((float) tipAngleX + centerX, (float) tipAngleY + centerY);
//        arrowPointerPath.lineTo((float) leftAngleX + centerX, (float) leftAngleY + centerY);
//        arrowPointerPath.lineTo((float) rightAngleX + centerX, (float) rightAngleY + centerY);
        arrowPointerPath.lineTo((float) tipAngleX + centerX, (float) tipAngleY + centerY);

        valuePointerArrowPaint.setColor(Color.HSVToColor(colorHSV));
        valuePointerArrowPaint.setStyle(Style.FILL);
        canvas.drawPath(arrowPointerPath, valuePointerArrowPaint);

        valuePointerArrowPaint.setStyle(Style.STROKE);
        valuePointerArrowPaint.setStrokeJoin(Join.ROUND);
        valuePointerArrowPaint.setColor(Color.BLACK);
        canvas.drawPath(arrowPointerPath, valuePointerArrowPaint);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        int centerX = width / 2;
        int centerY = height / 2;

        innerPadding = (int) (paramInnerPadding * width / 100);
        outerPadding = (int) (paramOuterPadding * width / 100);
//        arrowPointerSize = (int) (paramArrowPointerSize * width / 100);
        valueSliderWidth = (int) (paramValueSliderWidth * width / 100);

//        outerWheelRadius = width / 2 - outerPadding - arrowPointerSize;
        outerWheelRadius = width / 2 - outerPadding;
        innerWheelRadius = outerWheelRadius - valueSliderWidth;

//        innerWheelRadius = outerWheelRadius;

        centerOuterRadius = outerWheelRadius / 2.3f;
        centerInnerRadius = innerWheelRadius / 1.7f;

        colorWheelRadius = (int) (centerInnerRadius - innerPadding);

        // Color picker
        outerWheelRect.set(centerX - outerWheelRadius, centerY - outerWheelRadius, centerX + outerWheelRadius, centerY + outerWheelRadius);
        innerWheelRect.set(centerX - innerWheelRadius, centerY - innerWheelRadius, centerX + innerWheelRadius, centerY + innerWheelRadius);

        // Center color picker
        centerOuterWheelRect.set(centerX - centerOuterRadius, centerY - centerOuterRadius, centerX + centerOuterRadius, centerY + centerOuterRadius);
        centerInnerWheelRect.set(centerX - centerInnerRadius, centerY - centerInnerRadius, centerX + centerInnerRadius, centerY + centerInnerRadius);

        // Black button (off)
        offButtonRect.set(centerX - colorWheelRadius, centerY - colorWheelRadius, centerX + colorWheelRadius, centerY + colorWheelRadius);

//        colorWheelBitmap = createColorWheelBitmap(colorWheelRadius * 2, colorWheelRadius * 2);

        gradientRotationMatrix = new Matrix();
        gradientRotationMatrix.preRotate(270, width / 2, height / 2);

        valueSliderPath.arcTo(outerWheelRect, 270, 180);
        valueSliderPath.arcTo(innerWheelRect, 90, -180);
    }

    private Bitmap createColorWheelBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);

//        int colorCount = 12;
//        int colorAngleStep = 360 / 12;
//        int colors[] = new int[colorCount + 1];
//        float hsv[] = new float[]{0f, 1f, 1f};
//        for (int i = 0; i < colors.length; i++) {
//            hsv[0] = (i * colorAngleStep + 180) % 360;
//            colors[i] = Color.HSVToColor(hsv);
//        }
//        colors[colorCount] = colors[0];
//
//        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, null);
//        RadialGradient radialGradient = new RadialGradient(width / 2, height / 2, colorWheelRadius, 0xFFFFFFFF, 0x00FFFFFF, TileMode.CLAMP);
//        ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);
//
//        colorWheelPaint.setShader(composeShader);

        Canvas canvas = new Canvas(bitmap);
        float[] blackColor = new float[]{0f, 0f, 0f};
        float[] whiteColor = new float[]{0f, 0f, 1f};
        colorWheelPaint.setColor(Color.HSVToColor(blackColor));
        canvas.drawCircle(width / 2, height / 2, colorWheelRadius, colorWheelPaint);

        colorWheelPaint.setColor(Color.HSVToColor(whiteColor));
        colorWheelPaint.setTextSize(dpToPx(getContext(), 14));
        colorWheelPaint.setTextAlign(Paint.Align.CENTER);

        int xPos = canvas.getWidth() / 2;
        int yPos = (int) (canvas.getHeight() / 2 - (colorWheelPaint.descent() + colorWheelPaint.ascent() / 2));

        canvas.drawText("OFF", xPos, yPos, colorWheelPaint);

//        int xPos = (canvas.getWidth() / 2);
//        int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.

//        canvas.drawText("Hello", xPos, yPos, textPaint);

        return bitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setColorPick((int) event.getX(), (int) event.getY());
                return true;
            case MotionEvent.ACTION_MOVE:
                int x = (int) event.getX();
                int y = (int) event.getY();
                int cx = x - getWidth() / 2;
                int cy = y - getHeight() / 2;
                double d = sqrt(cx * cx + cy * cy);

                if (d <= colorWheelRadius) {
                    colorHSV[0] = (float) (toDegrees(atan2(cy, cx)) + 180f);
                    colorHSV[1] = max(0f, min(1f, (float) (d / colorWheelRadius)));

                    Log.d("Debug", Float.toString(colorHSV[0]));
                    updateAdjacentHue();
                    invalidate();

                } else if (d <= outerWheelRadius && d >= innerWheelRadius) {
                    colorHSV[0] = (float) (toDegrees(atan2(cy, cx)));
                    colorHSV[2] = (float) max(0, min(1, atan2(cy, cx) / PI + 0.5f));

                    if (colorHSV[0] < 0) {
                        colorHSV[0] += 360;
                    }

                    if (colorHSV[0] <= colorPickRanges[0][0] && colorHSV[0] >= colorPickRanges[0][1]) {
                        Log.d("Debug", "Red color...");
                    }

                    updateAdjacentHue();
                    invalidate();
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void setColorPick(int x, int y) {
        int cx = x - getWidth() / 2;
        int cy = y - getHeight() / 2;
        double d = sqrt(cx * cx + cy * cy);

        if (d <= outerWheelRadius && d >= innerWheelRadius) {
            colorHSV[0] = (float) (toDegrees(atan2(cy, cx)));
            colorHSV[2] = (float) max(0, min(1, atan2(cy, cx) / PI + 0.5f));

            if (colorHSV[0] <= -90 && colorHSV[0] >= -180) {
                colorHSV[0] += 360;
            }

            if (colorHSV[0] <= colorPickRanges[0][0] && colorHSV[0] >= colorPickRanges[0][1]) {
                Log.d("Debug", "Red color...");
                selectedColorPick = 0;
            } else if (colorHSV[0] <= colorPickRanges[1][0] && colorHSV[0] >= colorPickRanges[1][1]) {
                Log.d("Debug", "Vermilion color...");
                selectedColorPick = 1;
            } else if (colorHSV[0] <= colorPickRanges[2][0] && colorHSV[0] >= colorPickRanges[2][1]) {
                Log.d("Debug", "Orange color...");
                selectedColorPick = 2;
            } else if (colorHSV[0] <= colorPickRanges[3][0] && colorHSV[0] >= colorPickRanges[3][1]) {
                Log.d("Debug", "Amber color...");
                selectedColorPick = 3;
            } else if (colorHSV[0] <= colorPickRanges[4][0] && colorHSV[0] >= colorPickRanges[4][1]) {
                Log.d("Debug", "Yellow color...");
                selectedColorPick = 4;
            } else if (colorHSV[0] <= colorPickRanges[5][0] && colorHSV[0] >= colorPickRanges[5][1]) {
                Log.d("Debug", "Lime Green color...");
                selectedColorPick = 5;
            } else if (colorHSV[0] <= colorPickRanges[6][0] && colorHSV[0] >= colorPickRanges[6][1]) {
                Log.d("Debug", "Chartreuse Green color...");
                selectedColorPick = 6;
            } else if (colorHSV[0] <= colorPickRanges[7][0] && colorHSV[0] >= colorPickRanges[7][1]) {
                Log.d("Debug", "Green color...");
                selectedColorPick = 7;
            } else if (colorHSV[0] <= colorPickRanges[8][0] && colorHSV[0] >= colorPickRanges[8][1]) {
                Log.d("Debug", "Turquoise color...");
                selectedColorPick = 8;
            } else if (colorHSV[0] <= colorPickRanges[9][0] && colorHSV[0] >= colorPickRanges[9][1]) {
                Log.d("Debug", "Cyan color...");
                selectedColorPick = 9;
            } else if (colorHSV[0] <= colorPickRanges[10][0] && colorHSV[0] >= colorPickRanges[10][1]) {
                Log.d("Debug", "Cerulean color...");
                selectedColorPick = 10;
            } else if (colorHSV[0] <= colorPickRanges[11][0] && colorHSV[0] >= colorPickRanges[11][1]) {
                Log.d("Debug", "Azure color...");
                selectedColorPick = 11;
            } else if (colorHSV[0] <= colorPickRanges[12][0] && colorHSV[0] >= colorPickRanges[12][1]) {
                Log.d("Debug", "Sapphire Blue color...");
                selectedColorPick = 12;
            } else if (colorHSV[0] <= colorPickRanges[13][0] && colorHSV[0] >= colorPickRanges[13][1]) {
                Log.d("Debug", "Blue color...");
                selectedColorPick = 13;
            } else if (colorHSV[0] <= colorPickRanges[14][0] && colorHSV[0] >= colorPickRanges[14][1]) {
                Log.d("Debug", "Indigo color...");
                selectedColorPick = 14;
            } else if (colorHSV[0] <= colorPickRanges[15][0] && colorHSV[0] >= colorPickRanges[15][1]) {
                Log.d("Debug", "Violet color...");
                selectedColorPick = 15;
            } else if (colorHSV[0] <= colorPickRanges[16][0] && colorHSV[0] >= colorPickRanges[16][1]) {
                Log.d("Debug", "Mulberry color...");
                selectedColorPick = 16;
            } else if (colorHSV[0] <= colorPickRanges[17][0] && colorHSV[0] >= colorPickRanges[17][1]) {
                Log.d("Debug", "Magenta color...");
                selectedColorPick = 17;
            } else if (colorHSV[0] <= colorPickRanges[18][0] && colorHSV[0] >= colorPickRanges[18][1]) {
                Log.d("Debug", "Fuchsia color...");
                selectedColorPick = 18;
            } else if (colorHSV[0] <= colorPickRanges[19][0] && colorHSV[0] >= colorPickRanges[19][1]) {
                Log.d("Debug", "Rose color...");
                selectedColorPick = 19;
            } else if (colorHSV[0] <= colorPickRanges[20][0] && colorHSV[0] >= colorPickRanges[20][1]) {
                Log.d("Debug", "Crimson color...");
                selectedColorPick = 20;
            }
        } else if (d <= centerOuterRadius && d >= centerInnerRadius) {
            colorHSV[0] = (float) (toDegrees(atan2(cy, cx)));

            if (colorHSV[0] <= centerColorPickRanges[0][0] && colorHSV[0] >= centerColorPickRanges[0][1]) {
                Log.d("Debug", "3000K");
                selectedColorPick = 21;
            } else if (colorHSV[0] <= centerColorPickRanges[1][0] && colorHSV[0] >= centerColorPickRanges[1][1]) {
                Log.d("Debug", "4500K");
                selectedColorPick = 22;
            } else if (colorHSV[0] <= centerColorPickRanges[2][0] && colorHSV[0] >= centerColorPickRanges[2][1]) {
                Log.d("Debug", "6000K");
                selectedColorPick = 23;
            }
        } else if (d <= colorWheelRadius) {
            Log.d("Debug", "Black");
            selectedColorPick = 24;
        }
    }

    public int getColorPick() {
        return this.selectedColorPick;
    }

    public void clearColorCode() {
        selectedColorPick = 0;
    }

    public void setOffButtonBgChange(boolean state) {
        if (state) {
            offButtonPaint.setColor(Color.parseColor("#616161"));
        } else {
            offButtonPaint.setColor(Color.parseColor("#000000"));
        }

        invalidate();
    }

    public int getOuterWheelRadius() {
        return this.outerWheelRadius;
    }

    private float dpToPx(Context context, float dp) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }

    private float pxToDp(Context context, float px) {
        float density = context.getResources().getDisplayMetrics().density;

        if (density == 1.0) {
            density *= 4.0;
        } else if (density == 1.5) {
            density *= (8.0 / 3);
        } else if (density == 2.0) {
            density *= 2.0;
        }

        return px / density;
    }

    private void updateAdjacentHue() {
        for (int i = 0; i < paramColorCount; i++) {
            adjacentHue[i] = (colorHSV[0] - paramHueSpreadAngle * (paramColorCount / 2 - i)) % 360.0f;
            adjacentHue[i] = (adjacentHue[i] < 0) ? adjacentHue[i] + 360f : adjacentHue[i];
        }
        adjacentHue[paramColorCount / 2] = colorHSV[0];

    }

    public void setColor(int color) {
        Color.colorToHSV(color, colorHSV);
        updateAdjacentHue();
    }

    public int getColor() {
        return Color.HSVToColor(colorHSV);
    }

    public int[] getColors() {
        int[] colors = new int[paramColorCount];
        float[] hsv = new float[3];
        for (int i = 0; i < paramColorCount; i++) {
            hsv[0] = adjacentHue[i];
            hsv[1] = colorHSV[1];
            hsv[2] = colorHSV[2];
            colors[i] = Color.HSVToColor(hsv);
        }
        return colors;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putFloatArray("color", colorHSV);
        state.putParcelable("super", super.onSaveInstanceState());
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            colorHSV = bundle.getFloatArray("color");
            updateAdjacentHue();
            super.onRestoreInstanceState(bundle.getParcelable("super"));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

}
