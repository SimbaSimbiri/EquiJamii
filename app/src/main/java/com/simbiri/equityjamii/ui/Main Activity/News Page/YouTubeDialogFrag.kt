package com.simbiri.equityjamii.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simbiri.equityjamii.R

class YouTubeDialogFrag : BottomSheetDialogFragment() {

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
    private lateinit var videoId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.you_tube_dialog_frag, container, false)

        webViewTube = view.findViewById(R.id.YouTubeWebView)

        val videoItem = arguments?.getParcelable<Video>(YOU_TUBE_VID_ARGS)

         videoId = videoItem!!.videoId

        webViewTube.settings.javaScriptEnabled

        webViewTube.loadData(videoIdToIframeStr(), "text/html", "utf-8")
        Log.d("webview perfomed in OnCreate", "Perfomed web  view")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR


    }

    private fun videoIdToIframeStr() : String {

        val iframeAPIJsString = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <body>\n" +
                "    <!-- 1. The <iframe> (and video player) will replace this <div> tag. -->\n" +
                "    <div id=\"player\"></div>\n" +
                "\n" +
                "    <script>\n" +
                "      // 2. This code loads the IFrame Player API code asynchronously.\n" +
                "      var tag = document.createElement('script');\n" +
                "\n" +
                "      tag.src = \"https://www.youtube.com/iframe_api\";\n" +
                "      var firstScriptTag = document.getElementsByTagName('script')[0];\n" +
                "      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);\n" +
                "\n" +
                "      // 3. This function creates an <iframe> (and YouTube player)\n" +
                "      //    after the API code downloads.\n" +
                "      var player;\n" +
                "      function onYouTubeIframeAPIReady() {\n" +
                "        player = new YT.Player('player', {\n" +
                "          height: '390',\n" +
                "          width: '640',\n" +
                "          videoId: '${videoId}',\n" +
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
                "      // 4. The API will call this function when the video player is ready.\n" +
                "      function onPlayerReady(event) {\n" +
                "        event.target.playVideo();\n" +
                "      }\n" +
                "\n" +
                "      // 5. The API calls this function when the player's state changes.\n" +
                "      //    The function indicates that when playing a video (state=1),\n" +
                "      //    the player should play for six seconds and then stop.\n" +
                "      var done = false;\n" +
                "      function onPlayerStateChange(event) {\n" +
                "        if (event.data == YT.PlayerState.PLAYING && !done) {\n" +
                "          setTimeout(stopVideo, 13000);\n" +
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

    override fun onDismiss(dialog: DialogInterface) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onDismiss(dialog)

    }

    override fun onCancel(dialog: DialogInterface) {

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCancel(dialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.you_tube_dialog_frag)
        dialog.setCanceledOnTouchOutside(true)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.peekHeight = resources.getDimensionPixelSize(R.dimen.peek_height)
                behavior.isDraggable = true
                behavior.isHideable = true
            }
        }

        webViewTube = dialog.findViewById(R.id.YouTubeWebView)

        val videoItem = arguments?.getParcelable<Video>(YOU_TUBE_VID_ARGS)

        videoId = videoItem!!.videoId

        webViewTube.settings.javaScriptEnabled

        webViewTube.loadData(videoIdToIframeStr(), "text/html", "utf-8")
        Log.d("webview perfomed in OnCreateDialog", "Perfomed web  view")



        return dialog
    }

    override fun onResume() {
        super.onResume()

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(YouTubeDialogViewModel::class.java)
        // TODO: Use the ViewModel
    }

}