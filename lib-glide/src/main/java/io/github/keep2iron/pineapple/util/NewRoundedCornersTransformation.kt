package io.github.keep2iron.pineapple.util

import android.content.Context
import android.graphics.*
import android.graphics.Shader.TileMode.CLAMP
import androidx.annotation.ColorInt
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import java.security.MessageDigest

class NewRoundedCornersTransformation(
  val topLeftRadius: Float,
  val topRightRadius: Float,
  val bottomRightRadius: Float,
  val bottomLeftRadius: Float,
  @ColorInt val borderColor: Int,
  val borderSize: Float
) : BitmapTransformation() {

  private val paint = Paint()

  init {
    paint.color = borderColor
    paint.isAntiAlias = true
    paint.strokeWidth = borderSize
    paint.style = Paint.Style.STROKE
  }

  override fun transform(
    context: Context,
    pool: BitmapPool,
    toTransform: Bitmap,
    outWidth: Int,
    outHeight: Int
  ): Bitmap {
    val width = toTransform.width
    val height = toTransform.height

    val bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setHasAlpha(true)

    setCanvasBitmapDensity(toTransform, bitmap)

    val canvas = Canvas(bitmap)
    val bitmapPaint = Paint()
    bitmapPaint.isAntiAlias = true
    bitmapPaint.shader = BitmapShader(toTransform, CLAMP, CLAMP)
    drawRoundRect(canvas, bitmapPaint, width.toFloat(), height.toFloat())

    return bitmap
  }

  private fun drawRoundRect(canvas: Canvas, bitmapPaint: Paint, width: Float, height: Float) {
    val path = composeRoundedRectPath(
      RectF(0f, 0f, width, height),
      topLeftRadius,
      topRightRadius,
      bottomRightRadius,
      bottomLeftRadius
    )
    canvas.drawPath(
      path, bitmapPaint
    )
    if (borderSize > 0) {
      canvas.drawPath(
        path, paint
      )
    }
  }

  private fun composeRoundedRectPath(
    rect: RectF,
    topLeftDiameter: Float,
    topRightDiameter: Float,
    bottomRightDiameter: Float,
    bottomLeftDiameter: Float
  ): Path {
    val radii = floatArrayOf(topLeftDiameter, topLeftDiameter, topRightDiameter, topRightDiameter,
      bottomRightDiameter, bottomRightDiameter, bottomRightDiameter, bottomRightDiameter)

    return  Path().apply { addRoundRect(rect, radii, Path.Direction.CW) }
  }

  override fun updateDiskCacheKey(messageDigest: MessageDigest) {
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is NewRoundedCornersTransformation) return false

    if (topLeftRadius != other.topLeftRadius) return false
    if (topRightRadius != other.topRightRadius) return false
    if (bottomRightRadius != other.bottomRightRadius) return false
    if (bottomLeftRadius != other.bottomLeftRadius) return false
    if (borderColor != other.borderColor) return false
    if (borderSize != other.borderSize) return false

    return true
  }

  override fun hashCode(): Int {
    var result = 31
    result = (31 * result + topLeftRadius).toInt()
    result = (31 * result + topRightRadius).toInt()
    result = (31 * result + bottomRightRadius).toInt()
    result = (31 * result + bottomLeftRadius).toInt()
    result = 31 * result + borderColor
    result = (31 * result + borderSize).toInt()
    return result
  }
}