package com.simbiri.equityjamii.ui.main_activity.workspace_page

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.SchedulingDataAdapter
import com.simbiri.equityjamii.adapters.TaskDataAdapter
import com.simbiri.equityjamii.data.model.AvailableSlots


var listTitleBranch = ArrayList<String>()
var listDescriptionBranch = ArrayList<String>()
var listAssignedToBranch = ArrayList<String>()

var listTitlePersonal = ArrayList<String>()
var listDescriptionPersonal = ArrayList<String>()
var listAssignedToPersonal = ArrayList<String>()

class myWorkspace : Fragment(){

    companion object {
        fun newInstance() = myWorkspace()
    }

    private lateinit var viewModel: WorkspaceViewModel

    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var addTask: Button
    lateinit var branchRecyclerView: RecyclerView
    lateinit var personalRecyclerView: RecyclerView
    lateinit var cardViewBranch: CardView
    lateinit var cardViewPersonal: CardView
    lateinit var taskBranchAdapter : TaskDataAdapter
    lateinit var taskPersonalAdapter: TaskDataAdapter
    lateinit var timeSlotAdapter: SchedulingDataAdapter
    private lateinit var availableTimeSlotsRecycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.my_workspace_page, container, false)

        appBarLayout = view.findViewById(R.id.appBarLayout)
        collapsingToolbarLayout = appBarLayout.findViewById(R.id.collapsingToolbar)
        toolbar = view.findViewById(R.id.topAppBar)
        cardViewBranch =view.findViewById(R.id.cardViewBranchTask)
        cardViewPersonal =view.findViewById(R.id.cardViewPersonalTask)
        addTask = view.findViewById(R.id.addTaskButton)
        branchRecyclerView = view.findViewById(R.id.recyclerViewBranchTask)
        personalRecyclerView = view.findViewById(R.id.recyclerViewPersonalTask)
        availableTimeSlotsRecycler =  view.findViewById(R.id.timeSlotsRecycler)


        val drawerLayout =
            requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout) //for you to instantiate  a drawer layout you have to use the activity's context since

        val toggle = ActionBarDrawerToggle(
            requireActivity(), drawerLayout, toolbar,
            R.string.openDrawer,
            R.string.closeDrawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        addTask.setOnClickListener {
            val bottomFragmentAddtask =  AddTaskDialogFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            bottomFragmentAddtask.show(transaction, bottomFragmentAddtask.tag)
        }

        cardViewBranch.setOnClickListener{
            branchRecyclerView.adapter!!.notifyDataSetChanged()
        }

        cardViewPersonal.setOnClickListener{
            personalRecyclerView.adapter!!.notifyDataSetChanged()
        }

        setUpTasksRecyclers()

        return view
    }

    fun setUpTasksRecyclers() {
        val context = requireContext()

        taskBranchAdapter = TaskDataAdapter(
            context,
            listTitleBranch,
            listDescriptionBranch,
            listAssignedToBranch
        )
        taskPersonalAdapter = TaskDataAdapter(
            context,
            listTitlePersonal,
            listDescriptionPersonal,
            listAssignedToPersonal
        )
        timeSlotAdapter = SchedulingDataAdapter(context, AvailableSlots.timeSlotToday!!)


        val layoutManagerBranch = LinearLayoutManager(context)
        layoutManagerBranch.orientation = RecyclerView.VERTICAL
        val layoutManagerPersonal = LinearLayoutManager(context)
        layoutManagerPersonal.orientation = RecyclerView.VERTICAL
        val  layoutManagerTimeSlot = LinearLayoutManager(context)
        layoutManagerTimeSlot.orientation = RecyclerView.HORIZONTAL

        branchRecyclerView.adapter = taskBranchAdapter
        branchRecyclerView.layoutManager = layoutManagerBranch
        branchRecyclerView.hasFixedSize()

        personalRecyclerView.adapter = taskPersonalAdapter
        personalRecyclerView.layoutManager = layoutManagerPersonal
        personalRecyclerView.hasFixedSize()

        availableTimeSlotsRecycler.adapter = timeSlotAdapter
        availableTimeSlotsRecycler.layoutManager = layoutManagerTimeSlot
        availableTimeSlotsRecycler.hasFixedSize()

    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WorkspaceViewModel::class.java)
        // TODO: Use the ViewModel
    }


}

