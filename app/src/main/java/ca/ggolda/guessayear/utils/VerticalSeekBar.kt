package ca.ggolda.guessayear.utils

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar

class VerticalSeekBar : SeekBar {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onSizeChanged(w: Int, h: Int, wOld: Int, hOld: Int) {
        super.onSizeChanged(h, w, hOld, wOld)
    }

    // necessary for calling setProgress on click of a button
    override fun setProgress(progress: Int) {
        super.setProgress(progress)
        onSizeChanged(width, height, 0, 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(c: Canvas) {
        c.rotate(-90f)
        c.translate(-height.toFloat(), 0f)

        super.onDraw(c)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                progress = (max - (max * event.y / height)).toInt()
                onSizeChanged(width, height, 0, 0)
            }
            MotionEvent.ACTION_UP -> {
                progress = (max - (max * event.y / height)).toInt()
                onSizeChanged(width, height, 0, 0)
            }
            MotionEvent.ACTION_MOVE -> {
                progress = (max - (max * event.y / height)).toInt()
                onSizeChanged(width, height, 0, 0)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                progress = (max - (max * event.y / height)).toInt()
                onSizeChanged(width, height, 0, 0)
            }
            MotionEvent.ACTION_POINTER_UP -> {
            }
            else -> {
            }
        }

        return true
    }
}