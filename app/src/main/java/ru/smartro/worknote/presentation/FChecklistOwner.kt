package ru.smartro.worknote.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.AF
import ru.smartro.worknote.LOG
import ru.smartro.worknote.log.todo.OrganisationEntity

class FChecklistOwner: AF(), SwipeRefreshLayout.OnRefreshListener {

    private var mOrganisationAdapter: OrganisationAdapter? = null
    private var srlRefresh: SwipeRefreshLayout? = null

    override fun onGetLayout(): Int = R.layout.f_start_owner

    private val viewModel: AXChecklist.ChecklistViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!App.getAppliCation().hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, 1)
        }

        (requireActivity() as AXChecklist).apply {
            acibGoToBack?.visibility = View.GONE
            setBarTitle("Организация")
        }

        srlRefresh = view.findViewById(R.id.srl__f_start_owner__refresh)
        srlRefresh?.setOnRefreshListener(this)


        mOrganisationAdapter = OrganisationAdapter { owner ->
            goToNextStep(owner.id, owner.name)
        }
        val rv = view.findViewById<RecyclerView>(R.id.rv__f_start_owner__owners).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mOrganisationAdapter
        }

        val organisationS= viewModel.database.getOrganisationS()
        mOrganisationAdapter?.setItems(organisationS)
        onRefresh()
    }

    override fun onLiveData() {
        super.onLiveData()

    }

    private fun goToNextStep(ownerId: Int, ownerName: String) {
        paramS().ownerId = ownerId
        paramS().ownerName = ownerName
        // TODO!! will be changed to navigateMain
        navigateMainChecklist(R.id.startVehicleF, ownerId, ownerName)
    }

    override fun onRefresh() {
        getOwnersList()
        srlRefresh?.isRefreshing = false
        (requireActivity() as AXChecklist).showProgressBar("список организаций")
    }

    private fun getOwnersList() {
        val ownerRequest = RGETOwner()
        ownerRequest.getLiveDate().observe(viewLifecycleOwner) { result ->
            LOG.debug("safka${result}")
            (requireActivity() as AXChecklist).hideProgressBar()
            if (result.isSent) {

                val organisationS= viewModel.database.getOrganisationS()
                if (organisationS.size == 1) {
                    goToNextStep(organisationS[0].id, organisationS[0].name)
                } else {
                    mOrganisationAdapter?.setItems(organisationS)
                }
            }
        }
        App.oKRESTman().put(ownerRequest)
        
    }


    class OrganisationAdapter(private val listener: (OrganisationEntity) -> Unit): RecyclerView.Adapter<OrganisationAdapter.OrganisationOwnerViewHolder>() {

        private val mItems: MutableList<OrganisationEntity> = mutableListOf()
        fun setItems(ownersList: List<OrganisationEntity>) {
            mItems.clear()
            mItems.addAll(ownersList)
            notifyDataSetChanged()
        }
        fun isItemsEmpty() = mItems.isEmpty()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganisationOwnerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.f_start_owner__rv_item, parent, false)
            return OrganisationOwnerViewHolder(view, listener)
        }

        override fun onBindViewHolder(holder: OrganisationOwnerViewHolder, position: Int) {
            holder.bind(mItems[position])
        }

        override fun getItemCount(): Int = mItems.size

        class OrganisationOwnerViewHolder(val itemView: View, val listener: (OrganisationEntity) -> Unit): RecyclerView.ViewHolder(itemView) {
            fun bind(owner: OrganisationEntity) {
                itemView.findViewById<TextView>(R.id.owner_name).text = owner.name
                itemView.setOnClickListener {
                    listener(owner)
                }
            }
        }
    }
}