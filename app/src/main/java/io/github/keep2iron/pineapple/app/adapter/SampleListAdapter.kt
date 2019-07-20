package io.github.keep2iron.pineapple.app.adapter

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pineapple.ImageLoaderOptions
import io.github.keep2iron.pineapple.app.R
import io.github.keep2iron.pineapple.app.ShareElementActivity
import io.github.keep2iron.pineapple.app.databinding.ItemSampleListBinding
import android.support.v4.app.ActivityOptionsCompat
import io.github.keep2iron.pineapple.MiddlewareView


/**
 *
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @date 2018/10/29
 */
class SampleListViewHolder(val binding: ItemSampleListBinding) : RecyclerView.ViewHolder(binding.root)

class SampleListAdapter(val activity: AppCompatActivity, private val data: List<String>) :
    RecyclerView.Adapter<SampleListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleListViewHolder {
        val binding = DataBindingUtil.inflate<ItemSampleListBinding>(
            LayoutInflater.from(parent.context.applicationContext),
            R.layout.item_sample_list,
            parent,
            false
        )
        return SampleListViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SampleListViewHolder, position: Int) {
        // 通过 ImageLoadManager.INSTANCE 直接加载
        ImageLoaderManager.getInstance().showImageView(
            holder.binding.imageView, data[position]
        ) {
            if (position == 0) {
                isCircleImage = false
                scaleType = ImageLoaderOptions.ScaleType.FIT_XY
                placeHolder = null
                isLoadGif = true
            } else {
                scaleType = ImageLoaderOptions.ScaleType.CENTER_CROP
                placeHolder = null
                isLoadGif = true
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.itemView.transitionName = "imageView"
        }
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ShareElementActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                holder.binding.imageView,
                "imageView"
            )
            intent.putExtra("url", data[position])
            activity.startActivity(intent, options.toBundle())
        }
    }
}