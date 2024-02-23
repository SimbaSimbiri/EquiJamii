package com.simbiri.equityjamii.ui.main_activity.news_page.live_youtube

import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Video

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
    private lateinit var youTubePlayerView: YouTubePlayerView
    private var displayMetrics =  DisplayMetrics()
    private val width=  displayMetrics.widthPixels
    private var isFullscreen = false
    private var originalScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_you_tube_player, container, false)

        youTubePlayerView = view.findViewById(R.id.youtube_player_view)

        lifecycle.addObserver(youTubePlayerView)

        val videoItem = arguments?.getParcelable<Video>(YOU_TUBE_VID_ARGS)
        val VIDEO_ID = videoItem!!.videoId

        youTubePlayerView.addYouTubePlayerListener(object  : AbstractYouTubePlayerListener(){

            override fun onReady(youTubePlayer: YouTubePlayer) {
                // Set the video ID here
                youTubePlayer.loadVideo(VIDEO_ID,0.0F)
            }

        })



        return view

    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.dialog_you_tube_player)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0.5f)

        val windowManager = requireActivity().windowManager
        val display = windowManager.defaultDisplay
        val point = Point()
        display.getRealSize(point)
        val screenWidth = point.x
        val dialogHeight = screenWidth * 18 / 16
        dialog.window?.setLayout(screenWidth, dialogHeight)



        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        youTubePlayerView.release()
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(YouTubeDialogViewModel::class.java)
        // TODO: Use the ViewModel
    }

}