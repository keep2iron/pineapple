package io.github.keep2iron.pineapple

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import io.github.keep2iron.pineapple.transform.CircleCropBorderTransform
import io.github.keep2iron.pineapple.transform.RoundedCornersTransformation
import java.lang.IllegalArgumentException


@GlideModule
class PinappleAppGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val calculator = MemorySizeCalculator.Builder(context)
            .setBitmapPoolScreens(3f)
            .setMemoryCacheScreens(2f)
            .build()
        builder.setBitmapPool(LruBitmapPool(calculator.bitmapPoolSize.toLong()))
        //设置缓存
        builder.setMemoryCache(LruResourceCache(calculator.memoryCacheSize.toLong()))
        //设置颜色
        builder.setDefaultRequestOptions(
            RequestOptions()
                .format(DecodeFormat.PREFER_RGB_565)
                .disallowHardwareConfig()
        )
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, "cache_images", 200 * 1024 * 1024))
    }
}

/**
 *
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @date 2018/11/17
 */
class GlideImageLoader : ImageLoader {
    private lateinit var appContext: Context

    override fun init(context: Application) {
        appContext = context
    }

    @SuppressLint("CheckResult")
    override fun showImageView(imageView: MiddlewareView, url: String, options: ImageLoaderOptions) {
        var request = GlideApp.with(imageView.context)
            .load(url)

        if (!options.smallImageUri.isNullOrEmpty()) {
            val thumbnailRequest = GlideApp
                .with(imageView.context)
                .load(options.smallImageUri)
            request = request.thumbnail(thumbnailRequest)
        }

        if (options.isCircleImage) {
            request = request.apply(
                bitmapTransform(
                    CircleCropBorderTransform(
                        options.borderSize.toInt(),
                        options.borderOverlayColor
                    )
                )
            )
        }

        if (options.radius > 0) {
            request = request.apply(
                bitmapTransform(
                    RoundedCornersTransformation(
                        options.radius.toInt(),
                        0,
                        options.borderSize.toInt(),
                        options.borderOverlayColor,
                        RoundedCornersTransformation.CornerType.ALL
                    )
                )
            )

        }

        if (options.scaleType != ImageLoaderOptions.ScaleType.NONE) {
            setMode(request, options)
        }

        request.into(imageView)
    }

    private fun setMode(request: GlideRequest<Drawable>, options: ImageLoaderOptions) {
        when (options.scaleType) {
            ImageLoaderOptions.ScaleType.CENTER_CROP -> {
                request.centerCrop()
            }
            ImageLoaderOptions.ScaleType.NONE -> {
                request.centerCrop()
            }
            ImageLoaderOptions.ScaleType.CENTER_INSIDE -> {
                request.centerInside()
            }
            ImageLoaderOptions.ScaleType.FIT_CENTER -> {
                request.fitCenter()
            }
            ImageLoaderOptions.ScaleType.FIT_START,
            ImageLoaderOptions.ScaleType.FOCUS_CROP,
            ImageLoaderOptions.ScaleType.CENTER,
            ImageLoaderOptions.ScaleType.FIT_END,
            ImageLoaderOptions.ScaleType.FIT_XY,
            ImageLoaderOptions.ScaleType.MATRIX -> throw IllegalArgumentException("not support ${options.scaleType} mode")
        }
    }

    override fun showImageView(imageView: MiddlewareView, uri: Uri, options: ImageLoaderOptions) {
        this.showImageView(imageView, uri.toString(), options)
    }

    override fun getBitmap(context: Context, url: String, options: ImageLoaderOptions, onGetBitmap: (Bitmap?) -> Unit) {
    }

    override fun cleanMemory(context: Context) {
        Glide.get(context).clearMemory()
    }

    override fun pause(context: Context) {
        GlideApp.with(context)
            .pauseAllRequests()
    }

    override fun resume(context: Context) {
        GlideApp.with(context)
            .resumeRequests()
    }

    override fun clearAllCache() {
        Glide.get(appContext).clearDiskCache()
        Glide.get(appContext).clearMemory()
    }

    override fun getConfig(): Any? {
        return null
    }
}