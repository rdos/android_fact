package ru.smartro.worknote.ui.log

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_log.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.LogAdapter
import ru.smartro.worknote.adapter.LogClickListener
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import java.util.*

class LogActivity : AppCompatActivity(), LogClickListener {
    private val viewModel: LogViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        supportActionBar!!.title = "Журнал"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initViews()
    }

    private fun initViews() {
        viewModel.findAllPlatforms().let {
            log_rv.adapter = LogAdapter(it as ArrayList<PlatformEntity>)
        }
    }

    override fun logDetailClicked(item: PlatformEntity) {
        startActivity(Intent(this, LogDetailActivity::class.java)
            .putExtra("platform_id", item.platformId))
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}