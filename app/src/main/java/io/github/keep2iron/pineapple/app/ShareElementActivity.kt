package io.github.keep2iron.pineapple.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.DraweeTransition
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pineapple.ImageLoaderOptions
import io.github.keep2iron.pineapple.MiddlewareView

class ShareElementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_element)
        window.sharedElementEnterTransition = DraweeTransition.createTransitionSet(
            ScalingUtils.ScaleType.CENTER_CROP,
            ScalingUtils.ScaleType.CENTER_CROP) // 进入
        window.sharedElementReturnTransition = DraweeTransition.createTransitionSet(
            ScalingUtils.ScaleType.CENTER_CROP,
            ScalingUtils.ScaleType.CENTER_CROP) // 返回

        val imageView = findViewById<MiddlewareView>(R.id.imageView)
        imageView.transitionName = "imageView"
        val url = intent.getStringExtra("url")
        ImageLoaderManager.getInstance().showImageView(
            imageView, url, ImageLoaderOptions(
                isCircleImage = false,
                scaleType = ImageLoaderOptions.ScaleType.CENTER_CROP,
                placeHolderRes = R.drawable.ic_launcher_background,
                isLoadGif = true
            )
        )
    }

}