package com.simbiri.equityjamii.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.simbiri.equityjamii.R

class ForYouFragment : Fragment() {

    companion object {
        fun newInstance() = ForYouFragment()
    }


    private lateinit var elpRecyclerView: RecyclerView
    private lateinit var inKenyaRecyclerView: RecyclerView
    private lateinit var inRwandaRecyclerView: RecyclerView
    /*
        private lateinit var inDRCRecyclerView: RecyclerView
        private lateinit var inEquityAfyaRecyclerView: RecyclerView
    */

    private var firebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var databaseReferenceKenya: DatabaseReference
    private lateinit var databaseReferenceRwanda: DatabaseReference
    private lateinit var databaseReferenceScholars: DatabaseReference

    private lateinit var firebaseRecyclerAdapterKenya: FypNewsAdapter
    private lateinit var firebaseRecyclerAdapterRwanda: FypNewsAdapter
    private lateinit var firebaseRecyclerAdapterScholars: FypNewsAdapter

    private lateinit var optionsKenya: FirebaseRecyclerOptions<NewsText>
    private lateinit var optionsRwanda: FirebaseRecyclerOptions<NewsText>
    private lateinit var optionsScholars: FirebaseRecyclerOptions<NewsText>


    private val viewModel by lazy {
        ViewModelProvider(this).get(ForYouViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.for_you_fragment, container, false)

        elpRecyclerView = view.findViewById(R.id.elpRecyclerView)
        inKenyaRecyclerView = view.findViewById(R.id.inKenyaRecyclerView)
        inRwandaRecyclerView = view.findViewById(R.id.inRwandaRecyclerView)
        /*  inDRCRecyclerView = view.findViewById(R.id.inDRCRecylerView)
          inEquityAfyaRecyclerView = view.findViewById(R.id.equityAfyaRecyclerView)*/

        databaseReferenceKenya = firebaseDatabase.getReference("For You/Kenya")
        databaseReferenceScholars = firebaseDatabase.getReference("For You/Scholars")
        databaseReferenceRwanda = firebaseDatabase.getReference("For You/Rwanda")


        setUpKenyaRecyclerNews(view)
        setUpRwandaRecyclers(view)
        setUpScholarsRecyclers(view)

        return view

    }


    private fun setUpKenyaRecyclerNews(view: View?) {

        val context = requireContext()
        val layoutManagerKenya = LinearLayoutManager(context)
        layoutManagerKenya.orientation = RecyclerView.VERTICAL
        layoutManagerKenya.stackFromEnd = true


        optionsKenya = FirebaseRecyclerOptions.Builder<NewsText>()
            .setQuery(databaseReferenceKenya, NewsText::class.java).build()
        firebaseRecyclerAdapterKenya = FypNewsAdapter(context, optionsKenya)

        inKenyaRecyclerView.adapter = firebaseRecyclerAdapterKenya
        inKenyaRecyclerView.layoutManager = layoutManagerKenya
        inKenyaRecyclerView.hasFixedSize()


    }

    fun setUpScholarsRecyclers(view: View) {
        val context = requireContext()

        val layoutManagerScholars = LinearLayoutManager(context)
        layoutManagerScholars.orientation = RecyclerView.VERTICAL
        layoutManagerScholars.stackFromEnd = true

        optionsScholars = FirebaseRecyclerOptions.Builder<NewsText>()
            .setQuery(databaseReferenceScholars, NewsText::class.java).build()
        firebaseRecyclerAdapterScholars = FypNewsAdapter(context, optionsScholars)


        elpRecyclerView.adapter = firebaseRecyclerAdapterScholars
        elpRecyclerView.layoutManager = layoutManagerScholars
        elpRecyclerView.hasFixedSize()

    }

    fun setUpRwandaRecyclers(view: View) {

        val context = requireContext()

        val layoutManagerRwanda = LinearLayoutManager(context)
        layoutManagerRwanda.orientation = RecyclerView.VERTICAL
        layoutManagerRwanda.stackFromEnd = true

        optionsRwanda = FirebaseRecyclerOptions.Builder<NewsText>()
            .setQuery(databaseReferenceRwanda, NewsText::class.java).build()
        firebaseRecyclerAdapterRwanda = FypNewsAdapter(context, optionsRwanda)



        inRwandaRecyclerView.adapter = firebaseRecyclerAdapterRwanda
        inRwandaRecyclerView.layoutManager = layoutManagerRwanda
        inRwandaRecyclerView.hasFixedSize()

    }

    override fun onStart() {
        super.onStart()

        firebaseRecyclerAdapterKenya.startListening()
        firebaseRecyclerAdapterRwanda.startListening()
        firebaseRecyclerAdapterScholars.startListening()
    }

    override fun onStop() {
        super.onStop()

        firebaseRecyclerAdapterKenya.stopListening()
        firebaseRecyclerAdapterRwanda.stopListening()
        firebaseRecyclerAdapterScholars.stopListening()
    }


}

