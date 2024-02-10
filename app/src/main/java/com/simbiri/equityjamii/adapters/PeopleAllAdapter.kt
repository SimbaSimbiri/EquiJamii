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


class PeopleDataAdapter(var context: Context, var peopleList: List<Person>) :
    RecyclerView.Adapter<PeopleDataAdapter.PersonViewHolder>() {

    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private var positionItem = 1
        private var currentPerson: Person? = null


        private var profilePicImageView: ImageView = itemView.findViewById(R.id.profileOnPeople)
        private var imageViewBackG : ImageView = itemView.findViewById(R.id.imageView4)

        private var namePersonTextView: TextView = itemView.findViewById(R.id.nameOnPeople)
        private var designationTextView: TextView = itemView.findViewById(R.id.designationOnPeople)
        private var cardViewPerson : CardView = itemView.findViewById(R.id.cardViewPerson)
        private var cardViewMaterial : CardView = itemView.findViewById(R.id.materialCardView)


        fun setDatatoItem(personInstance: Person, position: Int) {

            this.positionItem = position
            this.currentPerson = personInstance

            adjustHolderSize()

            Glide.with(itemView)
                .load(Uri.parse(currentPerson!!.profileUri)).into(profilePicImageView)


            Glide.with(itemView).load(currentPerson!!.backGUri).into(imageViewBackG)

            namePersonTextView.text = currentPerson!!.name
            designationTextView.text = currentPerson!!.designation + "\n@" + currentPerson!!.branch

        }

        fun adjustHolderSize(){

            val layoutParamsPerson = cardViewPerson.layoutParams
            val layoutParamsInnerCard = cardViewMaterial.layoutParams
            val layoutParamsImageBackg = imageViewBackG.layoutParams
            val displayMetrics = DisplayMetrics()


            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            layoutParamsPerson.width = screenWidth/2 - 60

            layoutParamsInnerCard.width = layoutParamsPerson.width -60
            layoutParamsInnerCard.height = layoutParamsPerson.width -60

            layoutParamsImageBackg.height = layoutParamsInnerCard.height/2

            cardViewPerson.layoutParams = layoutParamsPerson
            cardViewMaterial.layoutParams = layoutParamsInnerCard
            imageViewBackG.layoutParams = layoutParamsImageBackg
        }

        fun setOnClickListeners() {
            itemView.setOnClickListener(this@PersonViewHolder)
        }

        override fun onClick(view: View?) {

            val personDialogFrag = PersonInfoFragment.newInstance(currentPerson!!)
            val transaction =
                (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            personDialogFrag.show(transaction, personDialogFrag.tag)

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapters_people_all_item, parent, false)


        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(personViewHolder: PersonViewHolder, position: Int) {
        val personInstance = peopleList[position]

        personViewHolder.setDatatoItem(personInstance, position)
        personViewHolder.setOnClickListeners()
    }

    override fun getItemCount(): Int {

        return peopleList.size
    }
}
