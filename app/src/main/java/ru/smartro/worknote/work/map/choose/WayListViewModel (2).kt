package ru.smartro.worknote.work.map.choose

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.network.body.WayListBody
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.response.way_list.WayListResponse

class WayListViewModel(application: Application) : BaseViewModel(application) {

    fun getWayList(body : WayListBody): LiveData<Resource<WayListResponse>> {
        return network.getWayList(body)
    }

}

