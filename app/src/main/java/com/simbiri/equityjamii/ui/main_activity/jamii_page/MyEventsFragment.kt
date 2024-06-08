package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.simbiri.equityjamii.R
class MyEventsFragment : Fragment() {

    companion object {

        fun newInstance() = MyEventsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.my_events_fragment, container, false)
    }

}