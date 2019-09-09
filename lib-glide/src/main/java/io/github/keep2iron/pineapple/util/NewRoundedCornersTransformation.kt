package io.github.keep2iron.pineapple.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
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
      topLeftRadius * 2,
      topRightRadius * 2,
      bottomRightRadius * 2,
      bottomLeftRadius * 2
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

  fun composeRoundedRectPath(
    rect: RectF,
    topLeftDiameter: Float,
    topRightDiameter: Float,
    bottomRightDiameter: Float,
    bottomLeftDiameter: Float
  ): Path {
    var topLeftDiameter = topLeftDiameter
    var topRightDiameter = topRightDiameter
    var bottomRightDiameter = bottomRightDiameter
    var bottomLeftDiameter = bottomLeftDiameter
    val path = Path()
    topLeftDiameter = if (topLeftDiameter < 0) 0f else topLeftDiameter
    topRightDiameter = if (topRightDiameter < 0) 0f else topRightDiameter
    bottomLeftDiameter = if (bottomLeftDiameter < 0) 0f else bottomLeftDiameter
    bottomRightDiameter = if (bottomRightDiameter < 0) 0f else bottomRightDiameter

    path.moveTo(rect.left + topLeftDiameter, rect.top)
    path.lineTo(rect.right - topRightDiameter, rect.top)
    path.quadTo(rect.right, rect.top, rect.right, rect.top + topRightDiameter)
    path.lineTo(rect.right, rect.bottom - bottomRightDiameter)
    path.quadTo(rect.right, rect.bottom, rect.right - bottomRightDiameter, rect.bottom)
    path.lineTo(rect.left + bottomLeftDiameter, rect.bottom)
    path.quadTo(rect.left, rect.bottom, rect.left, rect.bottom - bottomLeftDiameter)
    path.lineTo(rect.left, rect.top + topLeftDiameter)
    path.quadTo(rect.left, rect.top, rect.left + topLeftDiameter, rect.top)
    path.close()

    return path
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