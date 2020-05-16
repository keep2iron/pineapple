package io.github.keep2iron.pineapple.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
//import com.facebook.drawee.drawable.ScalingUtils
//import com.facebook.drawee.view.DraweeTransition
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pineapple.ImageLoaderOptions
import io.github.keep2iron.pineapple.MiddlewareView

class ShareElementActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_share_element)

    val imageView = findViewById<MiddlewareView>(R.id.imageView)

//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//      window.sharedElementEnterTransition = DraweeTransition.createTransitionSet(
//          ScalingUtils.ScaleType.CENTER_CROP,
//          ScalingUtils.ScaleType.FIT_CENTER
//      )// 进入
//      window.sharedElementReturnTransition = DraweeTransition.createTransitionSet(
//          ScalingUtils.ScaleType.FIT_CENTER,
//          ScalingUtils.ScaleType.CENTER_CROP
//      ) // 返回
//      imageView.transitionName = "imageView"
//    }

    val url = intent.getStringExtra("url")
    ImageLoaderManager.getInstance()
      .showImageView(
        imageView, url
      ) {
        isCircleImage = false
        scaleType = ImageLoaderOptions.ScaleType.FIT_CENTER
        setPlaceHolderRes(this@ShareElementActivity, R.drawable.ic_launcher_background)
        isLoadGif = true
      }
  }

}