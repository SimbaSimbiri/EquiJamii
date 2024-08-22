package com.simbiri.equityjamii.ui.main_activity.my_profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.simbiri.equityjamii.R

class ProfMainFragment : Fragment() {

    companion object {
        fun newInstance() = ProfMainFragment()
    }

    private val viewModel: ProfMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = childFragmentManager.findFragmentById(R.id.profile_fragment_container)
            ?.findNavController()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.prof_main, container, false)
    }
}