package ru.smartro.worknote.ui.choose.way_list_3

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.Resource
import ru.smartro.worknote.service.body.WayListBody
import ru.smartro.worknote.service.response.way_list.WayListResponse

class WayListViewModel(application: Application) : BaseViewModel(application) {

    fun getWayList(body : WayListBody): LiveData<Resource<WayListResponse>> {
        return network.getWayList(body)
    }

}

