package com.simbiri.equityjamii.ui

import androidx.lifecycle.ViewModel
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.simbiri.equityjamii.R

class ForYouViewModel : ViewModel() {

     /*   val newsListRecommended = NewsToday.newsTextList
        val newsListBkMarked = NewsToday.isBookMarkedNews
        val newsAddedList = NewsToday.listImageIds*/

        val imageList  = ArrayList<SlideModel>()

        fun addtoImageList(){

                imageList.add(SlideModel(R.drawable.hackathonequity, "Meet the winners of Equity Hackathon 2nd Edition", ScaleTypes.FIT))
                imageList.add(SlideModel(R.drawable.bankacquisition, "Equity Group  acquires 91% of COGEBANQUE  equity stake", ScaleTypes.FIT))
                imageList.add(SlideModel(R.drawable.cewicequity, "Commonwealth Enterprise partners with Equity", ScaleTypes.FIT))
                imageList.add(SlideModel(R.drawable.americanecommerce, "Why is Africa The Future of the world?", ScaleTypes.FIT))

        }



}