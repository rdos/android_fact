package ru.smartro.worknote.presentation.checklist.vehicle

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.service.network.RetrofitClient
import ru.smartro.worknote.awORKOLDs.service.network.response.EmptyResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.organisation.OrganisationResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.vehicle.VehicleResponse
import ru.smartro.worknote.work.Resource
import ru.smartro.worknote.work.THR

class StartVehicleViewModel(application: Application) : BaseViewModel(application) {

    private val _vehicleList: MutableLiveData<Resource<VehicleResponse>> = MutableLiveData(null)
    val mVehicleList: LiveData<Resource<VehicleResponse>>
        get() = _vehicleList

    fun getVehicleList(organisationId: Int) {
        viewModelScope.launch {
            Log.i(TAG, "getVehicle.before")
            try {
                val response = networkDat.getVehicle(organisationId)
                Log.d(TAG, "getVehicle.after ${response.body().toString()}")
                when {
                    response.isSuccessful -> {
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
}