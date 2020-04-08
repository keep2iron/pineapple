package io.github.keep2iron.pineapple

import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference

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
    @ColorInt var borderOverlayColor: Int = Color.TRANSPARENT,
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
     * 同时设置 placeHolderRes 和 placeHolder 该优先级更高
     *
     * 占位图
     */
    var placeHolder: Drawable? = null,

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
     * 向下取样的宽度
     */
    var resizeImageWidth: Int = 0,

    /**
     * 向下取样的高度
     */
    var resizeImageHeight: Int = 0,

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
    var blurRadius: Int = 0,

    var animationDuration: Int = 300,

    /**
     * 错误展位图
     */
    var errorHolder: Drawable? = null
) {

    /**
     * 占位图资源id
     */
    fun setPlaceHolderRes(context: Context, @DrawableRes resId: Int) {
        this.placeHolder = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            context.resources.getDrawable(resId)
        } else {
            context.resources.getDrawable(resId, context.theme)
        }
    }

    /**
     * 设置错误占位图
     */
    fun setErrorHolderRes(context: Context, @DrawableRes resId: Int) {
        this.errorHolder = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            context.resources.getDrawable(resId)
        } else {
            context.resources.getDrawable(resId, context.theme)
        }
    }

    /**
     * 绑定生命周期的软引用
     */
    private var lifecycleOwnerRef: WeakReference<LifecycleOwner>? = null

    /**
     * 对于glide绑定生命周期
     */
    var lifecycleOwner: LifecycleOwner?
        set(value) {
            this.lifecycleOwnerRef?.clear()

            checkNotNull(value)

            this.lifecycleOwnerRef = WeakReference(value)
        }
        get() {
            return this.lifecycleOwnerRef?.get()
        }

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

    fun copyOptions(otherOptions: ImageLoaderOptions) {
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
        if (otherOptions.animationDuration != this.animationDuration) {
            this.animationDuration = otherOptions.animationDuration
        }
        if (otherOptions.resizeImageWidth != this.resizeImageWidth) {
            this.resizeImageWidth = otherOptions.resizeImageWidth
        }
        if (otherOptions.resizeImageHeight != this.resizeImageHeight) {
            this.resizeImageHeight = otherOptions.resizeImageHeight
        }
        if (otherOptions.errorHolder != this.errorHolder) {
            this.errorHolder = otherOptions.errorHolder
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageLoaderOptions

        if (radius != other.radius) return false
        if (radiusTopLeft != other.radiusTopLeft) return false
        if (radiusTopRight != other.radiusTopRight) return false
        if (radiusBottomRight != other.radiusBottomRight) return false
        if (radiusBottomLeft != other.radiusBottomLeft) return false
        if (isProgressiveLoadImage != other.isProgressiveLoadImage) return false
        if (isCircleImage != other.isCircleImage) return false
        if (borderOverlayColor != other.borderOverlayColor) return false
        if (borderSize != other.borderSize) return false
        if (scaleType != other.scaleType) return false
        if (smallImageUri != other.smallImageUri) return false
        if (placeHolder != other.placeHolder) return false
        if (matrix != other.matrix) return false
        if (isLoadGif != other.isLoadGif) return false
        if (isSetByImageSize != other.isSetByImageSize) return false
        if (imageWidth != other.imageWidth) return false
        if (imageHeight != other.imageHeight) return false
        if (onFinalImageSetListener != other.onFinalImageSetListener) return false
        if (onImageFailure != other.onImageFailure) return false
        if (iterations != other.iterations) return false
        if (blurRadius != other.blurRadius) return false
        if (animationDuration != other.animationDuration) return false
        if (errorHolder != other.errorHolder) return false

        return true
    }

    override fun hashCode(): Int {
        var result = radius.hashCode()
        result = 31 * result + radiusTopLeft.hashCode()
        result = 31 * result + radiusTopRight.hashCode()
        result = 31 * result + radiusBottomRight.hashCode()
        result = 31 * result + radiusBottomLeft.hashCode()
        result = 31 * result + isProgressiveLoadImage.hashCode()
        result = 31 * result + isCircleImage.hashCode()
        result = 31 * result + borderOverlayColor
        result = 31 * result + borderSize.hashCode()
        result = 31 * result + scaleType.hashCode()
        result = 31 * result + (smallImageUri?.hashCode() ?: 0)
        result = 31 * result + (placeHolder?.hashCode() ?: 0)
        result = 31 * result + (matrix?.hashCode() ?: 0)
        result = 31 * result + isLoadGif.hashCode()
        result = 31 * result + isSetByImageSize.hashCode()
        result = 31 * result + imageWidth
        result = 31 * result + imageHeight
        result = 31 * result + (onFinalImageSetListener?.hashCode() ?: 0)
        result = 31 * result + (onImageFailure?.hashCode() ?: 0)
        result = 31 * result + iterations
        result = 31 * result + blurRadius
        result = 31 * result + animationDuration
        result = 31 * result + (errorHolder?.hashCode() ?: 0)
        return result
    }

    fun clear() {
        this.radius = 0f
        this.radiusTopLeft = 0f
        this.radiusTopRight = 0f
        this.radiusBottomRight = 0f
        this.radiusBottomLeft = 0f

        this.isProgressiveLoadImage = false
        /**
         * 是否为圆形图片
         */
        this.isCircleImage = false
        /**
         * 图片边界颜色
         */
        this.borderOverlayColor = 0
        /**
         * 边界大小
         */
        this.borderSize = 0f
        /**
         * 显示mode
         */
        this.scaleType = ScaleType.NONE

        /**
         * 小图url
         */
        this.smallImageUri = null

        /**
         * 同时设置 placeHolderRes 和 placeHolder
         *
         * 占位图
         */
        this.placeHolder = null

        this.matrix = null

        /**
         * 是否自动加载gif
         */
        this.isLoadGif = false

        /**
         * 是否是通过image size进行设置大小,如果设置该属性，需要设置imageWidth
         */
        this.isSetByImageSize = false
        /**
         * isSetByImageSize = true时有效
         */
        this.imageWidth = 0

        /**
         * isSetByImageSize = true有效 如果需要通过自适应图片时 该值可以不进行设置
         */
        this.imageHeight = 0

        /**
         * 当图片被设置时触发监听
         */
        this.onFinalImageSetListener = null

        /**
         * 当图片加载失败
         */
        this.onImageFailure = null

        /**
         * 高斯模糊的迭代次数,次数越高越魔性
         */
        this.iterations = 0
        /**
         * 模糊半径 越高越模糊
         */
        this.blurRadius = 0

        this.animationDuration = 300

        this.errorHolder = null
    }
}