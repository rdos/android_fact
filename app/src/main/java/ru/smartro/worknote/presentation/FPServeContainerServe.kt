package ru.smartro.worknote.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import ru.smartro.worknote.Inull
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ARGUMENT_NAME___PARAM_ID
import ru.smartro.worknote.abs.AbstractBottomSheetF
import ru.smartro.worknote.hideDialog


class FPServeContainerServe : AbstractBottomSheetF() {

    companion object {
        const val NAV_ID = R.id.FPServeContainerServe
    }

    private var mContainerId: Int = Inull
    private val vm: VMPserve by activityViewModels()
    private var volume: Double? = null

    private var rgPercents: RadioGroup? = null
    private var apbFailure: AppCompatButton? = null
    private var apbBreakdown: AppCompatButton? = null
    private var apbBeforeMedia: AppCompatButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_container_serve, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mContainerId = arguments?.getInt(ARGUMENT_NAME___PARAM_ID)!!

        rgPercents = view.findViewById(R.id.enter_info_percent_rg)
        apbFailure = view.findViewById(R.id.apb_fragment_container_serve_failure)
        apbBreakdown = view.findViewById(R.id.apb_fragment_container_serve_breakdown)
        apbBeforeMedia = view.findViewById(R.id.apb_fragment_container_serve__before_media)

        val containerEntity = vm.getContainer(mContainerId)

        containerEntity.let {
            view.findViewById<TextInputEditText>(R.id.comment_et).setText(it.comment)
            volume = it.volume
            setVolume(view, it.volume)
            if(it.number == null)
                view.findViewById<TextView>(R.id.enter_info_tittle).text = "?????????????????????????? ????????????????????"
            else
                view.findViewById<TextView>(R.id.enter_info_tittle).text = "?????????????????????????? ???????? ???${it.number}"
        }
        view.findViewById<TextInputEditText>(R.id.comment_et).addTextChangedListener{
            // TODO:
            vm.updateContainerComment(mContainerId, it.toString())
        }

        if (containerEntity.isFailureNotEmpty()) {
            setUseButtonStyleBackgroundRed(apbFailure!!)
        }
        apbFailure?.setOnClickListener {
            val checkedId = rgPercents!!.checkedRadioButtonId
            val radioButton = view.findViewById<RadioButton>(checkedId)
            val volume = toPercent(radioButton.text.toString())
            vm.updateContainerVolume(mContainerId, volume)
            navigateNext(FPhotoFailureMediaContainer.NAV_ID, mContainerId, vm.getPlatformId().toString())
        }
        if (containerEntity.isBreakdownNotEmpty()) {
            setUseButtonStyleBackgroundRed(apbBreakdown!!)
        }
        apbBreakdown?.setOnClickListener {
            val checkedId = rgPercents!!.checkedRadioButtonId
            val radioButton = view.findViewById<RadioButton>(checkedId)
            val volume = toPercent(radioButton.text.toString())
            vm.updateContainerVolume(mContainerId, volume)
            navigateNext(FPhotoBreakdownMediaContainer.NAV_ID, mContainerId, vm.getPlatformId().toString())
        }

//        if (containerEntity.isBreakdownNotEmpty()) {
//            setUseButtonStyleBackgroundRed(apbBreakdown)
//        }
        apbBeforeMedia?.setOnClickListener {
            navigateNext(FPhotoBeforeMediaContainer.NAV_ID, vm.getPlatformId())
            hideDialog()
        }

//        if (containerEntity.isBreakdownNotEmpty()) {
//            setUseButtonStyleBackgroundRed(apbBreakdown)
//        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        vm.updateContainerComment(
            mContainerId,
            view?.findViewById<TextInputEditText>(R.id.comment_et)?.text.toString()
        )
    }

    private fun setUseButtonStyleBackgroundRed(appCompatButton: AppCompatButton) {
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_red__usebutton))
    }

    private fun setVolume(view: View, volume: Double?) {
        val percent0 = view.findViewById<RadioButton>(R.id.percent_0)
        val percent25 = view.findViewById<RadioButton>(R.id.percent_25)
        val percent50 = view.findViewById<RadioButton>(R.id.percent_50)
        val percent75 = view.findViewById<RadioButton>(R.id.percent_75)
        val percent100 = view.findViewById<RadioButton>(R.id.percent_100)
        val percent125 = view.findViewById<RadioButton>(R.id.percent_125)
        when (volume) {
            0.00 -> percent0.isChecked = true
            0.25 -> percent25.isChecked = true
            0.50 -> percent50.isChecked = true
            0.75 -> percent75.isChecked = true
            1.00 -> percent100.isChecked = true
            1.25 -> percent125.isChecked = true
            null -> {
                percent0.isChecked = false
                percent25.isChecked = false
                percent50.isChecked = false
                percent75.isChecked = false
                percent100.isChecked = true
                percent125.isChecked = false
            }
        }
        var prevRadioButton = percent0
        rgPercents?.setOnCheckedChangeListener { group, checkedId ->
            prevRadioButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            val radioButton = view.findViewById<RadioButton>(checkedId)
            val newVolume = toPercent(radioButton.text.toString())
            LOG.debug("newVolume=${newVolume}")
            this.volume = newVolume
            vm.updateContainerVolume(mContainerId, this.volume)
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