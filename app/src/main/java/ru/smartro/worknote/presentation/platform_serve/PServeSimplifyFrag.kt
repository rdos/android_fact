package ru.smartro.worknote.presentation.platform_serve

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.extensions.hideDialog
import ru.smartro.worknote.awORKOLDs.util.PhotoTypeEnum
import ru.smartro.worknote.presentation.platform_serve.adapters.SimplifiedContainerAdapter
import ru.smartro.worknote.presentation.platform_serve.adapters.TypedContainerAdapter
import ru.smartro.worknote.work.cam.CameraAct


class PServeSimplifyFrag : AFragment() {

    private val vm: PlatformServeSharedViewModel by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.fragment_platform_serve_simplified
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterCurrentTask = SimplifiedContainerAdapter(requireContext(), object : TypedContainerAdapter.TypedContainerListener {
            override fun onDecrease(clientGroupInd: Int, typeGroupInd: Int) {
                vm.onDecrease(clientGroupInd, typeGroupInd)
            }

            override fun onIncrease(clientGroupInd: Int, typeGroupInd: Int) {
                vm.onIncrease(clientGroupInd, typeGroupInd)
            }

            override fun onAddPhoto(clientGroupInd: Int, typeGroupInd: Int) {
//                val intent = Intent(requireActivity(), CameraAct::class.java)
//                intent.putExtra("isNoLimitPhoto", true)
//                intent.putExtra("platform_id", vm.mPlatformEntity.value!!.platformId!!)
//                intent.putExtra("photoFor", PhotoTypeEnum.forSimplifyServeBefore)
                navigateMain(R.id.PhotoBeforeMediaContainerSimplifyF, vm.mPlatformEntity.value!!.platformId!!)
//hideDialog()
//                startActivityForResult(intent, 1685)
            }
        })

        val rvCurrentTask = view.findViewById<RecyclerView>(R.id.rv_main).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterCurrentTask
        }

        vm.mSortedContainers.observe(viewLifecycleOwner) { list ->
            if(list != null) {
                adapterCurrentTask.containers = list
            }
        }

        vm.mServedContainers.observe(viewLifecycleOwner) { list ->
            if(list != null) {
                adapterCurrentTask.served = list
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == 1685 && resultCode == 5861) {
//            val intent = Intent(requireActivity(), CameraAct::class.java)
//            intent.putExtra("isNoLimitPhoto", true)
//            intent.putExtra("platform_id", vm.mPlatformEntity.value!!.platformId!!)
//            intent.putExtra("photoFor", PhotoTypeEnum.forSimplifyServeAfter)
//            hideDialog()
//            startActivityForResult(intent, 2685)
//        }
    }
}