/*
 * Copyright (c) 2022 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.company.android.whatsthememe

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jakewharton.espresso.OkHttp3IdlingResource
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

  private val mockWebServer = MockWebServer()
  private lateinit var okHttp3IdlingResource: OkHttp3IdlingResource

  @Before
  fun setup() {
    okHttp3IdlingResource = OkHttp3IdlingResource.create(
        "okhttp",
        OkHttpProvider.getOkHttpClient()
    )
    IdlingRegistry.getInstance().register(
        okHttp3IdlingResource
    )

    mockWebServer.start(8080)
  }

  @After
  fun teardown() {
    mockWebServer.shutdown()
    IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
  }

  @Test
  fun testSuccessfulResponse() {
    mockWebServer.dispatcher = object : Dispatcher() {
      override fun dispatch(request: RecordedRequest): MockResponse {
        return MockResponse()
            .setResponseCode(200)
            .setBody(FileReader.readStringFromFile("success_response.json"))
      }
    }
    val scenario = launchActivity<MainActivity>()

    onView(withId(R.id.progress_bar))
        .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    onView(withId(R.id.meme_recyclerview))
        .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    onView(withId(R.id.textview))
        .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

    scenario.close()
  }

  @Test
  fun testFailedResponse() {
    mockWebServer.dispatcher = object : Dispatcher() {
      override fun dispatch(request: RecordedRequest): MockResponse {
        return MockResponse()
            .setResponseCode(200)
            .setBody(FileReader.readStringFromFile("success_response.json"))
            .throttleBody(1024, 5, TimeUnit.SECONDS)
      }
    }

    val scenario = launchActivity<MainActivity>()

    onView(withId(R.id.progress_bar))
        .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    onView(withId(R.id.meme_recyclerview))
        .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    onView(withId(R.id.textview))
        .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    onView(withId(R.id.textview))
        .check(matches(withText(R.string.something_went_wrong)))

    scenario.close()
  }

}
