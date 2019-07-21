package io.github.keep2iron.pineapple

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri

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

    override fun getDefaultImageOptions(): ImageLoaderOptions? = imageLoader.getDefaultImageOptions()

    override fun getConfig(): Any = imageLoader.getConfig()

    override fun init(
        context: Application,
        config: ImageLoaderConfig,
        defaultImageLoaderOptions: (ImageLoaderOptions.() -> Unit)?
    ) {
        imageLoader.init(context, config, defaultImageLoaderOptions)
    }

    override fun showImageView(imageView: MiddlewareView, url: String, options: (ImageLoaderOptions.() -> Unit)?) {
        imageLoader.showImageView(imageView, url, options)
    }

    override fun showImageView(imageView: MiddlewareView, uri: Uri, options: (ImageLoaderOptions.() -> Unit)?) {
        imageLoader.showImageView(imageView, uri, options)
    }

    override fun showImageView(imageView: MiddlewareView, resId: Int, options: (ImageLoaderOptions.() -> Unit)?) {
        imageLoader.showImageView(imageView, resId, options)
    }

    override fun getBitmap(
        context: Context,
        url: String,
        onGetBitmap: (Bitmap?) -> Unit,
        options: (ImageLoaderOptions.() -> Unit)?
    ) {
        imageLoader.getBitmap(context, url, onGetBitmap, options)
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

        @JvmStatic
        fun init(
            application: Application,
            config: ImageLoaderConfig? = null,
            imageLoader: ImageLoader = FRESCO,
            defaultImageLoaderOptions: (ImageLoaderOptions.() -> Unit)? = null
        ) {
            val perConfig = config ?: ImageLoaderConfig(application)
            INSTANCE = ImageLoaderManager(imageLoader)
            INSTANCE!!.init(application, perConfig, defaultImageLoaderOptions)
        }

        private var INSTANCE: ImageLoaderManager? = null

        fun getInstance(): ImageLoaderManager {
            return INSTANCE!!
        }

        @JvmStatic
        private val FRESCO: ImageLoader by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            FrescoImageLoader()
        }
    }
}