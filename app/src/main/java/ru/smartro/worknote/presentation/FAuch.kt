package ru.smartro.worknote.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.AAct
import ru.smartro.worknote.abs.AF

class FAuch : AF() {
    
    private val vm: ActStart.AuthViewModel by activityViewModels()

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
            App.getAppliCation().hideKeyboard(requireActivity())
        }

        viewInit()
    }

    // TODO: 27.05.2022 !! !
    private fun gotoNextAct(isHasToken: Boolean = false) {
//            val isHasTask = true
        val isHasTask = vm.database.hasWorkOrderInProgress()
        if (isHasToken && isHasTask) {
            LOG.debug("::: HAS TOKEN AND TASK")
//            val rpcAppStartup = RPCappStartup()
//            rpcAppStartup.getLiveDate().observe(viewLifecycleOwner) { result ->
//                LOG.debug("${result}")
//                hideProgress()
//                if (result.isSent) {
//                    gotoNextAct()
//                }
//            }
//            App.oKRESTman().add(rpcAppStartup)
//
                //            vm.viewModelScope.launch {
                //                App.getAppliCation().getNetwork().sendAppStartUp()
                //            }
            startActivity(Intent(requireActivity(), ActMain::class.java))
            requireActivity().finish()
            return
        }

        if (isHasTask) {
            LOG.debug("::: HAS TASK")
            paramS().showClearCurrentTasks = true
            App.getAppliCation().hideKeyboard(requireActivity())
            findNavController().navigate(R.id.ReAuthWarningDialogF)
        } else {
            LOG.debug("::: NOT HAVE TASK")
            startActivity(Intent(requireActivity(), AXChecklist::class.java))
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
        val showClearCurrentTasks = paramS().showClearCurrentTasks
        LOG.debug("isHasToken=${isHasToken}")
        if (isHasToken && showClearCurrentTasks == false) {
            gotoNextAct(isHasToken = true)
        }
    }

    private fun isDevelMode() = (requireActivity() as AAct).isDevelMode()

    private fun clickAuthEnter() {
        if (!authLoginEditText?.text.isNullOrBlank() && !authPasswordEditText?.text.isNullOrBlank()) {
            showingProgress()
            paramS().userName = authLoginEditText?.text.toString()
            paramS().userPass = authPasswordEditText?.text.toString()

            val authRequest = RPOSTAuth()
            authRequest.getLiveDate().observe(viewLifecycleOwner) { result ->
                LOG.debug("${result}")
                hideProgress()
                if (result.isSent) {
                    gotoNextAct()
                }
            }
            App.oKRESTman().put(authRequest)
            
//
//            vm.auth()
//                .observe(viewLifecycleOwner) { result ->
//                    val data = result.data
//                    when (result.status) {
//                        Status.SUCCESS -> {
//                            hideProgress()
//                            toast("Вы авторизованы")
////                            AppPreferences.BoTlogin = auth_login.text.toString()
//                            paramS().token = data!!.data.token
//                            paramS().userName = authLoginEditText?.text.toString()
//
//                        }
//                        Status.ERROR -> {
//                            hideProgress()
//
//                        }
//                        Status.NETWORK -> {
//                            hideProgress()
//                            toast("Проблемы с интернетом")
//                        }
//                    }
//                }
        } else {
            authLoginOut?.error = "Проверьте логин"
            authPasswordOut?.error = "Проверьте пароль"
        }
    }


}