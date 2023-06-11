package com.simbiri.equityjamii

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible


class KaribuActivity : AppCompatActivity() {
    private lateinit var karibu_text : TextView
    private lateinit var card_View: CardView
    private  var Karibu_Scr_TAG: String = "Karibu_Onclick was perfomed"


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
        karibu_text.isVisible =false

        Toast.makeText(this, "Press back again to exit\nTouch to return to news page", Toast.LENGTH_SHORT).show()

        super.onRestart()
    }


    fun onClick(){
        val intent =  Intent(this, MainEquiActivity::class.java)
        startActivity(intent)

        Log.i(Karibu_Scr_TAG, " User pressed Card View")


    }
}