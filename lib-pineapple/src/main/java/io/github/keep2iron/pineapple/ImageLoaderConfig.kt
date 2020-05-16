package io.github.keep2iron.pineapple

import android.content.Context
import java.io.File

class ImageLoaderConfig(
  var context: Context,

  /**
   * 最大缓存数量 fresco有效
   */
  var maxCacheCount: Int = 200,

  /**
   * 最大缓存大小
   */
  var maxCacheSize: Long = (200 * Util.MB),

  /**
   * 缓存文件名字
   */
  var cacheDirName: String = "cache_images",

  /**
   * 缓存的路径
   */
  var cacheDirPath: File = context.cacheDir,

  /**
   * option cache 的最大大小
   */
  var optionCacheSize: Int = 30,

  /**
   * 是否是调试模式
   */
  var debug: Boolean = false
)