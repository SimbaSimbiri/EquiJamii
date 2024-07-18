import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.OtherProfilesAdapter
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_MEMBERS_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_ADMINS_SUB_COLLECTION
import com.simbiri.equityjamii.data.model.Workspace
import com.simbiri.equityjamii.data.model.Person

class WorkspaceAdapter(
    private val context: Context,
    private val workspaces: List<Workspace>
) : RecyclerView.Adapter<WorkspaceAdapter.WorkspaceViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkspaceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapters_workspace_item, parent, false)
        return WorkspaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkspaceViewHolder, position: Int) {
        val workspace = workspaces[position]
        holder.bind(workspace)
    }

    override fun getItemCount(): Int = workspaces.size

    inner class WorkspaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val workspaceHeaderCard: CardView = itemView.findViewById(R.id.workspaceHeaderCard)
        private val imageMotivational: ImageView = itemView.findViewById(R.id.imageMotivational)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private val textKaribu: TextView = itemView.findViewById(R.id.textKaribu)
        private val notificationsImage: ImageView = itemView.findViewById(R.id.notificationsImage)
        private val signInWorkspace: TextView = itemView.findViewById(R.id.signInWorkspace)
        private val textPeople: TextView = itemView.findViewById(R.id.textPeople)
        private val adminsRecyclerView: RecyclerView = itemView.findViewById(R.id.adminProfileRecyclerView)

        fun bind(workspace: Workspace) {
            loadImage(workspace)
            setAboutText(workspace)
            setMembersCount(workspace)
            setupAdminsRecyclerView(workspace)
            setupClickListeners()
        }

        private fun loadImage(workspace: Workspace) {
            workspace.titleImage?.fileUri?.let {
                Glide.with(context)
                    .load(it)
                    .into(imageMotivational)
            }
        }

        private fun setAboutText(workspace: Workspace) {
            textKaribu.text = workspace.about
        }

        private fun setMembersCount(workspace: Workspace) {
            firestore.collection(WORKSPACE_COLLECTION)
                .document(workspace.workspaceId ?: "")
                .collection(WORKSP_MEMBERS_SUB_COLLECTION)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val onePerson = querySnapshot.documents.first().toObject(Person::class.java)
                    val memberCount = querySnapshot.size()
                    textPeople.text = "${onePerson!!.name} and $memberCount others"
                }
        }

        private fun setupAdminsRecyclerView(workspace: Workspace) {
            adminsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adminsList = mutableListOf<Person>()
            val adapter = OtherProfilesAdapter(context, adminsList)
            adminsRecyclerView.adapter = adapter

            firestore.collection(WORKSPACE_COLLECTION)
                .document(workspace.workspaceId ?: "")
                .collection(WORKSP_ADMINS_SUB_COLLECTION)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val person = document.toObject(Person::class.java)
                        if (person != null) {
                            adminsList.add(person)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
        }

        private fun setupClickListeners() {
            signInWorkspace.setOnClickListener {
                // Handle Sign In click
            }

            notificationsImage.setOnClickListener {
                // Handle Notifications click
            }
        }
    }
}
