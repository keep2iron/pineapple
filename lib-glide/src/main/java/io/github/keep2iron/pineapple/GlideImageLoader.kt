package io.github.keep2iron.pineapple

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.util.LruCache
import android.view.View
import android.widget.ImageView
import androidx.annotation.CallSuper
import androidx.collection.SparseArrayCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.*
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import io.github.keep2iron.pineapple.ImageLoaderOptions.ScaleType
import io.github.keep2iron.pineapple.ImageLoaderOptions.ScaleType.*
import io.github.keep2iron.pineapple.util.CropCircleWithBorderTransformation
import io.github.keep2iron.pineapple.util.NewRoundedCornersTransformation
import io.github.keep2iron.pineapple.util.SupportRSBlurTransformation
import java.io.File
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

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(File::class.java, BitmapFactory.Options::class.java, BitmapSizeDecoder())
        registry.register(
            BitmapFactory.Options::class.java,
            Size2::class.java,
            OptionsSizeResourceTranscoder()
        )

    }
}

class ImageLoaderImpl : ImageLoader {
    private lateinit var application: Application

    private lateinit var applicationConfig: ImageLoaderConfig

    private val drawableCrossFadeArrayMap: SparseArrayCompat<DrawableCrossFadeFactory> =
        SparseArrayCompat()

    private val clearTaskExectors by lazy(NONE) {
        Executors.newCachedThreadPool()
    }

    private val sizeOptions by lazy(NONE) {
        RequestOptions()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
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
        requestBuilder: RequestBuilder<Drawable>,
        imageView: View,
        imageOptions: ImageLoaderOptions
    ) {

        requestBuilder.apply {
            val transforms = ArrayList<Transformation<Bitmap>>()
            when (imageOptions.scaleType) {
                CENTER_CROP -> {
                    transforms.add(CenterCrop())
                }
                FOCUS_CROP,
                CENTER,
                FIT_START,
                FIT_END,
                FIT_XY,
                MATRIX -> {
                    //only support in fresco
                    if (applicationConfig.debug) {
                        Log.d("pineapplie", "not support this transform")
                    }
                }
                CENTER_INSIDE -> {
                    transforms.add(CenterInside())
                }
                ScaleType.NONE -> {
                    apply(RequestOptions.noTransformation())
                }
                FIT_CENTER -> {
                    transforms.add(FitCenter())
                }
            }

            //向下取样
            if (imageOptions.resizeImageWidth > 0 && imageOptions.resizeImageHeight > 0) {
                override(imageOptions.resizeImageWidth, imageOptions.resizeImageHeight)
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
                transforms.add(
                    SupportRSBlurTransformation(
                        imageOptions.blurRadius,
                        imageOptions.iterations
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

                transforms.add(
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

            into(imageView as ImageView)

//            val viewTarget = into(imageView as ImageView)
//
//            if (imageOptions.isSetByImageSize || imageOptions.onFinalImageSetListener != null || imageOptions.onImageFailure != null) {
//                viewTarget.getSize { width, height ->
//
//                    val layoutParams = imageView.layoutParams
//                    val viewWidth = layoutParams.width
//                    val viewHeight = layoutParams.height
//
//                    if (viewWidth == imageOptions.imageWidth && viewHeight == imageOptions.imageHeight) {
//                        imageView.layoutParams = layoutParams
//                        imageOptions.onFinalImageSetListener?.invoke(width, height)
//                        return@getSize
//                    }
//
//                    if (imageOptions.isSetByImageSize && imageOptions.imageWidth > 0) {
//                        layoutParams.width = imageOptions.imageWidth
//                        if (imageOptions.imageHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
//                            layoutParams.height =
//                                (imageOptions.imageWidth.toFloat() / width * height).toInt()
//                        } else {
//                            layoutParams.height = imageOptions.imageHeight
//                        }
//                        imageView.layoutParams = layoutParams
//                    }
//                    imageOptions.onFinalImageSetListener?.invoke(width, height)
//                }
//            }
        }
    }

    override fun showImageView(
        imageView: View,
        url: String,
        options: (ImageLoaderOptions.() -> Unit)?
    ) {
        val imageOptions = getImageOptions(Uri.parse(url), options)

        showImageInternal(
            setGlideInternal(imageView, imageOptions).load(url)
            , imageView, imageOptions
        )
    }

    private fun setGlideInternal(
        imageView: View,
        imageOptions: ImageLoaderOptions
    ): RequestManager {

        val lifecycleOwner = imageOptions.lifecycleOwner
        val requestManager = when {
            lifecycleOwner != null && lifecycleOwner is FragmentActivity -> {
                Glide.with(lifecycleOwner)
            }
            lifecycleOwner != null && lifecycleOwner is Fragment -> {
                Glide.with(lifecycleOwner)
            }
            else -> {
                Glide.with(imageView)
            }
        }

        return requestManager
            .apply {
                if (imageOptions.isLoadGif) {
                    asGif()
                        .transition(
                            DrawableTransitionOptions.withCrossFade(
                                getDrawableCrossFadeFactory(
                                    imageOptions.animationDuration
                                )
                            )
                        )
                } else {
                    asBitmap()
                        .transition(
                            BitmapTransitionOptions.withCrossFade(
                                getDrawableCrossFadeFactory(
                                    imageOptions.animationDuration
                                )
                            )
                        )
                }
            }
    }

    override fun showImageView(
        imageView: View,
        uri: Uri,
        options: (ImageLoaderOptions.() -> Unit)?
    ) {
        val imageOptions = getImageOptions(uri, options)

        showImageInternal(
            setGlideInternal(imageView, imageOptions).load(uri)
            , imageView, imageOptions
        )
    }

    override fun showImageView(
        imageView: View,
        resId: Int,
        options: (ImageLoaderOptions.() -> Unit)?
    ) {
        //用于生成option的key
        val optionsKey = Uri.parse("res://$resId")
        val imageOptions = getImageOptions(optionsKey, options)

        showImageInternal(
            setGlideInternal(imageView, imageOptions).load(resId),
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
        Glide.with(context)
            .asBitmap()
            .load(url)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onGetBitmap(null)
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onGetBitmap(resource)
                    return false
                }
            }
            ).submit()
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