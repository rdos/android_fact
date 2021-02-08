package ru.smartro.worknote.ui.camera

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.alert_warning_camera.view.accept_btn
import kotlinx.android.synthetic.main.alert_warning_delete.view.*
import kotlinx.android.synthetic.main.fragment_gallery_before.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.PhotoBeforeAdapter
import ru.smartro.worknote.adapter.listener.ImageClickListener
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.extensions.warningDelete
import ru.smartro.worknote.service.db.entity.container_service.PhotoBeforeEntity
import ru.smartro.worknote.ui.point_service.PointServiceViewModel
import ru.smartro.worknote.util.PhotoTypeEnum


class GalleryFragment(private val containerId: Int, private val photoFor: Int) : BottomSheetDialogFragment(), ImageClickListener {
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
        when (photoFor) {
            PhotoTypeEnum.forBeforeMedia -> {
                activity?.actionBar?.title = getString(R.string.service_before)
                image_title.text = getString(R.string.service_before)
                viewModel.findBeforePhoto(containerId).observe(viewLifecycleOwner, Observer {
                    image_rv.adapter = PhotoBeforeAdapter(listener, requireContext(), it as ArrayList<PhotoBeforeEntity>)
                    Log.d(TAG, "findBeforePhoto: ${Gson().toJson(it)}")
                })
            }

            PhotoTypeEnum.forAfterMedia -> {
                activity?.actionBar?.title = getString(R.string.service_after)
                image_title.text = getString(R.string.service_after)
                viewModel.findBeforePhoto(containerId).observe(viewLifecycleOwner, Observer {
                    image_rv.adapter = PhotoBeforeAdapter(listener, requireContext(), it as ArrayList<PhotoBeforeEntity>)
                    Log.d(TAG, "findAfterPhoto: ${Gson().toJson(it)}")
                })
            }

            PhotoTypeEnum.forProblemMedia -> {
                activity?.actionBar?.title = getString(R.string.service_before)
                image_title.text = getString(R.string.service_before)
                viewModel.findBeforePhoto(containerId).observe(viewLifecycleOwner, Observer {
                    image_rv.adapter = PhotoBeforeAdapter(listener, requireContext(), it as ArrayList<PhotoBeforeEntity>)
                    Log.d(TAG, "findBeforePhoto: ${Gson().toJson(it)}")
                })
            }

        }
    }

    override fun imageDetailClicked(photoPath: String) {
        val dialogFragment = ImageDetailFragment(photoPath, photoFor)
        dialogFragment.show(childFragmentManager, "ImageDetailFragment")
    }

    override fun imageRemoveClicked(photoPath: String) {
        warningDelete(getString(R.string.warning_detele)).run {
            this.accept_btn.setOnClickListener {
                when (photoFor) {
                    PhotoTypeEnum.forBeforeMedia -> {
                        viewModel.delete1BeforePhoto(photoPath)
                    }
                    PhotoTypeEnum.forAfterMedia -> {
                        viewModel.delete1AfterPhoto(photoPath)
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
