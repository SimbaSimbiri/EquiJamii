package com.simbiri.equityjamii

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toolbar
import androidx.appcompat.widget.ActionMenuView
import androidx.navigation.findNavController
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class entre_page : AppCompatActivity() {

    lateinit var image_Slider : ImageSlider
    lateinit var tool_bar : androidx.appcompat.widget.Toolbar
    lateinit var bottom_bar: BottomAppBar
    lateinit var fab: FloatingActionButton
    lateinit var bottom_nav: BottomNavigationView
    lateinit var actionMenuView: ActionMenuView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entre_page)




       /* val image_list =  ArrayList<SlideModel>()

        image_list.add(SlideModel(R.drawable.europeanu_talks_image , "EU officials grace Equity Group Holdings", ScaleTypes.FIT))
        image_list.add(SlideModel(  R.drawable.jamesshakinghands, "Equity Group  registers  21% growth in total assets", ScaleTypes.FIT))
        image_list.add(SlideModel(R.drawable.stanfordmba , "Stanford MBA influential visit to the bank", ScaleTypes.FIT))
        image_list.add(SlideModel(R.drawable.elimu_talk, "Elimu Scholarship Program Day", ScaleTypes.FIT))
        image_list.add(SlideModel(R.drawable.equity_assurance , "Equity maintains outstanding financial perfomance", ScaleTypes.FIT))
        image_list.add(SlideModel(R.drawable.wings_to_fly_girls , "Wings to Fly Scholars fly abroad", ScaleTypes.FIT))


        image_Slider = findViewById<ImageSlider>(R.id.image_slider)
        image_Slider.setImageList(image_list)



        bottom_bar =  findViewById(R.id.bottom_app_bar)
        bottom_nav = findViewById(R.id.bottom_nav_view)
        fab = findViewById(R.id.floating_action_button)
*/
        /*actionMenuView = findViewById(R.id.additional_menu)

        menuInflater.inflate(R.menu.bottom_menu, actionMenuView.menu)
        actionMenuView.setBackgroundColor(titleColor)*/





    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.top_menu, menu)

        return true
    }

   /* override fun onConfigurationChanged(newConfig: Configuration) {

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.activity_entre_page_landscape)

            val image_list =  ArrayList<SlideModel>()

            image_list.add(SlideModel(R.drawable.europeanu_talks_image , "EU officials grace Equity Group Holdings", ScaleTypes.FIT))
            image_list.add(SlideModel(  R.drawable.jamesshakinghands, "Equity Group  registers  21% growth in total assets", ScaleTypes.FIT))
            image_list.add(SlideModel(R.drawable.stanfordmba , "Stanford MBA influential visit to the bank", ScaleTypes.FIT))
            image_list.add(SlideModel(R.drawable.elimu_talk, "Elimu Scholarship Program Day", ScaleTypes.FIT))
            image_list.add(SlideModel(R.drawable.equity_assurance , "Equity maintains outstanding financial perfomance", ScaleTypes.FIT))
            image_list.add(SlideModel(R.drawable.wings_to_fly_girls , "Wings to Fly Scholars fly abroad", ScaleTypes.FIT))


            image_Slider = findViewById<ImageSlider>(R.id.image_slider)
            image_Slider.setImageList(image_list)



            bottom_bar =  findViewById(R.id.bottom_app_bar)
            bottom_nav = findViewById(R.id.bottom_nav_view)

        } else
            setContentView(R.layout.activity_entre_page)
        super.onConfigurationChanged(newConfig)
    }
*/
}