package ru.smartro.worknote.work.ac

import android.Manifest
import android.app.AlertDialog
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.act_start.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.ActAbstract
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.extensions.hideDialog
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.awORKOLDs.extensions.toast
import ru.smartro.worknote.awORKOLDs.service.network.Resource
import ru.smartro.worknote.awORKOLDs.service.network.Status
import ru.smartro.worknote.awORKOLDs.service.network.body.AuthBody
import ru.smartro.worknote.awORKOLDs.service.network.response.auth.AuthResponse
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.work.ac.checklist.StartOwnerAct


public val PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.READ_PHONE_STATE,
    Manifest.permission.LOCATION_HARDWARE,
    Manifest.permission.ACCESS_NETWORK_STATE
)
class StartAct : ActAbstract() {
    private var mInfoDialog: AlertDialog? = null
    private val vm: AuthViewModel by viewModel()
    // TODO: 27.05.2022 !! !
    private fun gotoNextAct(isHasToken: Boolean = false) {
//            val isHasTask = true
        val isHasTask = vm.baseDat.hasWorkOrderInProgress_know0()
        if (isHasToken && isHasTask) {
            hideDialog()
            hideInfoDialog()
            startActivity(Intent(this, MapAct::class.java))
            finish()
            return
        }

        if (isHasTask) {
            // TODO: 01.11.2021 !! !
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.dialog___act_start, null)
            MyUtil.hideKeyboard(this)
            createInfoDialog(dialogView).let {
                val btnOk = dialogView.findViewById<Button>(R.id.dialog___act_start_point__ok)
                btnOk.setOnClickListener {
                    hideDialog()
                    hideInfoDialog()
                    startActivity(Intent(this, MapAct::class.java))
                    finish()
                }
                val btnCancel = dialogView.findViewById<Button>(R.id.dialog___act_start_point__ie)
                btnCancel.setOnClickListener {
                    hideInfoDialog()
                    showingProgress()
                    //ниже "супер код"
                    //todo: copy-past from SYNCworkER
                    val timeBeforeRequest: Long
                    val lastSynchroTime = App.getAppParaMS().lastSynchroTimeInSec
                    val platforms: List<PlatformEntity>

                    val mMinutesInSec = 30 * 60
                    if (lastSynchroTime - MyUtil.timeStampInSec() > mMinutesInSec) {
                        platforms = vm.baseDat.findPlatforms30min()
                        timeBeforeRequest = lastSynchroTime + mMinutesInSec
                        Log.d(TAG, "SYNCworkER PLATFORMS IN LAST 30 min")
                    } else {
                        platforms =  vm.baseDat.findLastPlatforms()
                        timeBeforeRequest = MyUtil.timeStampInSec()
                        log("SYNCworkER LAST PLATFORMS")
                    }
                    val noSentPlatformCnt = platforms.size


                    val noServedPlatformCnt = vm.baseDat.findPlatformsIsNew().size
                    var dialogString = ""
                    if (noSentPlatformCnt > 0) {
                        dialogString += "Не отправлено ${noSentPlatformCnt} данных, если не взять в работу, данные не будут отправлены на сервер;"
                    }
                    if (noServedPlatformCnt > 0) {
                        dialogString += "\nНе обслужено ${noServedPlatformCnt} площадок."
                    }

                    dialogString += "\nВы уверены, что хотите выйти из задания?"
                    //todo: ))))))))))))))))))))))))))))))))))))))))))))))))))))
                    val dialogView2 = inflater.inflate(R.layout.dialog___act_start, null)
                    dialogView2.findViewById<AppCompatTextView>(R.id.dialog___act_start__dialog_string).text = dialogString
                    createInfoDialog(dialogView2).let {
                        val btnOk2 = dialogView2.findViewById<Button>(R.id.dialog___act_start_point__ie)
                        btnOk2.text = "Да, стереть и выйти"
                        btnOk2.setOnClickListener {
                            hideDialog()
                            hideInfoDialog()
                            App.getAppParaMS().setAppRestartParams()
                            vm.baseDat.clearDataBase()
                            startActivity(Intent(this,  StartOwnerAct::class.java))
                            finish()
                        }
                        val btnCancel2 = dialogView2.findViewById<Button>(R.id.dialog___act_start_point__ok)
                        btnCancel2.text = "Вернуться в задание"
                        btnCancel2.setOnClickListener {
                            hideDialog()
                            hideInfoDialog()
                            startActivity(Intent(this, MapAct::class.java))
                            finish()
                        }
                    }
                    hideDialog()
                }
            }
        } else {
            hideDialog()
            hideInfoDialog()
            startActivity(Intent(this,  StartOwnerAct::class.java))
            finish()
        }

    }

    override fun onNewGPS() {
        // TODO: r_dos!!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppliCation().stopWorkERS()
        if (paramS().isRestartApp) {
            paramS().AppRestarted()
        }
        setContentView(R.layout.act_start)
        actionBar?.title = "Вход в Систему"

        auth_appversion.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        cl_act_start.setOnClickListener {
            MyUtil.hideKeyboard(this)
        }
        viewInit()
    }

    private fun viewInit() {
        auth_enter.setOnClickListener {
           clickAuthEnter()
        }

        actv_activity_auth__it_test_version.isVisible = false
        if (BuildConfig.BUILD_TYPE != "debugProd" && BuildConfig.BUILD_TYPE != "release") {
            val versionName = BuildConfig.VERSION_NAME
            //oopsTestqA
            val textIsTestEnv = getString(R.string.act_st_art_it_test_version).format(versionName)

            actv_activity_auth__it_test_version.isVisible = true
            actv_activity_auth__it_test_version.text = textIsTestEnv // + actv_activity_auth__it_test_version.text

            auth_enter.setOnLongClickListener {
                auth_login.setText("admin@smartro.ru")
                auth_password.setText("xot1ieG5ro~hoa,ng4Sh")
                return@setOnLongClickListener true
            }
        }
//
        if (isDevelMode()) {
            auth_login.setText("admin@smartro.ru")
            auth_password.setText("xot1ieG5ro~hoa,ng4Sh")
            clickAuthEnter()
        }


/*        auth_enter.setOnLongClickListener {
            auth_login.setText("gkh2@smartro.ru")
            auth_password.setText("JT8NcST%sDqUpuc")
            return@setOnLongClickListener true
        }*/
    }

    private fun clickAuthEnter() {
        if (!auth_login.text.isNullOrBlank() && !auth_password.text.isNullOrBlank()) {
            showingProgress()
            vm.auth(AuthBody(auth_login.text.toString(), auth_password.text.toString()))
                .observe(this, Observer { result ->
                    val data = result.data
                    when (result.status) {
                        Status.SUCCESS -> {
                            hideProgress()
                            toast("Вы авторизованы")
//                            AppPreferences.BoTlogin = auth_login.text.toString()
                            if (paramS().token.isNullOrEmpty()) {
                                oopsTestqA()
                            }
                            paramS().token = data!!.data.token
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
                })
        } else {
            auth_password_out.error = "Проверьте пароль"
            login_login_out.error = "Проверьте логин"
        }
    }

    private fun hideInfoDialog() {
        mInfoDialog?.hide()
    }
    private fun createInfoDialog(view: View): View {
//        val dlg = AlertDialog.Builder(this, R.style.Theme_Inventory_Dialog)
        val builder = AlertDialog.Builder(this)

        builder.setView(view)
        mInfoDialog = builder.create()
        try {
//            val window: Window? = mInfoDialog?.window
//            val wlp: WindowManager.LayoutParams = window!!.attributes

//            wlp.gravity = Gravity.TOP
//            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
//            window.attributes = wlp
            mInfoDialog?.show()
        } catch (ex: Exception) {
            Log.e(TAG, "showInfoDialog", ex)
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        val isHasToken = paramS().token.isShowForUser()
        log("isHasToken=${isHasToken}")
        if (isHasToken) {
            gotoNextAct(isHasToken = true)
        }
    }

    open class AuthViewModel(application: Application) : BaseViewModel(application) {

        fun auth(authModel: AuthBody): LiveData<Resource<AuthResponse>> {
            return networkDat.auth(authModel)
        }
    }

}

