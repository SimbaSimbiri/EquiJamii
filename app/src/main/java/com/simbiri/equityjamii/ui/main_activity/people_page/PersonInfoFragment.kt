package com.simbiri.equityjamii.ui.main_activity.people_page

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.opengl.Visibility
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.SocialAdapter
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Social
import com.simbiri.equityjamii.databinding.DialogPeopleDetailBinding

class PersonInfoFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARGS_PERSON_INFO = "person"

        fun newInstance(person: Person): PersonInfoFragment {
            val fragment = PersonInfoFragment()
            val argumentBundle = Bundle()
            argumentBundle.putParcelable(ARGS_PERSON_INFO, person)
            fragment.arguments = argumentBundle
            return fragment

        }
    }

    private lateinit var viewModel: PersonInfoViewModel
    private var _binding: DialogPeopleDetailBinding? = null
    private val binding get() = _binding
    private lateinit var listsSocials: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DialogPeopleDetailBinding.inflate(layoutInflater, container, false)
        val view = binding!!.root

        adjustSize()

        val personParceled = arguments?.getParcelable<Person>(ARGS_PERSON_INFO)
        personParceled?.let {
            visibilityViews(it.city, it.country, it.social)

            binding!!.nameOnPeople.text = it.name
            binding!!.designationOnPeople.text = it.designation + " at " + it.branch
            binding!!.aboutTextContent.text = it.social.about
            binding!!.textCounty.text = it.city
            binding!!.countryEmojiText.text = it.country
            listsSocials =
                arrayListOf(it.social.linkedin, it.social.insta, it.social.webs, it.social.faceb, it.social.xAcc)
            listsSocials.shuffle()

            Glide.with(this).load(Uri.parse(it.profileUri))
                .into(binding!!.profileOnPeopleImageV)
                .onLoadFailed(requireContext().getDrawable(R.drawable.account_box))
            Glide.with(this).load(Uri.parse(it.backGUri))
                .into(binding!!.detailBackImageV)
                .onLoadFailed(requireContext().getDrawable(R.drawable.equityjamiibackground))


        }

        setRecyclerViewSocials()

        return view
    }

    private fun visibilityViews(city: String, country: String, social: Social) {
        val socialEmpty =
            social.linkedin.isEmpty() && social.insta.isEmpty() && social.webs.isEmpty()
                    && social.faceb.isEmpty()

        binding!!.let {
            when {
                city.isEmpty() && country.isEmpty() -> it.countryTextHead.visibility =
                    View.GONE

                city.isEmpty() && country.isEmpty() -> it.countryEmojiCard.visibility = View.GONE
                socialEmpty -> it.socialTextHead.visibility = View.GONE
                social.about.isEmpty() -> it.aboutTextHead.visibility = View.GONE
                social.about.isEmpty() -> it.aboutCardInfo.visibility = View.GONE
            }
        }

    }

    private fun setRecyclerViewSocials() {
        val context = requireContext()
        val filtered = listsSocials.filter { it != "" }
        val socialAdapter = SocialAdapter(context, filtered)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        binding!!.recyclerSocials.adapter = socialAdapter
        binding!!.recyclerSocials.layoutManager = layoutManager
        binding!!.recyclerSocials.hasFixedSize()

    }

    private fun adjustSize() {


        val layoutParamsProfileCardOut = binding!!.materialCardView.layoutParams
        val layoutParamsBackG = binding!!.detailBackImageV.layoutParams

        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels
        layoutParamsProfileCardOut.width = screenWidth / 5 + 80
        layoutParamsProfileCardOut.height = screenWidth / 5 + 80

        layoutParamsBackG.height = screenWidth / 4 + 100
        layoutParamsBackG.width = screenWidth


        binding!!.materialCardView.layoutParams = layoutParamsProfileCardOut
        binding!!.detailBackImageV.layoutParams = layoutParamsBackG
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.dialog_people_detail)
        dialog.setCanceledOnTouchOutside(true)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = true
                behavior.isHideable = true
                behavior.peekHeight = 1000

            }
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PersonInfoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}