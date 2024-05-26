package com.simbiri.equityjamii.adapters

import android.content.Context
import android.net.Uri
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.ui.main_activity.people_page.PersonInfoFragment

class LeadersAllAdapter(var context: Context, var leadersList: List<Person>) :
    RecyclerView.Adapter<LeadersAllAdapter.LeaderViewHolder>() {

    inner class LeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private var positionItem = 1
        private var currentLeader: Person? = null

        private var profilePicImageView: ImageView = itemView.findViewById(R.id.profileOnLeaders)
        private var imageViewBackG : ImageView = itemView.findViewById(R.id.imageViewLeadersBackground)

        private var nameLeaderTextView: TextView = itemView.findViewById(R.id.nameOnLeaders)
        private var designationTextView: TextView = itemView.findViewById(R.id.designationOnLeaders)
        private var cardViewLeader : CardView = itemView.findViewById(R.id.cardViewLeader)
        private var cardViewMaterial : CardView = itemView.findViewById(R.id.materialCardViewLeaders)
        private var verifiedImage: ImageView = itemView.findViewById(R.id.verifiedPersonelImage)

        fun setDatatoItem(leaderInstance: Person, position: Int) {

            this.positionItem = position
            this.currentLeader = leaderInstance

            adjustHolderSize()

            if (leaderInstance.verified){
                verifiedImage.visibility = View.VISIBLE
            }

            Glide.with(itemView)
                .load(Uri.parse(currentLeader!!.profileUri)).into(profilePicImageView)

            Glide.with(itemView).load(currentLeader!!.backGUri).into(imageViewBackG)

            nameLeaderTextView.text = currentLeader!!.name
            designationTextView.text = currentLeader!!.designation + " @ " + currentLeader!!.branch
        }

        fun adjustHolderSize() {

            val layoutParamsLeader = cardViewLeader.layoutParams
            val layoutParamsInnerCard = cardViewMaterial.layoutParams
            val layoutParamsImageBackg = imageViewBackG.layoutParams
            val displayMetrics = DisplayMetrics()

            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            layoutParamsLeader.width = screenWidth - 120
            layoutParamsLeader.height = displayMetrics.heightPixels/4

            layoutParamsInnerCard.width = (layoutParamsLeader.width/ 3.5).toInt()
            layoutParamsInnerCard.height = (layoutParamsLeader.width/ 3.5).toInt()

            layoutParamsImageBackg.height = layoutParamsLeader.height * 3/5
            layoutParamsImageBackg.width = screenWidth - 120

            cardViewLeader.layoutParams = layoutParamsLeader
            cardViewMaterial.layoutParams = layoutParamsInnerCard
            imageViewBackG.layoutParams = layoutParamsImageBackg
        }

        fun setOnClickListeners() {
            itemView.setOnClickListener(this@LeaderViewHolder)
        }

        override fun onClick(view: View?) {

            val leaderDialogFrag = PersonInfoFragment.newInstance(currentLeader!!)
            val transaction =
                (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            leaderDialogFrag.show(transaction, leaderDialogFrag.tag)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_leaders_all_item, parent, false)
        return LeaderViewHolder(view)
    }

    override fun onBindViewHolder(leaderViewHolder: LeaderViewHolder, position: Int) {
        val leaderInstance = leadersList[position]

        leaderViewHolder.setDatatoItem(leaderInstance, position)
        leaderViewHolder.setOnClickListeners()
    }

    override fun getItemCount(): Int {
        return leadersList.size
    }
}
