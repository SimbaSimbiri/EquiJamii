package com.simbiri.equityjamii

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.snackbar.Snackbar
import java.util.*

class ForYouFragment : Fragment() {

    companion object {
        fun newInstance() = ForYouFragment()
    }

    private lateinit var recyclerSavedForYou: RecyclerView
    private lateinit var recyclerRecForYou : RecyclerView
    private lateinit var image_Slider: ImageSlider
    private lateinit var  newsLaterAdapter : WatchLaterAdapter
    private lateinit var recommendAdapter: WatchLaterAdapter
    private lateinit var imageLater : ImageView
    private lateinit var textSaved : TextView

    private val viewModel by lazy {  ViewModelProvider(this).get(ForYouViewModel::class.java)
    }


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
        viewModel.addtoImageList()
        image_Slider.setImageList(viewModel.imageList)

        setUpRecyclerRecommend(view)

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

            Collections.swap(viewModel.newsListBkMarked, initPosition, finalPosition)

            recyclerView.adapter?.notifyItemMoved(initPosition, finalPosition)

            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            val positionOfSwiped = viewHolder.adapterPosition
            val deletedSaved : NewsText = viewModel.newsListBkMarked[positionOfSwiped]

            deleteSavedNews(positionOfSwiped)

            Snackbar.make(recyclerSavedForYou, "Deleted from saved", Snackbar.LENGTH_LONG).setAction("Undo") {
                undoDeleted(positionOfSwiped, deletedSaved)
            }.show()

        }

    })

    private fun undoDeleted(positionOfSwiped: Int, deletedSaved: NewsText) {

        viewModel.newsListBkMarked.add(positionOfSwiped, deletedSaved)
        newsLaterAdapter.notifyItemInserted(positionOfSwiped)
        newsLaterAdapter.notifyItemRangeChanged(positionOfSwiped, viewModel.newsListBkMarked.size)

    }

    private fun deleteSavedNews(positionOfSwiped: Int) {

        val imageId =viewModel.newsListBkMarked[positionOfSwiped].imageId
        val deletedNews = viewModel.newsListBkMarked[positionOfSwiped]

        viewModel.newsAddedList.remove(imageId)
        viewModel.newsListBkMarked.remove(deletedNews)

        newsLaterAdapter.notifyItemRemoved(positionOfSwiped)
        newsLaterAdapter.notifyItemRangeChanged(positionOfSwiped, viewModel.newsListBkMarked.size)
    }


    override fun onResume() {
        super.onResume()

        setUpRecyclerSaved(view)
    }


    private fun setUpRecyclerRecommend(view: View?) {

        val context = requireContext()
        recommendAdapter = WatchLaterAdapter(context, viewModel.newsListRecommended!!)

        val layoutManagerRec = LinearLayoutManager(context)
        layoutManagerRec.orientation = RecyclerView.VERTICAL

        recyclerRecForYou.adapter = recommendAdapter
        recyclerRecForYou.layoutManager = layoutManagerRec
        recyclerRecForYou.hasFixedSize()

    }

    private fun setUpRecyclerSaved(view: View?) {

        val context = requireContext()
        newsLaterAdapter =  WatchLaterAdapter(context, viewModel.newsListBkMarked)

        val layoutManagerSaved = LinearLayoutManager(context)
        layoutManagerSaved.orientation = RecyclerView.VERTICAL

        recyclerSavedForYou.adapter = newsLaterAdapter
        recyclerSavedForYou.layoutManager = layoutManagerSaved
        recyclerSavedForYou.setHasFixedSize(true)

        val layoutManagerRec = LinearLayoutManager(context)
        layoutManagerRec.orientation = RecyclerView.VERTICAL

        itemTouchHelper.attachToRecyclerView(recyclerSavedForYou)

    }




}

