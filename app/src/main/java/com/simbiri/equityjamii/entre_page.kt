package com.simbiri.equityjamii

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu


class entre_page : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entre_page)



    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.top_menu, menu)

        return true
    }

}