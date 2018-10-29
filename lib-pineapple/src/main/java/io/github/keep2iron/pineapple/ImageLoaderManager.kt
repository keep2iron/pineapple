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
class ImageLoaderManager(private val imageLoader: ImageLoader, application: Application) : ImageLoader {

    init {
        this.init(application)
    }

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
}