package com.simbiri.equityjamii.ui.main_activity.news_page.featuring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
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
            /*
                        setUpObservers()
            */
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NewsPageFeaturingBinding.inflate(inflater, container, false)

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
                newsText.newsTag.contentEquals(
                    "featuring",
                    true
                )
            }

            val imageList = ArrayList<SlideModel>()
            val isYouTubeFile = { curFile: FileTitle ->
                curFile.fileTitle.contentEquals(
                    "youtube",
                    true
                )
            }

            val youTubeNews =
                curNewsFeaturing.fileTitleList.first { fileTitle -> isYouTubeFile(fileTitle) }

            lifecycleScope.launch {
                videoYt = YouTubeVids.getYoutubeVideo(requireContext(), youTubeNews.fileUri)

                if (videoYt != null) {

                    if (!videoYt?.videoId.contentEquals("null")) {

                        binding.apply {
                            moreAboutTextView.visibility = View.VISIBLE
                            youTubeCardView.visibility = View.VISIBLE
                            context?.let {
                                Glide.with(it).load(videoYt?.thumbnailUrl)
                                    .into(youTubeThumbNail)
                            }

                            youTubeCardView.setOnClickListener {
                                val youTubeDialogFrag = YouTubeDialogFrag.newInstance(videoYt!!)
                                val transaction =
                                    requireActivity().supportFragmentManager.beginTransaction()
                                youTubeDialogFrag.show(transaction, youTubeDialogFrag.tag)
                            }
                        }
                    }

                }
            }

            curNewsFeaturing.fileTitleList.filter { fileTitle -> !isYouTubeFile(fileTitle) }
                .forEach {
                    imageList.add(SlideModel(it.fileUri, it.fileTitle))
                }
            binding.apply {


                snapShotsImageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)

            }


        }
    }


}