package com.simbiri.equityjamii.ui.main_activity.workspace_page

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.simbiri.equityjamii.R

class AddMentionFragment : Fragment() {

    companion object {
        fun newInstance() = AddMentionFragment()
    }

    private val viewModel: AddMentionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.add_mention_frag, container, false)
    }
}