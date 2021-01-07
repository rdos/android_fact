package ru.smartro.worknote.service


import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import ru.smartro.worknote.service.body.AuthBody


class NetworkRepository {
    fun auth(model: AuthBody) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient.apiService().auth(model)
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

    fun getOwners() = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient.apiService().getOwners()
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    emit(Resource.error("Ошибка", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

}

