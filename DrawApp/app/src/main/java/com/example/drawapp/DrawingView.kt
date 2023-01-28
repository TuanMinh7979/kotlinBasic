package com.example.drawapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View


class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {


    private var mDrawPath: CustomPath? = null
    private var mCanvasBitMap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas: Canvas? = null

    private var mPaths = ArrayList<CustomPath>()
    private var mUndoPaths = ArrayList<CustomPath>()


    init {
        setUpDrawing()
    }

    fun onClickUndo() {
        if (mPaths.size > 0) {
            mUndoPaths.add(mPaths.removeAt(mPaths.size - 1))
        }

        invalidate()
    }

    private fun setUpDrawing() {


        println("SETUP DRAWING ...____________________________________")
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)

        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE

        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND


        mCanvasPaint = Paint(Paint.DITHER_FLAG)


    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitMap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        canvas = Canvas(mCanvasBitMap!!)
    }

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        println("ONDRAW CALLED ....._____________________________>>>>> ${mPaths.size}")
        mCanvasBitMap?.let {
            canvas.drawBitmap(it, 0f, 0f, mCanvasPaint)
        }

        for (path in mPaths) {
            mDrawPaint!!.strokeWidth = path!!.brushThickness
            mDrawPaint!!.color = path!!.color
            canvas.drawPath(path!!, mDrawPaint!!)

        }

        if (!mDrawPath!!.isEmpty) {
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }


    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x // Touch event of X coordinate
        val touchY = event.y // touch event of Y coordinate

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset() // Clear any lines and curves from the path, making it empty.
                mDrawPath!!.moveTo(
                    touchX,
                    touchY
                ) // Set the beginning of the next contour to the point (x,y).
            }

            MotionEvent.ACTION_MOVE -> {
                mDrawPath!!.lineTo(
                    touchX,
                    touchY
                ) // Add a line from the last point to the specified point (x,y).
            }

            MotionEvent.ACTION_UP -> {

                mPaths.add(mDrawPath!!);

                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }

        invalidate()
        return true
    }

    fun setSizeForBrush(newSize: Float) {

        mBrushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newSize,
            resources.displayMetrics
        )

        mDrawPaint!!.strokeWidth = mBrushSize


    }

    fun setColor(newColor: String) {
        println("-________________NEW COLOR: ${newColor}")
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }

    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path() {

    }


}

