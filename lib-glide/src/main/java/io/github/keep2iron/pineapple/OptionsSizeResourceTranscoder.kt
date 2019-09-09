package io.github.keep2iron.pineapple

import android.graphics.BitmapFactory
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder

class OptionsSizeResourceTranscoder : ResourceTranscoder<BitmapFactory.Options, Size2> {

  override fun transcode(
    toTranscode: Resource<BitmapFactory.Options>,
    options: Options
  ): Resource<Size2> {
    val bmOptions = toTranscode.get()
    val size = Size2(bmOptions.outWidth, bmOptions.outHeight)
    return SimpleResource(size)
  }

}