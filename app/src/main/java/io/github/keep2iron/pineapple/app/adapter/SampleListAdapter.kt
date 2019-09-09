package io.github.keep2iron.pineapple.app.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pineapple.ImageLoaderOptions
import io.github.keep2iron.pineapple.MiddlewareView
import io.github.keep2iron.pineapple.app.R


class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

}

class SampleListAdapter(
  val activity: AppCompatActivity,
  private val data: List<String>
) :
  RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): RecyclerView.ViewHolder {
//    val binding = DataBindingUtil.inflate<ItemSampleListBinding>(
//      LayoutInflater.from(parent.context.applicationContext),
//      R.layout.item_sample_list,
//      parent,
//      false
//    )
    return MyViewHolder(
      LayoutInflater.from(parent.context).inflate(
        R.layout.item_sample_list,
        parent,
        false
      )
    )
  }

  override fun getItemCount(): Int = data.size

  override fun onBindViewHolder(
    holder: RecyclerView.ViewHolder,
    position: Int
  ) {
    val imageView = holder.itemView.findViewById<MiddlewareView>(R.id.imageView)
    // 通过 ImageLoadManager.INSTANCE 直接加载
    ImageLoaderManager.getInstance()
      .showImageView(
        imageView,
        data[position]
      ) {
        if (position == 0) {
          isCircleImage = false
          scaleType = ImageLoaderOptions.ScaleType.FIT_XY
          isLoadGif = true

          borderSize = 2f
          borderOverlayColor = Color.RED

//          radiusBottomLeft = 30f
//          radiusBottomRight = 30f
          isSetByImageSize = true
          imageWidth = activity.resources.displayMetrics.widthPixels
          imageHeight = LayoutParams.WRAP_CONTENT

          blurRadius = 3
          iterations = 4

          resizeImageWidth = 500
          resizeImageHeight = 500
        } else {
          scaleType = ImageLoaderOptions.ScaleType.CENTER_CROP
          isSetByImageSize = true
          isLoadGif = true

          imageWidth = activity.resources.displayMetrics.widthPixels
          imageHeight = LayoutParams.WRAP_CONTENT

          resizeImageWidth = 500
          resizeImageHeight = 500
        }
      }
//       val imageRequest =
//          ImageRequestBuilder.newBuilderWithSource(Uri.parse(data[position]))
//              .build()
//      holder.binding.imageView.setImageRequest(imageRequest)
//    holder.binding.executePendingBindings()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      holder.itemView.transitionName = "imageView"
    }
//    holder.itemView.setOnClickListener {
//      val context = holder.itemView.context
//      val intent = Intent(context, ShareElementActivity::class.java)
//      val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//        activity,
//        holder.binding.imageView,
//        "imageView"
//      )
//      intent.putExtra("url", data[position])
//      activity.startActivity(intent, options.toBundle())
//    }
  }
}