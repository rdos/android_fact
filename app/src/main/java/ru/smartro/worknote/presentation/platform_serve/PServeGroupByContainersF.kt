package ru.smartro.worknote.presentation.platform_serve

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.andPOintD.SmartROsc
import ru.smartro.worknote.work.ConfigName
import ru.smartro.worknote.work.ContainerGROUPClientEntity
import ru.smartro.worknote.work.ContainerGROUPClientTypeEntity
import ru.smartro.worknote.work.PlatformEntity


class PServeGroupByContainersF : AFragment() {

    private var adapter: PServeGROUPClientsAdapter? = null
    private var mBackPressedCnt: Int = 2
    private val _PlatformEntity: PlatformEntity
        get() = vm.getPlatformEntity()
    private var btnCompleteTask: AppCompatButton? = null
    private var tvContainersProgress: AppCompatTextView? = null
    private var actvAddress: AppCompatTextView? = null
    private var srosToPserveFMode: SmartROsc? = null
    private var screenModeLabel: TextView? = null
    private var rvMain: RecyclerView? = null

    private val vm: ServePlatformVM by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.f_pserve_groupby
    }

    override fun onInitLayoutView(view: SmartROllc): Boolean {
        tvContainersProgress = view.findViewById(R.id.actv_f_pserve_groupby__sprid)
        btnCompleteTask = view.findViewById(R.id.acb_activity_platform_serve__complete)
        actvAddress = view.findViewById(R.id.tv_platform_serve__address)
        srosToPserveFMode = view.findViewById(R.id.sros_f_pserve_groupby__mode)
        screenModeLabel = view.findViewById(R.id.screen_mode_label)
        rvMain = view.findViewById(R.id.rv_f_pserve_groupby__main)
        rvMain?.layoutManager = LinearLayoutManager(getAct())
        adapter = PServeGROUPClientsAdapter(listOf())
        rvMain?.adapter = adapter
        screenModeLabel?.text = "По типам"

        srosToPserveFMode?.isChecked = true
        srosToPserveFMode?.setOnCheckedChangeListener { _, _ ->
            // TODO: !!!
            vm.database.setConfig(ConfigName.USER_WORK_SERVE_MODE_CODENAME, PlatformEntity.Companion.ServeMode.PServeF)

            navigateMain(R.id.PServeF, vm.getPlatformId())
        }

        return super.onInitLayoutView(view)
    }

    override fun onBindLayoutState(): Boolean {
        val groupByContainerClientS = vm.getGroupByContainerClientS()
        val platformServeMode = _PlatformEntity.getServeMode()

        if(platformServeMode == PlatformEntity.Companion.ServeMode.PServeGroupByContainersF){
            srosToPserveFMode?.visibility = View.GONE
        } else {
            srosToPserveFMode?.visibility = View.VISIBLE
        }
        
        adapter?.change(groupByContainerClientS)

        tvContainersProgress?.text = "№${_PlatformEntity.srpId} / ${_PlatformEntity.containerS.size} конт."

        btnCompleteTask?.setOnClickListener {
            navigateMain(R.id.PhotoAfterMediaF, _PlatformEntity.platformId!!)
        }

        actvAddress?.text = "${_PlatformEntity.address}"
        if (_PlatformEntity.containerS.size >= 7 ) {
            actvAddress?.apply {
                setOnClickListener { view ->
                    maxLines = if (maxLines < 3) {
                        3
                    } else {
                        1
                    }
                }
            }
        } else {
            actvAddress?.maxLines = 3
        }
        return super.onBindLayoutState()
    }

    override fun onNewLiveData() {
        vm.todoLiveData.observe(viewLifecycleOwner) {
            LOG.debug("onBindLayoutState")
            val result = onBindLayoutState()
            LOG.trace("onBindLayoutState.result=${result}")
        }
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)


//        val adapterCurrentTask = PServeGroupedByClientsAdapter(requireContext(), object : PServeGroupedByClientsAdapter.SimplifyContainerServeListener {
//            override fun onDecrease(clientName: String, typeName: String) {
//                viewModel.onDecrease(clientName, typeName)
//            }
//
//            override fun onIncrease(clientName: String, typeName: String) {
//                viewModel.onIncrease(clientName, typeName)
//            }
//
//            override fun onAddPhoto(clientName: String, typeName: String) {
//                navigateMain(R.id.PhotoBeforeMediaContainerSimplifyF, viewModel.mPlatformEntity.value!!.platformId!!)
//            }
//        }


//        rvMain.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = adapterCurrentTask
//        }
//
//        vm.mSortedContainers.observe(viewLifecycleOwner) { list ->
//            if(list != null) {
//                adapterCurrentTask.containers = list
//            }
//        }
//
//        vm.mServedContainers.observe(viewLifecycleOwner) { list ->
//            if(list != null) {
//                adapterCurrentTask.served = list
//            }
//        }



//        if (getAct() is MainAct) {
//            (getAct() as MainAct).setSpecialProcessingForRecycler(rvMain)
//        }




//        rvCurrentTask.viewTreeObserver
//            .addOnGlobalLayoutListener(
//                object : OnGlobalLayoutListener {
//                    override fun onGlobalLayout() {
//                        // At this point the layout is complete and the
//                        // dimensions of recyclerView and any child views
//                        if (getAct() is PServeAct) {
//                            (getAct() as PServeAct).onNewfromAFragment()
//                        }
//                        // are known.
//                        rvCurrentTask.viewTreeObserver
//                            .removeOnGlobalLayoutListener(this)
//                    }
//                })

//    todo: Ох, рано встаёт охрана!
//    private fun hideSwitch() {
//        srosToPserveFMode?.visibility = View.GONE
//    }
//}

    override fun onBackPressed() {
        mBackPressedCnt -= 1
        if (mBackPressedCnt <= 0) {
            vm.updatePlatformStatusUnfinished()
            navigateBack(R.id.MapPlatformsF)
//            toast("Вы не завершили обслуживание КП.")
        } else {
            toast("Вы не завершили обслуживание КП. Нажмите ещё раз, чтобы выйти")
        }
    }


    inner class PServeGROUPClientsAdapter(
        private var groupByContainerClientS: List<ContainerGROUPClientEntity>
    ) : RecyclerView.Adapter<PServeGROUPClientsAdapter.PServeGroupedByContainerClientViewHolder>() {
/** следOK
        init {
            groupByContainerClientS.observe(viewLifecycleOwner) { groupByContainerClientEntity ->
                onNewItem(groupByContainerClientEntity)
            }
        }
        */

        fun change(groupByContainerClientS: MutableList<ContainerGROUPClientEntity>) {
            this.groupByContainerClientS = groupByContainerClientS
            notifyDataSetChanged()
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PServeGroupedByContainerClientViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.f_pserve_groupby_container_client__rv_item, parent, false)
            return PServeGroupedByContainerClientViewHolder(view)
        }
/** следOK2
        fun onNewItem(item: Entity){
            groupByContainerClientS.item
            this.notifyItemChanged()
        }
 */
        override fun getItemCount(): Int {
            return groupByContainerClientS.size
        }

        override fun onBindViewHolder(holder: PServeGroupedByContainerClientViewHolder, position: Int) {
            val clientGroup = groupByContainerClientS[position]
            LOG.debug("clientGroup = ${clientGroup} name: ${clientGroup.client}")

            val rvGroupedContainers = holder.itemView.findViewById<RecyclerView>(R.id.typed_containers)

            // TODO:::
            holder.tvClient.text = clientGroup.getClientForUser()

            rvGroupedContainers?.layoutManager = LinearLayoutManager(context)
            val groupByContainerClientTypeS = vm.getGroupByContainerTypeClientS(clientGroup.client)
            LOG.debug("A groupByContainerClientTypeS=${groupByContainerClientTypeS}")
            rvGroupedContainers?.adapter = PServeGroupedByContainerClientTypesAdapter(groupByContainerClientTypeS)
        }

        inner class PServeGroupedByContainerClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvClient: AppCompatTextView = itemView.findViewById(R.id.client_label)
        }



        inner class PServeGroupedByContainerClientTypesAdapter(
            private val groupByContainerClientTypeS: List<ContainerGROUPClientTypeEntity>
        ) : RecyclerView.Adapter<PServeGroupedByContainerClientTypesAdapter.PServeGroupedByContainerClientTypeViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PServeGroupedByContainerClientTypeViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.f_pserve_by_types__rv_conainers_typed, parent, false)
                return PServeGroupedByContainerClientTypeViewHolder(view)
            }

            override fun getItemCount(): Int {
                return groupByContainerClientTypeS.size
            }

            override fun onBindViewHolder(holder: PServeGroupedByContainerClientTypeViewHolder, position: Int) {
                val groupByContainerClientType = groupByContainerClientTypeS[position]
                holder.bind(groupByContainerClientType)
            }



            inner class PServeGroupedByContainerClientTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                fun bind(containerGROUPClientTypeEntity: ContainerGROUPClientTypeEntity) {

                    val tvTypeName = itemView.findViewById<TextView>(R.id.container_type)
                    val bDecrease = itemView.findViewById<AppCompatButton>(R.id.button_decrease_cont)
                    val tvCount = itemView.findViewById<TextView>(R.id.containers_count)
                    val bIncrease = itemView.findViewById<AppCompatButton>(R.id.button_increase_cont)
                    val bAddPhoto = itemView.findViewById<AppCompatButton>(R.id.button_add_photo)
                    val tvContSize = itemView.findViewById<TextView>(R.id.containers_size)

                    // TODO:::
                    tvTypeName.text = containerGROUPClientTypeEntity.getTypetForUser()
                    val sum = containerGROUPClientTypeEntity.getServeCNT()
                    tvCount.text =  sum.toString()
                    tvContSize.text = containerGROUPClientTypeEntity.getTypeCount()


                    bDecrease.setOnClickListener {
                        vm.decGroupByContainerTypeClientS(containerGROUPClientTypeEntity)
                    }

                    bIncrease.setOnClickListener {
                        vm.incGroupByContainerTypeClientS(containerGROUPClientTypeEntity)
                    }

                    bAddPhoto.setOnClickListener {
                       navigateMain(R.id.PhotoBeforeMediaContainerSimplifyF, containerGROUPClientTypeEntity.typeId, containerGROUPClientTypeEntity.client)
                    }
                }
            }
        }
    }
}