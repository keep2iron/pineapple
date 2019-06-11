![](https://i.imgur.com/mnGMMc1.png)

# Pineapple
![](https://img.shields.io/badge/version-0.1.2-brightgreen.svg) ![](https://img.shields.io/badge/fresco-1.11.0-brightgreen.svg) ![](https://img.shields.io/badge/support-27.1.1-brightgreen.svg)

pineapple使用kotlin编写的一个图片加载的封装框架。实现简单，无缝切换，针对Fresco默认实现，如果需要提供Glide版本后面会进行提供。

1.先在项目的gradle文件中添加如下的依赖
```
implementation deps.kotlin.stdlib_jdk7
implementation deps.fresco

如果需要支持gif
implementation deps.fresco.fresco_gif

```
2.初始化ImageLoaderManager（不调用的话 后续会抛出异常）
```
        ImageLoaderManager.init(
            application,
            ImageLoaderConfig(
                applicationContext,
                maxCacheCount = 300,									//最大缓存数
                maxCacheSize = (400 * ByteConstants.MB).toLong(),		//最大缓存大小
				cacheDirName = "cache_images",							//缓存文件夹名
				cacheDirPath =  context.cacheDir						//默认缓存位置
            ),
            defaultImageLoaderOptions = ImageLoaderOptions(
                isCircleImage = true,
                scaleType = ImageLoaderOptions.ScaleType.FIT_CENTER,
                placeHolderRes = R.drawable.ic_launcher_background
            )
        )
```
普通加载
```
ImageLoaderManager.getInstance().showImageView(middleImageView, "url")
```