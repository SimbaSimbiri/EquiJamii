package com.simbiri.equityjamii

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Person(val imageId: Int, val name: String, val designation: String)

object PeopleData {

    private val imageList = arrayOf(
        R.drawable.good_profile,
        R.drawable.good_profile,
        R.drawable.good_profile,
        R.drawable.good_profile,
        R.drawable.good_profile,
        R.drawable.good_profile,
        R.drawable.good_profile,
        R.drawable.good_profile,
        R.drawable.good_profile,
        R.drawable.good_profile
    )

    private val nameList = arrayOf(
        "Ray Simbiri",
        "Ron George",
        "Jerry Onyango",
        "Peter Otieno",
        "Daniel Ouma",
        "Peter Kirika",
        "Felix Onyango",
        "Charles Mauti",
        "Alloyce Lugoma",
        "Josiah Muriuki"
    )

    private val designationList = arrayOf(

        "Teller at Ngara Branch",
        "Operations Officer at Ngara Branch",
        "Communications officer at Kitui Branch",
        "Human Resource Official at Nairobi Main",
        "ELP lead mentor",
        "EGS Board Member",
        "ELP Mentee class of  '25",
        "EGS Board Member",
        "Teller at Mombasa Branch",
        "ELP Group Leader"

    )

    var peopleList: ArrayList<Person>? = null
        get() {

            if (field != null)
                return field

            field = ArrayList()

            for (imagePosition in imageList.indices) {
                val imageId = imageList[imagePosition]
                val nameofPerson = nameList[imagePosition]
                val designation = designationList[imagePosition]
                val person = Person(imageId, nameofPerson, designation)

                field!!.add(person)
            }

            return field

        }


}


class PeopleDataAdapter(var context: Context, var peopleList: List<Person>) :
    RecyclerView.Adapter<PeopleDataAdapter.PersonViewHolder>() {

    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var positionItem = 1
        private var currentPerson: Person? = null

        private var profilePicImageView: ImageView = itemView.findViewById(R.id.profileOnPeople)
        private var namePersonTextView: TextView = itemView.findViewById(R.id.nameOnPeople)
        private var designationTextView: TextView = itemView.findViewById(R.id.designationOnPeople)


        fun setDatatoItem(personInstance: Person, position: Int) {

            this.positionItem = position
            this.currentPerson = personInstance

            profilePicImageView.setImageResource(currentPerson!!.imageId)
            namePersonTextView.text = currentPerson!!.name
            designationTextView.text = currentPerson!!.designation


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.people_item_sample, parent, false)

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
