package io.github.keep2iron.pineapple

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import android.util.LruCache
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.request.LoadRequest
import coil.request.LoadRequestBuilder
import coil.transform.BlurTransformation
import coil.transform.Transformation
import io.github.keep2iron.pineapple.util.NewCircleCropTransformation
import io.github.keep2iron.pineapple.util.NewRoundedCornersTransformation
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

class ImageLoaderImpl : ImageLoader {

  private lateinit var _applicationConfig: ImageLoaderConfig

  /**
   * 用来复用之前的ImageLoaderOptions
   */
  private lateinit var _cache: LruCache<Uri, ImageLoaderOptions>

  private lateinit var _imageLoader: coil.ImageLoader

  private var _defaultImageLoaderOptions: ImageLoaderOptions? = null

  override fun init(
    context: Application,
    config: ImageLoaderConfig,
    defaultImageLoaderOptions: (ImageLoaderOptions.() -> Unit)?
  ) {

    _applicationConfig = config

    this._defaultImageLoaderOptions = if (defaultImageLoaderOptions != null) {
      ImageLoaderOptions.newClearOption(defaultImageLoaderOptions)
    } else {
      null
    }

    val builder = coil.ImageLoader.Builder(context)
      .okHttpClient {
        OkHttpClient.Builder()
          .connectTimeout(5000, TimeUnit.MILLISECONDS)
          .callTimeout(5000, TimeUnit.MILLISECONDS)
          .readTimeout(5000, TimeUnit.MILLISECONDS)
          .cache(Cache(File(config.cacheDirPath, config.cacheDirName), config.maxCacheSize))
          .build()
      }

    //gif support
    try {
      Class.forName("coil.decode.GifDecoder")
      //如果不抛异常则说明支持gif
      builder.componentRegistry {
        if (SDK_INT >= 28) {
          add(ImageDecoderDecoder())
        } else {
          add(GifDecoder())
        }
      }
    } catch (e: ClassNotFoundException) {
      Log.e("pineapple",Log.getStackTraceString(e))
    }

    //svg support
    try {
      Class.forName("coil.decode.SvgDecoder")
      //如果不抛异常则说明支持gif
      builder.componentRegistry {
        add(SvgDecoder(context))
      }
    } catch (e: ClassNotFoundException) {
      Log.e("pineapple",Log.getStackTraceString(e))
    }

    _imageLoader = builder.build()

    _cache = LruCache(config.optionCacheSize)
  }

  private fun showImageInternal(
    requestBuilder: LoadRequestBuilder,
    imageView: View,
    imageOptions: ImageLoaderOptions
  ) {
    val request = requestBuilder.apply {
      crossfade(true)
      val transformationList = mutableListOf<Transformation>()

      when (imageOptions.scaleType) {
        ImageLoaderOptions.ScaleType.CENTER_CROP -> {
          (imageView as ImageView).scaleType = ImageView.ScaleType.CENTER_CROP
        }
        ImageLoaderOptions.ScaleType.FOCUS_CROP -> {
        }
        ImageLoaderOptions.ScaleType.CENTER -> {
          (imageView as ImageView).scaleType = ImageView.ScaleType.CENTER
        }
        ImageLoaderOptions.ScaleType.FIT_START -> {
          (imageView as ImageView).scaleType = ImageView.ScaleType.FIT_START
        }
        ImageLoaderOptions.ScaleType.FIT_END -> {
          (imageView as ImageView).scaleType = ImageView.ScaleType.FIT_END
        }
        ImageLoaderOptions.ScaleType.FIT_XY -> {
          (imageView as ImageView).scaleType = ImageView.ScaleType.FIT_XY
        }
        ImageLoaderOptions.ScaleType.MATRIX -> {
          (imageView as ImageView).scaleType = ImageView.ScaleType.MATRIX
          imageView.imageMatrix = imageOptions.matrix
        }
        ImageLoaderOptions.ScaleType.CENTER_INSIDE -> {
          (imageView as ImageView).scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
        ImageLoaderOptions.ScaleType.FIT_CENTER -> {
          (imageView as ImageView).scaleType = ImageView.ScaleType.FIT_CENTER
        }
        ImageLoaderOptions.ScaleType.NONE -> {
          //do nothing!
        }
      }

      //向下取样
      if (imageOptions.resizeImageWidth > 0 && imageOptions.resizeImageHeight > 0) {
        size(imageOptions.resizeImageWidth, imageOptions.resizeImageHeight)
      }

      //占位图
      if (imageOptions.placeHolder != null) {
        placeholder(imageOptions.placeHolder)
      }

      //Error展位图
      if (imageOptions.errorHolder != null) {
        error(imageOptions.errorHolder)
      }

      //blur
      if (imageOptions.iterations > 0 && imageOptions.blurRadius > 0) {
        transformationList.add(
          BlurTransformation(
            _applicationConfig.context,
            imageOptions.blurRadius.toFloat(),
            imageOptions.iterations.toFloat()
          )
        )
      }

      //radius
      if ((imageOptions.radius > 0 ||
          imageOptions.radiusTopLeft > 0 ||
          imageOptions.radiusTopRight > 0 ||
          imageOptions.radiusBottomLeft > 0 ||
          imageOptions.radiusBottomRight > 0 ||
          imageOptions.borderSize > 0 ||
          imageOptions.borderOverlayColor > 0) && !imageOptions.isCircleImage
      ) {
        if (imageOptions.radiusTopLeft == 0f) {
          imageOptions.radiusTopLeft = imageOptions.radius
        }

        if (imageOptions.radiusTopRight == 0f) {
          imageOptions.radiusTopRight = imageOptions.radius
        }

        if (imageOptions.radiusBottomLeft == 0f) {
          imageOptions.radiusBottomLeft = imageOptions.radius
        }

        if (imageOptions.radiusBottomRight == 0f) {
          imageOptions.radiusBottomRight = imageOptions.radius
        }

        transformationList.add(
          NewRoundedCornersTransformation(
            imageOptions.radiusTopLeft,
            imageOptions.radiusTopRight,
            imageOptions.radiusBottomRight,
            imageOptions.radiusBottomLeft,
            imageOptions.borderOverlayColor,
            imageOptions.borderSize
          )
        )
      }

      //circle
      if (imageOptions.isCircleImage) {
        transformationList.add(
          NewCircleCropTransformation(
            imageOptions.borderOverlayColor,
            imageOptions.borderSize
          )
        )
      }

      transformations(transformationList)

      target(imageView as ImageView)
    }.build()

    _imageLoader.execute(request)
  }

  override fun showImageView(
    imageView: View,
    url: String,
    options: (ImageLoaderOptions.() -> Unit)?
  ) {
    val imageOptions = getImageOptions(Uri.parse(url), options)

    showImageInternal(
      LoadRequest.Builder(imageView.context)
        .data(url),
      imageView,
      imageOptions
    )
  }

  private fun getImageOptions(
    uri: Uri,
    options: (ImageLoaderOptions.() -> Unit)?
  ): ImageLoaderOptions {
    var option: ImageLoaderOptions? = _cache.get(uri)
    if (option == null) {
      val newOption = if (_defaultImageLoaderOptions != null) {
        ImageLoaderOptions.newOptionWithDefaultOptions(options)
      } else {
        ImageLoaderOptions.newClearOption(options)
      }
      _cache.put(uri, newOption)
      option = newOption
    } else {
      if (_defaultImageLoaderOptions != null) {
        option.copyOptions(_defaultImageLoaderOptions!!)
      } else {
        option.clear()
      }
      if (options != null) {
        option.apply(options)
      }
    }
    return option
  }

  override fun showImageView(imageView: View, uri: Uri, options: (ImageLoaderOptions.() -> Unit)?) {
    val imageOptions = getImageOptions(uri, options)

    showImageInternal(
      LoadRequest.Builder(imageView.context)
        .data(uri),
      imageView,
      imageOptions
    )
  }

  override fun showImageView(
    imageView: View,
    resId: Int,
    options: (ImageLoaderOptions.() -> Unit)?
  ) {
    val uri = Uri.parse("android.resource://$resId")
    val imageOptions = getImageOptions(uri, options)

    showImageInternal(
      LoadRequest.Builder(imageView.context)
        .data(uri),
      imageView,
      imageOptions
    )
  }

  override fun getBitmap(
    context: Context,
    url: String,
    onGetBitmap: (Bitmap?) -> Unit,
    options: (ImageLoaderOptions.() -> Unit)?
  ) {
    val request = LoadRequest.Builder(context)
      .data("https://www.example.com/image.jpg")
      .target(
        onSuccess = { result ->
          // Handle the successful result.
          onGetBitmap(result.toBitmap())
        },
        onError = { _ ->
          // Handle the error drawable.
          onGetBitmap(null)
        }
      )
      .build()
    _imageLoader.execute(request)
  }

  override fun cleanMemory(context: Context) {
    _imageLoader.clearMemory()
  }

  override fun pause(context: Context) {
    throw IllegalArgumentException("coil pause is automatically pausing.")
  }

  override fun resume(context: Context) {
    throw IllegalArgumentException("coil pause is automatically resume.")
  }

  override fun clearAllCache() {
    _imageLoader.clearMemory()
    _imageLoader.shutdown()
  }

  override fun getConfig(): Any = _applicationConfig

  override fun getDefaultImageOptions(): ImageLoaderOptions? = _defaultImageLoaderOptions

}