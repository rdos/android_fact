package ru.smartro.worknote.presentation.checklist

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress

class XChecklistAct: ActNOAbst() {

    var acibGoToBack: AppCompatImageButton? = null
    private var pbLoading: ProgressBar? = null
    private var actvLoadingLabel: TextView? = null
    private var actvBarTitle: AppCompatTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_checklist)
        hideProgress()

        supportActionBar?.hide()

        findViewById<SearchView>(R.id.sv__act_checklist__filteraddress).visibility = View.GONE

        pbLoading = findViewById(R.id.pb__act_checklist__loading)
        actvLoadingLabel = findViewById(R.id.actv__act_checklist__loading_label)
        actvBarTitle = findViewById(R.id.actv__act_checklist__bar_title)
        acibGoToBack = findViewById(R.id.acib__act_checklist__gotoback)
    }

    fun setBarTitle(title: String) {
        actvBarTitle?.text = title
    }

    fun showProgressBar() {
        pbLoading?.visibility = View.VISIBLE
    }

    fun showProgressBar(labelText: String) {
        pbLoading?.visibility = View.VISIBLE
        actvLoadingLabel?.visibility = View.VISIBLE
        actvLoadingLabel?.text = "Загружается ${labelText}"
    }

    fun hideProgressBar() {
        pbLoading?.visibility = View.GONE
        actvLoadingLabel?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        hideProgress()
    }

    override fun onPause() {
        super.onPause()
        hideProgress()
    }
}