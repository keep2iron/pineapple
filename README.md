![](images/banner.png)

# Pineapple

[中文](README.zh-cn.md)

Glide version :![Release](https://api.bintray.com/packages/keep2iron/maven/pineapple-glide/images/download.svg) 

Fresco version: ![Release](https://api.bintray.com/packages/keep2iron/maven/pineapple-fresco/images/download.svg)

Build Status: ![BuildStatus](https://travis-ci.org/keep2iron/pineapple.svg?branch=master)

`Pineapple` is an imageLoader manger library.

- Base on kotlin.
- Glide and Fresco implements same interfaces.
- Switch Fresco or Glide is easy,just switch gradle dependencies,android change some code.
- Base on androidx

# Downloads

Glide version and fresco version can be installed one, both installations will have an error switch

#### Glide

```groovy
dependencies {
    //glide version
    implementation 'io.github.keep2iron:pineapple-glide:$latest_version'
    
    //Since pineapple does not integrate glide versions internally, it needs to integrate itself.
    implementation 'com.github.bumptech.glide:glide:$glide_latest_version'
    //kotlin should use kapt
    annotationProcessor 'com.github.bumptech.glide:compiler:$glide_latest_version'
}
```

> One thing to note is that due to the features of Glide v4, you need to use **GlideAppModule** to set some global features or properties, so I implemented a class myself **io.github.keep2iron.pineapple.GlideInitModule**
>
> If you use the properties such as setting the cache, you must inherit the class when you get the width and height of the image. Remember to call the parent class method when overriding the method.

```kotlin
@GlideModule
class GlideModule : GlideInitModule()
```



#### Fresco

````groovy
dependencies {
    //fresco version
    implementation 'io.github.keep2iron:pineapple-fresco:$latest_version'
   
    //Since pineapple does not integrate fresco versions internally, it needs to integrate itself.
    implementation 'com.facebook.fresco:fresco:2.0.0'
}
````



#### Use

Initialize the ImageLoaderManager as early as possible, the internal will automatically load fresco or glide, no external parameters are required.
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

If you are using a loaded image scene, use MiddlewareView to replace **IimageView** in xml, because this class has different implementations in the two versions of the library.
```xml
<io.github.keep2iron.pineapple.MiddlewareView
	android:id="@+id/middleImageView"
	android:layout_width="match_parent"
	android:layout_height="match_parent" /> 
```

Load Image by default options
```kotlin
ImageLoaderManager.getInstance().showImageView(middleImageView, url)
```

Loading the image through option, be careful, internally I will merge the defaultImageLoaderOptions object passed in the previous init method with the currently loaded options. The defaultImageLoaderOptions property will be passed to the new options object, and the properties of both will take precedence. The level will be higher in the current options.
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

In the init method above we passed the isCircleImage to load the prototype image, but the current options have higher priority, so the final isCircleImage = true

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
