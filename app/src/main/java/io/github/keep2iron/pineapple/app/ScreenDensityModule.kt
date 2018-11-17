package io.github.keep2iron.pineapple.app

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration

class ScreenDensityModule {

    companion object {
        @JvmField
        var sNonCompatDensity: Float = 0f
        @JvmField
        var sNonCompatScaleDensity: Float = 0f
    }

    fun setCustomDensity(activity: Activity, application: Application) {
        val applicationDisplayMetrics = application.resources.displayMetrics
        if (sNonCompatDensity == 0f) {
            sNonCompatDensity = applicationDisplayMetrics.density
            sNonCompatScaleDensity = applicationDisplayMetrics.scaledDensity
            application.registerComponentCallbacks(object : ComponentCallbacks2 {
                override fun onTrimMemory(level: Int) {

                }

                override fun onConfigurationChanged(newConfig: Configuration?) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNonCompatScaleDensity = application.resources.displayMetrics.scaledDensity
                    }
                }

                override fun onLowMemory() {}
            })
        }

        val targetDensity = applicationDisplayMetrics.widthPixels / 480f
        val targetScaledDensity = sNonCompatScaleDensity * (targetDensity / sNonCompatDensity)
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