package ru.smartro.worknote.ui.camera

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.alert_warning_camera.view.accept_btn
import kotlinx.android.synthetic.main.alert_warning_delete.view.*
import kotlinx.android.synthetic.main.fragment_gallery_before.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.GalleryPhotoAdapter
import ru.smartro.worknote.adapter.listener.ImageClickListener
import ru.smartro.worknote.extensions.hideDialog
import ru.smartro.worknote.extensions.warningDelete
import ru.smartro.worknote.service.database.entity.way_task.PlatformEntity
import ru.smartro.worknote.ui.platform_service.PlatformServiceViewModel
import ru.smartro.worknote.util.PhotoTypeEnum
import java.io.File


class GalleryFragment(private val platformId: Int, private val photoFor: Int) : BottomSheetDialogFragment(), ImageClickListener, ImageDetailDeleteListener{
    private val TAG = "GalleryFragment_LOG"
    private val viewModel: PlatformServiceViewModel by viewModel()
    private val listener: ImageClickListener = this
    private lateinit var platform: PlatformEntity
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery_before, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        Log.d("GalleryFragment_LOG", "photoFor: $photoFor")
        Log.d("GalleryFragment_LOG ", "wayPoinId: $platformId")
        platform = viewModel.findPlatformEntity(platformId)!!
        when (photoFor) {
            PhotoTypeEnum.forBeforeMedia -> {
                activity?.actionBar?.title = getString(R.string.service_before)
                image_title.text = getString(R.string.service_before)
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(platform.mediaBefore!!))
            }

            PhotoTypeEnum.forAfterMedia -> {
                activity?.actionBar?.title = getString(R.string.service_after)
                image_title.text = getString(R.string.service_after)
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(platform.mediaAfter!!))
            }

            PhotoTypeEnum.forPlatformProblem -> {
                activity?.actionBar?.title = getString(R.string.problem_container)
                image_title.text = getString(R.string.problem_container)
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(platform.mediaPlatformProblem!!))
            }

            PhotoTypeEnum.forContainerProblem -> {
                activity?.actionBar?.title = getString(R.string.problem_container)
                image_title.text = getString(R.string.problem_container)
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(platform.mediaContainerProblem!!))
            }
        }
    }

    override fun imageDetailClicked(photoPath: String) {
        val dialogFragment = ImageDetailFragment(platformId, photoPath, photoFor, this)
        dialogFragment.show(childFragmentManager, "ImageDetailFragment")
    }

    override fun imageRemoveClicked(photoPath: String) {
        warningDelete(getString(R.string.warning_detele)).run {
            this.accept_btn.setOnClickListener {
                viewModel.removePhotoFromServedEntity(photoFor, photoPath, platformId)
                lifecycleScope.launch(Dispatchers.IO) {
                    File(photoPath).delete()
                }
                initViews()
                hideDialog()
            }
            this.dismiss_btn.setOnClickListener {
                hideDialog()
            }
        }
    }

    override fun imageDeleted(imagePath: String) {
        initViews()
    }
}
