package ru.smartro.worknote.work.ac.checklist

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.start_act__rv_item_know1.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.awORKOLDs.extensions.toast
import ru.smartro.worknote.awORKOLDs.service.network.Resource
import ru.smartro.worknote.awORKOLDs.service.network.Status
import ru.smartro.worknote.awORKOLDs.service.network.response.organisation.Organisation
import ru.smartro.worknote.awORKOLDs.service.network.response.organisation.OrganisationResponse
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.work.ac.PERMISSIONS

//todo:r_dos choose in checklist(как у QA)
class StartOwnerAct : ActNOAbst() {
    private val vs: OrganisationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MyUtil.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }
        setContentView(R.layout.act_start_owner)
        supportActionBar?.title = "Организация"
        val rv = findViewById<RecyclerView>(R.id.rv_act_start_owner)
        rv.layoutManager = LinearLayoutManager(this)
        showingProgress()
        vs.getOwners().observe(this, Observer { result ->
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    val owners = data!!.data.organisations
                    rv.adapter = OwnerAdapter(owners)
                    if (owners.size == 1) {
                        gotoNextAct(owners[0])
                    }
                    hideProgress()
                }
                Status.ERROR -> {
                    toast(result.msg)
                    hideProgress()
                }
                Status.NETWORK -> {
                    toast("Проблемы с интернетом")
                    hideProgress()
                }
            }
        })

    }

    private fun gotoNextAct(owner: Organisation) {
        paramS().organisationId = owner.id
        val intent = Intent(this@StartOwnerAct, StartVehicleAct::class.java)
        intent.putExtra(PUT_EXTRA_PARAM_NAME, owner.name)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MyUtil.onMenuOptionClicked(this, item.itemId)
        return super.onOptionsItemSelected(item)
    }

    open class OrganisationViewModel(application: Application) : BaseViewModel(application) {
        fun getOwners(): LiveData<Resource<OrganisationResponse>> {
            return networkDat.getOwners()
        }
    }

    inner class OwnerAdapter(private val items: List<Organisation>) :
        RecyclerView.Adapter<OwnerAdapter.OwnerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.start_act__rv_item_know1, parent, false)
//            logSentry("BB")
            return OwnerViewHolder(view)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
            val owner = items[position]
            holder.itemView.choose_title.text = owner.name
            holder.itemView.setOnClickListener {
                setAntiErrorClick(holder.itemView)
                gotoNextAct(owner)

            }
        }

         inner class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }


}
