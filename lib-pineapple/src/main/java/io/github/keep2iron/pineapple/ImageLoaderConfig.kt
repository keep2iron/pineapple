package io.github.keep2iron.pineapple

import android.content.Context
import com.facebook.common.util.ByteConstants
import java.io.File

class ImageLoaderConfig(
    var context: Context,

    var maxCacheCount: Int = 200,

    var maxCacheSize: Long = (200 * ByteConstants.MB).toLong(),

    var cacheDirName: String = "cache_images",

    var cacheDirPath: File = context.cacheDir
)