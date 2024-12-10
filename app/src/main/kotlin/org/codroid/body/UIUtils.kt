package org.codroid.body

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.util.TypedValue

/**
 * Convert dip to px
 *
 * @param dpValue value of dp
 */
fun Context.dip2px(dpValue: Float): Float {
    val scale = this.resources.displayMetrics.density;
    return dpValue * scale + 0.5f
}

/**
 * Convert px to dip
 *
 * @param pxValue value of px
 */
fun Context.px2dip(pxValue: Float): Float {
    val scale = this.resources.displayMetrics.density;
    return pxValue / scale + 0.5f
}

fun Context.sp2px(spValue: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        spValue,
        this.resources.displayMetrics
    )
}

fun Bitmap.zoom(scale: Float): Bitmap {
    val matrix = Matrix()
    if (scale.toInt() == 1) return this
    matrix.setScale(scale, scale)
    val new = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    if (this != new && !this@zoom.isRecycled) {
        this.recycle ()
    }
    return new
}

fun Context.getAttrColor(id: Int): Int {
    this.obtainStyledAttributes(intArrayOf(id)).let {
        val temp = it.getColor(0, Color.RED)
        it.recycle()
        return temp
    }
}
