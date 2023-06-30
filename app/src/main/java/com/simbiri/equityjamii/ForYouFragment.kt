package com.simbiri.equityjamii

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList

class ForYouFragment : Fragment() {

    companion object {
        fun newInstance() = ForYouFragment()
    }

    private lateinit var viewModel: ForYouViewModel
    private lateinit var recyclerSavedForYou: RecyclerView
    private lateinit var recyclerRecForYou : RecyclerView
    private lateinit var image_Slider: ImageSlider
    private lateinit var  newsLaterAdapter : WatchLaterAdapter
    private lateinit var recommendAdapter: WatchLaterAdapter
    private lateinit var imageLater : ImageView
    private lateinit var textSaved : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.for_you_fragment, container, false)

        recyclerSavedForYou = view.findViewById(R.id.forYouRecylerView)
        recyclerRecForYou =  view.findViewById(R.id.recommendforYouRecylerView)
        imageLater =  view.findViewById(R.id.imageViewLater)
        textSaved = view.findViewById(R.id.textViewSaved)



        image_Slider = view?.findViewById<ImageSlider>(R.id.image_slider) as ImageSlider

        val imageList =  ArrayList<SlideModel>()

        imageList.add(SlideModel(R.drawable.hackathonequity , "Meet the winners of Equity Hackathon 2nd Edition", ScaleTypes.FIT))
        imageList.add(SlideModel( R.drawable.bankacquisition, "Equity Group  acquires 91% of COGEBANQUE  equity stake", ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.cewicequity , "Commonwealth Enterprise partners with Equity", ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.americanecommerce, "Why is Africa The Future of the world?", ScaleTypes.FIT))


        image_Slider.setImageList(imageList)


        return view
    }

    private val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,ItemTouchHelper.LEFT){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            targetViewHolder: RecyclerView.ViewHolder
        ): Boolean {
             val initPosition =  viewHolder.adapterPosition
            val finalPosition = targetViewHolder.adapterPosition

            Collections.swap(NewsToday.isBookMarkedNews, initPosition, finalPosition)

            recyclerView.adapter?.notifyItemMoved(initPosition, finalPosition)

            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            val positionOfSwiped = viewHolder.adapterPosition
            val deletedSaved : NewsText = NewsToday.isBookMarkedNews[positionOfSwiped]

            deleteSavedNews(positionOfSwiped)

            Snackbar.make(recyclerSavedForYou, "Deleted from saved", Snackbar.LENGTH_LONG).setAction("Undo") {
                undoDeleted(positionOfSwiped, deletedSaved)
            }.show()

        }

    })

    private fun undoDeleted(positionOfSwiped: Int, deletedSaved: NewsText) {

        NewsToday.isBookMarkedNews.add(positionOfSwiped, deletedSaved)
        newsLaterAdapter.notifyItemInserted(positionOfSwiped)
        newsLaterAdapter.notifyItemRangeChanged(positionOfSwiped, NewsToday.isBookMarkedNews.size)

    }

    private fun deleteSavedNews(positionOfSwiped: Int) {

        val imageId =NewsToday.isBookMarkedNews[positionOfSwiped].imageId
        newsAddedList.remove(imageId)
        NewsToday.isBookMarkedNews.remove(NewsToday.isBookMarkedNews[positionOfSwiped])

        newsLaterAdapter.notifyItemRemoved(positionOfSwiped)
        newsLaterAdapter.notifyItemRangeChanged(positionOfSwiped, NewsToday.isBookMarkedNews.size)
    }


    override fun onResume() {

         setUpRecyclers(view)

        super.onResume()
    }


    private fun setUpRecyclers(view: View?) {

        val context = requireContext()
        newsLaterAdapter =  WatchLaterAdapter(context, NewsToday.isBookMarkedNews)
        recommendAdapter = WatchLaterAdapter(context, NewsToday.newsTextList!!)

         val layoutManagerSaved = LinearLayoutManager(context)
        layoutManagerSaved.orientation = RecyclerView.VERTICAL

        recyclerSavedForYou.adapter = newsLaterAdapter
        recyclerSavedForYou.layoutManager = layoutManagerSaved
        recyclerSavedForYou.setHasFixedSize(true)

        val layoutManagerRec = LinearLayoutManager(context)
        layoutManagerRec.orientation = RecyclerView.VERTICAL

        recyclerRecForYou.adapter = recommendAdapter
        recyclerRecForYou.layoutManager = layoutManagerRec
        recyclerRecForYou.hasFixedSize()

        itemTouchHelper.attachToRecyclerView(recyclerSavedForYou)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ForYouViewModel::class.java)
        // TODO: Use the ViewModel
    }

}