package com.simbiri.equityjamii

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel

class karibu_class : AppCompatActivity() {
    lateinit var karibu_text : TextView
    lateinit var card_View: CardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.karibu_layout)
        karibu_text =  findViewById(R.id.karibu_txtv)
        card_View =  findViewById(R.id.karibuCardView)

        card_View.setOnClickListener {

            onClick()
        }

    }

    override fun onRestart() {
        karibu_text.isVisible = false


        Toast.makeText(this, "Press back again to exit\nTouch to return to news page", Toast.LENGTH_LONG).show()


        super.onRestart()
    }


    fun onClick(){
        val intent =  Intent(this, entre_page::class.java)
        startActivity(intent)


    }
}