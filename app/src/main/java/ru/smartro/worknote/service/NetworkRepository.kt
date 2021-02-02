package ru.smartro.worknote.service

import android.content.Context
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import ru.smartro.worknote.service.body.AuthBody
import ru.smartro.worknote.service.body.ProgressBody
import ru.smartro.worknote.service.body.WayListBody
import ru.smartro.worknote.service.body.WayTaskBody

class NetworkRepository(private val context: Context) {
    fun auth(model: AuthBody) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context).apiService(false).auth(model)
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    emit(Resource.error("Неверный логин или пароль", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun getVehicle(organisationId: Int) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context).apiService(true).getVehicle(organisationId)
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun getWayList(body: WayListBody) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context).apiService(true).getWayList(body)
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun getWayTask(wayId: Int, wayTaskBody: WayTaskBody) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context).apiService(true).getWayTask(wayId, wayTaskBody)
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun progress(id: Int, body: ProgressBody) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context).apiService(true).progress(id, body)
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun getOwners() = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context).apiService(false).getOwners()
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

}

