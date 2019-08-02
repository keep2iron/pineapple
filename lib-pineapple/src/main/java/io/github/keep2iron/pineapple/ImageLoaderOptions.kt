package io.github.keep2iron.pineapple

import android.graphics.Matrix
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

/**
 *
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2018/06/25 19:59
 */
class ImageLoaderOptions private constructor(
  /**
   * 圆角
   */
  var radius: Float = 0f,
  var radiusTopLeft: Float = 0f,
  var radiusTopRight: Float = 0f,
  var radiusBottomRight: Float = 0f,
  var radiusBottomLeft: Float = 0f,

  /**
   * 是否以渐进式方式加载图片 ，仅Fresco支持
   */
  var isProgressiveLoadImage: Boolean = false,
  /**
   * 是否为圆形图片
   */
  var isCircleImage: Boolean = false,
  /**
   * 图片边界颜色
   */
  @ColorInt var borderOverlayColor: Int = 0,
  /**
   * 边界大小
   */
  var borderSize: Float = 0f,
  /**
   * 显示mode
   */
  var scaleType: ScaleType = ScaleType.NONE,

  /**
   * 小图url
   */
  var smallImageUri: String? = null,

  /**
   * 同时设置 placeHolderRes 和 placeHolder
   *
   * 占位图
   */
  var placeHolder: Drawable? = null,

  /**
   * 占位图资源id
   */
  @DrawableRes
  var placeHolderRes: Int = 0,

  var matrix: Matrix? = null,

  /**
   * 是否自动加载gif
   */
  var isLoadGif: Boolean = false,

  /**
   * 是否是通过image size进行设置大小,如果设置该属性，需要设置imageWidth
   */
  var isSetByImageSize: Boolean = false,
  /**
   * isSetByImageSize = true时有效
   */
  var imageWidth: Int = 0,

  /**
   * isSetByImageSize = true有效 如果需要通过自适应图片时 该值可以不进行设置
   */
  var imageHeight: Int = 0,

  /**
   * 当图片被设置时触发监听
   */
  var onFinalImageSetListener: ((width: Int, height: Int) -> Unit)? = null,

  /**
   * 当图片加载失败
   */
  var onImageFailure: (() -> Unit)? = null,

  /**
   * 高斯模糊的迭代次数,次数越高越魔性
   */
  var iterations: Int = 0,
  /**
   * 模糊半径 越高越模糊
   */
  var blurRadius: Int = 0
) {

  companion object {
    @JvmStatic
    fun newOption(): ImageLoaderOptions {
      return ImageLoaderOptions()
    }

    @JvmStatic
    fun newClearOption(block: (ImageLoaderOptions.() -> Unit)?): ImageLoaderOptions {
      return if (block != null) {
        newOption().apply(block)
      } else {
        newOption()
      }
    }

    @JvmStatic
    fun newOptionWithDefaultOptions(block: (ImageLoaderOptions.() -> Unit)?): ImageLoaderOptions {
      val options = ImageLoaderOptions()
      val defaultOptions = ImageLoaderManager.getInstance()
          .getDefaultImageOptions()
      if (defaultOptions != null) {
        options.copyOptions(defaultOptions)
      }

      return if (block != null) {
        options.apply(block)
      } else {
        options
      }
    }
  }

  /**
   * if use fresco see http://frescolib.org/docs/scaletypes.html
   */
  enum class ScaleType {
    CENTER,
    CENTER_CROP,
    FOCUS_CROP,
    CENTER_INSIDE,
    FIT_CENTER,
    FIT_START,
    FIT_END,
    FIT_XY,
    MATRIX,
    NONE,
  }

  private fun copyOptions(otherOptions: ImageLoaderOptions) {
    if (otherOptions.radius != this.radius) {
      this.radius = otherOptions.radius
    }
    if (otherOptions.radiusTopLeft != this.radiusTopLeft) {
      this.radiusTopLeft = otherOptions.radiusTopLeft
    }
    if (otherOptions.radiusTopRight != this.radiusTopRight) {
      this.radiusTopRight = otherOptions.radiusTopRight
    }
    if (otherOptions.radiusBottomRight != this.radiusBottomRight) {
      this.radiusBottomRight = otherOptions.radiusBottomRight
    }
    if (otherOptions.radiusBottomLeft != this.radiusBottomLeft) {
      this.radiusBottomLeft = otherOptions.radiusBottomLeft
    }
    if (otherOptions.isProgressiveLoadImage != this.isProgressiveLoadImage) {
      this.isProgressiveLoadImage = otherOptions.isProgressiveLoadImage
    }
    if (otherOptions.isCircleImage != this.isCircleImage) {
      this.isCircleImage = otherOptions.isCircleImage
    }
    if (otherOptions.borderOverlayColor != this.borderOverlayColor) {
      this.borderOverlayColor = otherOptions.borderOverlayColor
    }
    if (otherOptions.borderSize != this.borderSize) {
      this.borderSize = otherOptions.borderSize
    }
    if (otherOptions.scaleType != this.scaleType) {
      this.scaleType = otherOptions.scaleType
    }
    if (otherOptions.smallImageUri != this.smallImageUri) {
      this.smallImageUri = otherOptions.smallImageUri
    }
    if (otherOptions.placeHolder != this.placeHolder) {
      this.placeHolder = otherOptions.placeHolder
    }
    if (otherOptions.placeHolderRes != this.placeHolderRes) {
      this.placeHolderRes = otherOptions.placeHolderRes
    }
    if (otherOptions.matrix != this.matrix) {
      this.matrix = otherOptions.matrix
    }
    if (otherOptions.isLoadGif) {
      this.isLoadGif = otherOptions.isLoadGif
    }
    if (otherOptions.isSetByImageSize) {
      this.isSetByImageSize = otherOptions.isSetByImageSize
    }
    if (otherOptions.imageWidth > 0) {
      this.imageWidth = otherOptions.imageWidth
    }
    if (otherOptions.imageHeight > 0) {
      this.imageHeight = otherOptions.imageHeight
    }
    if (otherOptions.onFinalImageSetListener != this.onFinalImageSetListener) {
      this.onFinalImageSetListener = otherOptions.onFinalImageSetListener
    }
    if (otherOptions.onImageFailure != this.onImageFailure) {
      this.onImageFailure = otherOptions.onImageFailure
    }
    if (otherOptions.iterations > this.iterations) {
      this.iterations = otherOptions.iterations
    }
    if (otherOptions.blurRadius > this.blurRadius) {
      this.blurRadius = otherOptions.blurRadius
    }
  }
}