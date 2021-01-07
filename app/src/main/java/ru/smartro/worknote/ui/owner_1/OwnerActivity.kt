package ru.smartro.worknote.ui.owner_1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_choose.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.OwnerAdapter
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.Status
import ru.smartro.worknote.service.response.owner.Organisation
import ru.smartro.worknote.ui.type_2.TypeAppActivity

class OwnerActivity : AppCompatActivity() {
    private val viewModel: OwnerViewModel by viewModel()
    private lateinit var adapter: OwnerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)
        supportActionBar?.title = "Выберите организацию"

        viewModel.getOwners().observe(this, Observer { result ->
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    adapter = OwnerAdapter(data!!.data.organisations as ArrayList<Organisation>)
                    choose_rv.adapter = adapter
                }
                Status.ERROR -> {
                    toast("Ошибка")
                }
                Status.NETWORK -> {
                    toast("Проблемы с интернетом")
                }
            }
        })

        next_btn.setOnClickListener {
            AppPreferences.ownerId = adapter.getSelectedId()
            Log.d("OwnerActivity", "getSelectedId: ${adapter.getSelectedId()}")
            startActivity(Intent(this, TypeAppActivity::class.java))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       menuInflater.inflate(R.menu.menu_logout_owner, menu)
        return true
    }

}
