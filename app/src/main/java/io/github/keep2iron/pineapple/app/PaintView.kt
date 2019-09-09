package io.github.keep2iron.pineapple.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import android.graphics.Shader
import android.util.AttributeSet
import android.widget.ImageView

class PaintView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

  private val paint = Paint()

  private lateinit var bitmap: Bitmap
  private var bitmapPaint = Paint()

  private val rects: Array<RectF> = Array(4) { RectF() }

  val rectF = RectF()

  private val topLeftRadius: Int = 40
  private val topRightRadius: Int = 20
  private val bottomRightRadius: Int = 30
  private val bottomLeftRadius: Int = 40

  private val path: Path = Path()

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    val width = MeasureSpec.getSize(widthMeasureSpec)
    val height = MeasureSpec.getSize(heightMeasureSpec)


    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.BLACK)

    val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    bitmapPaint.shader = shader

    rectF.set(0f, 0f, width.toFloat(), height.toFloat())

  }

  init {
    paint.isAntiAlias = true
    paint.strokeWidth = 10f
    paint.style = Paint.Style.STROKE
    paint.color = Color.BLACK
  }

  private fun drawRoundRect(canvas: Canvas, paint: Paint, width: Float, height: Float) {
    path.reset()

    if (topLeftRadius > 0) {
      rects[0].set(0f, 0f, (topLeftRadius * 2).toFloat(), (topLeftRadius * 2).toFloat())
      path.addArc(rects[0], 180f, 90f)
//      path.moveTo(topLeftRadius.toFloat(), 0f)
    } else {
      path.moveTo(0f, 0f)
    }

    if (topRightRadius > 0) {
      path.lineTo(width - topRightRadius, 0f)
      rects[1].set(width - topRightRadius * 2, 0f, width, (topRightRadius * 2).toFloat())
      path.addArc(rects[1], 270f, 90f)
//      path.moveTo(width, topRightRadius.toFloat())
    } else {
      path.lineTo(width, 0f)
    }

    if (bottomRightRadius > 0) {
      path.lineTo(width, height - bottomRightRadius)
      rects[2].set(width - bottomRightRadius * 2, height - bottomRightRadius * 2, width, height)
      path.addArc(rects[2], 0f, 90f)
//      path.moveTo(width - bottomRightRadius, height)
    } else {
      path.lineTo(width, height)
    }

    if (bottomLeftRadius > 0) {
      path.lineTo(bottomLeftRadius.toFloat(), height)
      rects[3].set(0f, height - bottomLeftRadius * 2, (bottomLeftRadius * 2).toFloat(), height)
      path.addArc(rects[3], 90f, 90f)
//      path.moveTo(0f, height - bottomLeftRadius)
    } else {
      path.lineTo(0f, height)
    }

    if (topLeftRadius > 0) {
      path.lineTo(0f, topLeftRadius.toFloat())
    } else {
      path.lineTo(0f, 0f)
    }

    path.close()

    canvas.save()
    canvas.clipPath(path, Region.Op.INTERSECT)
    canvas.drawBitmap(bitmap, 0f, 0f, null)
    canvas.restore()

//    canvas.drawPath(path, paint)

//    canvas.drawPath(path, bitmapPaint)
  }

  override fun onDraw(canvas: Canvas) {
//    drawRoundRect(canvas, paint, width.toFloat(), height.toFloat())
    canvas.drawPath(composeRoundedRectPath(rectF, 0f, 0f, 0f, 0f), paint)
//    canvas.drawPath(
//      composeRoundedRectPath(
//        rectF.left,
//        rectF.top,
//        rectF.right,
//        rectF.bottom,
//        100f,
//        100f,
//        true
//      ),
//      paint
//    )
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

    path.moveTo(rect.left + topLeftDiameter / 2, rect.top)
    path.lineTo(rect.right - topRightDiameter / 2, rect.top)
    path.quadTo(rect.right, rect.top, rect.right, rect.top + topRightDiameter / 2)
    path.lineTo(rect.right, rect.bottom - bottomRightDiameter / 2)
    path.quadTo(rect.right, rect.bottom, rect.right - bottomRightDiameter / 2, rect.bottom)
    path.lineTo(rect.left + bottomLeftDiameter / 2, rect.bottom)
    path.quadTo(rect.left, rect.bottom, rect.left, rect.bottom - bottomLeftDiameter / 2)
    path.lineTo(rect.left, rect.top + topLeftDiameter / 2)
    path.quadTo(rect.left, rect.top, rect.left + topLeftDiameter / 2, rect.top)
    path.close()

    return path
  }

  fun composeRoundedRectPath(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    rx: Float,
    ry: Float,
    conformToOriginalPost: Boolean
  ): Path {
    var rx = rx
    var ry = ry
    val path = Path()
    if (rx < 0) rx = 0f
    if (ry < 0) ry = 0f
    val width = right - left
    val height = bottom - top
    if (rx > width / 2) rx = width / 2
    if (ry > height / 2) ry = height / 2
    val widthMinusCorners = width - 2 * rx
    val heightMinusCorners = height - 2 * ry

    path.moveTo(right, top + ry)
    path.rQuadTo(0f, -ry, -rx, -ry)//top-right corner
    path.rLineTo(-widthMinusCorners, 0f)
    path.rQuadTo(-rx, 0f, -rx, ry) //top-left corner
    path.rLineTo(0f, heightMinusCorners)

    if (conformToOriginalPost) {
      path.rLineTo(0f, ry)
      path.rLineTo(width, 0f)
      path.rLineTo(0f, -ry)
    } else {
      path.rQuadTo(0f, ry, rx, ry)//bottom-left corner
      path.rLineTo(widthMinusCorners, 0f)
      path.rQuadTo(rx, 0f, rx, -ry) //bottom-right corner
    }

    path.rLineTo(0f, -heightMinusCorners)

    path.close()//Given close, last lineto can be removed.

    return path
  }

}