package com.simbiri.equityjamii.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simbiri.equityjamii.R

class YouTubeDialogFrag : DialogFragment() {

    companion object {
        private const val YOU_TUBE_VID_ARGS = "YOU_TUBE_VID_ID"
        fun newInstance(video: Video): YouTubeDialogFrag {
            val fragment = YouTubeDialogFrag()
            val args = Bundle()
            args.putParcelable(YOU_TUBE_VID_ARGS, video)
            fragment.arguments = args

            return fragment
        }

    }


    private lateinit var viewModel: YouTubeDialogViewModel
    private lateinit var webViewTube: WebView
    private var webChromeClient = WebChromeClient()
    private var isFullscreen = false
    private var originalScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.you_tube_dialog_frag, container, false)

        webViewTube = view.findViewById(R.id.YouTubeWebView)


        val videoItem = arguments?.getParcelable<Video>(YOU_TUBE_VID_ARGS)

        val html =
            "<html><body><iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/${videoItem!!.videoId}\" frameborder=\"0\" allowfullscreen></iframe></body></html>"

        webViewTube.loadData(html, "text/html", "utf-8")
        webViewTube.webChromeClient = webChromeClient

        webViewTube.settings.javaScriptEnabled = true

        return view

    }


    private fun ifRamestr(video: Video) : String {

        val iframeAPIJsString =  "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <body>\n" +
                "    <div id=\"player\"></div>\n" +
                "\n" +
                "    <script>\n" +
                "      var tag = document.createElement('script');\n" +
                "\n" +
                "      tag.src = \"https://www.youtube.com/iframe_api\";\n" +
                "      var firstScriptTag = document.getElementsByTagName('script')[0];\n" +
                "      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);\n" +
                "\n" +
                "      var player;\n" +
                "      function onYouTubeIframeAPIReady() {\n" +
                "        player = new YT.Player('player', {\n" +
                "          height: '200',\n" +
                "          width: '320',\n" +
                "          videoId: '${video.videoId}',\n" +
                "          playerVars: {\n" +
                "            'playsinline': 1\n" +
                "          },\n" +
                "          events: {\n" +
                "            'onReady': onPlayerReady,\n" +
                "            'onStateChange': onPlayerStateChange\n" +
                "          }\n" +
                "        });\n" +
                "      }\n" +
                "\n" +
                "      function onPlayerReady(event) {\n" +
                "        event.target.playVideo();\n" +
                "      }\n" +
                "\n" +
                "      var done = false;\n" +
                "      function onPlayerStateChange(event) {\n" +
                "        if (event.data == YT.PlayerState.PLAYING && !done) {\n" +
                "          setTimeout(stopVideo, 6000);\n" +
                "          done = true;\n" +
                "        }\n" +
                "      }\n" +
                "      function stopVideo() {\n" +
                "        player.stopVideo();\n" +
                "      }\n" +
                "    </script>\n" +
                "  </body>\n" +
                "</html>"

        Log.d("Our iframe string", iframeAPIJsString)

        return iframeAPIJsString
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.you_tube_dialog_frag)
        dialog.setCanceledOnTouchOutside(false)

        /*dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.peekHeight = resources.getDimensionPixelSize(R.dimen.peek_height)
                behavior.isDraggable = true
                behavior.isHideable = true
            }
        }*/

        return dialog
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(YouTubeDialogViewModel::class.java)
        // TODO: Use the ViewModel
    }

}