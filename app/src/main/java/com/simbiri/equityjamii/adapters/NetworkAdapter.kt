package com.simbiri.equityjamii.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Person

class NetworkAdapter(
    private val context: Context,
    private val personList: List<Person>
) : RecyclerView.Adapter<NetworkAdapter.PersonViewHolder>() {

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.imageProfile)
        val nameText: TextView = itemView.findViewById(R.id.textNameProfile)
        val designationText: TextView = itemView.findViewById(R.id.textDesignation)
        val messageButton: TextView = itemView.findViewById(R.id.messageUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapters_network_item, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(personViewHolder: PersonViewHolder, position: Int) {
        val person = personList[position]

        Glide.with(context)
            .load(person.profileUri)
            .into(personViewHolder.profileImage)

        personViewHolder.nameText.text = person.name
        personViewHolder.designationText.text = person.designation +" at " + person.branch

        personViewHolder.messageButton.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return personList.size
    }
}
