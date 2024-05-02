package com.simbiri.equityjamii.adapters

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Post
import java.text.SimpleDateFormat
import java.util.Locale


class PostAdapter(var context: Context, var postList: MutableList<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>(){

    inner class PostViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var imagePostUser: ImageView = itemView.findViewById(R.id.imageUserPost)
        var imagePosted :ImageView =  itemView.findViewById(R.id.imagePosted)
        var nameText: TextView = itemView.findViewById(R.id.textNamePost)
        var captionText : TextView =  itemView.findViewById(R.id.captionExpandable)
        var dateText : TextView = itemView.findViewById(R.id.textDatePost)
        var thumbsLikePost :ImageView =  itemView.findViewById(R.id.likesImage)
        var numLikes: TextView  =  itemView.findViewById(R.id.numLikesText)
        private val MAX_CHAR_COLLAPSED = 100
        private var isExpanded = false

        var currentPost : Post? =  null
        var person : Person?  = null
        var currentPosition = 0

        private fun toggleCaptionExpansion() {
            isExpanded = !isExpanded
            setTextsToggled(currentPost?.caption, isExpanded)
        }
        private fun setTextsToggled(caption: String?, isExpanded: Boolean) {
            val spannable = SpannableStringBuilder(caption)
            if (caption != null) {
                if (isExpanded) {
                    spannable.append(" ...read less")
                } else if (caption.length > MAX_CHAR_COLLAPSED) {
                    spannable.delete(MAX_CHAR_COLLAPSED, caption.length)
                    spannable.append(" ...read more")
                }
            }

            spannable.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    toggleCaptionExpansion()
                }
            }, spannable.length - 9, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            captionText.text = spannable
            captionText.movementMethod = LinkMovementMethod.getInstance()
        }

        fun setDataToPost(postInstance : Post, position: Int){
            this.currentPosition = position

            this.currentPost = postInstance
            this.person = postInstance.person

            setImages(postInstance.image, person!!.profileUri, postInstance.liked)
            setTexts(postInstance.caption, postInstance.time, postInstance.likes, person!!.name)
            toggleCaptionExpansion()

        }

        private fun setTexts(caption: String, time: Timestamp?, likes: Int, name: String) {

            this.captionText.text =  caption
            this.numLikes.text = likes.toString()
            this.nameText.text = name

            val date_raw = time?.toDate()
            val date_format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date_str = date_format.format(date_raw!!)
            this.dateText.text = date_str
        }

        private fun setImages(imagePosted: String?, imageUser: String?, liked: Boolean) {
            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenHeight = displayMetrics.heightPixels
            val screenWidth = displayMetrics.widthPixels

            Glide.with(context).load(imagePosted).
            apply(RequestOptions().override(screenWidth - 20, screenHeight*3/5 ))
                .centerCrop()
                .into(this.imagePosted)
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
