package io.github.keep2iron.pineapple

import org.junit.Test

import org.junit.Assert.assertEquals

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, (2 + 2).toLong())

    val defaultImageLoaderOptions1: (ImageLoaderOptions.() -> Unit) = {
    }

    val defaultImageLoaderOptions2: (ImageLoaderOptions.() -> Unit) = {
    }

    println("o1 : ${ImageLoaderOptions.newOption().apply(defaultImageLoaderOptions1).hashCode()} o2 : ${ImageLoaderOptions.newOption().apply(defaultImageLoaderOptions2).hashCode()}")
  }
}