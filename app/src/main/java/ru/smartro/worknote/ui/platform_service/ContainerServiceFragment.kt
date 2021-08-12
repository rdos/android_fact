package ru.smartro.worknote.ui.platform_service

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_container_service.*
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R

class ContainerServiceFragment(val containerId: Int, val platformId: Int) : BottomSheetDialogFragment() {
    private val viewModel: PlatformServiceViewModel by viewModel()
    private var comment: String = ""
    private var volume: Double = 0.0
    private lateinit var hostFragment: PlatformServiceFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_container_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hostFragment = parentFragment as PlatformServiceFragment
        viewModel.findContainerEntity(containerId).let {
            comment_et.setText(it.comment)
            setPercent(
                enter_info_percent_rg, percent_0, percent_25,
                percent_50, percent_75,
                percent_100, percent_125, it.volume!!
            )
            enter_info_tittle.text = "Заполненность конт №${it.number}"
        }

        enter_info_percent_rg.setOnSelectListener {
            volume = toPercent(it.text.replace("%", "").toInt())
        }
        comment_et.addTextChangedListener { comment = it.toString() }

        comment_clear.setOnClickListener {
            comment_et.setText("")
            enter_info_percent_rg.selectButton(percent_0)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (isNotDefault(volume, comment)) {
            viewModel.updateContainerVolume(platformId, containerId, volume, comment)
            hostFragment.updateRecyclerview()
        }
    }

    private fun setPercent(
        group: ThemedToggleButtonGroup, view0: ThemedButton, view25: ThemedButton, view50:
        ThemedButton, view75: ThemedButton, view100: ThemedButton, view125: ThemedButton, percent: Double
    ) {
        when (percent) {
            0.00 -> {
                group.selectButton(view0)
            }
            0.25 -> {
                group.selectButton(view25)
            }
            0.50 -> {
                group.selectButton(view50)
            }
            0.75 -> {
                group.selectButton(view75)
            }
            1.00 -> {
                group.selectButton(view100)
            }
            else -> {
                group.selectButton(view125)
            }
        }

    }

    private fun isNotDefault(volume: Double, comment: String): Boolean {
        Log.d("ContainerExpandAdapter", "volumeIsNotO:${volume != 0.0} commentIsNotNullOrEmpty:${comment.isNullOrEmpty()} ")
        return volume != 0.0 || comment.isNotEmpty()
    }

    private fun toPercent(percent: Int): Double {
        return when (percent) {
            0 -> 0.00
            25 -> 0.25
            50 -> 0.50
            75 -> 0.75
            100 -> 1.00
            else -> 1.25
        }
    }

}