package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.constants.USER_ID
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.databinding.JamiiPageBinding

class JamiiPageFragment : Fragment() {

    companion object {
        fun newInstance() = JamiiPageFragment()
    }

    private lateinit var personPost: Person
    private lateinit var firestore: FirebaseFirestore
    private val viewModel: JamiiPageViewModel by viewModels()
    private lateinit var binding: JamiiPageBinding
    private lateinit var stateFragAdapter: FragmentStateAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = JamiiPageBinding.inflate(layoutInflater)
        val view = binding.root

        firestore = FirebaseFirestore.getInstance()
        firestore.collection(USERS_COLLECTION).document(AuthUtils.getCurrentUserId()!!).get()
            .addOnCompleteListener { taskDocSnapShot ->
                if (taskDocSnapShot.isSuccessful) {
                    if (taskDocSnapShot.result.exists()) {
                        val profilePicUrl = taskDocSnapShot.result.getString("profileUri")
                        personPost = taskDocSnapShot.result.toObject(Person::class.java)!!
                        Glide.with(requireContext()).load(profilePicUrl)
                            .into(binding.currentUserImage)
                    }
                }

            }


        setupViewPager()

        binding.currentUserImage.setOnClickListener {
            val addPostFragment = AddPostFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            addPostFragment.show(transaction, addPostFragment.tag)
        }

        binding.yourThoughtsTv.setOnClickListener {
            val addPostFragment = AddPostFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            addPostFragment.show(transaction, addPostFragment.tag)
        }


        AuthUtils.getCurrentPerson(USER_ID) { currentPerson ->
            if (currentPerson == null) {
                Toast.makeText(
                    requireContext(),
                    "Set up profile to access EquiJamii features",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val initialTabIndex = arguments?.getInt("initial_tab_index") ?: 0
        binding.viewPagerPosts.setCurrentItem(initialTabIndex, false)

        binding.swipeRefresh.setOnRefreshListener {
            refreshJamii()
        }

        return view
    }

    private fun refreshJamii() {
        val currentTabPosition = binding.tabLayoutposts.selectedTabPosition

        val fragmentTransaction = parentFragmentManager.beginTransaction()

        fragmentTransaction.detach(this).commitNow()
        fragmentTransaction.attach(this).commitNow()

        binding.viewPagerPosts.currentItem = currentTabPosition
        binding.swipeRefresh.isRefreshing = false
    }

    private fun setupViewPager() {

        stateFragAdapter = PostSlidePageAdapter(this@JamiiPageFragment)
        binding.viewPagerPosts.isUserInputEnabled = false
        binding.viewPagerPosts.adapter = stateFragAdapter
        TabLayoutMediator(
            binding.tabLayoutposts,
            binding.viewPagerPosts,
            true,
            true
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Discover"
                }

                1 -> {
                    tab.text = "Feed"
                }

                2 -> {
                    tab.text = "My Posts"
                }

            }
        }.attach()

    }

    class PostSlidePageAdapter(jamiiPageFragment: JamiiPageFragment) :
        FragmentStateAdapter(jamiiPageFragment) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    DiscoverFragment()
                }

                1 -> {
                    FeedFragment()
                }

                2 -> {
                    MyPostsFragment()
                }

                else -> {
                    Fragment()
                }
            }
        }


    }

}