package io.github.keep2iron.pineapple

import android.graphics.BitmapFactory
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import java.io.File
import java.io.IOException

class BitmapSizeDecoder : ResourceDecoder<File, BitmapFactory.Options> {
  @Throws(IOException::class)
  override fun handles(file: File, options: Options): Boolean {
    return true
  }

  override fun decode(
    file: File,
    width: Int,
    height: Int,
    options: Options
  ): Resource<BitmapFactory.Options>? {
    val bmOptions: BitmapFactory.Options = BitmapFactory.Options()
    bmOptions.inJustDecodeBounds = true
    BitmapFactory.decodeFile(file.absolutePath, bmOptions)
    return SimpleResource(bmOptions)
  }
}