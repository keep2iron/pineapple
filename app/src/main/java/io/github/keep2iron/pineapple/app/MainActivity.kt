package io.github.keep2iron.pineapple.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import io.github.keep2iron.pineapple.ImageLoaderConfig
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pineapple.ImageLoaderOptions
import io.github.keep2iron.pineapple.Util
import io.github.keep2iron.pineapple.app.adapter.SampleListAdapter

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ScreenDensityModule().setCustomDensity(this, application)
    setContentView(R.layout.activity_main)

    ImageLoaderManager.init(
      application,
      ImageLoaderConfig(
        applicationContext,
        maxCacheCount = 300,
        maxCacheSize = (400 * Util.MB)
      ),
      defaultImageLoaderOptions = {
        scaleType = ImageLoaderOptions.ScaleType.FIT_CENTER
        setPlaceHolderRes(this@MainActivity, R.mipmap.ic_launcher)
        placeHolder = ResourcesCompat.getDrawable(resources, R.mipmap.ic_launcher, null)
      }
    )

    val data = arrayListOf(
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=9827cac0dc5440dcaab89f7a1f0da5a8&imgtype=0&src=http%3A%2F%2Fb.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F32fa828ba61ea8d3fcd2e9ce9e0a304e241f5803.jpg",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=4b9154fe74baea94f61794eddaa0db14&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=4b9154fe74baea94f61794eddaa0db14&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=4b9154fe74baea94f61794eddaa0db14&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=4b9154fe74baea94f61794eddaa0db14&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=4b9154fe74baea94f61794eddaa0db14&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=4b9154fe74baea94f61794eddaa0db14&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=4b9154fe74baea94f61794eddaa0db14&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=4b9154fe74baea94f61794eddaa0db14&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=4b9154fe74baea94f61794eddaa0db14&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=4b9154fe74baea94f61794eddaa0db14&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=4b9154fe74baea94f61794eddaa0db14&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=4b9154fe74baea94f61794eddaa0db14&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=4b9154fe74baea94f61794eddaa0db14&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=4b9154fe74baea94f61794eddaa0db14&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=4b9154fe74baea94f61794eddaa0db14&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
      "https://timgsa.baidu.com/ti",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565282832589&di=4b9154fe74baea94f61794eddaa0db14&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F4610b912c8fcc3cef70d70409845d688d53f20f7.jpg",
      "https://techcrunch.com/wp-content/uploads/2015/08/safe_image.gif",
      "https://techcrunch.com/wp-content/uploads/2015/08/safe_image.gif",
      "https://techcrunch.com/wp-content/uploads/2015/08/safe_image.gif",
      "https://blog-assets.hootsuite.com/wp-content/uploads/2018/04/Nyan-Cat-GIF-source.gif",
      "https://blog-assets.hootsuite.com/wp-content/uploads/2018/04/Nyan-Cat-GIF-source.gif"
    )

    val recyclerView =
      findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView)
    recyclerView.adapter = SampleListAdapter(this, data)
  }
}
