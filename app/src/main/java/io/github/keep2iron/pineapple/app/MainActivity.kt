package io.github.keep2iron.pineapple.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pineapple.app.adapter.SampleListAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ImageLoaderManager.init(application)

        val data = arrayListOf(
            "https://img.ipcfun.com/uploads/post/17593/5bcec0e42417f.jpg",
            "http://d.lanrentuku.com/down/png/1807/10shuguopng.jpg",
            "https://alpha-head.oss-cn-shenzhen.aliyuncs.com/65c57a0e-ccb8-41be-807d-a0d6dba0f80e.jpeg"
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = SampleListAdapter(data)
    }
}
