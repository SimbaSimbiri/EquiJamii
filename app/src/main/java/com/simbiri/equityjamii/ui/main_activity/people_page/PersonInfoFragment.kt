package com.simbiri.equityjamii.ui.main_activity.people_page

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.OtherProfilesAdapter
import com.simbiri.equityjamii.adapters.SocialAdapter
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.data.objects.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Social
import com.simbiri.equityjamii.data.objects.UserNetworkUtils
import com.simbiri.equityjamii.databinding.DialogPeopleDetailBinding
import kotlinx.coroutines.launch

class PersonInfoFragment : Fragment() {

    companion object {
        private const val ARGS_PERSON_INFO = "person"
        private const val ARGS_DEST_ID = "destId"

        fun newInstance(person: Person, destId: Int): PersonInfoFragment {
            val fragment = PersonInfoFragment()
            val argumentBundle = Bundle()
            argumentBundle.putParcelable(ARGS_PERSON_INFO, person)
            argumentBundle.putInt(ARGS_DEST_ID, destId)

            fragment.arguments = argumentBundle
            return fragment

        }
    }

    private var destId: Int? = 0
    private var listFromScope: List<Person> = mutableListOf()
    private lateinit var otherSimilarProfilesAdapter: OtherProfilesAdapter
    private var isCurrentPersonDetails: Boolean = false
    private lateinit var viewModel: PersonInfoViewModel
    private var _binding: DialogPeopleDetailBinding? = null
    private val binding get() = _binding
    private lateinit var listsSocials: ArrayList<String>
    private var otherPeopleProfilesList: MutableList<Person> = mutableListOf()
    private lateinit var personParceled: Person
    private lateinit var currPerson: Person
    private var isAlreadyFollowed: Boolean? = false
    private var isAboutExpanded = false
    private val MAX_CHAR_COLLAPSED_ABOUT = 200

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _binding = DialogPeopleDetailBinding.inflate(layoutInflater)

        destId = arguments?.getInt(ARGS_DEST_ID)
        personParceled = arguments?.getParcelable<Person>(ARGS_PERSON_INFO)!!
        otherSimilarProfilesAdapter = OtherProfilesAdapter(context, otherPeopleProfilesList)
        loadData()

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        val allMenu = listOf(
            R.id.newsFrag,
            R.id.myWorkspace,
            R.id.jamiiFrag,
            R.id.peopleFrag,
            R.id.myProfile
        )

        if (destId != null && allMenu.contains(destId)) {
            destId?.let {
                bottomNavigationView.menu.findItem(it).isChecked = true
            }
        }

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
                listFromScope = UserNetworkUtils.followingFollowers(
                    followingList
                ).filter { person -> !person.userId.contentEquals(personParceled.userId) }

                if (otherPeopleProfilesList.isEmpty()) {
                    otherPeopleProfilesList.clear()
                    if (listFromScope.size > 5) {
                        otherPeopleProfilesList.addAll(listFromScope.shuffled().subList(0, 5))

                    } else {
                        otherPeopleProfilesList.addAll(listFromScope.shuffled())
                    }
                }

                if (otherPeopleProfilesList.isNotEmpty()) {
                    binding!!.similarProfTextHead.visibility = View.VISIBLE
                    binding!!.recyclerOtherProfiles.visibility = View.VISIBLE
                    binding!!.progressBar.visibility = View.GONE
                    otherSimilarProfilesAdapter.notifyDataSetChanged()
                }

            }
        }

    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun saveData() {
        val sharedPreferences =
            requireContext().getSharedPreferences("PersonInfoPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            isAlreadyFollowed?.let { putBoolean("isAlreadyFollowed_${personParceled.userId}", it) }

            val peopleListJson = Gson().toJson(otherPeopleProfilesList)
            putString("otherPeopleProfilesList_${personParceled.userId}", peopleListJson)

            apply()
        }
    }

    private fun loadData() {
        val sharedPreferences =
            requireContext().getSharedPreferences("PersonInfoPrefs", Context.MODE_PRIVATE)
        isAlreadyFollowed =
            sharedPreferences.getBoolean("isAlreadyFollowed_${personParceled.userId}", false)

        val peopleListJson =
            sharedPreferences.getString("otherPeopleProfilesList_${personParceled.userId}", null)
        if (peopleListJson != null) {
            val type = object : TypeToken<MutableList<Person>>() {}.type
            otherPeopleProfilesList = Gson().fromJson(peopleListJson, type)
        }

        if (isAlreadyFollowed == true) {
            binding!!.tufuataneImageView.setImageResource(R.drawable.following_icon)
        } else {
            binding!!.tufuataneImageView.setImageResource(R.drawable.add_friend)
        }
        if (otherPeopleProfilesList.isNotEmpty()) {
            Handler().postDelayed({
                otherSimilarProfilesAdapter =
                    OtherProfilesAdapter(requireContext(), otherPeopleProfilesList)
                binding!!.similarProfTextHead.visibility = View.VISIBLE
                binding!!.recyclerOtherProfiles.visibility = View.VISIBLE
                binding!!.progressBar.visibility = View.GONE
                otherSimilarProfilesAdapter.notifyDataSetChanged()
            }, 500)
        }
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DialogPeopleDetailBinding.inflate(layoutInflater, container, false)
        val view = binding!!.root

        adjustSize()

        personParceled.let { it ->

            binding!!.apply {

                val aboutText = getString(R.string.about_text, it.name)
                aboutTextHead.text = aboutText
                nameOnPeople.text = it.name
                designationOnPeople.text = it.designation + " at " + it.branch
                setAboutText(it.social.about)
                textCounty.text = it.city
                countryEmojiText.text = it.country
                listsSocials =
                    arrayListOf(
                        it.social.linkedin,
                        it.social.insta,
                        it.social.webs,
                        it.social.faceb,
                        it.social.xAcc
                    )
                listsSocials.shuffle()

                Glide.with(this@PersonInfoFragment).load(Uri.parse(it.profileUri))
                    .into(profileOnPeopleImageV)
                    .onLoadFailed(requireContext().getDrawable(R.drawable.account_box))
                Glide.with(this@PersonInfoFragment).load(Uri.parse(it.backGUri))
                    .into(detailBackImageV)
                    .onLoadFailed(requireContext().getDrawable(R.drawable.equityjamiibackground))

                if (personParceled.verified) {
                    verifiedPersonelImage.visibility = View.VISIBLE
                }

                cardNetwork.setOnClickListener {
                    val navHostFrag =
                        requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                    val action =
                        PeopleFragmentDirections.actionGlobalOpenNetwork(
                            personParceled.network,
                            destId!!
                        )
                    navHostFrag.navController.navigate(action)
                }
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
                    spannable.length - 12,
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

        binding!!.recyclerSocials.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = socialAdapter
            hasFixedSize()
        }
    }

    private fun setRecyclerViewProfiles() {

        val context = requireContext()
        otherSimilarProfilesAdapter = OtherProfilesAdapter(context, otherPeopleProfilesList)

        binding!!.recyclerOtherProfiles.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = otherSimilarProfilesAdapter
            hasFixedSize()
        }

    }

    private fun adjustSize() {

        binding!!.apply {

            val layoutParamsProfileCardOut = materialCardView.layoutParams
            val layoutParamsBackG = detailBackImageV.layoutParams

            val displayMetrics = DisplayMetrics()
            val windowManager =
                requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            layoutParamsProfileCardOut.width = screenWidth / 4 + 80
            layoutParamsProfileCardOut.height = screenWidth / 4 + 80

            layoutParamsBackG.height = screenWidth / 4 + 100
            layoutParamsBackG.width = screenWidth


            materialCardView.layoutParams = layoutParamsProfileCardOut
            detailBackImageV.layoutParams = layoutParamsBackG
        }
    }

}