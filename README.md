![](https://i.imgur.com/mnGMMc1.png)

# Pineapple
![](https://img.shields.io/badge/version-0.1.2-brightgreen.svg) ![](https://img.shields.io/badge/fresco-1.11.0-brightgreen.svg) ![](https://img.shields.io/badge/support-27.1.1-brightgreen.svg)

pineapple使用kotlin编写的一个图片加载的封装框架。实现简单，无缝切换，针对Fresco默认实现，如果需要提供Glide版本后面会进行提供。

1.先在项目的gradle文件中添加如下的依赖
```
implementation deps.kotlin.stdlib_jdk7
implementation deps.fresco
kapt deps.kotlin_databinding_compiler
```
2.初始化ImageLoaderManager（不调用的话 后续会抛出异常）
```
ImageLoaderManager.init(application)
```
3.需要注意的是所有使用网络加载图片的时候必须使用如下写法(或者自己继承该类进行显示)
```
<io.github.keep2iron.pineapple.MiddlewareView
		...
   />

```
提供databinding绑定以及手动显示图片的两种使用方法
databinding:

```
<io.github.keep2iron.pineapple.MiddlewareView
        android:id="@+id/imageView"
        app:url="@{imageUrl}"
        android:src="@mipmap/ic_launcher"
        android:layout_width="match_parent"
        android:layout_height="300dp"/>

```
普通加载
```
ImageLoaderManager.INSTANCE.showImageView(holder.binding.imageView, data[position], ImageLoaderOptions())
```