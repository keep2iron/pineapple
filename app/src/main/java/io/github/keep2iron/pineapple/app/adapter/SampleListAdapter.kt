package io.github.keep2iron.pineapple.app.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pineapple.ImageLoaderOptions
import io.github.keep2iron.pineapple.app.R
import io.github.keep2iron.pineapple.app.databinding.ItemSampleListBinding

/**
 *
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @date 2018/10/29
 */
class SampleListViewHolder(val binding: ItemSampleListBinding) : RecyclerView.ViewHolder(binding.root)

class SampleListAdapter(private val data: List<String>) : RecyclerView.Adapter<SampleListViewHolder>() {
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
//        ImageLoaderManager.INSTANCE.showImageView(holder.binding.imageView, data[position], ImageLoaderOptions())
//        holder.binding.imageUrl = data[position]
        // 通过databinding 绑定item加载
        holder.binding.imageUrl = data[position]
    }
}