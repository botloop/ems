package com.ems

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.AnimatedImageDecoder
import coil.decode.GifDecoder
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EmsApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .components {
                if (SDK_INT >= 28) add(AnimatedImageDecoder.Factory())
                else add(GifDecoder.Factory())
            }
            .build()
}
