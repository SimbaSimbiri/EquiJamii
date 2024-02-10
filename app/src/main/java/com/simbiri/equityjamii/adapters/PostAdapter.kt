package com.simbiri.equityjamii.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Post


class PostAdapter (var context: Context, var userList: List<Person>, var  postList: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>(){

    inner class PostViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var imagePostUser: ImageView = itemView.findViewById(R.id.imageUserPost)
        var imagePosted :ImageView =  itemView.findViewById(R.id.imagePosted)
        var captionText : TextView =  itemView.findViewById(R.id.captionExpandable)
        var dateText : TextView = itemView.findViewById(R.id.textDatePost)
        var thumbsLikePost :ImageView =  itemView.findViewById(R.id.likesImage)
        var numLikes: TextView  =  itemView.findViewById(R.id.numLikesText)

        var currentPost : Post? =  null
        var person : Person?  = null
        var currentPosition = 1


        fun setDataToPost(postInstance : Post, position: Int){
            this.currentPosition = position
            this.currentPost = postInstance
            this.person = postInstance.person

            setImages(postInstance.image, person!!.profileUri, postInstance.liked)
            setTexts(postInstance.caption, postInstance.date, postInstance.likes)

        }


        private fun setTexts(caption: String, date: String, likes: Int) {

            this.captionText.text =  caption
            this.dateText.text = date
            this.numLikes.text = likes.toString()
        }

        private fun setImages(imagePosted: String, imageUser: String, liked: Boolean) {

            Glide.with(context).load(imagePosted).into(this.imagePosted)
            Glide.with(context).load(imageUser).into(this.imagePostUser)

            if(liked){this.thumbsLikePost.setImageResource(R.drawable.liked)}
            else{this.thumbsLikePost.setImageResource(R.drawable.not_liked_yet)}

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.adapters_post_item, parent, false)

        return PostViewHolder(view)

    }

    override fun getItemCount(): Int  = postList.size

    override fun onBindViewHolder(postViewHolder: PostViewHolder, position: Int) {
        val post =  postList[position]

        postViewHolder.setDataToPost(post, position)


    }

}
