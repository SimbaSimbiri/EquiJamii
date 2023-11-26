package com.simbiri.equityjamii.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.simbiri.equityjamii.R

class secondActivity : AppCompatActivity() {

    private lateinit var webViewTube: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity)

        this@secondActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        webViewTube = findViewById(R.id.YouTubeWebView)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels

        val layoutParams = webViewTube.layoutParams
        layoutParams.width = screenWidth
        layoutParams.height = screenWidth*9/16
        webViewTube.layoutParams = layoutParams


        val videoId = intent.getStringExtra(VIDEO_CONSTANT)

        val html =
            "<html><body><iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/$videoId?autoplay=\"1\" style=\"border: 0;\"></iframe></body></html>"

        webViewTube.settings.javaScriptEnabled = true
        webViewTube.settings.loadsImagesAutomatically = true
        webViewTube.settings.supportZoom()

        webViewTube.loadData(html, "text/html", "utf-8")
        webViewTube.webChromeClient = WebChromeClient()

        Log.d("VideoInfo", videoId!!)



    }


}
