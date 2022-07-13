package ru.smartro.worknote.presentation.checklist.workorder

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.MapAct
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.work.WoRKoRDeR_know1
import ru.smartro.worknote.work.ac.PERMISSIONS

class StartWorkOrderF: AFragment() {

    private val viewModel: StartWorkOrderViewModel by viewModels()

    override fun onGetLayout(): Int = R.layout.f_start_workorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!MyUtil.hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, 1)
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Сменное Задание"
            setDisplayHomeAsUpEnabled(true)
        }


    }
    fun insertWayTask(woRKoRDeRknow1List: List<WoRKoRDeR_know1>) {
        vm.baseDat.clearDataBase()
        vm.baseDat.insertWorkorder(woRKoRDeRknow1List)
    }

    fun insertWayTask(woRKoRDeRknow10: WoRKoRDeR_know1) {
        insertWayTask(listOf(woRKoRDeRknow10))
    }

    fun gotoNextAct(workOrderIndex: Int?) {
        var workOrderId: Int? = null
        if (workOrderIndex == null) {
            insertWayTask(workOrders)
        } else {
            workOrderId = workOrders[workOrderIndex].id
            insertWayTask(workOrders[workOrderIndex])
        }
        val intent = Intent(requireActivity(), MapAct::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        workOrderIndex?.let {
            intent.putExtra(getAct().PUT_EXTRA_PARAM_ID, workOrderId)
        }
        startActivity(intent)
        getAct().finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navigateBackChecklist()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}