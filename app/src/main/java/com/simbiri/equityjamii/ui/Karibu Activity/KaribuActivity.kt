package com.simbiri.equityjamii.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.simbiri.equityjamii.R


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


        val delayedTime : Long = 3400
        val intent = Intent(this@KaribuActivity, MainEquiActivity::class.java)

        val rootView = window.decorView.rootView
        rootView.postDelayed({startActivity(intent)}, delayedTime)

    }

    override fun onRestart() {
        super.onRestart()
        finish()

    }


}
