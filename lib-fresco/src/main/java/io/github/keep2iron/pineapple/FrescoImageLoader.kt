package io.github.keep2iron.pineapple

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import android.view.View
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.common.logging.FLog
import com.facebook.common.memory.MemoryTrimType
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.cache.MemoryCacheParams
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.core.ImagePipelineFactory
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor
import com.facebook.imagepipeline.request.ImageRequestBuilder
import java.util.concurrent.Executors

class MatrixScaleType(private val matrix: Matrix) : ScalingUtils.ScaleType {

  override fun getTransform(
    outTransform: Matrix,
    parentBounds: Rect,
    childWidth: Int,
    childHeight: Int,
    focusX: Float,
    focusY: Float
  ): Matrix {
    val sX = parentBounds.width().toFloat() / childWidth
    val sY = parentBounds.height().toFloat() / childHeight
    val scale = sX.coerceAtLeast(sY)
    outTransform.postConcat(matrix)
    outTransform.postScale(scale, scale)
    return outTransform
  }
}

/**
 *
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2018/06/25 20:12
 */
class ImageLoaderImpl : ImageLoader {
  private lateinit var config: ImagePipelineConfig

  private lateinit var imageLoaderConfig: ImageLoaderConfig

  private var defaultImageLoaderOptions: ImageLoaderOptions? = null

  override fun getDefaultImageOptions(): ImageLoaderOptions? = defaultImageLoaderOptions

  override fun getConfig(): Any {
    return config
  }

  private val handler = Handler(Looper.getMainLooper())

  private lateinit var cache: LruCache<Uri, ImageLoaderOptions>

  override fun init(
    context: Application,
    config: ImageLoaderConfig,
    defaultImageLoaderOptions: (ImageLoaderOptions.() -> Unit)?
  ) {
    this.imageLoaderConfig = config

    this.defaultImageLoaderOptions = if (defaultImageLoaderOptions != null) {
      ImageLoaderOptions.newClearOption(defaultImageLoaderOptions)
    } else {
      null
    }
    val createMemoryCacheParams = {
      val maxHeapSize = Runtime.getRuntime()
        .maxMemory()
        .toInt()
      val maxMemoryCacheSize = maxHeapSize / 3 * 2//取手机内存最大值的三分之二作为可用的最大内存数
      MemoryCacheParams( //
        // 可用最大内存数，以字节为单位
        maxMemoryCacheSize,
        // 内存中允许的最多图片数量
        config.maxCacheCount,
        // 内存中准备清理但是尚未删除的总图片所可用的最大内存数，以字节为单位
        Integer.MAX_VALUE,
        // 内存中准备清除的图片最大数量
        Integer.MAX_VALUE,
        // 内存中单图片的最大大小
        Integer.MAX_VALUE
      )
    }
    val imagePipelineConfigBuilder = ImagePipelineConfig.newBuilder(context)
    imagePipelineConfigBuilder.setMainDiskCacheConfig(
      DiskCacheConfig.newBuilder(context)
        .setBaseDirectoryPath(config.cacheDirPath)//设置磁盘缓存的路径
        .setBaseDirectoryName(config.cacheDirName)//设置磁盘缓存文件夹的名称
        .setMaxCacheSize(config.maxCacheSize)//设置磁盘缓存的大小
        .build()
    )
    imagePipelineConfigBuilder.isDownsampleEnabled = true
    //设置已解码的内存缓存（Bitmap缓存）
    imagePipelineConfigBuilder.setBitmapMemoryCacheParamsSupplier {
      return@setBitmapMemoryCacheParamsSupplier createMemoryCacheParams()
    }
    //设置未解码的内存缓存
    imagePipelineConfigBuilder.setEncodedMemoryCacheParamsSupplier {
      return@setEncodedMemoryCacheParamsSupplier createMemoryCacheParams()
    }
    //设置内存紧张时的应对措施
    val memoryTrimmableRegistry = NoOpMemoryTrimmableRegistry.getInstance()
    memoryTrimmableRegistry.registerMemoryTrimmable { trimType ->
      val suggestedTrimRatio = trimType.suggestedTrimRatio
      if (MemoryTrimType.OnCloseToDalvikHeapLimit.suggestedTrimRatio == suggestedTrimRatio
        || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.suggestedTrimRatio == suggestedTrimRatio
        || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.suggestedTrimRatio == suggestedTrimRatio
      ) {
        //清空内存缓存
        ImagePipelineFactory.getInstance()
          .imagePipeline.clearMemoryCaches()
      }
    }
    imagePipelineConfigBuilder.setProgressiveJpegConfig(SimpleProgressiveJpegConfig())
    imagePipelineConfigBuilder.setMemoryTrimmableRegistry(memoryTrimmableRegistry)
    imagePipelineConfigBuilder.isDownsampleEnabled = true
    imagePipelineConfigBuilder.setBitmapsConfig(Bitmap.Config.RGB_565)

    this.config = imagePipelineConfigBuilder.build()
    Fresco.initialize(context.applicationContext, this.config)
    if (config.debug) {
      FLog.setMinimumLoggingLevel(FLog.VERBOSE)
    }

    cache = LruCache(config.optionCacheSize)
  }

  override fun showImageView(
    imageView: View,
    resId: Int,
    options: (ImageLoaderOptions.() -> Unit)?
  ) {
    this.showImageView(
      imageView,
      Uri.parse("res://" + imageView.context.applicationContext.packageName + "/" + resId),
      options
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

  override fun showImageView(
    imageView: View,
    uri: Uri,
    options: (ImageLoaderOptions.() -> Unit)?
  ) {
    val newOptions = getImageOptions(uri, options)

    val draweeView = imageView as SimpleDraweeView
    val requestBuilder = buildImageRequest(uri, newOptions)
    if (newOptions.iterations > 0 && newOptions.blurRadius > 0) {
      requestBuilder.postprocessor =
        IterativeBoxBlurPostProcessor(newOptions.iterations, newOptions.blurRadius)
    }
    val controllerBuilder = buildController(requestBuilder, draweeView, newOptions)
    if (newOptions.isLoadGif) {
      controllerBuilder.autoPlayAnimations = true
    }
    if (newOptions.smallImageUri != null) {
      controllerBuilder.lowResImageRequest =
        buildImageRequest(Uri.parse(newOptions.smallImageUri), newOptions).build()
    }
    val controller = controllerBuilder.build()
    setImageLoaderOptions(newOptions, draweeView)
    draweeView.controller = controller
  }

  private fun buildController(
    request: ImageRequestBuilder,
    draweeView: SimpleDraweeView,
    options: ImageLoaderOptions
  ): PipelineDraweeControllerBuilder {
    val controllerBuilder = Fresco.newDraweeControllerBuilder()
      .setImageRequest(request.build())
      .setOldController(draweeView.controller)

    require(!(options.isSetByImageSize && options.imageWidth <= 0)) { "if you set options isSetByImageSize,you must set imageWidth,because compute height dependency by imageWidth." }

    if (options.isSetByImageSize || options.onFinalImageSetListener != null || options.onImageFailure != null) {
      val controllerListener = object : BaseControllerListener<ImageInfo>() {
        override fun onFinalImageSet(
          id: String?,
          imageInfo: ImageInfo?,
          animatable: Animatable?
        ) {
          if (imageInfo == null) {
            return
          }
          if (options.isSetByImageSize) {
            val layoutParams = draweeView.layoutParams
            val height = imageInfo.height
            val width = imageInfo.width
            layoutParams.width = options.imageWidth
            if (options.imageHeight <= 0) {
              layoutParams.height = (options.imageWidth.toFloat() / width * height).toInt()
            } else {
              layoutParams.height = options.imageHeight
            }
            draweeView.layoutParams = layoutParams
          }
          options.onFinalImageSetListener?.invoke(imageInfo.width, imageInfo.height)
        }

        override fun onFailure(
          id: String?,
          throwable: Throwable?
        ) {
          super.onFailure(id, throwable)

          options.onImageFailure?.invoke()
        }
      }
      controllerBuilder.controllerListener = controllerListener
    }
    return controllerBuilder
  }

  override fun showImageView(
    imageView: View,
    url: String,
    options: (ImageLoaderOptions.() -> Unit)?
  ) {
    showImageView(imageView, Uri.parse(url), options)
  }

  private fun buildImageRequest(
    uri: Uri,
    options: ImageLoaderOptions
  ): ImageRequestBuilder {
    val request = ImageRequestBuilder.newBuilderWithSource(uri)
      //本功能仅支持本地URI，并且是JPEG图片格式 如果本地JPEG图，有EXIF的缩略图，image pipeline 可以立刻返回它作为一个缩略图
      .setLocalThumbnailPreviewsEnabled(true)
      //渐进式加载
      .setProgressiveRenderingEnabled(options.isProgressiveLoadImage)
    if (options.resizeImageWidth > 0 && options.resizeImageHeight > 0) {
      request.resizeOptions = ResizeOptions(options.resizeImageWidth, options.resizeImageHeight)
    }
    return request
  }

  override fun getBitmap(
    context: Context,
    url: String,
    onGetBitmap: (Bitmap?) -> Unit,
    options: (ImageLoaderOptions.() -> Unit)?
  ) {
    val opt = getImageOptions(Uri.parse(url), options)
    val request = buildImageRequest(Uri.parse(url), opt)
    val imagePipeline = Fresco.getImagePipeline()
    val dataSource = imagePipeline.fetchDecodedImage(request.build(), context.applicationContext)
    dataSource.subscribe(object : BaseBitmapDataSubscriber() {
      override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>?) {
        handler.post {
          onGetBitmap(null)
        }
      }

      override fun onNewResultImpl(bitmap: Bitmap?) {
        val copyBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        handler.post {
          onGetBitmap(copyBitmap)
        }
      }
    }, Executors.newCachedThreadPool())
  }

  private fun setImageLoaderOptions(
    options: ImageLoaderOptions,
    draweeView: SimpleDraweeView
  ) {
    val builder = GenericDraweeHierarchyBuilder(draweeView.context.resources)

    val hierarchy = builder
      .setFadeDuration(options.animationDuration)
      .build()
    draweeView.hierarchy = hierarchy

    if (options.isCircleImage) {
      loadCircleImage(draweeView)
    }
    if (options.radius > 0 ||
      options.radiusTopLeft > 0 ||
      options.radiusTopRight > 0 ||
      options.radiusBottomLeft > 0 ||
      options.radiusBottomRight > 0
    ) {
      loadRadiusImage(draweeView, options)
    }
    if (options.borderOverlayColor != -1) {
      setBorder(draweeView, options.borderOverlayColor, options.borderSize)
    }
    if (options.scaleType != ImageLoaderOptions.ScaleType.NONE) {
      setMode(draweeView, options)
    }
    if (options.placeHolderRes != 0) {
      hierarchy.setPlaceholderImage(options.placeHolderRes)
    }
    if (options.placeHolder != null) {
      hierarchy.setPlaceholderImage(options.placeHolder)
    }
  }

  private fun setMode(
    draweeView: SimpleDraweeView,
    options: ImageLoaderOptions
  ) {
    val optionScaleType = when (options.scaleType) {
      ImageLoaderOptions.ScaleType.CENTER -> ScalingUtils.ScaleType.CENTER
      ImageLoaderOptions.ScaleType.CENTER_CROP -> ScalingUtils.ScaleType.CENTER_CROP
      ImageLoaderOptions.ScaleType.FOCUS_CROP -> ScalingUtils.ScaleType.FOCUS_CROP
      ImageLoaderOptions.ScaleType.CENTER_INSIDE -> ScalingUtils.ScaleType.CENTER_INSIDE
      ImageLoaderOptions.ScaleType.FIT_CENTER -> ScalingUtils.ScaleType.FIT_CENTER
      ImageLoaderOptions.ScaleType.FIT_START -> ScalingUtils.ScaleType.FIT_START
      ImageLoaderOptions.ScaleType.FIT_END -> ScalingUtils.ScaleType.FIT_END
      ImageLoaderOptions.ScaleType.FIT_XY -> ScalingUtils.ScaleType.FIT_XY
      //默认fitCenter
      ImageLoaderOptions.ScaleType.NONE -> ScalingUtils.ScaleType.FIT_CENTER
      ImageLoaderOptions.ScaleType.MATRIX -> MatrixScaleType(options.matrix!!)
    }
    draweeView.hierarchy.actualImageScaleType = optionScaleType
    draweeView.hierarchy.setActualImageFocusPoint(PointF(0.5f, 0.5f))
  }

  private fun loadCircleImage(draweeView: SimpleDraweeView) {
    val asCircle = RoundingParams.asCircle()
    draweeView.hierarchy.roundingParams = asCircle
  }

  private fun loadRadiusImage(
    draweeView: SimpleDraweeView,
    options: ImageLoaderOptions
  ) {
    if (options.radiusTopLeft == 0f) {
      options.radiusTopLeft = options.radius
    }
    if (options.radiusTopRight == 0f) {
      options.radiusTopRight = options.radius
    }
    if (options.radiusBottomLeft == 0f) {
      options.radiusBottomLeft = options.radius
    }
    if (options.radiusBottomRight == 0f) {
      options.radiusBottomRight = options.radius
    }

    val cornersRadius = RoundingParams.fromCornersRadii(
      options.radiusTopLeft,
      options.radiusTopRight,
      options.radiusBottomRight,
      options.radiusBottomLeft
    )
    draweeView.hierarchy.roundingParams = cornersRadius
  }

  private fun setBorder(
    draweeView: SimpleDraweeView,
    color: Int,
    borderSize: Float
  ) {
    val roundingParams = draweeView.hierarchy.roundingParams
      ?: throw IllegalArgumentException("draweeView.hierarchy.roundingParams == null,you must set radius or set a circle image")
    require(borderSize > 0) { "do you forget set a borderSize?" }
    roundingParams.setBorder(color, borderSize)
  }

  override fun cleanMemory(context: Context) {
    //清空内存缓存
    ImagePipelineFactory.getInstance()
      .imagePipeline.clearMemoryCaches()
  }

  override fun clearAllCache() {
    val imagePipeline = Fresco.getImagePipeline()
    imagePipeline.clearCaches()
  }

  override fun pause(context: Context) {
    Fresco.getImagePipeline().pause()
  }

  override fun resume(context: Context) {
    Fresco.getImagePipeline().resume()
  }
}