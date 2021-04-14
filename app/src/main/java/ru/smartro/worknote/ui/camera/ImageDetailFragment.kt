package ru.smartro.worknote.ui.camera

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.alert_warning_camera.view.accept_btn
import kotlinx.android.synthetic.main.alert_warning_delete.view.*
import kotlinx.android.synthetic.main.fragment_image_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.extensions.hideDialog
import ru.smartro.worknote.extensions.warningDelete
import ru.smartro.worknote.ui.platform_service.PlatformServiceViewModel
import java.io.File


class ImageDetailFragment(private val platformId: Int, private val imagePath: String, private val photoFor: Int, private val listener : ImageDetailDeleteListener) : DialogFragment() {
    private val viewModel: PlatformServiceViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_AppCompat_Dialog)
        Glide.with(this).load(imagePath).into(image_detail)
        image_detail_delete.setOnClickListener {
            warningDelete(getString(R.string.warning_detele)).run {
                this.accept_btn.setOnClickListener {
                    viewModel.removePhotoFromServedEntity(photoFor, imagePath, platformId)
                    listener.imageDeleted(imagePath)
                    lifecycleScope.launch(Dispatchers.IO) {
                        File(imagePath).delete()
                    }
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
    fun imageDeleted(imagePath : String)
}