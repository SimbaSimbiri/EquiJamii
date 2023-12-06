package com.simbiri.equityjamii.ui

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.os.Parcel
import android.os.Parcelable
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.simbiri.equityjamii.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

data class NewsText(val image: String, val title: String, val allNews : String) : Parcelable {

    constructor() : this("", "", "")

    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString() ?: "",
        parcel.readString()?:""

    ) {
    }

    override fun describeContents(): Int {
    return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(image)
        dest.writeString(title)
        dest.writeString(allNews)
    }

    companion object CREATOR : Parcelable.Creator<NewsText> {
        override fun createFromParcel(parcel: Parcel): NewsText {
            return NewsText(parcel)
        }

        override fun newArray(size: Int): Array<NewsText?> {
            return arrayOfNulls(size)
        }
    }
}


class NewsAdapter(var context: Context, options: FirebaseRecyclerOptions<NewsText>) : FirebaseRecyclerAdapter<NewsText, NewsAdapter.NewsViewHolder>(options) {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private var positionItem: Int = 1
        private var currentNewsTextItem: NewsText? = null

         var textNewsHeadlineView: TextView = itemView.findViewById(R.id.textViewHeadline)
         var imageNewsHeadlineView: ImageView = itemView.findViewById(R.id.imageViewHeadNews)



        fun setDetails( image: String, title:String, allNews: String){
            textNewsHeadlineView.text = title
            this.currentNewsTextItem = NewsText(image, title, allNews)


            Glide.with(itemView.context as AppCompatActivity)
                .load(image)
                .into(imageNewsHeadlineView)

            Log.d("IMAGE URL", image)
        }


        fun setOnclickListeners() {
            itemView.setOnClickListener(this@NewsViewHolder)
        }


        override fun onClick(view: View?) {

            val bottomFragmentDetail = NewsDetailFragment.newInstance(currentNewsTextItem!!)
            val transaction = (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            bottomFragmentDetail.show(transaction, bottomFragmentDetail.tag)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.newsitemsample, parent, false)

            return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int, model: NewsText) {
        holder.setDetails(model.image, model.title,model.allNews)
        holder.setOnclickListeners()
    }

}









