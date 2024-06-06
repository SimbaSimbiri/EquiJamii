package com.simbiri.equityjamii.ui.main_activity.news_page

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.AnimationTypes
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.NewsText

class NewsDetailFragment : BottomSheetDialogFragment() {

    companion object {

        private const val ARG_NEWS_ITEM = "news_item"

        fun newInstance(newsItem: NewsText): NewsDetailFragment {
            val fragment = NewsDetailFragment()
            val args = Bundle()
            args.putParcelable(ARG_NEWS_ITEM, newsItem)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: NewsDetailViewModel
    private lateinit var ImageSliderNews: ImageSlider
    private lateinit var newsDetailText: TextView
    private lateinit var newsDetailAllNews: TextView
    private lateinit var authorSpecialTv: TextView
    private lateinit var newsTitleTv : TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.dialog_news_detail)
        dialog.setCanceledOnTouchOutside(true)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = true
                behavior.isHideable = true

            }
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.dialog_news_detail, container, false)

        ImageSliderNews = view.findViewById(R.id.imageSliderNews)
        newsDetailAllNews = view.findViewById(R.id.newsDetailAllnews)
        authorSpecialTv = view.findViewById(R.id.authorTextView)
        newsTitleTv = view.findViewById(R.id.newsTitle)

        var layoutParams = ImageSliderNews.layoutParams

        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels
        layoutParams.width = screenWidth
        layoutParams.height = screenWidth * 9 / 16

        ImageSliderNews.layoutParams = layoutParams


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        arguments?.getParcelable<NewsText>(ARG_NEWS_ITEM)?.let { newstText ->

            val imageList = ArrayList<SlideModel>()
            newstText.imageDescList.forEach { imageDesc ->
                imageList.add(SlideModel(imageDesc.image, imageDesc.description))
            }

            ImageSliderNews.setImageList(imageList, ScaleTypes.CENTER_CROP)
            authorSpecialTv.text = newstText.newsName + " by " + newstText.author
            newsTitleTv.text = newstText.title
            ImageSliderNews.setSlideAnimation(AnimationTypes.DEPTH_SLIDE)

            ImageSliderNews.setOnClickListener {
                ImageSliderNews.stopSliding()
            }

            newsDetailAllNews.text = newstText.allNews
        }


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewsDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
