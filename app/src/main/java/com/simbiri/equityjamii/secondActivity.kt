package com.simbiri.equityjamii

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout

class secondActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        tabLayout.addTab(tabLayout.newTab().setText(R.string.profileTab))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.settingsTab))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.aboutUsTab))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.share))


        val position = intent.getIntExtra(EXTRA_POS, 0)


        val pageAdapter = PageAdapter(supportFragmentManager, tabLayout.tabCount, TitleList.titles)
        pageAdapter.getPageTitle(tabLayout.selectedTabPosition)

        viewPager.adapter = pageAdapter
        viewPager.currentItem = position


        tabLayout.setupWithViewPager(viewPager)


    }

}
