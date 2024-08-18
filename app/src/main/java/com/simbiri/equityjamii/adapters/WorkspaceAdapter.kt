import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.OtherProfilesAdapter
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_MEMBERS_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_ADMINS_SUB_COLLECTION
import com.simbiri.equityjamii.data.objects.AuthUtils
import com.simbiri.equityjamii.data.model.Workspace
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.objects.UserNetworkUtils
import com.simbiri.equityjamii.ui.main_activity.workspace_page.ViewWorkspFragment
import com.simbiri.equityjamii.ui.main_activity.workspace_page.WorkspaceViewModel
import kotlinx.coroutines.launch

class WorkspaceAdapter(
    private val context: Context,
    private val workspaces: List<Workspace>
) : RecyclerView.Adapter<WorkspaceAdapter.WorkspaceViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkspaceViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.adapters_workspace_item, parent, false)
        return WorkspaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkspaceViewHolder, position: Int) {
        val workspace = workspaces[position]
        holder.bind(workspace)
    }

    override fun getItemCount(): Int = workspaces.size

    inner class WorkspaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val workspHeaderCard: CardView = itemView.findViewById(R.id.workspaceHeaderCard)
        private val imageMotivational: ImageView = itemView.findViewById(R.id.imageMotivational)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private val textTitle: TextView = itemView.findViewById(R.id.textKaribu)
        private val aboutTextHead: TextView = itemView.findViewById(R.id.aboutTextHead)
        private val textAbout: TextView = itemView.findViewById(R.id.aboutTextContent)
        private val notificationsImage: ImageView = itemView.findViewById(R.id.notificationsImage)
        private val signInWorkspace: TextView = itemView.findViewById(R.id.signInWorkspace)
        private val textPeople: TextView = itemView.findViewById(R.id.textPeople)
        private val adminsRecyclerView: RecyclerView =
            itemView.findViewById(R.id.adminProfileRecyclerView)
        private val viewModel = WorkspaceViewModel()
        private val loginLayout: TextInputLayout = itemView.findViewById(R.id.passwordLayout)
        private val loginInput: TextInputEditText = itemView.findViewById(R.id.passET)
        private val submitCode: CardView = itemView.findViewById(R.id.submitPassCardView)
        private var currentPerson: Person? = null
        private var textPeopleInWorksp: String? = null
        private var membersCount: Int? = null
        val firestore = FirebaseFirestore.getInstance()
        val workspaceCollection = firestore.collection(
            WORKSPACE_COLLECTION
        )

        fun bind(workspace: Workspace) {
            AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()) { person: Person? ->
                currentPerson = person
            }
            adjustParams()
            loadImage(workspace)
            setAboutText(workspace)
            setMembersCount(workspace)
            setupAdminsRecyclerView(workspace)
            setupClickListeners(workspace)
        }

        private fun adjustParams() {

            val layoutParams = workspHeaderCard.layoutParams
            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenHeight = displayMetrics.heightPixels
            layoutParams.height = screenHeight / 3
            layoutParams.width = layoutParams.width

            workspHeaderCard.layoutParams = layoutParams

        }

        private fun loadImage(workspace: Workspace) {

            workspace.titleImage?.fileUri?.let {
                Glide.with(context)
                    .load(it)
                    .into(imageMotivational)
            }
        }

        private fun setAboutText(workspace: Workspace) {
            aboutTextHead.text = "About ${workspace.titleImage!!.fileTitle}"
            textAbout.text = workspace.description
            textTitle.text = workspace.titleImage!!.fileTitle

        }

        private fun setMembersCount(workspace: Workspace) {
            if (membersCount != null || !textPeopleInWorksp.isNullOrEmpty()) {
                textPeople.text = textPeopleInWorksp
                return
            }

            firestore.collection(WORKSPACE_COLLECTION)
                .document(workspace.workspaceId ?: "")
                .collection(WORKSP_MEMBERS_SUB_COLLECTION)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        textPeople.text = "No members found"
                        return@addOnSuccessListener
                    }

                    val friendUserInWorksp = querySnapshot.documents.shuffled()
                        .filter { doc ->
                            currentPerson?.network?.followerList?.contains(doc.id) == true ||
                                    currentPerson?.network?.followingList?.contains(doc.id) == true
                        }

                    viewModel.viewModelScope.launch {
                        val onePerson = if (friendUserInWorksp.isNotEmpty()) {
                            UserNetworkUtils.narrowDownUsers(mutableListOf(friendUserInWorksp[0].id))
                                .firstOrNull()
                        } else {
                            UserNetworkUtils.narrowDownUsers(
                                mutableListOf(
                                    querySnapshot.documents.shuffled().firstOrNull()?.id
                                        ?: return@launch
                                )
                            ).firstOrNull() ?: return@launch
                        }

                        val memberCount = querySnapshot.size()
                        membersCount = memberCount
                        textPeople.text = if (onePerson != null) {
                            "${onePerson.name} and ${memberCount - 1} others"
                        } else {
                            "No valid members found"
                        }
                        textPeopleInWorksp = textPeople.text.toString()
                    }
                }
        }

        private fun setupAdminsRecyclerView(workspace: Workspace) {
            adminsRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adminsList = mutableListOf<Person>()
            val adapter = OtherProfilesAdapter(context, adminsList)
            adminsRecyclerView.adapter = adapter

            firestore.collection(WORKSPACE_COLLECTION)
                .document(workspace.workspaceId!!)
                .collection(WORKSP_ADMINS_SUB_COLLECTION)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    viewModel.viewModelScope.launch {
                        val listIds = mutableListOf<String>()
                        querySnapshot.documents.forEach { doc ->
                            listIds.add(doc.id)
                        }

                        adminsList.addAll(UserNetworkUtils.narrowDownUsers(listIds))
                        adapter.notifyDataSetChanged()

                    }
                }
        }

        private fun setupClickListeners(workspace: Workspace) {

            signInWorkspace.setOnClickListener {
                val frag = ViewWorkspFragment.newInstance(workspace.workspaceId!!)
                val transaction =
                    (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                frag.show(transaction, frag.tag)

                /*loginLayout.visibility = View.VISIBLE
                submitCode.visibility = View.VISIBLE*/
            }

            submitCode.setOnClickListener {
                val code = loginInput.text.toString()

                if (code.isNotEmpty()) {
                    loginInput.text?.clear()
                    loginLayout.visibility = View.GONE
                    submitCode.visibility = View.GONE
                    signIn(code, workspace.passCode, workspace.workspaceId!!)
                } else {
                    Toast.makeText(
                        context,
                        "Empty fields are not allowed!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            notificationsImage.setOnClickListener {
                // Handle Notifications click
            }

        }

        private fun signIn(code: String, loginCode: String, id: String) {
            progressBar.visibility = View.VISIBLE
            if (code.contentEquals(loginCode)) {
                progressBar.visibility = View.GONE
                val frag = ViewWorkspFragment.newInstance(id)
                val transaction =
                    (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                frag.show(transaction, frag.tag)

            } else {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    context,
                    "Wrong log in password, contact workspace admin for help",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
