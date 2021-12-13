package ru.smartro.worknote.ui.platform_serve

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.fragment_container_service.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.base.AbstractBottomDialog
import ru.smartro.worknote.ui.problem.ExtremeProblemActivity

class ContainerServiceFragment(val containerId: Int, val platformId: Int) : AbstractBottomDialog() {
    private val viewModel: PlatformServeViewModel by viewModel()
    private var comment: String? = null
    private var volume: Double? = null
    private lateinit var parentActivity: PlatformServeActivity

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
        parentActivity = requireActivity() as PlatformServeActivity
        viewModel.findContainerEntity(containerId).let {
            comment_et.setText(it.comment)
            comment = it.comment
            volume = it.volume
            setVolume(view, it.volume)
            enter_info_tittle.text = "Заполненность конт №${it.number}"
        }
        comment_et.addTextChangedListener { comment = it.toString() }
        comment_clear.setOnClickListener {
            clearContainerVolume()
        }
        enter_info_problem_btn.setOnClickListener {
            val intent = Intent(requireContext(), ExtremeProblemActivity::class.java)
            intent.putExtra("is_container", true)
            intent.putExtra("container_id", containerId)
            intent.putExtra("platform_id", platformId)
            startActivityForResult(intent, 99)
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (isNotDefault(volume, comment)) {
            viewModel.updateContainerVolume(platformId, containerId, volume, comment)
        }
        parentActivity.updateRecyclerview()
    }

    private fun setVolume(view: View, volume: Double?) {
        var prevRadioButton = view.findViewById<RadioButton>(R.id.percent_0)
        enter_info_percent_rg.setOnCheckedChangeListener { group, checkedId ->
            prevRadioButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            val radioButton = view.findViewById<RadioButton>(checkedId)
            this.volume = toPercent(radioButton.text.toString())
            when (radioButton.isChecked) {
                true -> {
                    radioButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                false -> {
                    radioButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }
            prevRadioButton = radioButton
        }
        when (volume) {
            0.00 -> percent_0.isChecked = true
            0.25 -> percent_25.isChecked = true
            0.50 -> percent_50.isChecked = true
            0.75 -> percent_75.isChecked = true
            1.00 -> percent_100.isChecked = true
            1.25 -> percent_125.isChecked = true
            null -> {
                percent_0.isChecked = false
                percent_25.isChecked = false
                percent_50.isChecked = false
                percent_75.isChecked = false
                percent_100.isChecked = true
                percent_125.isChecked = false
            }
        }

    }

    private fun isNotDefault(volume: Double?, comment: String?): Boolean {
        Log.d("ContainerExpandAdapter", "volumeIsNotO:${volume != null} commentIsNotNullOrEmpty:${!comment.isNullOrEmpty()} ")
        return volume != null || !comment.isNullOrEmpty()
    }

    private fun clearContainerVolume() {
        percent_0.isChecked = false
        percent_25.isChecked = false
        percent_50.isChecked = false
        percent_75.isChecked = false
        percent_100.isChecked = false
        percent_125.isChecked = false
        comment_et.setText(null)
        comment = null
        volume = null
        viewModel.clearContainerVolume(platformId, containerId)
        parentActivity.updateRecyclerview()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 99 && resultCode == 99) {
            clearContainerVolume()
            dismiss()
        }
    }

    private fun toPercent(percent: String): Double {
        return when (percent.replace("%", "").toInt()) {
            0 -> 0.00
            25 -> 0.25
            50 -> 0.50
            75 -> 0.75
            100 -> 1.00
            else -> 1.25
        }
    }

}