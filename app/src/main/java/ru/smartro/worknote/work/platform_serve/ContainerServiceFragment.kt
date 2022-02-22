package ru.smartro.worknote.work.platform_serve

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.fragment_container_service.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.Inull
import ru.smartro.worknote.R
import ru.smartro.worknote.base.AbstractBottomDialog
import ru.smartro.worknote.ui.problem.ContainerBreakdownAct
import ru.smartro.worknote.ui.problem.ContainerFailureAct

const val ARGUMENT_NAME__PLATFORM_ID = "ARGUMENT_NAME__PLATFORM_ID"
const val ARGUMENT_NAME__CONTAINER_ID = "ARGUMENT_NAME__CONTAINER_ID"
class ContainerServiceFragment : AbstractBottomDialog() {
    private val viewModel: PlatformServeViewModel by viewModel()
    private var volume: Double? = null
    private lateinit var parentAct: PlatformServeAct

    private val p_platform_id: Int by lazy {
        getArgumentPlatformID()
    }
    private val p_container_id: Int by lazy {
        getArgumentContainerID()
    }
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

    fun addArgument(platformId: Int, containerId: Int) {
        val bundle = Bundle(2)
        bundle.putInt(ARGUMENT_NAME__PLATFORM_ID, platformId)
        bundle.putInt(ARGUMENT_NAME__CONTAINER_ID, containerId)
        this.arguments = bundle
    }

    private fun getArgumentPlatformID(): Int {
        val result = requireArguments().getInt(ARGUMENT_NAME__PLATFORM_ID, Inull)
        return result
    }

    private fun getArgumentContainerID(): Int {
        val result = requireArguments().getInt(ARGUMENT_NAME__CONTAINER_ID, Inull)
        return result
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentAct = requireActivity() as PlatformServeAct
        val containerEntity = viewModel.findContainerEntity(p_container_id)
        containerEntity.let {
            comment_et.setText(it.comment)
            volume = it.volume
            setVolume(view, it.volume)
            enter_info_tittle.text = "Заполненность конт №${it.number}"
        }
        comment_et.addTextChangedListener{
            viewModel.updateContainerComment(p_platform_id, p_container_id, it.toString())
        }

        val apbFailure = view.findViewById<AppCompatButton>(R.id.apb_fragment_container_serve_failure)
        if (containerEntity.isFailureNotEmpty()) {
            setUseButtonStyleBackgroundRed(apbFailure)
        }
        apbFailure.setOnClickListener {
            val intent = Intent(requireContext(), ContainerFailureAct::class.java)
            intent.putExtra("is_container", true)
            intent.putExtra("container_id", p_container_id)
            intent.putExtra("platform_id", p_platform_id)
            startActivityForResult(intent, 99)
        }
        val apbBreakdown = view.findViewById<AppCompatButton>(R.id.apb_fragment_container_serve_breakdown)
        if (containerEntity.isBreakdownNotEmpty()) {
            setUseButtonStyleBackgroundRed(apbBreakdown)
        }
        apbBreakdown.setOnClickListener {
            val intent = Intent(requireContext(), ContainerBreakdownAct::class.java)
            intent.putExtra("is_container", true)
            intent.putExtra("container_id", p_container_id)
            intent.putExtra("platform_id", p_platform_id)
            startActivityForResult(intent, 99)
        }

    }


    private fun setUseButtonStyleBackgroundRed(appCompatButton: AppCompatButton) {
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_red__usebutton))
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
//        if (isNotDefault(volume, comment)) {
//
//        }
        parentAct.updateRecyclerview()
    }

    private fun setVolume(view: View, volume: Double?) {
        var prevRadioButton = view.findViewById<RadioButton>(R.id.percent_0)
        enter_info_percent_rg.setOnCheckedChangeListener { group, checkedId ->
            prevRadioButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            val radioButton = view.findViewById<RadioButton>(checkedId)
            this.volume = toPercent(radioButton.text.toString())
            viewModel.updateContainerVolume(p_platform_id, p_container_id, this.volume)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 99 && resultCode == 99) {
//            clearContainerVolume()
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