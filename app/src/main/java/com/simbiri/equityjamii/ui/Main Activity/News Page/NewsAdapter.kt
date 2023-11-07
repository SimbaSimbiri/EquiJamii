package com.simbiri.equityjamii.ui

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.os.Parcel
import android.os.Parcelable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R

data class NewsText(val imageId: Int, val textHeadLine: String, val textNewsAll : String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString()?:""

    ) {
    }

    override fun describeContents(): Int {
    return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(imageId)
        dest.writeString(textHeadLine)
    }

    companion object CREATOR : Parcelable.Creator<NewsText> {
        override fun createFromParcel(parcel: Parcel): NewsText {
            return NewsText(parcel)
        }

        override fun newArray(size: Int): Array<NewsText?> {
            return arrayOfNulls(size)
        }
    }
}

object NewsToday {

    private val imageList = arrayOf(

        R.drawable.europeanu_talks_image,
        R.drawable.equity_assurance,
        R.drawable.wings_to_fly_girls,
        R.drawable.jamesshakinghands,
        R.drawable.elimu_talk,
        R.drawable.stanfordmba,
        R.drawable.good_profile
    )

    private val textLineList = arrayOf(
        "EU officials grace Equity Group Holdings",
        "Equity maintains outstanding financial perfomance",
        "Wings to Fly Scholars fly abroad",
        "Equity Group  registers  21% growth in total assets",
        "Elimu Scholarship Program Day",
        "Stanford MBA influential visit to the bank",
        "Behind the  scenes of EquityJamii"

    )

    private const val textNewsAllList = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. Etiam a odio bibendum, blandit odio vitae, rhoncus nulla. Nam luctus tortor vel nibh finibus, eu tempor nunc tempus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla facilisi. Integer condimentum eros ac tincidunt pellentesque. Etiam gravida, turpis nec pellentesque congue, eros felis vehicula eros, non sagittis turpis tellus nec neque. Vivamus mattis arcu justo, eu sollicitudin velit scelerisque eu. Mauris cursus, velit ac dapibus porttitor, velit elit sodales nunc, vitae euismod justo nisi at mauris. Nunc sit amet odio nec metus consectetur hendrerit.\n" +
            "\n" +
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam sit amet euismod purus. Suspendisse potenti. Integer nec aliquet ipsum. Proin eu nisl vitae dui rutrum interdum. Suspendisse potenti. Ut scelerisque, odio ac facilisis feugiat, purus tellus placerat mauris, eget facilisis urna mauris id purus. Phasellus scelerisque odio eu ligula dapibus, vitae feugiat tellus facilisis. Aenean a venenatis lorem. Etiam vehicula, nisi a blandit fringilla, turpis nulla auctor odio, sit amet accumsan ex mi sit amet nulla. Aenean at eros luctus, feugiat lectus eu, tincidunt odio. Mauris semper elit at odio eleifend, at convallis lacus posuere. Suspendisse eu diam dui.\n" +
            "\n" +
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. Etiam a odio bibendum, blandit odio vitae, rhoncus nulla. Nam luctus tortor vel nibh finibus, eu tempor nunc tempus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla facilisi. Integer condimentum eros ac tincidunt pellentesque. Etiam gravida, turpis nec pellentesque congue, eros felis vehicula eros, non sagittis turpis tellus nec neque. Vivamus mattis arcu justo, eu sollicitudin velit scelerisque eu. Mauris cursus, velit ac dapibus porttitor, velit elit sodales nunc, vitae euismod justo nisi at mauris. Nunc sit amet odio nec metus consectetur hendrerit.\n" +
            "\n" +
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam sit amet euismod purus. Suspendisse potenti. Integer nec aliquet ipsum. Proin eu nisl vitae dui rutrum interdum. Suspendisse potenti. Ut scelerisque, odio ac facilisis feugiat, purus tellus placerat mauris, eget facilisis urna mauris id purus. Phasellus scelerisque odio eu ligula dapibus, vitae feugiat tellus facilisis. Aenean a venenatis lorem. Etiam vehicula, nisi a blandit fringilla, turpis nulla auctor odio, sit amet accumsan ex mi sit amet nulla. Aenean at eros luctus, feugiat lectus eu, tincidunt odio. Mauris semper elit at odio eleifend, at convallis lacus posuere. Suspendisse eu diam dui.\n" +
            "\n" +
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec lacinia metus nec elit vulputate, vel ultrices dolor iaculis. Sed et ex in purus dignissim consequat. Nam id arcu eget erat elementum pulvinar. Maecenas id tortor et dui posuere mollis non eu justo. Donec rutrum nunc vel quam suscipit pellentesque. Cras ullamcorper feugiat fringilla. Suspendisse maximus justo ligula, id bibendum mauris ultrices vel. Sed malesuada auctor dui, id aliquet tellus pellentesque quis."

    var newsTextList: ArrayList<NewsText>? = null
        get() {
            if (field != null)

                return field
            field = ArrayList()

            for (imagePosition in imageList.indices) {
                val imageId = imageList[imagePosition]
                val textLine = textLineList[imagePosition]
                val newsText = NewsText(imageId, textLine, textNewsAllList)
                field!!.add(newsText)
            }

            return field

        }

    var isBookMarkedNews: ArrayList<NewsText> = arrayListOf()

    var listImageIds: ArrayList<Int> = arrayListOf()

}


class NewsAdapter(var context: Context, var newsTextList: List<NewsText>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private var positionItem: Int = 1
        private var currentNewsTextItem: NewsText? = null

        private var textNewsHeadlineView: TextView = itemView.findViewById(R.id.textViewHeadline)
        private var imageNewsHeadlineView: ImageView = itemView.findViewById(R.id.imageViewNews)



        fun setDataToItem(newsText: NewsText, position: Int) {
            this.positionItem = position
            this.currentNewsTextItem = newsText
            var layoutParams = imageNewsHeadlineView.layoutParams

            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            layoutParams.width = screenWidth
            layoutParams.height = screenWidth * 9/16

            imageNewsHeadlineView.layoutParams = layoutParams

            textNewsHeadlineView.text = newsText.textHeadLine
            imageNewsHeadlineView.setImageResource(newsText.imageId)
        }

        fun setOnclickListeners() {
            itemView.setOnClickListener(this@NewsViewHolder)
        }


        override fun onClick(view: View?) {

            val bottomFragmentDetail = NewsDetailFragment.newInstance(currentNewsTextItem!!)
            val transaction = (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            bottomFragmentDetail.show(transaction, bottomFragmentDetail.tag)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.newsitemsample, parent, false)

        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(newsViewholder: NewsViewHolder, position: Int) {

        val newsTextInstance = newsTextList[position]

        newsViewholder.setDataToItem(newsTextInstance, position)
        newsViewholder.setOnclickListeners()

    }

    override fun getItemCount(): Int {

        return newsTextList.size
    }
}









