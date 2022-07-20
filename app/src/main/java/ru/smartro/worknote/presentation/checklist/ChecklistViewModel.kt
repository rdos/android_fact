package ru.smartro.worknote.presentation.checklist

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import ru.smartro.worknote.awORKOLDs.BaseViewModel
import ru.smartro.worknote.awORKOLDs.service.network.body.WayListBody
import ru.smartro.worknote.awORKOLDs.service.network.response.EmptyResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.organisation.OrganisationResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.vehicle.VehicleResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.way_list.WayBillDto
import ru.smartro.worknote.work.Resource
import ru.smartro.worknote.work.THR
import ru.smartro.worknote.work.WoRKoRDeR_know1
import ru.smartro.worknote.work.WorkOrderResponse_know1

sealed class ViewState(val msg: String? = null) {
    class IDLE(): ViewState()
    class LOADING(): ViewState()
    class DATA(): ViewState()
    class ERROR(_msg: String? = null): ViewState(_msg)
    class MESSAGE(_msg: String? = null): ViewState(_msg)
    class REFRESH(): ViewState()
}

class ChecklistViewModel(application: Application) : BaseViewModel(application) {

    // OWNERS
    private val _ownersList: MutableLiveData<Resource<OrganisationResponse>> = MutableLiveData(null)
    val mOwnersList: LiveData<Resource<OrganisationResponse>>
        get() = _ownersList
    var mLastOwnerId = -1

    // VEHICLES
    private val _vehicleList: MutableLiveData<Resource<VehicleResponse>> = MutableLiveData(null)
    val mVehicleList: LiveData<Resource<VehicleResponse>>
        get() = _vehicleList
    var mLastVehicleId = -1

    // WAYBILLS
    private val _wayBillList: MutableLiveData<List<WayBillDto>> = MutableLiveData(null)
    val mWayBillList: LiveData<List<WayBillDto>>
        get() = _wayBillList
    val mWayBillsViewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.IDLE())
    var mLastWayBillId = -1

    // WORKORDERS
    private val _workOrderList: MutableLiveData<Resource<WorkOrderResponse_know1>> = MutableLiveData(null)
    val mWorkOrderList: LiveData<Resource<WorkOrderResponse_know1>>
        get() = _workOrderList

    val mSelectedWorkOrders: MutableLiveData<MutableList<Int>> = MutableLiveData(mutableListOf())

    fun getOwnersList() {
        viewModelScope.launch {
            Log.i(TAG, "getOwners")
            val response = networkDat.getOwners()
            try {
                when {
                    response.isSuccessful -> {
                        _ownersList.postValue(Resource.success(response.body()))
                    }
                    else -> {
                        THR.BadRequestOwner(response)
                        _ownersList.postValue(Resource.error("Ошибка ${response.code()}", null))

                    }
                }
            } catch (e: Exception) {
                _ownersList.postValue(Resource.network("Проблемы с подключением интернета", null))
            }
        }
    }

    fun getVehicleList(organisationId: Int) {
        viewModelScope.launch {
            Log.i(TAG, "getVehicle.before")
            try {
                val response = networkDat.getVehicle(organisationId)
                Log.d(TAG, "getVehicle.after ${response.body().toString()}")
                when {
                    response.isSuccessful -> {
                        mLastOwnerId = organisationId
                        _vehicleList.postValue(Resource.success(response.body()))
                    }
                    else -> {
                        THR.BadRequestVehicle(response)
                        val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                        Log.d(TAG, "getVehicle.after errorResponse=${errorResponse}")
                        _vehicleList.postValue(Resource.error("Ошибка ${response.code()}", null))
                    }
                }
            } catch (e: Exception) {
                _vehicleList.postValue(Resource.network("Проблемы с подключением интернета", null))
            }
        }
    }
    fun clearVehicleList() {
        _vehicleList.postValue(null)
    }

    fun getWayBillsList(body : WayListBody, isRefresh: Boolean = false) {
        _wayBillList.postValue(null)
        //
        if(isRefresh) {
            mWayBillsViewState.postValue(ViewState.REFRESH())
        } else {
            mWayBillsViewState.postValue(ViewState.LOADING())
        }
        //
        mLastWayBillId = -1
        //
        viewModelScope.launch {
            try {
                val response = networkDat.getWayList(body)
                when {
                    response.isSuccessful -> {
                        mLastOwnerId = body.organisationId
                        mLastVehicleId = body.vehicleId
                        Log.d(TAG, "getWayList.after ${response.body().toString()}")
                        mWayBillsViewState.postValue(ViewState.DATA())
                        Log.d("TEST :::", "waybills:::: ${response.body()?.data}")
                        _wayBillList.postValue(response.body()?.data)
                    }
                    else -> {
                        THR.BadRequestWaybill(response)
                        val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                        Log.d(TAG, "getWayList.after errorResponse=${errorResponse}")
                        mWayBillsViewState.postValue(ViewState.ERROR("Ошибка ${response.code()}"))
                    }
                }
            } catch (e: Exception) {
                mWayBillsViewState.postValue(ViewState.ERROR("Проблемы с подключением интернета"))
            }
        }
    }

    fun getWorkOrderList(orgId: Int, wayBillId: Int) {
        viewModelScope.launch {
            Log.i(TAG, "getWorkOder.before")
            try {
                val response = networkDat.getWorkOrder(orgId, wayBillId)
                mSelectedWorkOrders.postValue(mutableListOf())
                Log.d(TAG, "getWorkOder.after ${response.body().toString()}")
                when {
                    response.isSuccessful -> {
                        mLastOwnerId = orgId
                        mLastWayBillId = wayBillId
                        _workOrderList.postValue(Resource.success(response.body()))
                    }
                    else -> {
                        THR.BadRequestSynchro__o_id__w_id(response)
                        _workOrderList.postValue(Resource.error("Ошибка ${response.code()}", null))
                    }
                }
            } catch (e: Exception) {
                _workOrderList.postValue(Resource.network("Проблемы с подключением интернета", null))
            }
        }
    }

    fun insertWorkOrders(workOrders: List<WoRKoRDeR_know1>) {
        baseDat.clearDataBase()
        baseDat.insertWorkorder(workOrders)
    }

    fun clearWorkOrderList() {
        _workOrderList.postValue(null)
    }
}