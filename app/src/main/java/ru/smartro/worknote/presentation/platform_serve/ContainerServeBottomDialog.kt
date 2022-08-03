package ru.smartro.worknote.presentation.platform_serve

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import ru.smartro.worknote.ARGUMENT_NAME___PARAM_ID
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AbstractBottomDialog
import ru.smartro.worknote.awORKOLDs.extensions.hideDialog


class ContainerServeBottomDialog : AbstractBottomDialog() {
    private val viewModel: PlatformServeSharedViewModel by activityViewModels()
    private var volume: Double? = null
    private lateinit var parentAct: PServeAct

    private var firstTime = true

    val args: ContainerServeBottomDialogArgs by navArgs()

    private var p_container_id: Int = -1

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
        parentAct = requireActivity() as PServeAct

        p_container_id = arguments?.getInt(ARGUMENT_NAME___PARAM_ID)!!

        val containerEntity = viewModel.baseDat.getContainerEntity(p_container_id)
        containerEntity.let {
            view.findViewById<TextInputEditText>(R.id.comment_et).setText(it.comment)
            volume = it.volume
            setVolume(view, it.volume)
            view.findViewById<TextView>(R.id.enter_info_tittle).text = "Заполненность конт №${it.number}"
        }
        view.findViewById<TextInputEditText>(R.id.comment_et).addTextChangedListener{
            // TODO:
            viewModel.updateContainerComment(viewModel.mPlatformEntity.value!!.platformId!!, p_container_id, it.toString())
        }

        val apbFailure = view.findViewById<AppCompatButton>(R.id.apb_fragment_container_serve_failure)
        Log.d("TEST:::", "fucked up : ${containerEntity.containerId} : ${containerEntity.isFailureNotEmpty()}")
        if (containerEntity.isFailureNotEmpty()) {
            setUseButtonStyleBackgroundRed(apbFailure)
        }
        apbFailure.setOnClickListener {
//            val intent = Intent(requireContext(), ContainerFailureAct::class.java)
//            intent.putExtra("is_container", true)
//            intent.putExtra("container_id", p_container_id)
            val checkedId = view.findViewById<RadioGroup>(R.id.enter_info_percent_rg).checkedRadioButtonId
            val radioButton = view.findViewById<RadioButton>(checkedId)
            val volume = toPercent(radioButton.text.toString())
            viewModel.updateContainerVolume(viewModel.mPlatformEntity.value!!.platformId!!, p_container_id, volume)
            navigateMain(R.id.PhotoFailureMediaContainerF, p_container_id, viewModel.mPlatformEntity.value!!.platformId!!.toString())
//            intent.putExtra("platform_id", viewModel.mPlatformEntity.value!!.platformId!!)
//            startActivityForResult(intent, 99)
        }
        val apbBreakdown = view.findViewById<AppCompatButton>(R.id.apb_fragment_container_serve_breakdown)
        if (containerEntity.isBreakdownNotEmpty()) {
            setUseButtonStyleBackgroundRed(apbBreakdown)
        }
        apbBreakdown.setOnClickListener {
//            val intent = Intent(requireContext(), ContainerBreakdownAct::class.java)
//            intent.putExtra("is_container", true)
            val checkedId = view.findViewById<RadioGroup>(R.id.enter_info_percent_rg).checkedRadioButtonId
            val radioButton = view.findViewById<RadioButton>(checkedId)
            val volume = toPercent(radioButton.text.toString())
            viewModel.updateContainerVolume(viewModel.mPlatformEntity.value!!.platformId!!, p_container_id, volume)
            navigateMain(R.id.PhotoBreakdownMediaContainerF, p_container_id, viewModel.mPlatformEntity.value!!.platformId!!.toString())
//            intent.putExtra("container_id", p_container_id)
//            intent.putExtra("platform_id", viewModel.mPlatformEntity.value!!.platformId!!)
//            startActivityForResult(intent, 99)
        }

        val apbBeforeMedia = view.findViewById<AppCompatButton>(R.id.apb_fragment_container_serve__before_media)
//        if (containerEntity.isBreakdownNotEmpty()) {
//            setUseButtonStyleBackgroundRed(apbBreakdown)
//        }
        apbBeforeMedia.setOnClickListener {
            //todo: жопа))))))))))))) copy-past from PlatformServeAct search(startActivityForResult(intent, 1001)
//            val intent = Intent(requireActivity(), CameraAct::class.java)
//            intent.putExtra("platform_id", viewModel.mPlatformEntity.value!!.platformId!!)
////            intent.putExtra("photoFor", PhotoTypeEnum.forBeforeMedia)
//            intent.putExtra("isNoLimitPhoto", true)
//            startActivityForResult(intent, 1001)
            navigateMain(R.id.PhotoBeforeMediaContainerF, viewModel.mPlatformEntity.value!!.platformId!!)
            hideDialog()
        }

        val apbAfterMedia = view.findViewById<AppCompatButton>(R.id.apb_fragment_container_serve__after_media)
//        if (containerEntity.isBreakdownNotEmpty()) {
//            setUseButtonStyleBackgroundRed(apbBreakdown)
//        }
        apbAfterMedia.setOnClickListener {
            //todo: жоpa)№2)))))))))))) copy-past from PlatformServeAct search(startActivityForResult(intent, 1001)
//            val intent = Intent(requireActivity(), CameraAct::class.java)
//            intent.putExtra("platform_id", viewModel.mPlatformEntity.value!!.platformId!!)
//            intent.putExtra("photoFor", PhotoTypeEnum.forAfterMedia)
//            intent.putExtra("isNoLimitPhoto", true)
//            startActivityForResult(intent, 1001)
            navigateMain(R.id.PhotoAfterMediaContainerF, viewModel.mPlatformEntity.value!!.platformId!!)
            hideDialog()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.updateContainerComment(
            viewModel.mPlatformEntity.value!!.platformId!!,
            p_container_id,
            view?.findViewById<TextInputEditText>(R.id.comment_et)?.text.toString()
        )
    }

    private fun setUseButtonStyleBackgroundRed(appCompatButton: AppCompatButton) {
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_red__usebutton))
    }

//    override fun onDismiss(dialog: DialogInterface) {
//        super.onDismiss(dialog)
//        parentAct.findNavController(R.id.navigation_terminate).updateRecyclerView()
//    }

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
        view.findViewById<RadioGroup>(R.id.enter_info_percent_rg).setOnCheckedChangeListener { group, checkedId ->
            prevRadioButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            val radioButton = view.findViewById<RadioButton>(checkedId)
            this.volume = toPercent(radioButton.text.toString())
            viewModel.updateContainerVolume(viewModel.mPlatformEntity.value!!.platformId!!, p_container_id, this.volume)
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