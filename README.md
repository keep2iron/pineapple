![](/images/banner.png)

# Pineapple
[![Release](https://jitpack.io/v/keep2iron/pineapple.svg)](https://jitpack.io/v/#keep2iron/pineapple) ![BuildStatus](https://travis-ci.org/keep2iron/pineapple.svg?branch=master)

Pineapple is a image loader library.
- Base on kotlin
- Default by fresco lib

# Download

gradle:
```groovy
repositories {
	maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.keep2iron:pineapple:$latest_version'
    
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
        maxCacheSize = (400 * ByteConstants.MB).toLong(),
		cacheDirName = "cache_images",
		cacheDirPath =  context.cacheDir
    ),
    defaultImageLoaderOptions = ImageLoaderOptions(
        isCircleImage = true,
        scaleType = ImageLoaderOptions.ScaleType.FIT_CENTER,
        placeHolderRes = R.drawable.ic_launcher_background
    )
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
    middleImageView, url
) {
    scaleType = ImageLoaderOptions.ScaleType.CENTER_CROP
    placeHolderRes = R.color.colorAccent
    placeHolder = null
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
