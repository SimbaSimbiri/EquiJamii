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

class OtherProfilesAdapter(var context: Context, var peopleList: List<Person>) :
    RecyclerView.Adapter<OtherProfilesAdapter.OtherProfViewHolder>() {
    inner class OtherProfViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview),
        View.OnClickListener {

        private var positionItem = 1
        private var currentPerson: Person? = null

        private var cardViewHolder:CardView = itemView.findViewById(R.id.cardViewOtherProfiles)
        private var profilePicImageView: ImageView = itemView.findViewById(R.id.imageOtherProfiles)
        private var namePersonTextView: TextView = itemView.findViewById(R.id.textNameOtherProfiles)


        fun setOnClickListeners() {
            itemView.setOnClickListener(this@OtherProfViewHolder)
        }

        override fun onClick(v: View?) {
            val personDialogFrag = PersonInfoFragment.newInstance(currentPerson!!)
            val transaction =
                (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            personDialogFrag.show(transaction, personDialogFrag.tag)
        }

        fun setDatatoItem(personInstance: Person, position: Int) {
            this.positionItem = position
            this.currentPerson = personInstance
            adjustHolderSize()

            Glide.with(itemView)
                .load(Uri.parse(currentPerson!!.profileUri))
                .fitCenter().into(profilePicImageView)

            namePersonTextView.text = currentPerson!!.name
        }

        private fun adjustHolderSize() {
            val layoutParamsHolder = cardViewHolder.layoutParams
            val displayMetrics = DisplayMetrics()

            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            layoutParamsHolder.width = (screenWidth/3.5).toInt()
            layoutParamsHolder.height = (screenWidth/3.5 + 50.0).toInt()

            cardViewHolder.layoutParams = layoutParamsHolder


        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OtherProfViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.adapter_similar_profiles_item, parent, false)

        return OtherProfViewHolder(view)
    }

    override fun onBindViewHolder(otherProfHolder: OtherProfViewHolder, position: Int) {
        val personInstance = peopleList[position]

        otherProfHolder.setDatatoItem(personInstance, position)
        otherProfHolder.setOnClickListeners()
    }

    override fun getItemCount(): Int = peopleList.size

}