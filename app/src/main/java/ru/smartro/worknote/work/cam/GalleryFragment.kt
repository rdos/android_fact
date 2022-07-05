package ru.smartro.worknote.work.cam

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.alert_warning_camera.view.accept_btn
import kotlinx.android.synthetic.main.alert_warning_delete.view.*
import kotlinx.android.synthetic.main.fragment_gallery_before.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.adapter.GalleryPhotoAdapter
import ru.smartro.worknote.awORKOLDs.adapter.listener.ImageClickListener
import ru.smartro.worknote.awORKOLDs.base.AbstractBottomDialog
import ru.smartro.worknote.awORKOLDs.extensions.hideDialog
import ru.smartro.worknote.awORKOLDs.extensions.warningDelete
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.platform_serve.PlatformServeViewModel
import ru.smartro.worknote.awORKOLDs.util.PhotoTypeEnum
import ru.smartro.worknote.work.ui.ImageDetailDeleteListener
import ru.smartro.worknote.work.ui.ImageDetailFragment


class GalleryFragment(private val platformId: Int, private val photoFor: Int,
                      private val containerId : Int, private val imageCountListener : ImageCounter
)
    : AbstractBottomDialog(), ImageClickListener, ImageDetailDeleteListener {
    private val vs: PlatformServeViewModel by viewModel()
    private val listener: ImageClickListener = this
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery_before, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        Log.d(TAG, "photoFor: $photoFor")
        Log.d(TAG, "wayPoinId: $platformId")
        when (photoFor) {
            PhotoTypeEnum.forBeforeMedia -> {
              val platform = vs.baseDat._getPlatformEntity_know0(platformId)
                activity?.actionBar?.title = getString(R.string.service_before)
                image_title.text = getString(R.string.service_before)
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(platform.beforeMedia))
            }

            PhotoTypeEnum.forAfterMedia -> {
                val platform = vs.baseDat._getPlatformEntity_know0(platformId)
                activity?.actionBar?.title = getString(R.string.service_after)
                image_title.text = getString(R.string.service_after)
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(platform.afterMedia))
            }

            PhotoTypeEnum.forPlatformProblem -> {
                val  platform = vs.baseDat._getPlatformEntity_know0(platformId)
                activity?.actionBar?.title = getString(R.string.problem_container)
                image_title.text = getString(R.string.problem_container)
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(platform.failureMedia))
            }

            PhotoTypeEnum.forContainerFailure -> {
                val container = vs.baseDat._getContainerEntity_know0(containerId)
                activity?.actionBar?.title = getString(R.string.problem_container)
                image_title.text = getString(R.string.problem_container)
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(container.failureMedia))
            }
            PhotoTypeEnum.forContainerBreakdown -> {
                val container = vs.baseDat._getContainerEntity_know0(containerId)
                activity?.actionBar?.title = "Поломка контейнера"
                image_title.text = getString(R.string.problem_container)
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(container.breakdownMedia))
            }
            PhotoTypeEnum.forServedKGO -> {
                val  platform = vs.baseDat._getPlatformEntity_know0(platformId)
                activity?.actionBar?.title = getString(R.string.problem_container)
                image_title.text = getString(R.string.problem_container)
                // TODO: 14.01.2022 r_dos!!!
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(platform.kgoServed?.media))
            }
            PhotoTypeEnum.forRemainingKGO -> {
                val  platform = vs.baseDat._getPlatformEntity_know0(platformId)
                activity?.actionBar?.title = getString(R.string.problem_container)
                image_title.text = getString(R.string.problem_container)
                // TODO: 14.01.2022 r_dos!!! 
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(platform.kgoRemaining?.media))
            }
            PhotoTypeEnum.forPlatformPickupVolume -> {
                val  platform = vs.baseDat._getPlatformEntity_know0(platformId)
                activity?.actionBar?.title = getString(R.string.service_pickup_volume)
                image_title.text = getString(R.string.service_pickup_volume)
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(platform.pickupMedia))
            }
        }
    }

    override fun imageDetailClicked(imageBase64: ImageEntity) {
        val dialogFragment = ImageDetailFragment(platformId, containerId, imageBase64, photoFor, this)
        dialogFragment.show(childFragmentManager, "ImageDetailFragment")
    }

    override fun imageRemoveClicked(imageBase64: ImageEntity) {
        warningDelete(getString(R.string.warning_detele)).let {
            it.accept_btn.setOnClickListener {
                if (photoFor == PhotoTypeEnum.forContainerBreakdown
                    || photoFor == PhotoTypeEnum.forContainerFailure) {
                    vs.removeContainerMedia(photoFor, platformId, containerId, imageBase64)
                } else {
                    vs.removePlatformMedia(photoFor, imageBase64, platformId)
                }
                imageCountListener.mediaSizeChanged()
                Log.d(TAG, "imageRemoveClicked: initvews")
                initViews()
                hideDialog()
            }
            it.dismiss_btn.setOnClickListener {
                hideDialog()
            }
        }
    }

    override fun imageDeleted() {
        imageCountListener.mediaSizeChanged()
        initViews()
    }
}
