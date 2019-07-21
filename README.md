![](images/banner.png)

# Pineapple
![Release](https://api.bintray.com/packages/keep2iron/maven/pineapple/images/download.svg) ![BuildStatus](https://travis-ci.org/keep2iron/pineapple.svg?branch=master)

Pineapple is a image loader library.
- Base on kotlin
- Default by fresco lib

# Download

#### install

```groovy

dependencies {
    implementation 'io.github.keep2iron:pineapple:$latest_version'
    
    implementation deps.fresco
    // if you want to support gif add this
    implementation deps.fresco.fresco_gif
}
```

#### Simple usage snippet
Init ImageLoaderManger as eraly as possible
```kotlin
ImageLoaderManager.init(
    application,
    ImageLoaderConfig(
        applicationContext,
        maxCacheCount = 300,
        maxCacheSize = (400 * ByteConstants.MB).toLong()
    ),
    defaultImageLoaderOptions = {
        isCircleImage = true
        scaleType = ImageLoaderOptions.ScaleType.FIT_CENTER
        placeHolderRes = R.mipmap.ic_launcher
        placeHolder = ResourcesCompat.getDrawable(resources, R.mipmap.ic_launcher, null)
    }
)
```

Use MiddlewareView in xml
```xml
<io.github.keep2iron.pineapple.MiddlewareView
	android:id="@+id/middleImageView"
	android:layout_width="match_parent"
	android:layout_height="match_parent" /> 
```

Load an image url
```kotlin
ImageLoaderManager.getInstance().showImageView(middleImageView, url)
```

Load an image url by option
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
