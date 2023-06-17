package com.simbiri.equityjamii

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PageAdapter(
    fm: FragmentManager,
    private val numFrag: Int,
    private val titleList: MutableList<String>
) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
    return numFrag
        }

    override fun getItem(position: Int): Fragment {


        when(position){
             0 ->{
                val Profile= ProfileFrag()
                return Profile
            }

            1->{
                val Settings = SettingsFrag()
                return Settings
            }

            2 -> {
                val aboutUS = AboutUsFrag()
                return aboutUS
            }

            3 ->{
                val shareRate = ShareRateFrag()
                return shareRate
            }
        }

        return Fragment()
    }

    override fun getPageTitle(position: Int): CharSequence? {

        return titleList[position]
    }


}