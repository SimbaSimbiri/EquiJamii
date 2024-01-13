package com.simbiri.equityjamii.ui.karibu_splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.ui.main_activity.MainEquiActivity


class KaribuActivity : AppCompatActivity() {
    private lateinit var imageView1 : ImageView
    private lateinit var imageView2 : ImageView
    private lateinit var card_View: CardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_karibu_splash)


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
