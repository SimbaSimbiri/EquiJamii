package com.simbiri.equityjamii.ui.main_activity.people_page

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.OtherProfilesAdapter
import com.simbiri.equityjamii.adapters.SocialAdapter
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Social
import com.simbiri.equityjamii.data.model.UserNetworkUtils
import com.simbiri.equityjamii.databinding.DialogPeopleDetailBinding
import kotlinx.coroutines.launch

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

    private lateinit var otherSimilarProfilesAdapter: OtherProfilesAdapter
    private var isCurrentPersonDetails: Boolean = false
    private lateinit var viewModel: PersonInfoViewModel
    private var _binding: DialogPeopleDetailBinding? = null
    private val binding get() = _binding
    private lateinit var listsSocials: ArrayList<String>
    private var otherPeopleProfilesList: MutableList<Person> = mutableListOf()
    private lateinit var personParceled: Person
    private lateinit var currPerson: Person
    private val firestore = FirebaseFirestore.getInstance()
    private var isAlreadyFollowed: Boolean? = false
    private var isAboutExpanded = false
    private val MAX_CHAR_COLLAPSED_ABOUT = 200

    override fun onAttach(context: Context) {
        super.onAttach(context)

        personParceled = arguments?.getParcelable<Person>(ARGS_PERSON_INFO)!!

        AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()!!) { person ->
            if (person != null) {
                currPerson = person
                isAlreadyFollowed = person.network.followingList?.contains(personParceled.userId)

                isCurrentPersonDetails =
                    personParceled.userId.contentEquals(person.userId)

                if (isAlreadyFollowed == true) {
                    binding!!.tufuataneImageView.setImageResource(R.drawable.following_icon)
                } else {
                    binding!!.tufuataneImageView.setImageResource(R.drawable.add_friend)
                }

                Log.i("IsFollowed", "${isAlreadyFollowed}")
                Log.i("isCurrentPerson", "$isCurrentPersonDetails")

            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed to retrieve current user details",
                    Toast.LENGTH_LONG
                ).show()
            }

            lifecycleScope.launch {
                val followingList = currPerson.network.followingList
                val followerList = currPerson.network.followerList

                val listFromScope = UserNetworkUtils.followingFollowers(
                    followingList
                ).filter { person -> !person.userId.contentEquals(personParceled.userId) }



                otherPeopleProfilesList.clear()
                if (listFromScope.size > 5) {
                    otherPeopleProfilesList.addAll(listFromScope.shuffled().subList(0, 4))

                } else {
                    otherPeopleProfilesList.addAll(listFromScope.shuffled())
                }
            }.invokeOnCompletion {
                if (otherPeopleProfilesList.isNotEmpty()) {
                    binding!!.similarProfTextHead.visibility = View.VISIBLE
                    binding!!.recyclerOtherProfiles.visibility = View.VISIBLE
                    otherSimilarProfilesAdapter.notifyDataSetChanged()
                }
            }
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DialogPeopleDetailBinding.inflate(layoutInflater, container, false)
        val view = binding!!.root

        adjustSize()

        personParceled.let {

            val aboutText = getString(R.string.about_text, it.name)
            binding!!.aboutTextHead.text = aboutText
            binding!!.nameOnPeople.text = it.name
            binding!!.designationOnPeople.text = it.designation + " at " + it.branch
            setAboutText(it.social.about)
            binding!!.textCounty.text = it.city
            binding!!.countryEmojiText.text = it.country
            listsSocials =
                arrayListOf(
                    it.social.linkedin,
                    it.social.insta,
                    it.social.webs,
                    it.social.faceb,
                    it.social.xAcc
                )
            listsSocials.shuffle()

            Glide.with(this).load(Uri.parse(it.profileUri))
                .into(binding!!.profileOnPeopleImageV)
                .onLoadFailed(requireContext().getDrawable(R.drawable.account_box))
            Glide.with(this).load(Uri.parse(it.backGUri))
                .into(binding!!.detailBackImageV)
                .onLoadFailed(requireContext().getDrawable(R.drawable.equityjamiibackground))

            if (personParceled.verified) {
                binding!!.verifiedPersonelImage.visibility = View.VISIBLE
            }

        }


        binding!!.cardTufuatane.setOnClickListener {
            if (!isCurrentPersonDetails) {
                if (isAlreadyFollowed == true) {
                    unfollowCurrentPerson(personParceled.userId)
                    binding!!.tufuataneImageView.setImageResource(R.drawable.add_friend)

                } else {
                    followCurrentPerson(personParceled.userId)
                    binding!!.tufuataneImageView.setImageResource(R.drawable.following_icon)

                }
            }

        }

        visibilityViews()
        setRecyclerViewSocials()
        setRecyclerViewProfiles()

        return view
    }

    private fun setAboutText(aboutText: String?) {
        val spannable = SpannableStringBuilder(aboutText)
        if (aboutText != null) {
            if (aboutText.length > MAX_CHAR_COLLAPSED_ABOUT) {
                if (isAboutExpanded) {
                    spannable.append(" ...read less")
                } else {
                    spannable.delete(MAX_CHAR_COLLAPSED_ABOUT, aboutText.length)
                    spannable.append(" ...read more")
                }

                spannable.setSpan(
                    object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            toggleAboutExpansion()
                        }
                    },
                    spannable.length - " ...read more".length,
                    spannable.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                binding!!.aboutTextContent.text = spannable
                binding!!.aboutTextContent.movementMethod = LinkMovementMethod.getInstance()
            } else {
                binding!!.aboutTextContent.text = aboutText
            }
        }
    }

    private fun toggleAboutExpansion() {
        isAboutExpanded = !isAboutExpanded
        setAboutText(personParceled.social.about)
    }


    private fun unfollowCurrentPerson(userId: String) {
        val userCollection = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
        userCollection.document(AuthUtils.getCurrentUserId()!!)
            .update("network.followingList", FieldValue.arrayRemove(userId))
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    userCollection.document(userId).update(
                        "network.followerList", FieldValue.arrayRemove(
                            AuthUtils.getCurrentUserId()!!
                        )
                    ).addOnCompleteListener { taskSnap ->
                        if (taskSnap.isSuccessful) {

                            Toast.makeText(
                                requireContext(),
                                "Unfollowed ${personParceled.name}",
                                Toast.LENGTH_LONG
                            ).show()
                            isAlreadyFollowed = false
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_LONG)
                        .show()
                }
            }

    }

    private fun followCurrentPerson(userId: String) {
        val userCollection = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
        userCollection.document(AuthUtils.getCurrentUserId()!!)
            .update("network.followingList", FieldValue.arrayUnion(userId))
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    userCollection.document(userId).update(
                        "network.followerList", FieldValue.arrayUnion(
                            AuthUtils.getCurrentUserId()!!
                        )
                    ).addOnCompleteListener { taskSnap ->
                        if (taskSnap.isSuccessful) {

                            Toast.makeText(
                                requireActivity(),
                                "Followed ${personParceled.name}",
                                Toast.LENGTH_LONG
                            ).show()
                            isAlreadyFollowed = true
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_LONG)
                        .show()
                }
            }


    }


    private fun visibilityViews(
        city: String = personParceled.city,
        country: String = personParceled.country,
        social: Social = personParceled.social
    ) {
        val socialEmpty =
            social.linkedin.isEmpty() && social.insta.isEmpty() && social.webs.isEmpty()
                    && social.faceb.isEmpty()

        binding!!.let {
            when {
                city.isEmpty() && country.isEmpty() -> {
                    it.countryTextHead.visibility =
                        View.GONE
                    it.countryEmojiCard.visibility = View.GONE
                }

                !socialEmpty -> it.socialTextHead.visibility = View.VISIBLE
                social.about.isEmpty() -> {
                    it.aboutTextHead.visibility = View.GONE
                    it.aboutCardInfo.visibility = View.GONE
                }

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

    private fun setRecyclerViewProfiles() {

        val context = requireContext()
        otherSimilarProfilesAdapter = OtherProfilesAdapter(
            context,
            otherPeopleProfilesList
        )

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        binding!!.recyclerOtherProfiles.adapter = otherSimilarProfilesAdapter
        binding!!.recyclerOtherProfiles.layoutManager = layoutManager
        binding!!.recyclerOtherProfiles.hasFixedSize()

    }

    private fun adjustSize() {


        val layoutParamsProfileCardOut = binding!!.materialCardView.layoutParams
        val layoutParamsBackG = binding!!.detailBackImageV.layoutParams

        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels
        layoutParamsProfileCardOut.width = screenWidth / 4 + 80
        layoutParamsProfileCardOut.height = screenWidth / 4 + 80

        layoutParamsBackG.height = screenWidth / 4 + 100
        layoutParamsBackG.width = screenWidth


        binding!!.materialCardView.layoutParams = layoutParamsProfileCardOut
        binding!!.detailBackImageV.layoutParams = layoutParamsBackG
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.dialog_people_detail)
        dialog.setCanceledOnTouchOutside(true)

        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)


        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = true
                behavior.isHideable = true
                behavior.peekHeight = (displayMetrics.heightPixels * 0.9).toInt()

            }
        }

        return dialog
    }


}