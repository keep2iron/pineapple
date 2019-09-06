package io.github.keep2iron.pineapple

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.util.LruCache
import android.view.View
import android.widget.ImageView
import androidx.annotation.CallSuper
import androidx.collection.SparseArrayCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import io.github.keep2iron.pineapple.ImageLoaderOptions.ScaleType
import io.github.keep2iron.pineapple.ImageLoaderOptions.ScaleType.CENTER
import io.github.keep2iron.pineapple.ImageLoaderOptions.ScaleType.CENTER_CROP
import io.github.keep2iron.pineapple.ImageLoaderOptions.ScaleType.CENTER_INSIDE
import io.github.keep2iron.pineapple.ImageLoaderOptions.ScaleType.FIT_CENTER
import io.github.keep2iron.pineapple.ImageLoaderOptions.ScaleType.FIT_END
import io.github.keep2iron.pineapple.ImageLoaderOptions.ScaleType.FIT_START
import io.github.keep2iron.pineapple.ImageLoaderOptions.ScaleType.FIT_XY
import io.github.keep2iron.pineapple.ImageLoaderOptions.ScaleType.FOCUS_CROP
import io.github.keep2iron.pineapple.ImageLoaderOptions.ScaleType.MATRIX
import io.github.keep2iron.pineapple.util.CropCircleWithBorderTransformation
import io.github.keep2iron.pineapple.util.RoundedCornersTransformation
import io.github.keep2iron.pineapple.util.RoundedCornersTransformation.CornerType.BOTTOM_LEFT
import io.github.keep2iron.pineapple.util.RoundedCornersTransformation.CornerType.BOTTOM_RIGHT
import io.github.keep2iron.pineapple.util.RoundedCornersTransformation.CornerType.TOP_LEFT
import io.github.keep2iron.pineapple.util.RoundedCornersTransformation.CornerType.TOP_RIGHT
import io.github.keep2iron.pineapple.util.SupportRSBlurTransformation
import java.util.concurrent.Executors
import kotlin.LazyThreadSafetyMode.NONE

/**
 * 如果后面有库使用Glide加载 必须继承该类实现
 */
open class GlideInitModule : AppGlideModule() {

  @CallSuper
  override fun applyOptions(context: Context, builder: GlideBuilder) {
    val applicationConfig = ImageLoaderManager.getInstance().getConfig() as ImageLoaderConfig
    builder.setDiskCache(
      ExternalPreferredCacheDiskCacheFactory(
        context,
        applicationConfig.cacheDirName,
        applicationConfig.maxCacheSize
      )
    )
  }

  override fun isManifestParsingEnabled(): Boolean = false
}

class ImageLoaderImpl : ImageLoader {

  private lateinit var application: Application

  private lateinit var applicationConfig: ImageLoaderConfig

  private val drawableCrossFadeArrayMap: SparseArrayCompat<DrawableCrossFadeFactory> =
    SparseArrayCompat()

  private val clearTaskExectors by lazy(NONE) {
    Executors.newCachedThreadPool()
  }

  /**
   * 通过init()方法设置默认option
   */
  private var defaultImageLoaderOptions: ImageLoaderOptions? = null

  private lateinit var cache: LruCache<Uri, ImageLoaderOptions>

  private fun getDrawableCrossFadeFactory(animationDuration: Int): DrawableCrossFadeFactory {
    return drawableCrossFadeArrayMap[animationDuration] ?: DrawableCrossFadeFactory.Builder(
      animationDuration
    )
      .setCrossFadeEnabled(true).build().also {
        drawableCrossFadeArrayMap.put(animationDuration, it)
      }
  }

  override fun init(
    context: Application,
    config: ImageLoaderConfig,
    defaultImageLoaderOptions: (ImageLoaderOptions.() -> Unit)?
  ) {
    //init设置从通过继承GlideInitModule实现
    this.applicationConfig = config
    this.application = context
    this.defaultImageLoaderOptions = if (defaultImageLoaderOptions != null) {
      ImageLoaderOptions.newClearOption(defaultImageLoaderOptions)
    } else {
      null
    }
    cache = LruCache(config.optionCacheSize)

    //默认放置一个300毫秒的Factory
    drawableCrossFadeArrayMap.put(
      300,
      DrawableCrossFadeFactory.Builder(300)
        .setCrossFadeEnabled(true).build()
    )
  }

  private fun getImageOptions(
    uri: Uri,
    options: (ImageLoaderOptions.() -> Unit)?
  ): ImageLoaderOptions {
    var option: ImageLoaderOptions? = cache.get(uri)
    if (option == null) {
      val newOption = if (defaultImageLoaderOptions != null) {
        ImageLoaderOptions.newOptionWithDefaultOptions(options)
      } else {
        ImageLoaderOptions.newClearOption(options)
      }
      cache.put(uri, newOption)
      option = newOption
    } else {
      if (defaultImageLoaderOptions != null) {
        option.copyOptions(defaultImageLoaderOptions!!)
      } else {
        option.clear()
      }
      if (options != null) {
        option.apply(options)
      }
    }
    return option
  }

  private fun showImageInternal(
    requestBuilder: RequestBuilder<*>,
    imageView: View,
    imageOptions: ImageLoaderOptions
  ) {

    requestBuilder.apply {

      val iv = imageView as ImageView
      when (imageOptions.scaleType) {
        CENTER -> {
          iv.scaleType = ImageView.ScaleType.CENTER
        }
        CENTER_CROP -> {
          iv.scaleType = ImageView.ScaleType.CENTER_CROP
          centerCrop()
        }
        FOCUS_CROP -> {
          //only support in fresco
        }
        CENTER_INSIDE -> {
          iv.scaleType = ImageView.ScaleType.CENTER_INSIDE
          centerInside()
        }
        FIT_START -> {
          iv.scaleType = ImageView.ScaleType.FIT_START
        }
        FIT_END -> {
          iv.scaleType = ImageView.ScaleType.FIT_END
        }
        FIT_XY -> {
          iv.scaleType = ImageView.ScaleType.FIT_XY
        }
        MATRIX -> {
          //only support in fresco
          iv.scaleType = ImageView.ScaleType.MATRIX
          iv.imageMatrix = imageOptions.matrix
        }
        ScaleType.NONE -> {
          //only support in fresco
        }
        FIT_CENTER -> {
          iv.scaleType = ImageView.ScaleType.FIT_CENTER
          fitCenter()
        }
      }

      //向下取样
      if (imageOptions.resizeImageWidth > 0 && imageOptions.resizeImageHeight > 0) {
        override(imageOptions.resizeImageWidth, imageOptions.resizeImageHeight)
      }

      //place holder
      if (imageOptions.placeHolderRes != 0) {
        placeholder(imageOptions.placeHolderRes)
      }
      if (imageOptions.placeHolder == null) {
        placeholder(imageOptions.placeHolder)
      }

      //radius
      val transforms = ArrayList<Transformation<Bitmap>>()
      if (imageOptions.radius > 0 ||
        imageOptions.radiusTopLeft > 0 ||
        imageOptions.radiusTopRight > 0 ||
        imageOptions.radiusBottomLeft > 0 ||
        imageOptions.radiusBottomRight > 0
      ) {
        if (imageOptions.radiusTopLeft == 0f) {
          imageOptions.radiusTopLeft = imageOptions.radius
          transforms.add(
            RoundedCornersTransformation(
              imageOptions.radiusTopLeft.toInt(),
              0,
              TOP_LEFT
            )
          )
        }
        if (imageOptions.radiusTopRight == 0f) {
          imageOptions.radiusTopRight = imageOptions.radius
          transforms.add(
            RoundedCornersTransformation(
              imageOptions.radiusTopRight.toInt(),
              0,
              TOP_RIGHT
            )
          )
        }
        if (imageOptions.radiusBottomLeft == 0f) {
          imageOptions.radiusBottomLeft = imageOptions.radius
          transforms.add(
            RoundedCornersTransformation(
              imageOptions.radiusBottomLeft.toInt(),
              0,
              BOTTOM_LEFT
            )
          )
        }
        if (imageOptions.radiusBottomRight == 0f) {
          imageOptions.radiusBottomRight = imageOptions.radius
          transforms.add(
            RoundedCornersTransformation(
              imageOptions.radiusBottomLeft.toInt(),
              0,
              BOTTOM_RIGHT
            )
          )
        }
      }

      //blur
      if (imageOptions.iterations > 0 && imageOptions.blurRadius > 0) {
        transforms.add(
          SupportRSBlurTransformation(
            imageOptions.blurRadius,
            imageOptions.iterations
          )
        )
      }

      //circle
      if (imageOptions.isCircleImage) {
        transforms.add(
          CropCircleWithBorderTransformation(
            imageOptions.borderSize.toInt(),
            imageOptions.borderOverlayColor
          )
        )
      }

      if (transforms.isNotEmpty()) {
        this.apply(RequestOptions.bitmapTransform(MultiTransformation(transforms)))
      }

      into(imageView as MiddlewareView)
    }
  }

  override fun showImageView(
    imageView: View,
    url: String,
    options: (ImageLoaderOptions.() -> Unit)?
  ) {
    val imageOptions = getImageOptions(Uri.parse(url), options)

    showImageInternal(
      Glide.with(imageView.context)
        .asBitmap()
        .load(url)
        .transition(BitmapTransitionOptions.withCrossFade(getDrawableCrossFadeFactory(imageOptions.animationDuration)))
      , imageView, imageOptions
    )
  }

  override fun showImageView(
    imageView: View,
    uri: Uri,
    options: (ImageLoaderOptions.() -> Unit)?
  ) {
    val imageOptions = getImageOptions(uri, options)

    showImageInternal(
      Glide.with(imageView.context)
        .asBitmap()
        .load(uri)
        .transition(BitmapTransitionOptions.withCrossFade(getDrawableCrossFadeFactory(imageOptions.animationDuration)))
      , imageView, imageOptions
    )
  }

  override fun showImageView(
    imageView: View,
    resId: Int,
    options: (ImageLoaderOptions.() -> Unit)?
  ) {
    val imageOptions = getImageOptions(Uri.parse("res://$resId"), options)

    showImageInternal(
      Glide.with(imageView.context)
        .asDrawable()
        .load(resId)
        .transition(DrawableTransitionOptions.withCrossFade(getDrawableCrossFadeFactory(imageOptions.animationDuration)))
      , imageView, imageOptions
    )
  }

  override fun getBitmap(
    context: Context,
    url: String,
    onGetBitmap: (Bitmap?) -> Unit,
    options: (ImageLoaderOptions.() -> Unit)?
  ) {
  }

  override fun cleanMemory(context: Context) {
    Glide.get(context).clearMemory()
  }

  override fun pause(context: Context) {
    Glide.with(context).pauseRequests()
  }

  override fun resume(context: Context) {
    Glide.with(context).pauseRequests()
  }

  override fun clearAllCache() {
    clearTaskExectors.submit {
      Glide.get(application).clearDiskCache()
      Glide.get(application).clearMemory()
    }
  }

  override fun getConfig(): Any {
    return applicationConfig
  }

  override fun getDefaultImageOptions(): ImageLoaderOptions? = defaultImageLoaderOptions
}