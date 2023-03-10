package ru.smartro.worknote.presentation

import android.app.Application
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AAct
import ru.smartro.worknote.hideDialog
import ru.smartro.worknote.hideProgress
import ru.smartro.worknote.showDlgLogout

class AXChecklist: AAct() {

    var acibGoToBack: AppCompatImageButton? = null
    private var pbLoading: ProgressBar? = null
    private var actvLoadingLabel: TextView? = null
    private var actvBarTitle: AppCompatTextView? = null
    private var acivLogout: AppCompatImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_xchecklist)
        hideProgress()

        supportActionBar?.hide()

        acivLogout = findViewById(R.id.aciv__act_checklist__logout)
        acivLogout?.setOnClickListener {
            showDlgLogout().let { view ->
                val btnYes = view.findViewById<AppCompatButton>(R.id.acb__act_xchecklist__dialog_logout__yes)
                val btnNo = view.findViewById<AppCompatButton>(R.id.acb__act_xchecklist__dialog_logout__no)
                btnYes.setOnClickListener {
                    logout()
                }
                btnNo.setOnClickListener {
                    hideDialog()
                }
            }
        }
        pbLoading = findViewById(R.id.pb__act_checklist__loading)
        actvLoadingLabel = findViewById(R.id.actv__act_checklist__loading_label)
        actvBarTitle = findViewById(R.id.actv__act_checklist__bar_title)
        acibGoToBack = findViewById(R.id.acib__act_checklist__gotoback)
    }

    fun setBarTitle(title: String) {
        actvBarTitle?.text = title
    }

    fun showProgressBar() {
        pbLoading?.visibility = View.VISIBLE
    }

    fun showProgressBar(labelText: String) {
        pbLoading?.visibility = View.VISIBLE
        actvLoadingLabel?.visibility = View.VISIBLE
        actvLoadingLabel?.text = "?????????????????????? ${labelText}"
    }

    fun hideProgressBar() {
        pbLoading?.visibility = View.GONE
        actvLoadingLabel?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        hideProgress()
    }

    override fun onPause() {
        super.onPause()
        hideProgress()
    }
    /**
    ***???????????????????? ???????? R_)OS
    */
    sealed class ViewState(val msg: String? = null) {
        class IDLE(): ViewState()
        class LOADING(): ViewState()
        class DATA(): ViewState()
        class ERROR(_msg: String? = null): ViewState(_msg)
        class MESSAGE(_msg: String? = null): ViewState(_msg)
        class REFRESH(): ViewState()
    }
    /**???????????????????? ???????? R_)OS
       ???????????????????? ???????? R_)OS
        ???????????????????? ???????? R_)OS
     */

    class ChecklistViewModel(app: Application) : ru.smartro.worknote.ac.AViewModel(app) {


        // OWNERS
//        private val _ownersList: MutableLiveData<Resource<OwnerBodyOut>> = MutableLiveData(null)
//        val mOwnersList: LiveData<Resource<OwnerBodyOut>>
//            get() = _ownersList
        var mLastOwnerId = -1

        // VEHICLES
//        private val _vehicleList: MutableLiveData<Resource<VehicleBodyOutVehicle>> = MutableLiveData(null)
//        val mVehicleList: LiveData<Resource<VehicleBodyOutVehicle>>
//            get() = _vehicleList
        var mLastVehicleId = -1

        // WAYBILLS
//        private val _wayBillList: MutableLiveData<List<WayBillDto>> = MutableLiveData(null)
//        val mWayBillList: LiveData<List<WayBillDto>>
//            get() = _wayBillList
        val mWayBillsViewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.IDLE())
        var mLastWayBillId = -1

        // WORKORDERS
//        private val _workOrderList: MutableLiveData<Resource<WorkOrderResponse_know1>> = MutableLiveData(null)
//        val mWorkOrderList: LiveData<Resource<WorkOrderResponse_know1>>
//            get() = _workOrderList

        val mSelectedWorkOrders: MutableLiveData<MutableList<Int>> = MutableLiveData(mutableListOf())

        fun getOwnersList() {
            viewModelScope.launch {
                LOG.info( "getOwners")
//                val response = networkDat.getOwners()
//                try {
//                    when {
//                        response.isSuccessful -> {
//                            val gson = Gson()
//                            val bodyInStringFormat = gson.toJson(response.body())
//                            saveJSON(bodyInStringFormat, "getOwners")
//                            _ownersList.postValue(Resource.success(response.body()))
//                        }
//                        else -> {
////                            THR.BadRequestOwner(response)
//                            _ownersList.postValue(Resource.error("???????????? ${response.code()}", null))
//
//                        }
//                    }
//                } catch (e: Exception) {
//                    _ownersList.postValue(Resource.network("???????????????? ?? ???????????????????????? ??????????????????", null))
//                }
            }
        }

        fun getVehicleList(organisationId: Int) {
            viewModelScope.launch {
                LOG.info( "getVehicle.before")
//                try {
//                    val response = networkDat.getVehicle(organisationId)
//                    LOG.debug("getVehicle.after ${response.body().toString()}")
//                    when {
//                        response.isSuccessful -> {
//                            mLastOwnerId = organisationId
//                            val gson = Gson()
//                            val bodyInStringFormat = gson.toJson(response.body())
//                            saveJSON(bodyInStringFormat, "getVehicle")
//                            _vehicleList.postValue(Resource.success(response.body()))
//                        }
//                        else -> {
//                            THR.BadRequestVehicle(response)
//                            val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
//                            LOG.debug("getVehicle.after errorResponse=${errorResponse}")
//                            _vehicleList.postValue(Resource.error("???????????? ${response.code()}", null))
//                        }
//                    }
//                } catch (e: Exception) {
//                    _vehicleList.postValue(Resource.network("???????????????? ?? ???????????????????????? ??????????????????", null))
//                }
            }
        }

//        fun getWayBillsList(body : WayListBody, isRefresh: Boolean = false) {
//            _wayBillList.postValue(null)
//            //
//            if(isRefresh) {
//                mWayBillsViewState.postValue(ViewState.REFRESH())
//            } else {
//                mWayBillsViewState.postValue(ViewState.LOADING())
//            }
//            //
//            mLastWayBillId = -1
//            //
//            viewModelScope.launch {
//                try {
//                    val response = networkDat.getWayList(body)
//                    when {
//                        response.isSuccessful -> {
//                            mLastOwnerId = body.organisationId
//                            mLastVehicleId = body.vehicleId
//                            val gson = Gson()
//                            val bodyInStringFormat = gson.toJson(response.body())
//                            saveJSON(bodyInStringFormat, "getWayList")
//                            LOG.debug("getWayList.after ${response.body().toString()}")
//                            mWayBillsViewState.postValue(ViewState.DATA())
//                            LOG.debug("waybills:::: ${response.body()?.data}")
//                            _wayBillList.postValue(response.body()?.data)
//                        }
//                        else -> {
//                            THR.BadRequestWaybill(response)
////                            val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
////                            LOG.debug("getWayList.after errorResponse=${errorResponse}")
//                            mWayBillsViewState.postValue(ViewState.ERROR("???????????? ${response.code()}"))
//                        }
//                    }
//                } catch (e: Exception) {
//                    mWayBillsViewState.postValue(ViewState.ERROR("???????????????? ?? ???????????????????????? ??????????????????"))
//                }
//            }
//        }

//        fun getWorkOrderList(orgId: Int, wayBillId: Int) {
//            viewModelScope.launch {
//                LOG.info( "getWorkOder.before")
//                try {
//                    val response = networkDat.getWorkOrder(orgId, wayBillId)
//                    mSelectedWorkOrders.postValue(mutableListOf())
//                    LOG.debug("getWorkOder.after ${response.body().toString()}")
//                    when {
//                        response.isSuccessful -> {
//                            mLastOwnerId = orgId
//                            mLastWayBillId = wayBillId
//                            _workOrderList.postValue(Resource.success(response.body()))
//                        }
//                        else -> {
//                            THR.BadRequestSynchro__o_id__w_id(response)
//                            _workOrderList.postValue(Resource.error("???????????? ${response.code()}", null))
//                        }
//                    }
//                } catch (e: Exception) {
//                    _workOrderList.postValue(Resource.network("???????????????? ?? ???????????????????????? ??????????????????", null))
//                }
//            }
//        }
    }
}