package com.simbiri.equityjamii.ui

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.simbiri.equityjamii.R

data class User(val  profileImage: String, val backGroundImage : String, val name : String, val designation : String, val branch : String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: ""
    ) {
    }

    constructor() : this("", "", "","","")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(profileImage)
        parcel.writeString(backGroundImage)
        parcel.writeString(name)
        parcel.writeString(designation)
        parcel.writeString(branch)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}

data class Post (val image : String, val caption : String, val date : String, val likes : Int, val user : User, val postId : String , var liked : Boolean)



class PostAdapter (var context: Context, var userList: List<User>, var  postList: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>(){

    inner class PostViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var imagePostUser: ImageView = itemView.findViewById(R.id.imageUserPost)
        var imagePosted :ImageView =  itemView.findViewById(R.id.imagePosted)
        var captionText : TextView =  itemView.findViewById(R.id.captionExpandable)
        var dateText : TextView = itemView.findViewById(R.id.textDatePost)
        var thumbsLikePost :ImageView =  itemView.findViewById(R.id.likesImage)
        var numLikes: TextView  =  itemView.findViewById(R.id.numLikesText)

        var currentPost : Post? =  null
        var user : User?  = null
        var currentPosition = 1
        var currentPostId = ""



        fun setPostDetails(imagePosted: String, caption: String, date: String, likes: Int, user: User, postId: String , liked: Boolean) {
            this.currentPostId =  postId

            setImages(imagePosted, user.profileImage, liked)

            setTexts(caption, date, likes)

        }

        private fun setTexts(caption: String, date: String, likes: Int) {

            this.captionText.text =  caption
            this.dateText.text = date
            this.numLikes.text = likes.toString()
        }

        private fun setImages(imagePosted: String, imageUser: String, liked: Boolean) {
            Glide.with(context).load(imagePosted).into(this.imagePosted)
            Glide.with(context).load(imageUser).into(this.imagePostUser)

            if(liked){this.thumbsLikePost.setImageResource(R.drawable.thumb_up_on)}
            else{this.thumbsLikePost.setImageResource(R.drawable.thumb_up_off)}

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)

        return PostViewHolder(view)

    }

    override fun getItemCount(): Int  = postList.size

    override fun onBindViewHolder(postViewHolder: PostViewHolder, position: Int) {
        val post =  postList[position]

        postViewHolder.setPostDetails(post.image, post.caption, post.date, post.likes, post.user, post.postId, post.liked)

    }

}
