package io.github.keep2iron.pineapple

import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes

/**
 *
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2018/06/25 19:59
 */
data class ImageLoaderOptions(
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
    var onFinalImageSetListener: (() -> Unit)? = null,

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
        fun newDefaultOption(): ImageLoaderOptions {
            return ImageLoaderOptions()
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

    fun setOptions(otherOptions: ImageLoaderOptions) {
        if (otherOptions.radius != 0f) {
            this.radius = otherOptions.radius
        }
        if (otherOptions.radiusTopLeft != 0f) {
            this.radiusTopLeft = otherOptions.radiusTopLeft
        }
        if (otherOptions.radiusTopRight != 0f) {
            this.radiusTopRight = otherOptions.radiusTopRight
        }
        if (otherOptions.radiusBottomRight != 0f) {
            this.radiusBottomRight = otherOptions.radiusBottomRight
        }
        if (otherOptions.radiusBottomLeft != 0f) {
            this.radiusBottomLeft = otherOptions.radiusBottomLeft
        }
        if (otherOptions.isProgressiveLoadImage) {
            this.isProgressiveLoadImage = otherOptions.isProgressiveLoadImage
        }
        if (otherOptions.isCircleImage) {
            this.isCircleImage = otherOptions.isCircleImage
        }
        if (otherOptions.borderOverlayColor != 0) {
            this.borderOverlayColor = otherOptions.borderOverlayColor
        }
        if (otherOptions.borderSize != 0f) {
            this.borderSize = otherOptions.borderSize
        }
        if (otherOptions.scaleType != ScaleType.NONE) {
            this.scaleType = otherOptions.scaleType
        }
        if (otherOptions.smallImageUri != null) {
            this.smallImageUri = otherOptions.smallImageUri
        }
        if (otherOptions.placeHolderRes != 0) {
            this.placeHolderRes = otherOptions.placeHolderRes
        }
        if (otherOptions.matrix != null) {
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
        if (otherOptions.onFinalImageSetListener != null) {
            this.onFinalImageSetListener = otherOptions.onFinalImageSetListener
        }
        if (otherOptions.onImageFailure != null) {
            this.onImageFailure = otherOptions.onImageFailure
        }
        if (otherOptions.iterations > 0) {
            this.iterations = otherOptions.iterations
        }
        if (otherOptions.blurRadius > 0) {
            this.blurRadius = otherOptions.blurRadius
        }
    }


}