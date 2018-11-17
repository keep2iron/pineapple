package io.github.keep2iron.pineapple.app.adapter

import android.databinding.DataBindingUtil
import android.graphics.PointF
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.generic.RoundingParams
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
//        holder.binding.imageUrl = data[position]
//        ImageLoaderManager.INSTANCE.showImageView(holder.binding.imageView, data[position], ImageLoaderOptions().apply {
//            isCircleImage = true
//            scaleType = ImageLoaderOptions.ScaleType.CENTER_CROP
//        })

        //获取GenericDraweeHierarchy对象
        val hierarchy = GenericDraweeHierarchyBuilder.newInstance(holder.binding.root.resources)
            .setRoundingParams(RoundingParams.asCircle())
            //设置淡入淡出动画持续时间(单位：毫秒ms)
//            .setFadeDuration(5000)
            //构建
            .build()
        hierarchy.setActualImageFocusPoint(PointF(0.5f, 0.5f))


        //设置Hierarchy
        holder.binding.imageView.hierarchy = hierarchy

        //构建Controller
        val controller = Fresco.newDraweeControllerBuilder()
            //设置需要下载的图片地址
            .setUri(data[position])
            //构建
            .build()

        //设置Controller
        holder.binding.imageView.controller = controller
    }
}