package com.hun.dimming.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatSeekBar
import java.util.logging.Handler

class VerticalSeekBar @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatSeekBar(context, attrs, defStyleAttr) {

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(height, width, oldHeight, oldWidth)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.rotate((-90).toFloat())
        canvas.translate((-height).toFloat(), 0F)
        super.onDraw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isEnabled) {
            return false
        }

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {

            }

            MotionEvent.ACTION_MOVE -> {
                val i: Int = max - (max * event.y / height).toInt()

                progress = i
                onSizeChanged(width, height, 0, 0)


            }

            MotionEvent.ACTION_UP -> {
//                performClick()
//                val i: Int = max - (max * event.y / height).toInt()
//
//                progress = i
//                onSizeChanged(width, height, 0, 0)
            }

            MotionEvent.ACTION_CANCEL -> {

            }
        }
        return true
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }
}
