package com.simbiri.equityjamii.adapters
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Person


class PeopleDataAdapter(var context: Context, var peopleList: List<Person>) :
    RecyclerView.Adapter<PeopleDataAdapter.PersonViewHolder>() {

    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var positionItem = 1
        private var currentPerson: Person? = null

        private var profilePicImageView: ImageView = itemView.findViewById(R.id.profileOnPeople)
        private var namePersonTextView: TextView = itemView.findViewById(R.id.nameOnPeople)
        private var designationTextView: TextView = itemView.findViewById(R.id.designationOnPeople)
        private var cardViewPerson : CardView = itemView.findViewById(R.id.cardViewPerson)
        private var cardViewMaterial : CardView = itemView.findViewById(R.id.materialCardView)
        private var imageViewBackG : ImageView = itemView.findViewById(R.id.imageView4)


        fun setDatatoItem(personInstance: Person, position: Int) {

            this.positionItem = position
            this.currentPerson = personInstance

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
            /*
            layoutParams.height = layoutParams.width * 4/3*/

            profilePicImageView.setImageResource(currentPerson!!.imageId)
            namePersonTextView.text = currentPerson!!.name
            designationTextView.text = currentPerson!!.designation


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapters_people_all_item, parent, false)


        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(personViewHolder: PersonViewHolder, position: Int) {
        val personInstance = peopleList[position]

        personViewHolder.setDatatoItem(personInstance, position)
    }

    override fun getItemCount(): Int {

        return peopleList.size
    }
}
