package com.simbiri.equityjamii.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.ui.main_activity.my_profile.ProfMainFragmentDirections
import com.simbiri.equityjamii.ui.main_activity.people_page.PeopleFragmentDirections
import com.simbiri.equityjamii.ui.main_activity.people_page.PersonInfoFragment

class NetworkAdapter(
    private val context: Context,
    private val personList: List<Person>
) : RecyclerView.Adapter<NetworkAdapter.PersonViewHolder>() {

    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.imageProfile)
        val nameText: TextView = itemView.findViewById(R.id.textNameProfile)
        val designationText: TextView = itemView.findViewById(R.id.textDesignation)
        val messageButton: TextView = itemView.findViewById(R.id.messageUser)
        private var positionItem = 1

        fun setOnClick(personInstance: Person) {
             this.profileImage.setOnClickListener {
                 navigateToPeople(personInstance)

            }

            this.nameText.setOnClickListener {
                navigateToPeople(personInstance)
            }

            this.messageButton.setOnClickListener {
            }

        }

        private fun navigateToPeople(personInstance: Person){
            val navHostFrag =
                (context as AppCompatActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

            val action = ProfMainFragmentDirections.actionOpenPersonInfo(
                personInstance,
                R.id.peopleFrag
            )
            navHostFrag.navController.navigate(action)
        }


        fun setDataToItem(personInstance: Person, position: Int) {
            this.positionItem = position

            Glide.with(itemView.context)
                .load(personInstance.profileUri)
                .into(profileImage)

            nameText.text = personInstance.name
            designationText.text = personInstance.designation + " at " + personInstance.branch

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.adapters_network_item, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(personViewHolder: PersonViewHolder, position: Int) {
        val personInstance = personList[position]

        personViewHolder.setDataToItem(personInstance, position)
        personViewHolder.setOnClick(personInstance)


    }

    override fun getItemCount(): Int {
        return personList.size
    }
}
