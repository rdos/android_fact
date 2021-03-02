package ru.smartro.worknote.ui.camera

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.alert_warning_camera.view.accept_btn
import kotlinx.android.synthetic.main.alert_warning_delete.view.*
import kotlinx.android.synthetic.main.fragment_gallery_before.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.GalleryPhotoAdapter
import ru.smartro.worknote.adapter.listener.ImageClickListener
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.warningDelete
import ru.smartro.worknote.service.database.entity.container_service.ServedPointEntity
import ru.smartro.worknote.ui.point_service.PointServiceViewModel
import ru.smartro.worknote.util.PhotoTypeEnum
import java.io.File


class GalleryFragment(private val wayPointId: Int, private val photoFor: Int) : BottomSheetDialogFragment(), ImageClickListener {
    private val TAG = "GalleryBeforeFragment"
    private val viewModel: PointServiceViewModel by viewModel()
    private val listener: ImageClickListener = this
    private lateinit var servedPointEntity: ServedPointEntity
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery_before, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        Log.d("GalleryFragment_LOG", "wayPoinId: $photoFor")
        servedPointEntity = viewModel.findServedPointEntity(wayPointId)!!
        when (photoFor) {
            PhotoTypeEnum.forBeforeMedia -> {
                activity?.actionBar?.title = getString(R.string.service_before)
                image_title.text = getString(R.string.service_before)
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(servedPointEntity.mediaBefore!!))
                Log.d("GalleryFragment_LOG", "findBeforePhoto: ${Gson().toJson(servedPointEntity.mediaBefore!!)}")
            }

            PhotoTypeEnum.forAfterMedia -> {
                activity?.actionBar?.title = getString(R.string.service_after)
                image_title.text = getString(R.string.service_after)
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(servedPointEntity.mediaAfter!!))
                Log.d(TAG, "findBeforePhoto: ${Gson().toJson(servedPointEntity.mediaAfter!!)}")
            }

            PhotoTypeEnum.forProblemPoint -> {
                activity?.actionBar?.title = getString(R.string.problem_container)
                image_title.text = getString(R.string.problem_container)
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(servedPointEntity.mediaPointProblem!!))
                Log.d(TAG, "findBeforePhoto: ${Gson().toJson(servedPointEntity.mediaPointProblem!!)}")
            }

            PhotoTypeEnum.forProblemContainer -> {
                activity?.actionBar?.title = getString(R.string.problem_container)
                image_title.text = getString(R.string.problem_container)
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(servedPointEntity.mediaProblemContainer!!))
                Log.d(TAG, "findBeforePhoto: ${Gson().toJson(servedPointEntity.mediaProblemContainer!!)}")
            }
        }
    }

    override fun imageDetailClicked(photoPath: String) {
        val dialogFragment = ImageDetailFragment(wayPointId, photoPath, photoFor)
        dialogFragment.show(childFragmentManager, "ImageDetailFragment")
    }

    override fun imageRemoveClicked(photoPath: String) {
        warningDelete(getString(R.string.warning_detele)).run {
            this.accept_btn.setOnClickListener {
                viewModel.removePhotoFromServedEntity(photoFor, photoPath, wayPointId)
                lifecycleScope.launch(Dispatchers.IO) {
                    File(photoPath).delete()
                }
                initViews()
                loadingHide()
            }
            this.dismiss_btn.setOnClickListener {
                loadingHide()
            }
        }
    }
}
