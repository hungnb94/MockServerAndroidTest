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

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.company.android.whatsthememe.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Main Screen
 */
class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  private lateinit var memeAdapter: MemeAdapter

  private lateinit var mainViewModel: MainViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    // Switch to AppTheme for displaying the activity
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    mainViewModel = ViewModelProvider(this, MainViewModelFactory(
        MemeRepository(MemeApi.getInstance(application))
    )).get(MainViewModel::class.java)

    // Your code
    memeAdapter = MemeAdapter()
    binding.memeRecyclerview.adapter = memeAdapter

    mainViewModel.uiState.observe(this) { state ->
      when(state) {
        is MainUiState.DataLoaded -> showMemeList(state.memes)
        MainUiState.EmptyData -> showEmptyDataState()
        MainUiState.LoadFailed -> showErrorState()
        MainUiState.Loading -> showLoading()
      }
    }
  }

  private fun showLoading() {
    binding.progressBar.visibility = View.VISIBLE
  }

  private fun showEmptyDataState() {
    binding.memeRecyclerview.visibility = View.GONE
    binding.progressBar.visibility = View.GONE
    binding.textview.visibility = View.VISIBLE
    binding.textview.text = getString(R.string.there_seems_to_be_no_data)
  }

  private fun showMemeList(showMemeList: List<MemeModel>) {
    memeAdapter.setMemeList(showMemeList)
    binding.memeRecyclerview.visibility = View.VISIBLE
    binding.progressBar.visibility = View.GONE
    binding.textview.visibility = View.GONE
  }

  private fun showErrorState() {
    binding.memeRecyclerview.visibility = View.GONE
    binding.progressBar.visibility = View.GONE
    binding.textview.visibility = View.VISIBLE
    binding.textview.text = getString(R.string.something_went_wrong)
  }

}
