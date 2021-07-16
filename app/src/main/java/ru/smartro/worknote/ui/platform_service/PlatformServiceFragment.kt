package ru.smartro.worknote.ui.platform_service

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_platform_service.*
import kotlinx.android.synthetic.main.alert_fill_kgo.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.container_service.ContainerAdapter
import ru.smartro.worknote.base.BaseFragment
import ru.smartro.worknote.extensions.fillKgoVolume
import ru.smartro.worknote.extensions.hideDialog
import ru.smartro.worknote.service.database.entity.work_order.ContainerEntity
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import ru.smartro.worknote.ui.camera.CameraActivity
import ru.smartro.worknote.ui.problem.ExtremeProblemActivity
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.StatusEnum

class PlatformServiceFragment(val platformId: Int) : BaseFragment(), ContainerAdapter.ContainerPointClickListener {
    private val REQUEST_EXIT = 33
    private val viewModel: PlatformServiceViewModel by viewModel()
    private lateinit var platformEntity: PlatformEntity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_platform_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        platformEntity = viewModel.findPlatformEntity(platformId)
        (requireActivity() as ServiceActivity).supportActionBar?.title = "${platformEntity.address}"
        initContainer()

        problem_btn.setOnClickListener {
            val intent = Intent(requireContext(), ExtremeProblemActivity::class.java)
            intent.putExtra("platform_id", platformEntity.platformId)
            intent.putExtra("isContainerProblem", false)
            startActivityForResult(intent, REQUEST_EXIT)
        }
        kgo_btn.setOnClickListener {
            val intent = Intent(requireContext(), CameraActivity::class.java)
            intent.putExtra("platform_id", platformEntity.platformId)
            intent.putExtra("photoFor", PhotoTypeEnum.forKGO)
            startActivityForResult(intent, 101)
        }

        complete_task_btn.setOnClickListener {
            (requireActivity() as ServiceActivity).nextPage()
        }
    }

    private fun initContainer() {
        val platform = viewModel.findPlatformEntity(platformId = platformEntity.platformId!!)
        platform_service_rv.adapter = ContainerAdapter(this, platform.containers)
        point_info_tv.text = "№${platform.srpId} / ${platform.containers!!.size} конт."
    }

    override fun onResume() {
        super.onResume()
        initContainer()
    }

    override fun startContainerService(item: ContainerEntity) {
        val intent = Intent(requireContext(), ContainerServiceActivity::class.java)
        intent.putExtra("container_id", item.containerId)
        intent.putExtra("platform_id", platformEntity.platformId)
        startActivityForResult(intent, 14)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 && resultCode == Activity.RESULT_OK) {
            viewModel.updatePlatformStatus(platformEntity.platformId!!, StatusEnum.SUCCESS)
            requireActivity().setResult(Activity.RESULT_OK)
            requireActivity().finish()
        } else if (resultCode == 101 && requestCode == 101) {
            fillKgoVolume().let { view ->
                view.kgo_accept_btn.setOnClickListener {
                    if (!view.kgo_volume_in.text.isNullOrEmpty()) {
                        val kgoVolume = view.kgo_volume_in.text.toString().toDouble()
                        viewModel.updatePlatformKGO(platformEntity.platformId!!, kgoVolume)
                        hideDialog()
                    }
                }
            }
        }
    }
}