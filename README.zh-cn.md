![](images/banner.png)

# Pineapple

Glide version :![Release](https://api.bintray.com/packages/keep2iron/maven/pineapple-glide/images/download.svg) 

Fresco version: ![Release](https://api.bintray.com/packages/keep2iron/maven/pineapple-fresco/images/download.svg)

Build Status: ![BuildStatus](https://travis-ci.org/keep2iron/pineapple.svg?branch=master)

`Pineapple`是一个用于加载图片的管理框架

- 基于kotlin
- Glide和Fresco基于同一个接口进行实现
- 一键切换Glide与Fresco加载
- 基于androidx

# 下载

Glide版本与fresco版本安装一种即可，两者同时安装会有报错

#### Glide 安装

```groovy
dependencies {
    //glide version
    implementation 'io.github.keep2iron:pineapple-glide:$latest_version'
    
    //由于pineapple内部不集成glide版本因此需要自己集成
    implementation 'com.github.bumptech.glide:glide:$glide_latest_version'
    //kotlin需要换成kapt
    annotationProcessor 'com.github.bumptech.glide:compiler:$glide_latest_version'
}
```

> 需要注意的一点是由于Glide v4的特性，需要使用**GlideAppModule**来进行设置一些全局的特性或者属性，因此笔者这里自己实现了一个类 **io.github.keep2iron.pineapple.GlideInitModule**
>
> 如果用到了设置缓存之类的属性 还有获取图片宽高时必须继承该类，复写方法时请切记调用父类方法

```kotlin
@GlideModule
class GlideModule : GlideInitModule()
```



#### Fresco安装

````groovy
dependencies {
    //fresco version
    implementation 'io.github.keep2iron:pineapple-fresco:$latest_version'
   
    //由于pineapple内部不集成glide版本因此需要自己集成
    implementation 'com.facebook.fresco:fresco:2.0.0'
}
````



#### 简单使用

初始化ImageLoaderManager尽可能的早，内部会自动加载fresco或glide，不需要外部进行传递参数
```kotlin
ImageLoaderManager.init(
    application,
    ImageLoaderConfig(
        applicationContext,
        maxCacheCount = 300,
        maxCacheSize = 400 * Util.MB
    ),
    defaultImageLoaderOptions = {
        isCircleImage = false
        scaleType = ImageLoaderOptions.ScaleType.FIT_CENTER
        placeHolderRes = R.mipmap.ic_launcher
        placeHolder = ResourcesCompat.getDrawable(resources, R.mipmap.ic_launcher, null)
    }
)
```

如果有使用加载图片场景，使用MiddlewareView在xml中替**IimageView**，因为这个类在两个版本的库中是有不同的实现的
```xml
<io.github.keep2iron.pineapple.MiddlewareView
	android:id="@+id/middleImageView"
	android:layout_width="match_parent"
	android:layout_height="match_parent" /> 
```

加载图片
```kotlin
ImageLoaderManager.getInstance().showImageView(middleImageView, url)
```

通过option加载图片，要小心的是在内部我会让前面init方法中传入的defaultImageLoaderOptions对象和当前加载的options进行合并，defaultImageLoaderOptions的属性会传入到新的options对象中，两者的属性的优先级在当前options中的会更高。
```kotlin
ImageLoaderManager.getInstance().showImageView(
    holder.binding.imageView, data[position]
) {
    isCircleImage = true
    scaleType = ImageLoaderOptions.ScaleType.FIT_XY
    placeHolder = null
    isLoadGif = true
}
```

上面在init方法中我们传入了isCircleImage来加载原型图片，但是当前的options优先级更高，因此最终isCircleImage = true

## ProGuard

No need......

## License

	Copyright 2019 Keep2iron.
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	    http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
