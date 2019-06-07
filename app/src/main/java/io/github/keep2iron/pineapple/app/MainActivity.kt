package io.github.keep2iron.pineapple.app

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.facebook.common.util.ByteConstants
import io.github.keep2iron.pineapple.ImageLoaderConfig
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pineapple.ImageLoaderOptions
import io.github.keep2iron.pineapple.app.adapter.SampleListAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCustomDensity(this, this.application)
        setContentView(R.layout.activity_main)

//        Fresco.initialize(this.application)
        ImageLoaderManager.init(
            application,
            ImageLoaderConfig(
                applicationContext,
                maxCacheCount = 300,
                maxCacheSize = (400 * ByteConstants.MB).toLong()
            ),
            defaultImageLoaderOptions = ImageLoaderOptions(
                isCircleImage = true,
                scaleType = ImageLoaderOptions.ScaleType.FIT_CENTER,
                placeHolderRes = R.drawable.ic_launcher_background
            )
        )


        val data = arrayListOf(
            "http://img.mp.itc.cn/upload/20170614/769fdf466d504cd4a3caa50379e7a226_th.jpg",
            "http://d.lanrentuku.com/down/png/1807/10shuguopng.jpg",
            "https://alpha-head.oss-cn-shenzhen.aliyuncs.com/65c57a0e-ccb8-41be-"
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = SampleListAdapter(data)
    }

    fun setCustomDensity(activity: Activity, application: Application) {
        val applicationDisplayMetrics = application.resources.displayMetrics
        if (ScreenDensityModule.sNonCompatDensity == 0f) {
            ScreenDensityModule.sNonCompatDensity = applicationDisplayMetrics.density
            ScreenDensityModule.sNonCompatScaleDensity = applicationDisplayMetrics.scaledDensity
            application.registerComponentCallbacks(object : ComponentCallbacks2 {
                override fun onTrimMemory(level: Int) {

                }

                override fun onConfigurationChanged(newConfig: Configuration?) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        ScreenDensityModule.sNonCompatScaleDensity = application.resources.displayMetrics.scaledDensity
                    }
                }

                override fun onLowMemory() {}
            })
        }

        val targetDensity = applicationDisplayMetrics.widthPixels / 480f
        val targetScaledDensity =
            ScreenDensityModule.sNonCompatScaleDensity * (targetDensity / ScreenDensityModule.sNonCompatDensity)
        val targetDpi = (targetDensity * 160).toInt()

        applicationDisplayMetrics.density = targetDensity
        applicationDisplayMetrics.densityDpi = targetDpi
        applicationDisplayMetrics.scaledDensity = targetScaledDensity

        val activityDisplayMetrics = activity.resources.displayMetrics
        activityDisplayMetrics.density = targetDensity
        activityDisplayMetrics.scaledDensity = targetScaledDensity
        activityDisplayMetrics.densityDpi = targetDpi
    }
}
