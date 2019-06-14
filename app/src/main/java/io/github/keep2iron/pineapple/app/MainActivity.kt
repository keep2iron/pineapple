package io.github.keep2iron.pineapple.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import com.facebook.common.util.ByteConstants
import io.github.keep2iron.pineapple.ImageLoaderConfig
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pineapple.ImageLoaderOptions
import io.github.keep2iron.pineapple.app.adapter.SampleListAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenDensityModule().setCustomDensity(this,application)
        setContentView(R.layout.activity_main)

        ImageLoaderManager.init(
            application,
            ImageLoaderConfig(
                applicationContext,
                maxCacheCount = 300,
                maxCacheSize = (400 * ByteConstants.MB).toLong()
            ),
            defaultImageLoaderOptions = {
                isCircleImage = true
                scaleType = ImageLoaderOptions.ScaleType.FIT_CENTER
                placeHolderRes = R.mipmap.ic_launcher
                placeHolder = ResourcesCompat.getDrawable(resources, R.mipmap.ic_launcher, null)
            }
        )


        val data = arrayListOf(
            "http://img.mp.itc.cn/upload/20170614/769fdf466d504cd4a3caa50379e7a226_th.jpg",
            "http://d.lanrentuku.com/down/png/1807/10shuguopng.jpg",
            "https://alpha-head.oss-cn-shenzhen.aliyuncs.com/65c57a0e-ccb8-41be-"
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = SampleListAdapter(this, data)
    }
}
