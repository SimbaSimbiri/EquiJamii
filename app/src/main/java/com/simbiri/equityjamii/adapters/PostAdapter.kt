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
import com.simbiri.equityjamii.constants.POST_COLLECTION
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Post
import com.simbiri.equityjamii.ui.main_activity.jamii_page.AddPostFragment
import com.simbiri.equityjamii.ui.main_activity.people_page.PersonInfoFragment
import java.text.SimpleDateFormat
import java.util.Locale


class PostAdapter(
    var context: Context,
    var postList: MutableList<Post>,
    var editable: Boolean = false
) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val firestoreUsersCollection =
        firestore.collection(USERS_COLLECTION)
    private val firestorePostsCollection = firestore.collection(
        POST_COLLECTION
    )
    private val currentUserId = AuthUtils.getCurrentUserId()

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imagePostUser: ImageView = itemView.findViewById(R.id.imageUserPost)
        var imagePosted: ImageView = itemView.findViewById(R.id.imagePosted)
        var nameText: TextView = itemView.findViewById(R.id.textNamePost)
        var captionText: TextView = itemView.findViewById(R.id.captionExpandable)
        var dateText: TextView = itemView.findViewById(R.id.textDatePost)
        var thumbsLikePost: ImageView = itemView.findViewById(R.id.likesImage)
        var numLikes: TextView = itemView.findViewById(R.id.numLikesText)
        var verifiedImage: ImageView = itemView.findViewById(R.id.verifiedPersonelImage)
        var editPost: TextView = itemView.findViewById(R.id.editPost)
        var deletePost: ImageView = itemView.findViewById(R.id.deletePost)


        private val MAX_CHAR_COLLAPSED = 90
        private var isExpanded = false

        var currentPost: Post? = null
        var person: Person? = null
        var currentPosition = 0

        private fun toggleCaptionExpansion() {
            isExpanded = !isExpanded
            setTextsToggled(currentPost?.caption, isExpanded)
        }

        private fun likeUnlikeFirebaseLogic(userId: String, postId: String) {
            val currPostRef = firestorePostsCollection.document(postId)
            val likesSubCollectionCurrent = currPostRef
                .collection("Likes")

            val userLikeDocument = likesSubCollectionCurrent.document(userId)

            userLikeDocument.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        userLikeDocument.delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Post unliked by user $userId",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if (this.currentPost!!.liked && this.currentPost!!.likes > 0) {
                                    updateUIForUnlike()
                                }
                            }
                    } else {
                        userLikeDocument.set(emptyMap<String, Any>())
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Post liked by user $userId",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if (!this.currentPost!!.liked) {
                                    updateUIForLike()
                                }
                            }

                    }
                } else {
                    Toast.makeText(
                        context,
                        "Error checking like status for user $userId",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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

        fun updateDelete(postInstance: Post) {
            if (editable) {
                deletePost.visibility = View.VISIBLE
                editPost.visibility = View.VISIBLE
            }

            deletePost.setOnClickListener {
                firestorePostsCollection.document(postInstance.documentId!!).delete()
                Toast.makeText(context, "Post was deleted", Toast.LENGTH_SHORT).show()
                postList.remove(postInstance)
                notifyDataSetChanged()
            }

            editPost.setOnClickListener {
                val editPostDialog = AddPostFragment.newInstance(postInstance)
                val transaction =
                    (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                editPostDialog.show(transaction, editPostDialog.tag)
            }


        }

        private fun findPerson(userId: String) {
            firestoreUsersCollection.document(userId).get()
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
                    daysDifference == 1.0.toLong() -> "1d ago"
                    daysDifference > 1 -> "$daysDifference" + "d ago"
                    timeDifference >= 60 * 60 * 1000 -> "${timeDifference / (60 * 60 * 1000)} h ago"
                    timeDifference >= 60 * 1000 -> "${timeDifference / (60 * 1000)}m ago"
                    else -> "just now"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Couldn't display date"
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

            if (person!!.verified) {
                verifiedImage.visibility = View.VISIBLE
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

        private fun updateUIForLike() {
            this.currentPost!!.liked = true
            val postRef = firestorePostsCollection.document(currentPost!!.documentId!!)
            postRef.update("liked", true)

            val numFanLikes = this.currentPost!!.likes + 1
            this.currentPost!!.likes = numFanLikes
            this.thumbsLikePost.setImageResource(R.drawable.liked)
            this.numLikes.text = itemView.resources.getString(R.string.num_likes, numFanLikes)
        }

        private fun updateUIForUnlike() {
            this.currentPost!!.liked = false
            val postRef = firestorePostsCollection.document(currentPost!!.documentId!!)
            postRef.update("liked", false)

            val numFanLikes = this.currentPost!!.likes - 1
            this.currentPost!!.likes = numFanLikes
            this.thumbsLikePost.setImageResource(R.drawable.not_liked_yet)
            this.numLikes.text = itemView.resources.getString(R.string.num_likes, numFanLikes)
        }

        private fun likeUnlikePost() {
            val postId = currentPost?.documentId ?: return
            likeUnlikeFirebaseLogic(currentUserId!!, postId)
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
        postViewHolder.updateDelete(post)

    }

}
