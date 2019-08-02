package io.github.keep2iron.pineapple

import android.content.Context
import com.facebook.common.util.ByteConstants
import java.io.File

class ImageLoaderConfig(
  var context: Context,

  /**
   * 最大缓存数量
   */
  var maxCacheCount: Int = 200,

  /**
   * 最大缓存大小
   */
  var maxCacheSize: Long = (200 * ByteConstants.MB).toLong(),

  /**
   * 缓存文件名字
   */
  var cacheDirName: String = "cache_images",

  /**
   * 缓存的路径
   */
  var cacheDirPath: File = context.cacheDir,

  /**
   * 是否是调试模式
   */
  var debug: Boolean = false
)