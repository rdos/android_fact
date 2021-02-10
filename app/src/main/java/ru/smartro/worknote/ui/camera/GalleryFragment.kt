package ru.smartro.worknote.ui.camera

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.alert_warning_camera.view.accept_btn
import kotlinx.android.synthetic.main.alert_warning_delete.view.*
import kotlinx.android.synthetic.main.fragment_gallery_before.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.GalleryPhotoAdapter
import ru.smartro.worknote.adapter.listener.ImageClickListener
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.extensions.warningDelete
import ru.smartro.worknote.ui.point_service.PointServiceViewModel
import ru.smartro.worknote.util.PhotoTypeEnum


class GalleryFragment(private val wayPointId: Int, private val photoFor: Int) : BottomSheetDialogFragment(), ImageClickListener {
    private val TAG = "GalleryBeforeFragment"
    private val viewModel: PointServiceViewModel by viewModel()
    private val listener: ImageClickListener = this
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery_before, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val servedPointEntity = viewModel.findServedPointEntity(wayPointId)
        when (photoFor) {
            PhotoTypeEnum.forBeforeMedia -> {
                activity?.actionBar?.title = getString(R.string.service_before)
                image_title.text = getString(R.string.service_before)
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(servedPointEntity.mediaBefore!!))
                Log.d(TAG, "findBeforePhoto: ${Gson().toJson(servedPointEntity.mediaBefore!!)}")
            }

            PhotoTypeEnum.forAfterMedia -> {
                activity?.actionBar?.title = getString(R.string.service_after)
                image_title.text = getString(R.string.service_after)
                image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), ArrayList(servedPointEntity.mediaAfter!!))
                Log.d(TAG, "findBeforePhoto: ${Gson().toJson(servedPointEntity.mediaAfter!!)}")
            }

            PhotoTypeEnum.forProblemMedia -> {
                activity?.actionBar?.title = getString(R.string.service_before)
                image_title.text = getString(R.string.service_before)
                /*     viewModel.findBeforePhoto(wayPointId).observe(viewLifecycleOwner, Observer {
                         image_rv.adapter = GalleryPhotoAdapter(listener, requireContext(), it as ArrayList<PhotoBeforeEntity>)
                         Log.d(TAG, "findBeforePhoto: ${Gson().toJson(it)}")
                     })*/
            }

        }
    }

    override fun imageDetailClicked(photoPath: String) {
        val dialogFragment = ImageDetailFragment(wayPointId, photoPath, photoFor)
        dialogFragment.show(childFragmentManager, "ImageDetailFragment")
    }

    override fun imageRemoveClicked(photoPath: String) {
        val servedPointEntity = viewModel.findServedPointEntity(wayPointId)
        warningDelete(getString(R.string.warning_detele)).run {
            this.accept_btn.setOnClickListener {
                when (photoFor) {
                    PhotoTypeEnum.forBeforeMedia -> {
                        servedPointEntity.mediaBefore!!.remove(photoPath)
                    }
                    PhotoTypeEnum.forAfterMedia -> {
                        servedPointEntity.mediaAfter!!.remove(photoPath)
                    }
                    PhotoTypeEnum.forProblemMedia -> {
                        toast("В разработке")
                    }
                }
                loadingHide()
            }
            this.dismiss_btn.setOnClickListener {
                loadingHide()
            }
        }
    }
}
