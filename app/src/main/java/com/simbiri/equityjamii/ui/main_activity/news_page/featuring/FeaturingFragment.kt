package com.simbiri.equityjamii.ui.main_activity.news_page.featuring

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.Video
import com.simbiri.equityjamii.data.model.YouTubeVids
import com.simbiri.equityjamii.databinding.NewsPageFeaturingBinding
import com.simbiri.equityjamii.ui.main_activity.news_page.NewsViewModel
import com.simbiri.equityjamii.ui.main_activity.news_page.live_youtube.YouTubeDialogFrag
import kotlinx.coroutines.launch

class FeaturingFragment : Fragment() {

    companion object {
        fun newInstance() = FeaturingFragment()
    }

    private val viewModel: NewsViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private lateinit var binding: NewsPageFeaturingBinding
    private var canPublishEdit = false
    private var videoYt: Video? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (AuthUtils.getCurrentUserId() != null) {

            setUpObservers()

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        binding = NewsPageFeaturingBinding.inflate(layoutInflater)

        val layoutParams = binding.snapShotsImageSlider.layoutParams
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenheight = displayMetrics.heightPixels
        layoutParams.height = screenheight * 3 / 5

        binding.snapShotsImageSlider.layoutParams = layoutParams

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (AuthUtils.getCurrentUserId() != null) {

            AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()!!) { currentPerson ->
                canPublishEdit = currentPerson?.role?.contentEquals("journalist") == true

            }
        }



        return binding.root
    }

    private fun setUpObservers() {
        viewModel.newsList.observe(viewLifecycleOwner) { allNewsInstances ->

            val curNewsFeaturing = allNewsInstances.first { newsText ->
                newsText.newsName.contentEquals(
                    "featuring",
                    true
                )
            }

            val imageList = ArrayList<SlideModel>()
            val isYouTubeFile = { curFile: FileTitle ->
                curFile.fileTitle.contentEquals(
                    "youTube",
                    true
                )
            }

            val youTubeNews =
                curNewsFeaturing.fileTitleList.filter(isYouTubeFile).first()

            lifecycleScope.launch {
                videoYt = YouTubeVids.getYoutubeVideo(requireContext(), youTubeNews.fileUri)
                Log.i("VideoYT", videoYt.toString())
                videoYt = Video("Title", "url", youTubeNews.fileUri)

                binding.apply {
                    moreAboutTextView.visibility = View.VISIBLE
                    youTubeCardView.visibility = View.VISIBLE
                    context?.let {
                        Glide.with(it).load(videoYt?.thumbnailUrl)
                            .into(youTubeThumbNail)
                            .onLoadFailed(resources.getDrawable(R.drawable.equityjamiibackground))
                    }

                    youTubeCardView.setOnClickListener {
                        val youTubeDialogFrag = YouTubeDialogFrag.newInstance(videoYt!!)
                        val transaction =
                            requireActivity().supportFragmentManager.beginTransaction()
                        youTubeDialogFrag.show(transaction, youTubeDialogFrag.tag)
                    }
                }

            }


            curNewsFeaturing.fileTitleList.filter { fileTitle -> !isYouTubeFile(fileTitle) }
                .forEach {
                    imageList.add(SlideModel(it.fileUri, it.fileTitle))
                }
            binding.apply {
                snapShotsTv.text = "Snapshots of ${curNewsFeaturing.title}"
                titleMagicText.text = "About ${curNewsFeaturing.title}"
                moreAboutTextView.text = "A peek into the life of ${curNewsFeaturing.title}"
                tapTextView.text = "Tap card to reveal more about ${curNewsFeaturing.title}"
                magicCardView.setOnClickListener {

                }
                snapShotsImageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)

                val parts = curNewsFeaturing.allNews.split("\n\n")
                contentMagicText.text = parts.firstOrNull() ?: ""


                magicCardView.setOnClickListener {

                    snapShotsImageSlider.startSliding()
                    Handler().postDelayed({ snapShotsImageSlider.stopSliding() }, 1500)

                    val slideOut = ObjectAnimator.ofFloat(
                        magicCardView,
                        "translationX",
                        0f,
                        magicCardView.width.toFloat()
                    )
                    slideOut.duration = 700

                    val slideIn = ObjectAnimator.ofFloat(
                        magicCardView,
                        "translationX",
                        -magicCardView.width.toFloat(),
                        0f
                    )
                    slideIn.duration = 700
                    slideOut.addListener(object : AnimatorListenerAdapter() {

                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            val currentText = contentMagicText.text.toString()
                            val currentIndex = parts.indexOf(currentText)
                            val nextIndex = (currentIndex + 1) % parts.size
                            contentMagicText.text = parts[nextIndex]
                            slideIn.start()
                        }
                    })

                    // Combine the animations
                    val animatorSet = AnimatorSet()
                    animatorSet.playSequentially(slideOut, slideIn)
                    animatorSet.interpolator = AccelerateDecelerateInterpolator()
                    animatorSet.start()
                }


            }


        }
    }


}