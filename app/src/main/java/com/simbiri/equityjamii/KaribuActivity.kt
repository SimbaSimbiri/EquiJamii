package com.simbiri.equityjamii

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible


class KaribuActivity : AppCompatActivity() {
    private lateinit var imageView1 : ImageView
    private lateinit var imageView2 : ImageView
    private lateinit var card_View: CardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.karibu_layout)


        card_View = findViewById(R.id.karibuCardView)
        imageView1 = findViewById(R.id.imageView)
        imageView2 =  findViewById(R.id.imageView2)


        val delayedTime : Long = 2000
        val intent = Intent(this@KaribuActivity, MainEquiActivity::class.java)

        val rootView = window.decorView.rootView
        rootView.postDelayed({startActivity(intent)}, delayedTime)

    }

    override fun onRestart() {

        finish()

        super.onRestart()


    }


}
