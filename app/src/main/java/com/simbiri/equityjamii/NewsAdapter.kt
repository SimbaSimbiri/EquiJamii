package com.simbiri.equityjamii

data class NewsText(val imageId: Int, val textLine: String, val isBookmarked: Boolean)

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

    private val textLineList =  arrayOf(
        "EU officials grace Equity Group Holdings",
        "Equity maintains outstanding financial perfomance",
        "Wings to Fly Scholars fly abroad",
        "Equity Group  registers  21% growth in total assets",
        "Elimu Scholarship Program Day",
        "Stanford MBA influential visit to the bank",
        "Aspiring app developer creates a virtual home for Equity Bank"

    )

    var newsTextList : ArrayList<NewsText>? =  null
    get() {
        if (field!= null)

            return  field
        field =  ArrayList()

        for (imagePosition in imageList.indices){
            val imageId = imageList[imagePosition]
            val textLine =  textLineList[imagePosition]
            val newsText =  NewsText(imageId, textLine, false)
            field!!.add(newsText)
        }

        return field

    }

    var isBookMarkedBool : MutableList<NewsText> = mutableListOf()


}