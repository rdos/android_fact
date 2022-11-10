package ru.smartro.worknote.presentation.ac

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.AAct
import ru.smartro.worknote.abs.FragmentA
import ru.smartro.worknote.awORKOLDs.service.network.body.AuthBody
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.work.Status

class StartF : FragmentA() {
    
    private val vm: StartAct.AuthViewModel by activityViewModels()

    private var authLoginEditText: TextInputEditText? = null
    private var authPasswordEditText: TextInputEditText? = null
    private var authAppVersion: TextView? = null
    private var authRootView: ConstraintLayout? = null
    private var authEnter: AppCompatButton? = null
    private var authLoginOut: TextInputLayout? = null
    private var authPasswordOut: TextInputLayout? = null
    private var authDebugInfo: AppCompatTextView? = null
    
    override fun onGetLayout(): Int {
        return R.layout.f_start
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authLoginEditText = view.findViewById(R.id.auth_login)
        authPasswordEditText = view.findViewById(R.id.auth_password)
        authLoginEditText = view.findViewById(R.id.auth_login)
        authPasswordEditText = view.findViewById(R.id.auth_password)
        authAppVersion = view.findViewById(R.id.actv_act_start__appversion)
        authRootView = view.findViewById(R.id.cl_act_start)
        authEnter = view.findViewById(R.id.acb_login)
        authLoginOut = view.findViewById(R.id.login_login_out)
        authPasswordOut = view.findViewById(R.id.auth_password_out)
        authDebugInfo = view.findViewById(R.id.actv_activity_auth__it_test_version)

        authAppVersion?.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        authRootView?.setOnClickListener {
            MyUtil.hideKeyboard(requireActivity())
        }

        viewInit()
    }

    // TODO: 27.05.2022 !! !
    private fun gotoNextAct(isHasToken: Boolean = false) {
//            val isHasTask = true
        val isHasTask = vm.database.hasWorkOrderInProgress()
        if (isHasToken && isHasTask) {
            LOG.debug("::: HAS TOKEN AND TASK")
            vm.viewModelScope.launch {
                App.getAppliCation().getNetwork().sendAppStartUp()
            }
            startActivity(Intent(requireActivity(), MainAct::class.java))
            requireActivity().finish()
            return
        }

        if (isHasTask) {
            LOG.debug("::: HAS TASK")
            MyUtil.hideKeyboard(requireActivity())
            findNavController().navigate(R.id.ReAuthWarningDialogF)
        } else {
            LOG.debug("::: NOT HAVE TASK")
            startActivity(Intent(requireActivity(), XChecklistAct::class.java))
            requireActivity().finish()
        }

    }

    private fun viewInit() {
        authEnter?.setOnClickListener {
            clickAuthEnter()
        }

        authDebugInfo?.isVisible = false
        if (BuildConfig.BUILD_TYPE != "debugProd" && BuildConfig.BUILD_TYPE != "release") {
            val versionName = BuildConfig.VERSION_NAME
            //oopsTestqA
            val textIsTestEnv = getString(R.string.act_st_art_it_test_version).format(versionName)

            authDebugInfo?.isVisible = true
            authDebugInfo?.text = textIsTestEnv // + actv_activity_auth__it_test_version.text

            authEnter?.setOnLongClickListener {
                authLoginEditText?.setText("admin@smartro.ru")
                authPasswordEditText?.setText("xot1ieG5ro~hoa,ng4Sh")
                return@setOnLongClickListener true
            }
        }
//

        if (BuildConfig.BUILD_TYPE == "debugProd") {
            if (isDevelMode()) {
                authEnter?.setOnLongClickListener {
                    authLoginEditText?.setText("g79015884904@gmail.com")
                    authPasswordEditText?.setText("Grafik+76")
                    return@setOnLongClickListener true
                }
            }
        }

        if (BuildConfig.BUILD_TYPE != "release") {
            if (isDevelMode()) {
                if (BuildConfig.BUILD_TYPE == "debugProd") {
                    authLoginEditText?.setText("gkh2@smartro.ru")
                    authPasswordEditText?.setText("JT8NcST%sDqUpuc")
                } else {
                    authLoginEditText?.setText("admin@smartro.ru")
                    authPasswordEditText?.setText("xot1ieG5ro~hoa,ng4Sh")
                    clickAuthEnter()
                }
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val isHasToken = paramS().token.isShowForUser()
        LOG.debug("isHasToken=${isHasToken}")
        if (isHasToken) {
            gotoNextAct(isHasToken = true)
        }
    }

    private fun isDevelMode() = (requireActivity() as AAct).isDevelMode()

    private fun clickAuthEnter() {
        if (!authLoginEditText?.text.isNullOrBlank() && !authPasswordEditText?.text.isNullOrBlank()) {
            showingProgress()
            vm.auth(AuthBody(authLoginEditText?.text.toString(), authPasswordEditText?.text.toString()))
                .observe(viewLifecycleOwner) { result ->
                    val data = result.data
                    when (result.status) {
                        Status.SUCCESS -> {
                            hideProgress()
                            toast("Вы авторизованы")
//                            AppPreferences.BoTlogin = auth_login.text.toString()
                            paramS().token = data!!.data.token
                            paramS().userName = authLoginEditText?.text.toString()
                            gotoNextAct()
                        }
                        Status.ERROR -> {
                            hideProgress()
                            toast("Логин или пароль не совпадает")
                        }
                        Status.NETWORK -> {
                            hideProgress()
                            toast("Проблемы с интернетом")
                        }
                    }
                }
        } else {
            authLoginOut?.error = "Проверьте логин"
            authPasswordOut?.error = "Проверьте пароль"
        }
    }


}