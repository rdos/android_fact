package ru.smartro.worknote.ui.map

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.alert_successful_complete.view.*
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.showSuccessComplete
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.problem.CancelWayReasonEntity
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import ru.smartro.worknote.service.database.entity.work_order.WayTaskEntity
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.service.network.body.early_complete.EarlyCompleteBody
import ru.smartro.worknote.service.network.response.EmptyResponse
import ru.smartro.worknote.ui.choose.way_task_4.WayTaskActivity
import ru.smartro.worknote.util.MyUtil

class MapViewModel(application: Application) : BaseViewModel(application) {


    fun completeWay(id : Int, completeWayBody: CompleteWayBody) : LiveData<Resource<EmptyResponse>>{
        return network.completeWay(id, completeWayBody)
    }

    fun earlyComplete(id : Int, body : EarlyCompleteBody) : LiveData<Resource<EmptyResponse>>{
        return network.earlyComplete(id, body)
    }

    fun finishTask (context : AppCompatActivity){
        WorkManager.getInstance(context).cancelUniqueWork("UploadData")
        AppPreferences.workerStatus = false
        AppPreferences.thisUserHasTask = false
        clearData()
        context.loadingHide()
        context.showSuccessComplete().let {
            it.finish_accept_btn.setOnClickListener {
                context.startActivity(Intent(context, WayTaskActivity::class.java))
                context.finish()
            }
            it.exit_btn.setOnClickListener {
                MyUtil.logout(context)
            }
        }
    }

    fun clearData(){
        db.clearData()
    }

    fun findWayTask(): WayTaskEntity {
        return db.findWayTask()
    }

    fun findPlatformByCoordinate(lat : Double, lon : Double): PlatformEntity {
        return db.findPlatformByCoordinate(lat, lon)
    }

    fun findCancelWayReason(): List<CancelWayReasonEntity> {
        return db.findCancelWayReason()
    }

    fun findCancelWayReasonByValue(reason : String): Int{
        return db.findCancelWayReasonByValue(reason)
    }

}

