package com.simbiri.equityjamii.ui.main_activity.my_profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.simbiri.equityjamii.NavGraphDirections
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.SocialAdapter
import com.simbiri.equityjamii.data.objects.AuthUtils
import com.simbiri.equityjamii.data.objects.AuthUtils.getCurrentUserId
import com.simbiri.equityjamii.data.model.Network
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.databinding.ProfilePageDisplayBinding
import com.simbiri.equityjamii.ui.main_activity.people_page.PeopleFragmentDirections


class ProfDisplayFragment : Fragment() {

    companion object {
        fun newInstance() = ProfDisplayFragment()
    }

    private var currentPerson: Person = Person()
    private val viewModel: ProfDisplayViewModel by viewModels()

    private lateinit var binding: ProfilePageDisplayBinding
    private lateinit var listsSocials: ArrayList<String>
    private var myNetwork: Network? = Network()
    private var isAboutExpanded = false
    private val MAX_CHAR_COLLAPSED_ABOUT = 200
    private lateinit var navHostFrag: NavHostFragment
    private lateinit var nestedNavController: NavController


    override fun onAttach(context: Context) {
        super.onAttach(context)

        navHostFrag =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ProfilePageDisplayBinding.inflate(layoutInflater)
        val view = binding.root

        adjustSize()
        nestedNavController = findNavController()

        AuthUtils.getCurrentPerson(getCurrentUserId()) { person ->
            if (person != null) {

                binding.myJamiiTv.setOnClickListener {
                    progressBarToggle()
                    requireActivity().supportFragmentManager.popBackStackImmediate()
                    val action = ProfMainFragmentDirections.actionOpenJamii(3)
                    navHostFrag.findNavController().navigate(action)

                }

                binding.swipeRefresh.setOnRefreshListener {
                    refreshProfileInfo()
                }

                binding.myAssistant.setOnClickListener {
                    progressBarToggle()
                    requireActivity().supportFragmentManager.popBackStackImmediate()
                    val action = ProfMainFragmentDirections.actionOpenWorkspace(1)
                    navHostFrag.findNavController().navigate(action)

                }

                binding.mySettingsTv.setOnClickListener {
                    progressBarToggle()
                    val action = ProfDisplayFragmentDirections.actionGlobalToSettings()
                    nestedNavController.navigate(action)

                }


                binding.myWorkspaces.setOnClickListener {
                    progressBarToggle()
                    requireActivity().supportFragmentManager.popBackStackImmediate()
                    val action = ProfMainFragmentDirections.actionOpenWorkspace(0)
                    navHostFrag.findNavController().navigate(action)

                }

            }
        }

        return view
    }

    private fun progressBarToggle() {
        binding.contentLoadingProgressBar.visibility = View.VISIBLE
        Handler().postDelayed({
            binding.contentLoadingProgressBar.visibility = View.INVISIBLE
        }, 3000)
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

                binding.aboutTextContent.text = spannable
                binding.aboutTextContent.movementMethod = LinkMovementMethod.getInstance()
            } else {
                binding.aboutTextContent.text = aboutText
            }
        }
    }

    private fun toggleAboutExpansion() {
        isAboutExpanded = !isAboutExpanded
        setAboutText(currentPerson.social.about)
    }


    private fun refreshProfileInfo() {
        val fragmentTransactionExit = parentFragmentManager.beginTransaction()
        val fragmentTransactionEnter = parentFragmentManager.beginTransaction()

        fragmentTransactionExit.detach(this).commit()
        this.onAttach(requireContext())
        fragmentTransactionEnter.attach(this).commit()
        retreiveDisplayInfo()

        binding.swipeRefresh.isRefreshing = false
    }

    private fun retreiveDisplayInfo() {
        viewModel.myProf.observe(viewLifecycleOwner) { myProf ->

            if (myProf == null) {
                val newPerson = Person()
                newPerson.userId = getCurrentUserId()!!
                val action = ProfDisplayFragmentDirections.actionGlobalToProfileEdit(newPerson)
                findNavController().navigate(action)

            } else {

                binding.let {
                    Glide.with(requireContext()).load(Uri.parse(myProf.backGUri))
                        .into(it.backImageView)
                    Glide.with(requireContext()).load(Uri.parse(myProf.profileUri))
                        .into(it.profileImageView)
                    it.nameOnPeople.text = myProf.name
                    it.designationOnPeople.text =
                        myProf.designation + " at " + myProf.branch
                    setAboutText(myProf.social.about)
                    it.textCounty.text = myProf.city
                    it.countryEmojiText.text = myProf.country
                    listsSocials = arrayListOf(
                        myProf.social.linkedin,
                        myProf.social.insta,
                        myProf.social.webs,
                        myProf.social.faceb,
                        myProf.social.xAcc
                    )
                    listsSocials.sort()

                    if (myProf.verified) {
                        binding.verifiedPersonelImage.visibility = View.VISIBLE
                    }

                    setRecyclerViewSocials()
                    currentPerson = myProf

                    binding.cardPeople.setOnClickListener {
                        progressBarToggle()
                        val action = PeopleFragmentDirections.actionGlobalOpenNetwork(myProf.network, R.id.myProfile)
                        navHostFrag.navController.navigate(action)
                    }

                    myNetwork = myProf.network

                }
            }

        }

    }


    private fun setRecyclerViewSocials() {
        val context = requireContext()
        val filtered = listsSocials.filter { it != "" }
        val socialAdapter = SocialAdapter(context, filtered)
        if (filtered.isNotEmpty()) {
            binding.socialTextHead.visibility = View.VISIBLE
        }

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        binding.recyclerSocials.adapter = socialAdapter
        binding.recyclerSocials.layoutManager = layoutManager
        binding.recyclerSocials.hasFixedSize()

    }

    private fun adjustSize() {


        val layoutParamsProfileCardOut = binding.materialCardView.layoutParams
        val layoutParamsBackG = binding.backImageView.layoutParams

        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels
        layoutParamsProfileCardOut.width = screenWidth / 3
        layoutParamsProfileCardOut.height = screenWidth / 3

        layoutParamsBackG.height = screenWidth / 3 + 100
        layoutParamsBackG.width = screenWidth


        binding.materialCardView.layoutParams = layoutParamsProfileCardOut
        binding.backImageView.layoutParams = layoutParamsBackG
    }

    override fun onResume() {
        super.onResume()
        retreiveDisplayInfo()

    }
}