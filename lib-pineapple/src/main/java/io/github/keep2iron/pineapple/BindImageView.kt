package io.github.keep2iron.pineapple

import android.databinding.BindingAdapter

/**
 * @author keep2iron [Contract me.](http://keep2iron.github.io)
 * @version 1.0
 * @since 2017/11/21 13:25
 */
object BindImageView {

    @JvmStatic
    @BindingAdapter("url", "scaleType",requireAll = false)
    fun bindImageViewByUrl(
        view: MiddlewareView,
        url: String?,
        scaleType: ImageLoaderOptions.ScaleType?
    ) {
        val imageLoader = ImageLoaderManager.INSTANCE
        url?.apply {
            imageLoader.showImageView(view, this, ImageLoaderOptions().apply {
                this.scaleType = scaleType ?: ImageLoaderOptions.ScaleType.CENTER_CROP
                isProgressiveLoadImage = true
            })
        }
    }
}
