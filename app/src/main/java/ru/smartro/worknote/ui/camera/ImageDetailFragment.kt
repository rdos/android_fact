package ru.smartro.worknote.ui.camera

import android.app.Dialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.alert_warning_camera.view.accept_btn
import kotlinx.android.synthetic.main.alert_warning_delete.view.*
import kotlinx.android.synthetic.main.fragment_image_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.extensions.warningDelete
import ru.smartro.worknote.ui.point_service.PointServiceViewModel
import ru.smartro.worknote.util.PhotoTypeEnum
import java.io.File


class ImageDetailFragment(private val wayPointId: Int, private val photoPath: String, private val photoFor: Int) : DialogFragment() {
    private val viewModel: PointServiceViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_AppCompat_Dialog)
        val image = BitmapFactory.decodeFile(photoPath)
        image_detail.setImageBitmap(image)
        val servedPointEntity = viewModel.findServedPointEntity(wayPointId)

        image_detail_delete.setOnClickListener {
            warningDelete(getString(R.string.warning_detele)).run {
                this.accept_btn.setOnClickListener {
                    when (photoFor) {
                        PhotoTypeEnum.forBeforeMedia -> {
                            servedPointEntity.mediaBefore!!.remove(photoPath)
                        }
                        PhotoTypeEnum.forAfterMedia -> {
                            servedPointEntity.mediaBefore!!.remove(photoPath)
                        }
                        PhotoTypeEnum.forProblemMedia -> {
                            toast("В разработке")
                        }
                    }
                    lifecycleScope.launch(Dispatchers.IO) {
                        File(photoPath).delete()
                    }
                    loadingHide()
                    dismiss()
                }
                this.dismiss_btn.setOnClickListener {
                    loadingHide()
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