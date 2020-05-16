package io.github.keep2iron.pineapple.util

import android.graphics.*
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.graphics.applyCanvas
import coil.bitmappool.BitmapPool
import coil.decode.DecodeUtils
import coil.size.OriginalSize
import coil.size.PixelSize
import coil.size.Scale
import coil.size.Size
import coil.transform.Transformation
import kotlin.math.roundToInt

/**
 * A [Transformation] that crops the image to fit the target's dimensions and rounds the corners of the image.
 *
 * @param topLeft The radius for the top left corner.
 * @param topRight The radius for the top right corner.
 * @param bottomLeft The radius for the bottom left corner.
 * @param bottomRight The radius for the bottom right corner.
 */
class NewRoundedCornersTransformation(
  @Px private val topLeft: Float = 0f,
  @Px private val topRight: Float = 0f,
  @Px private val bottomLeft: Float = 0f,
  @Px private val bottomRight: Float = 0f,
  @ColorInt private val borderColor: Int,
  private val borderSize: Float
) : Transformation {

  constructor(
    @Px radius: Float, @ColorInt borderColor: Int,
    borderSize: Float
  ) : this(radius, radius, radius, radius, borderColor, borderSize)

  init {
    require(topLeft >= 0 && topRight >= 0 && bottomLeft >= 0 && bottomRight >= 0) { "All radii must be >= 0." }
  }

  override fun key() =
    "${NewRoundedCornersTransformation::class.java.name}-$topLeft,$topRight,$bottomLeft,$bottomRight"

  override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    if (borderSize > 0) {
      paint.style = Paint.Style.FILL_AND_STROKE
      paint.strokeWidth = borderSize
      paint.color = borderColor
    }

    val outputWidth: Int
    val outputHeight: Int
    when (size) {
      is PixelSize -> {
        val multiplier = DecodeUtils.computeSizeMultiplier(
          srcWidth = input.width,
          srcHeight = input.height,
          dstWidth = size.width,
          dstHeight = size.height,
          scale = Scale.FILL
        )
        outputWidth = (size.width / multiplier).roundToInt()
        outputHeight = (size.height / multiplier).roundToInt()
      }
      is OriginalSize -> {
        outputWidth = input.width
        outputHeight = input.height
      }
    }

    val output = pool.get(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
    output.applyCanvas {
      drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

      val matrix = Matrix()
      matrix.setTranslate((outputWidth - input.width) / 2f, (outputHeight - input.height) / 2f)
      val shader = BitmapShader(input, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
      shader.setLocalMatrix(matrix)
      paint.shader = shader

      val radii = floatArrayOf(
        topLeft,
        topLeft,
        topRight,
        topRight,
        bottomRight,
        bottomRight,
        bottomLeft,
        bottomLeft
      )
      val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
      val path = Path().apply { addRoundRect(rect, radii, Path.Direction.CW) }
      drawPath(path, paint)
    }
    pool.put(input)

    return output
  }
}
