package ru.smartro.worknote.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.*
import ru.smartro.worknote.ac.AbsF
import ru.smartro.worknote.ac.SmartROllc
import ru.smartro.worknote.ac.swipebtn.SmartROviewPServeWrapper
import ru.smartro.worknote.log.todo.ConfigName
import ru.smartro.worknote.log.todo.ContainerGROUPClientEntity
import ru.smartro.worknote.log.todo.ContainerGROUPClientTypeEntity
import ru.smartro.worknote.log.todo.PlatformEntity


class FPServeGroupByContainers : AbsF() {

    companion object {
        const val NAV_ID = R.id.FPServeGroupByContainers
    }

    private val _PlatformEntity: PlatformEntity
        get() = vm.getPlatformEntity()

    private var adapter: PServeGROUPClientsAdapter? = null
    private var mBackPressedCnt: Int = 2


    private var smartROPServeWrapper: SmartROviewPServeWrapper? = null
    private var rvMain: RecyclerView? = null

    private val vm: VMPserve by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.f_pserve_groupby
    }

    override fun onInitLayoutView(view: SmartROllc): Boolean {

        smartROPServeWrapper = view.findViewById(R.id.sro_pserve_wrapper__f_pserve_groupby__wrapper)

        smartROPServeWrapper?.setSwitchChecked(true)

        smartROPServeWrapper?.setScreenLabel("По типам")

        smartROPServeWrapper?.setPlatformEntity(_PlatformEntity, requireActivity())

        smartROPServeWrapper?.setOnSwitchMode {
            vm.database.setConfig(ConfigName.USER_WORK_SERVE_MODE_CODENAME, PlatformEntity.Companion.ServeMode.PServeF)
            navigateNext(FPServe.NAV_ID, vm.getPlatformId())
        }

        smartROPServeWrapper?.setOnCompleteServeListener {
            if(_PlatformEntity.needCleanup) {
                navigateNext(DFPMapNeedCleanup.NAV_ID, _PlatformEntity.platformId)
            } else {
                navigateNext(FPhotoAfterMedia.NAV_ID, _PlatformEntity.platformId)
            }
        }


        rvMain = view.findViewById(R.id.rv_f_pserve_groupby__main)

        rvMain?.layoutManager = LinearLayoutManager(getAct())
        adapter = PServeGROUPClientsAdapter(listOf())
        rvMain?.adapter = adapter

        return super.onInitLayoutView(view)
    }

    override fun onBindLayoutState(): Boolean {
        val groupByContainerClientS = vm.getGroupByContainerClientS()

        adapter?.change(groupByContainerClientS)

        val platformServeMode = _PlatformEntity.getServeMode()

        val flag = platformServeMode != PlatformEntity.Companion.ServeMode.PServeGroupByContainersF

        smartROPServeWrapper?.setSwitchVisibility(flag)

        return super.onBindLayoutState()
    }

    override fun onLiveData() {
        vm.todoLiveData.observe(viewLifecycleOwner) {
            LOG.debug("onBindLayoutState")
            val result = onBindLayoutState()
            LOG.trace("onBindLayoutState.result=${result}")
        }
    }

    override fun onBackPressed() {
        mBackPressedCnt -= 1
        if (mBackPressedCnt <= 0) {
            vm.updatePlatformStatusUnfinished()
            navigateBack(FPMap.NAV_ID)
        } else {
            toast("Вы не завершили обслуживание КП. Нажмите ещё раз, чтобы выйти")
        }
    }


    inner class PServeGROUPClientsAdapter(
        private var groupByContainerClientS: List<ContainerGROUPClientEntity>
    ) : RecyclerView.Adapter<PServeGROUPClientsAdapter.PServeGroupedByContainerClientViewHolder>() {

        fun change(groupByContainerClientS: MutableList<ContainerGROUPClientEntity>) {
            this.groupByContainerClientS = groupByContainerClientS
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PServeGroupedByContainerClientViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.f_pserve_groupby_container_client__rv_item, parent, false)
            return PServeGroupedByContainerClientViewHolder(view)
        }

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

                    val tvTypeName = itemView.findViewById<AppCompatTextView>(R.id.container_type)
                    val bDecrease = itemView.findViewById<AppCompatButton>(R.id.button_decrease_cont)
                    val tvCount = itemView.findViewById<AppCompatTextView>(R.id.containers_count)
                    val bIncrease = itemView.findViewById<AppCompatButton>(R.id.button_increase_cont)
                    val bAddPhoto = itemView.findViewById<AppCompatButton>(R.id.button_add_photo)
                    val tvContSize = itemView.findViewById<AppCompatTextView>(R.id.containers_size)

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
                       navigateNext(FPhotoBeforeMediaContainerByTypes.NAV_ID, containerGROUPClientTypeEntity.typeId, containerGROUPClientTypeEntity.client)
                    }
                }
            }
        }
    }
}