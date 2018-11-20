package io.github.keep2iron.pineapple

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.annotation.IntDef
import io.github.keep2iron.pineapple.ImageLoaderManager.Companion.FRESCO
import io.github.keep2iron.pineapple.ImageLoaderManager.Companion.GLIDE
import java.lang.IllegalArgumentException


/**
 *
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2018/06/25 19:42
 *
 * 相当于ImageLoader的代理
 */
class ImageLoaderManager private constructor(private val imageLoader: ImageLoader) :
    ImageLoader {

    override fun getConfig(): Any? = imageLoader.getConfig()

    override fun init(context: Application) {
        imageLoader.init(context)
    }

    override fun showImageView(imageView: MiddlewareView, url: String, options: ImageLoaderOptions) {
        imageLoader.showImageView(imageView, url, options)
    }

    override fun showImageView(imageView: MiddlewareView, uri: Uri, options: ImageLoaderOptions) {
        imageLoader.showImageView(imageView, uri, options)
    }

    override fun getBitmap(context: Context, url: String, options: ImageLoaderOptions, onGetBitmap: (Bitmap?) -> Unit) {
        imageLoader.getBitmap(context, url, options, onGetBitmap)
    }

    override fun cleanMemory(context: Context) {
        imageLoader.cleanMemory(context)
    }

    override fun pause(context: Context) {
        imageLoader.pause(context)
    }

    override fun resume(context: Context) {
        imageLoader.pause(context)
    }

    override fun clearAllCache() {
        imageLoader.clearAllCache()
    }

    companion object {

        const val FRESCO = 0
        const val GLIDE = 1

        @JvmStatic
        fun init(application: Application, @LOADER mode: Int = FRESCO) {
            ImageLoaderManager.INSTANCE = ImageLoaderManager(
                when (mode) {
                    FRESCO -> {
                        FRESCO_LOADER
                    }
                    GLIDE -> {
                        GLIDE_LOADER
                    }
                    else -> {
                        throw IllegalArgumentException("not support $mode loader")
                    }
                }
            )
            ImageLoaderManager.INSTANCE.init(application)
        }

        @JvmStatic
        lateinit var INSTANCE: ImageLoaderManager

        @JvmStatic
        private val FRESCO_LOADER: ImageLoader by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            FrescoImageLoader()
        }

        @JvmStatic
        private val GLIDE_LOADER: ImageLoader by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            GlideImageLoader()
        }
    }
}

@Retention(AnnotationRetention.SOURCE)
@IntDef(FRESCO, GLIDE)
annotation class LOADER
