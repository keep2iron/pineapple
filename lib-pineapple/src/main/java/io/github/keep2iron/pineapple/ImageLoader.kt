package io.github.keep2iron.pineapple

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.annotation.DrawableRes

/**
 *
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2018/06/25 19:54
 */
interface ImageLoader {
    /**
     * 该方法必须在主线程并且最好在application中初始化
     */
    fun init(
        context: Application,
        config: ImageLoaderConfig,
        defaultImageLoaderOptions: (ImageLoaderOptions.() -> Unit)?
    )

    /**
     * 显示imageView
     */
    fun showImageView(
        imageView: MiddlewareView,
        url: String,
        options: (ImageLoaderOptions.() -> Unit)? = null
    )

    fun showImageView(
        imageView: MiddlewareView,
        uri: Uri,
        options: (ImageLoaderOptions.() -> Unit)? = null
    )

    fun showImageView(
        imageView: MiddlewareView,
        @DrawableRes resId: Int,
        options: (ImageLoaderOptions.() -> Unit)? = null
    )


    fun getBitmap(
        context: Context,
        url: String,
        onGetBitmap: (Bitmap?) -> Unit,
        options: (ImageLoaderOptions.() -> Unit)? = null
    )


    /**
     * 清理内存
     */
    fun cleanMemory(context: Context)

    /**
     * 暂停
     */
    fun pause(context: Context)

    /**
     * 继续
     */
    fun resume(context: Context)

    /**
     * 清除所有缓存
     */
    fun clearAllCache()

    /**
     * 获取配置
     */
    fun getConfig(): Any

    /**
     * 获取默认的options
     */
    fun getDefaultImageOptions(): ImageLoaderOptions?
}