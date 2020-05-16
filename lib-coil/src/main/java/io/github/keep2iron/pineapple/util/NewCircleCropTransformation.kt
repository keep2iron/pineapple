@file:Suppress("unused")

package io.github.keep2iron.pineapple.util

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.annotation.ColorInt
import androidx.core.graphics.applyCanvas
import coil.bitmappool.BitmapPool
import coil.size.Size
import coil.transform.Transformation
import kotlin.math.min

/**
 * A [Transformation] that crops an image using a centered circle as the mask.
 */
class NewCircleCropTransformation(@ColorInt private val borderColor: Int,
                                  private val borderSize: Float) : Transformation {

  companion object {
    private val XFERMODE = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
  }

  override fun key(): String = NewCircleCropTransformation::class.java.name

  override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    if (borderSize > 0) {
      paint.style = Paint.Style.FILL_AND_STROKE
      paint.strokeWidth = borderSize
      paint.color = borderColor
    }

    val minSize = min(input.width, input.height)
    val radius = minSize / 2f
    val output = pool.get(minSize, minSize, Bitmap.Config.ARGB_8888)
    output.applyCanvas {
      drawCircle(radius, radius, radius, paint)
      paint.xfermode = XFERMODE
      drawBitmap(input, radius - input.width / 2f, radius - input.height / 2f, paint)
    }
    pool.put(input)

    return output
  }
}
