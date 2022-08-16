package ru.smartro.worknote.presentation.platform_serve

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.App
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.andPOintD.SmartROSwitch
import ru.smartro.worknote.log
import ru.smartro.worknote.presentation.ac.MainAct
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ConfigName
import ru.smartro.worknote.work.GroupByContainerClientEntity
import ru.smartro.worknote.work.GroupByContainerClientTypeEntity


class PServeGroupByContainersF : AFragment() {
    private var mBackPressedCnt: Int = 2

    private var btnCompleteTask: AppCompatButton? = null
    private var tvContainersProgress: AppCompatTextView? = null
    private var actvAddress: AppCompatTextView? = null
    private var srosToPserveFMode: SmartROSwitch? = null
    private var screenModeLabel: TextView? = null

    private val vm: ServePlatformVM by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.f_pserve_groupby
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            log("savedInstanceState == null")
        } else {
            log("savedInstanceState HE null")
        }

        tvContainersProgress = view.findViewById(R.id.actv_f_pserve_groupby__sprid)
        btnCompleteTask = view.findViewById(R.id.acb_activity_platform_serve__complete)
        actvAddress = view.findViewById(R.id.tv_platform_serve__address)
        srosToPserveFMode = view.findViewById(R.id.sros_f_pserve_groupby__mode)
        screenModeLabel = view.findViewById(R.id.screen_mode_label)

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
//        })

        srosToPserveFMode?.setOnClickListener {
            val configEntity = vm.database.loadConfig(ConfigName.USER_WORK_SERVE_MODE)
            configEntity.value = App.ServeMode.PServeF
            vm.database.saveConfig(configEntity)

            navigateMain(R.id.PServeF, vm.getPlatformId())
        }

        val rvMain = view.findViewById<RecyclerView>(R.id.rv_f_pserve_groupby__main)
        rvMain.layoutManager = LinearLayoutManager(getAct())
        val groupByContainerClientS = vm.getGroupByContainerClientS()
        rvMain.adapter = PServeGroupedByClientsAdapter(groupByContainerClientS)
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


            val platformEntity = vm.getPlatformEntity()

            tvContainersProgress?.text =
                "№${platformEntity.srpId} / ${platformEntity.containers.size} конт."

            btnCompleteTask?.setOnClickListener {
                navigateMain(R.id.PhotoAfterMediaF, platformEntity.platformId!!)
            }

            actvAddress?.text = "${platformEntity.address}"
            if (platformEntity.containers.size >= 7 ) {
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


        if (getAct() is MainAct) {
            (getAct() as MainAct).setSpecialProcessingForRecycler(rvMain)
        }




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
    }

    override fun onBackPressed() {
        mBackPressedCnt--
        if (mBackPressedCnt <= 0) {
            vm.updatePlatformStatusUnfinished()
            navigateBack(R.id.MapF)
            toast("Вы не завершили обслуживание КП.")
        } else {
            toast("Вы не завершили обслуживание КП. Нажмите ещё раз, чтобы выйти")
        }
    }



    inner class PServeGroupedByClientsAdapter(
        private val groupByContainerClientS: List<GroupByContainerClientEntity>
    ) : RecyclerView.Adapter<PServeGroupedByClientsAdapter.PServeGroupedByContainerClientViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PServeGroupedByContainerClientViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.f_pserve_groupby_container_client__rv_item, parent, false)
            return PServeGroupedByContainerClientViewHolder(view)
        }

        override fun getItemCount(): Int {
            return groupByContainerClientS.size
        }

        override fun onBindViewHolder(holder: PServeGroupedByContainerClientViewHolder, position: Int) {
            val clientGroup = groupByContainerClientS[position]

            val rvGroupedContainers = view?.findViewById<RecyclerView>(R.id.typed_containers)

            holder.tvClient.text = clientGroup.client
            rvGroupedContainers?.layoutManager = LinearLayoutManager(context)
            val groupByContainerClientTypeS = vm.getGroupByContainerClientTypeS()
            rvGroupedContainers?.adapter = PServeGroupedByContainerClientTypesAdapter(groupByContainerClientTypeS)
        }


        inner class PServeGroupedByContainerClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvClient: AppCompatTextView by lazy {
                itemView.findViewById(R.id.client_label)
            }
        }



        inner class PServeGroupedByContainerClientTypesAdapter(
            private val groupByContainerClientTypeS: List<GroupByContainerClientTypeEntity>
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
                fun bind(groupByContainerClientTypeEntity: GroupByContainerClientTypeEntity) {

                    val tvTypeName = itemView.findViewById<TextView>(R.id.container_type)
                    val bDecrease = itemView.findViewById<AppCompatButton>(R.id.button_decrease_cont)
                    val tvCount = itemView.findViewById<TextView>(R.id.containers_count)
                    val bIncrease = itemView.findViewById<AppCompatButton>(R.id.button_increase_cont)
                    val bAddPhoto = itemView.findViewById<AppCompatButton>(R.id.button_add_photo)
                    val tvContSize = itemView.findViewById<TextView>(R.id.containers_size)

                    tvTypeName.text = groupByContainerClientTypeEntity.typeName
//                    tvCount.text = groupByContainerClientType.containers.count { it.volume != null }.toString()
                    tvContSize.text = groupByContainerClientTypeEntity.getTypeCount()


                    bDecrease.setOnClickListener {
//                        vm.incVolume(groupByContainerClientTypeEntity.typeId)
                    }

                    bIncrease.setOnClickListener {
//                        listener.onIncrease(typeGroup.typeName)
                    }

                    bAddPhoto.setOnClickListener {
                       navigateMain(R.id.PhotoBeforeMediaContainerSimplifyF, vm.getPlatformId())
                    }
                }
            }
        }
    }

}