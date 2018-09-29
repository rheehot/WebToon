package com.pluu.webtoon.ui.intro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pluu.webtoon.R
import com.pluu.webtoon.ui.weekly.MainActivity
import com.pluu.webtoon.utils.observeNonNull
import com.pluu.webtoon.utils.viewModelProvider
import kotlinx.android.synthetic.main.activity_intro.*

/**
 * 인트로 화면 Activity
 * Created by pluu on 2017-05-07.
 */
class IntroActivity : AppCompatActivity() {
    private val TAG = IntroActivity::class.java.simpleName

    private lateinit var viewModel: IntroViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        viewModel = viewModelProvider()
        viewModel.observe.observeNonNull(this, ::nextView)
    }

    private fun nextView() {
        Log.i(TAG, "Login Process Complete")
        tvMsg.setText(R.string.msg_intro_complete)
        progressBar.visibility = View.INVISIBLE

        startActivity(Intent(this@IntroActivity, MainActivity::class.java))
        finish()
    }
}