package com.simbiri.equityjamii.ui.main_activity.news_page.for_you

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.TagNewsAdapter
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.databinding.EditNewsPrefBinding
import com.simbiri.equityjamii.ui.main_activity.news_page.NewsViewModel

class EditNewsPrefFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = EditNewsPrefFragment()
    }

    private val selectedTags = mutableSetOf<String>()
    private val viewModel = NewsViewModel()
    private lateinit var binding: EditNewsPrefBinding
    private val firebaseInstance = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun saveNewsPreferences() {
        val firebaseUserId = AuthUtils.getCurrentUserId()
        firebaseInstance.collection(USERS_COLLECTION).document(firebaseUserId!!)
            .update("newsTags", selectedTags.toList())
        Toast.makeText(context, "News preferences updated", Toast.LENGTH_SHORT).show()
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditNewsPrefBinding.inflate(layoutInflater)

        viewModel.allTagList.observe(viewLifecycleOwner) { allTags ->
            val tagsAdapter = TagNewsAdapter(allTags) { tag ->

                if (tag.isSelected) {
                    selectedTags.add(tag.name)
                } else {
                    selectedTags.remove(tag.name)
                }
            }
            val spanCount = calculateSpanCount()
            binding.tagsRecyclerView.apply {
                adapter = tagsAdapter
                layoutManager = StaggeredGridLayoutManager(
                    spanCount,
                    StaggeredGridLayoutManager.VERTICAL
                )
            }
            binding.tagsRecyclerView.adapter!!.notifyDataSetChanged()

        }

        binding.savePreferencesButton.setOnClickListener {
            saveNewsPreferences()

        }
        binding.cancelPreferencesButton.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    private fun calculateSpanCount(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        val itemWidth = screenWidth/5

        return (screenWidth / itemWidth)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.edit_news_pref)
        dialog.setCanceledOnTouchOutside(true)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = true
                behavior.isHideable = true

            }
        }

        return dialog
    }

}