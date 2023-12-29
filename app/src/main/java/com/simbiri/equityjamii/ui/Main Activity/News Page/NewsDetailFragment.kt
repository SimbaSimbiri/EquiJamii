package com.simbiri.equityjamii.ui

import android.app.Dialog
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simbiri.equityjamii.R

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
    private lateinit var newsDetailImage: ImageView
    private lateinit var newsDetailText: TextView
    private lateinit var newsDetailAllNews : TextView
    private  var detailFragHeight : Int = DisplayMetrics().heightPixels - 50

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.news_detail_fragment)
        dialog.setCanceledOnTouchOutside(true)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
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

        val view = inflater.inflate(R.layout.news_detail_fragment, container, false)

        newsDetailImage = view.findViewById(R.id.newsDetailImage)
        newsDetailText = view.findViewById(R.id.newsDetailText)
        newsDetailAllNews =view.findViewById(R.id.newsDetailAllnews)

        var layoutParams = newsDetailImage.layoutParams

        val displayMetrics = DisplayMetrics()
        val windowManager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels
        layoutParams.width = screenWidth
        layoutParams.height = screenWidth * 9 / 16

        newsDetailImage.layoutParams = layoutParams


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newsItem = arguments?.getParcelable<NewsText>(ARG_NEWS_ITEM)
        newsItem?.let {
            Glide.with(this)
                .load(it.image)
                .into(newsDetailImage)

            newsDetailText.text = it.title
            newsDetailAllNews.text = it.allNews
        }


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewsDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
