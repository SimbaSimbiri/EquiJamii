package com.simbiri.equityjamii.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.SocialMedia

class SocialAdapter(var context: Context, private var mediaLinkList: List<String>)  : RecyclerView.Adapter<SocialAdapter.SocialAdapterViewHolder>(){

    inner class SocialAdapterViewHolder(itemView : View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private var  linkItem : String =  ""

        private  var positionItem = 1

        private  val socialImageView =  itemView.findViewById<ImageView>(R.id.socialIconImageView)

        fun setDataToItem(url : String, position: Int){
            this.positionItem = position
            this.linkItem = url

            val linkedInBool = url.contains("linkedin")
            val instaBool = url.contains("instagram")
            val faceBool = url.contains("facebook")

            if (linkedInBool){
                socialImageView.setImageResource(SocialMedia.linkedIPic)
            }else if (instaBool){
                socialImageView.setImageResource(SocialMedia.instaGPic)
            }else if (faceBool){
                socialImageView.setImageResource(SocialMedia.faceBPic)
            }else{
                socialImageView.setImageResource(SocialMedia.webSPic)
            }

        }

        fun setOnclickListeners() {
            itemView.setOnClickListener(this@SocialAdapterViewHolder)
        }


        override fun onClick(view: View?) {
            val url = this.linkItem

            if (url.isNotEmpty()){
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "No valid link found!", Toast.LENGTH_SHORT).show()
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialAdapterViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapters_social_item, parent, false)


        return SocialAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  mediaLinkList.size
    }

    override fun onBindViewHolder(socialViewHolder: SocialAdapterViewHolder, position: Int) {

        val itemLink = mediaLinkList[position]

        socialViewHolder.setDataToItem(itemLink,position)
        socialViewHolder.setOnclickListeners()
    }

}