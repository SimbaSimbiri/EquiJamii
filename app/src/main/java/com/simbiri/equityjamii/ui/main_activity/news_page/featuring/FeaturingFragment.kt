package com.simbiri.equityjamii.ui.main_activity.news_page.featuring

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
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
import com.simbiri.equityjamii.data.objects.AuthUtils
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.NewsText
import com.simbiri.equityjamii.data.model.Video
import com.simbiri.equityjamii.data.objects.YouTubeVids
import com.simbiri.equityjamii.databinding.NewsPageFeaturingBinding
import com.simbiri.equityjamii.ui.main_activity.news_page.AddNewsFragment
import com.simbiri.equityjamii.ui.main_activity.news_page.NewsViewModel
import com.simbiri.equityjamii.ui.main_activity.news_page.live_youtube.YouTubeDialogFrag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        val layoutParamsSlider = binding.snapShotsImageSlider.layoutParams
        val layoutParamsCardYT = binding.youTubeCardView.layoutParams

        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenheight = displayMetrics.heightPixels
        layoutParamsSlider.height = screenheight *1 / 2
        layoutParamsCardYT.height = screenheight * 1 / 3

        binding.snapShotsImageSlider.layoutParams = layoutParamsSlider
        binding.youTubeCardView.layoutParams = layoutParamsCardYT
        binding.contentLoadingProgressBar.visibility = View.VISIBLE

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
            if (allNewsInstances.isNotEmpty()){
                binding.contentLoadingProgressBar.visibility = View.GONE
            }

            val curNewsFeaturing : NewsText = allNewsInstances.first { newsText ->
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

            val hasYouTubeVid: Boolean = curNewsFeaturing.fileTitleList.any { isYouTubeFile(it) }
            if (hasYouTubeVid) {

                val youTubeNews =
                    curNewsFeaturing.fileTitleList.first(isYouTubeFile)

                lifecycleScope.launch {
                    val video = withContext(Dispatchers.IO) {
                        YouTubeVids.getVideoDetails(requireContext(), youTubeNews.fileUri)
                    }

                    context?.let {
                        Glide.with(it).load(video?.thumbnailUrl).centerCrop()
                            .into(binding.youTubeThumbNail)
                    }

                    binding.apply {
                        moreAboutTextView.visibility = View.VISIBLE
                        youTubeCardView.visibility = View.VISIBLE

                        youTubeCardView.setOnClickListener {
                            val youTubeDialogFrag = YouTubeDialogFrag.newInstance(video!!)
                            val transaction =
                                requireActivity().supportFragmentManager.beginTransaction()
                            youTubeDialogFrag.show(transaction, youTubeDialogFrag.tag)
                        }

                    }
                }

            }


            curNewsFeaturing.fileTitleList.sortBy{fileTitle -> fileTitle.position }

            curNewsFeaturing.fileTitleList.filter { !isYouTubeFile(it) }
                .forEach {
                    imageList.add(SlideModel(it.fileUri, it.fileTitle))
                }
            binding.apply {
                snapShotsTv.text = "Today's featuring snapshots"
                titleMagicText.text = "Who is ${curNewsFeaturing.title}?"
                moreAboutTextView.text = "A better peek into the life of ${curNewsFeaturing.title}"

                if (canPublishEdit) {
                    editFeaturing.visibility = View.VISIBLE
                    editFeaturing.setOnClickListener {
                        val newsFrag = AddNewsFragment.newInstance(curNewsFeaturing)
                        val transaction =
                            requireActivity().supportFragmentManager.beginTransaction()

                        newsFrag.show(transaction, newsFrag.tag)
                    }
                }

                val parts = curNewsFeaturing.allNews.split("\n\n\n")
                contentMagicText.text = parts.firstOrNull() ?: ""
                titleMagicText.visibility = View.VISIBLE
                snapShotsImageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)

                nextSlide.setOnClickListener {
                    snapShotsImageSlider.startSliding()
                    animateCard(parts)
                }

            }
        }
    }

    private fun animateCard(parts: List<String>) {

        binding.apply {

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
                    Handler().postDelayed({ snapShotsImageSlider.stopSliding() }, 1000)

                }
            })

            val animatorSet = AnimatorSet()
            animatorSet.playSequentially(slideOut, slideIn)
            animatorSet.interpolator = AccelerateDecelerateInterpolator()
            animatorSet.start()
        }
    }


}