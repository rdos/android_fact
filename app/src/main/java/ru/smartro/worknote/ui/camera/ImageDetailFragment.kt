package ru.smartro.worknote.ui.camera

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.alert_warning_camera.view.accept_btn
import kotlinx.android.synthetic.main.alert_warning_delete.view.*
import kotlinx.android.synthetic.main.fragment_image_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.extensions.hideDialog
import ru.smartro.worknote.extensions.warningDelete
import ru.smartro.worknote.service.database.entity.work_order.ImageEntity
import ru.smartro.worknote.ui.platform_serve.PlatformServeViewModel
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.PhotoTypeEnum


class ImageDetailFragment(private val platformId: Int, private val containerId : Int,
                          private val imageBase64: ImageEntity, private val photoFor: Int,
                          private val listener: ImageDetailDeleteListener) : DialogFragment() {
    private val viewModel: PlatformServeViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_AppCompat_Dialog)
        Glide.with(this).load(MyUtil.base64ToImage(imageBase64.image)).into(image_detail)
        image_detail_delete.setOnClickListener {
            warningDelete(getString(R.string.warning_detele)).run {
                this.accept_btn.setOnClickListener {
                    if (photoFor == PhotoTypeEnum.forContainerProblem) {
                        viewModel.removeContainerMedia(platformId, containerId, imageBase64)
                    } else {
                        viewModel.removePlatformMedia(photoFor, imageBase64, platformId)
                    }
                    listener.imageDeleted()
                    hideDialog()
                    dismiss()
                }
                this.dismiss_btn.setOnClickListener {
                    hideDialog()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }
}

interface ImageDetailDeleteListener {
    fun imageDeleted()
}