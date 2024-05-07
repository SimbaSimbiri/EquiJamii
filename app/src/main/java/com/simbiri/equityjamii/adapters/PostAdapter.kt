package com.simbiri.equityjamii.adapters

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Post
import com.simbiri.equityjamii.ui.main_activity.people_page.PersonInfoFragment
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale


class PostAdapter(var context: Context, var postList: MutableList<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val firestoreCollection = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imagePostUser: ImageView = itemView.findViewById(R.id.imageUserPost)
        var imagePosted: ImageView = itemView.findViewById(R.id.imagePosted)
        var nameText: TextView = itemView.findViewById(R.id.textNamePost)
        var captionText: TextView = itemView.findViewById(R.id.captionExpandable)
        var dateText: TextView = itemView.findViewById(R.id.textDatePost)
        var thumbsLikePost: ImageView = itemView.findViewById(R.id.likesImage)
        var numLikes: TextView = itemView.findViewById(R.id.numLikesText)
        private val MAX_CHAR_COLLAPSED = 90
        private var isExpanded = false

        var currentPost: Post? = null
        var person: Person? = null
        var currentPosition = 0

        private fun toggleCaptionExpansion() {
            isExpanded = !isExpanded
            setTextsToggled(currentPost?.caption, isExpanded)
        }

        private fun setTextsToggled(caption: String?, isExpanded: Boolean) {
            val spannable = SpannableStringBuilder(caption)
            if (caption != null) {
                if (caption.length > MAX_CHAR_COLLAPSED) {
                    if (isExpanded) {
                        spannable.append(" ...read less")
                    } else {
                        spannable.delete(MAX_CHAR_COLLAPSED, caption.length)
                        spannable.append(" ...read more")
                    }

                    spannable.setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            toggleCaptionExpansion()
                        }
                    }, spannable.length - 9, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    captionText.text = spannable
                    captionText.movementMethod = LinkMovementMethod.getInstance()
                }
            }


        }

        fun setDataToPost(postInstance: Post, position: Int) {
            this.currentPosition = position
            this.currentPost = postInstance
            setOnClicks()
            findPerson(postInstance.userId)

        }

        private fun findPerson(userId: String) {
            firestoreCollection.document(userId).get()
                .addOnCompleteListener { taskDocSnapShot ->
                    if (taskDocSnapShot.isSuccessful) {
                        if (taskDocSnapShot.result.exists()) {
                            this.person = taskDocSnapShot.result.toObject(Person::class.java)

                            setImages(
                                this.currentPost!!.image, this.person!!.profileUri,
                                this.currentPost!!.liked
                            )
                            setTexts(
                                this.currentPost!!.caption, this.currentPost!!.time,
                                this.currentPost!!.likes, this.person!!.name
                            )
                            setTextsToggled(currentPost?.caption, false)

                        } else {
                            Toast.makeText(
                                itemView.context,
                                "Unable to retrieve person",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }


        private fun setTexts(caption: String, time: Timestamp?, likes: Int, name: String) {

            this.captionText.text = caption
            this.numLikes.text = likes.toString()
            this.nameText.text = name
            this.dateText.text = displayDate(time)
        }

        private fun displayDate(timestamp: Timestamp?): String? {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

            return try {
                val date = timestamp?.toDate()
                val currentDate = java.util.Date()

                val timeDifference = currentDate.time - date!!.time
                val daysDifference = timeDifference / (1000 * 60 * 60 * 24)

                when {
                    daysDifference >= 7 -> dateFormat.format(date)
                    daysDifference == 1.0.toLong() -> "1 day ago"
                    daysDifference > 1 -> "$daysDifference days ago"
                    timeDifference >= 60 * 60 * 1000 -> "${timeDifference / (60 * 60 * 1000)} hours ago"
                    timeDifference >= 60 * 1000 -> "${timeDifference / (60 * 1000)} minutes ago"
                    else -> "Just now"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

        private fun setImages(imagePosted: String?, imageUser: String?, liked: Boolean) {
            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenHeight = displayMetrics.heightPixels
            val screenWidth = displayMetrics.widthPixels

            Glide.with(context).load(imagePosted)
                .apply(RequestOptions().override(screenWidth - 50, screenHeight * 3 / 5))
                .fitCenter().into(this.imagePosted)
            Glide.with(context).load(imageUser).into(this.imagePostUser)

            if (liked) {
                this.thumbsLikePost.setImageResource(R.drawable.liked)
            } else {
                this.thumbsLikePost.setImageResource(R.drawable.not_liked_yet)
            }

        }

        private fun setOnClicks() {

            this.thumbsLikePost.setOnClickListener {
                likeUnlikePost()
            }

            val gestureDetector = GestureDetectorCompat(
                itemView.context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDoubleTap(e: MotionEvent): Boolean {
                        likeUnlikePost()
                        return true
                    }
                })

            this.imagePosted.setOnTouchListener { view, event ->

                view.performClick()
                gestureDetector.onTouchEvent(event)
            }

            this.captionText.setOnTouchListener { v, event ->
                v.performClick()
                gestureDetector.onTouchEvent(event)
            }



            this.imagePostUser.setOnClickListener {
                showPerson(this.person)
            }

            this.nameText.setOnClickListener {
                showPerson(this.person)
            }

        }

        private fun likeUnlikePost() {
            this.currentPost!!.liked = !this.currentPost!!.liked

            var numFanLikes = this.currentPost!!.likes

            if (this.currentPost!!.liked) {

                this.thumbsLikePost.setImageResource(R.drawable.liked)
                numFanLikes += 1
                this.currentPost!!.likes = numFanLikes
                this.numLikes.text =
                    itemView.resources.getString(R.string.num_likes, numFanLikes)
            } else {
                this.thumbsLikePost.setImageResource(R.drawable.not_liked_yet)
                numFanLikes -= 1
                this.currentPost!!.likes = numFanLikes
                this.numLikes.text =
                    itemView.resources.getString(R.string.num_likes, numFanLikes)
            }
        }

        private fun showPerson(person: Person?) {

            val personDialogFrag = PersonInfoFragment.newInstance(person!!)
            val transaction =
                (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            personDialogFrag.show(transaction, personDialogFrag.tag)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.adapters_post_item, parent, false)
        return PostViewHolder(view)

    }

    override fun getItemCount(): Int = postList.size

    override fun onBindViewHolder(postViewHolder: PostViewHolder, position: Int) {
        val post = postList[position]
        postViewHolder.setDataToPost(post, position)

    }

}
