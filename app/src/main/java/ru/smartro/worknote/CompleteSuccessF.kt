package ru.smartro.worknote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.alert_successful_complete.view.*
import kotlinx.android.synthetic.main.f_complete_success__act_map.*

import ru.smartro.worknote.awORKOLDs.extensions.showSuccessComplete
import ru.smartro.worknote.awORKOLDs.service.network.Status
import ru.smartro.worknote.awORKOLDs.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.work.ac.StartAct

import kotlin.math.round

class CompleteSuccessF : AFragment() {
    companion object {
        fun newInstance(workOrderId: Any): CompleteSuccessF {
            workOrderId as Int
            val fragment = CompleteSuccessF()
            fragment.addArgument(workOrderId)
            return fragment
        }
    }

    override fun onGetLayout(): Int {
        return R.layout.f_complete_success__act_map
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideProgress()
//        App.getAppliCation().getRouter().navigateTo(LIST_SCREEN)
        val workOrderId = getArgumentID()
        val database = App.getAppliCation().getDB()
        val totalVolume = App.getAppliCation().getDB().findContainersVolume(workOrderId)
            this.comment_et.setText("$totalVolume")
            this.accept_btn.setOnClickListener {
                fun finishTask() {
                    Log.i(TAG, "finishTask")
                    getAct().modeSyNChrON_off()
                    database.clearDataBase()
//            AppPreferences.isHasTask = false
                    getAct().showSuccessComplete().let {
                        it.finish_accept_btn.setOnClickListener {
                            getAct().startActivity(Intent(getAct(), StartAct::class.java))
                            getAct().finish()
                        }
                        it.exit_btn.setOnClickListener {
                            getAct().logout()
                        }
                    }
                }
                if (this.weight_tg.isChecked || this.volume_tg.isChecked) {
                    val unloadType = if (this.volume_tg.isChecked) 1 else 2
                    val unloadValue = round(this.comment_et.text.toString().toDouble() * 100) / 100
                    val body = CompleteWayBody(
                        finishedAt = MyUtil.timeStampInSec(),
                        unloadType = unloadType, unloadValue = unloadValue.toString()
                    )

                    App.getAppliCation().getNetwork().completeWay(workOrderId, body)
                        .observe(viewLifecycleOwner) { result ->
                            when (result.status) {
                                Status.SUCCESS -> {
                                    hideProgress()
                                    val workOrderEntity = database.getWorkOrderEntity(workOrderId)
                                    database.setCompleteWorkOrderData(workOrderEntity)
                                    if (database.hasWorkOrderInNotProgress()) {
                                        finishTask()
                                    }
                                }
                                Status.ERROR -> {
//                                    toast(result.msg)
                                    hideProgress()
                                }
                                Status.NETWORK -> {
                                    toast("Проблемы с интернетом")
                                    hideProgress()
                                }
                            }
                        }
                } else {
                    toast("Выберите тип показателей")
                }
            }
        }
}