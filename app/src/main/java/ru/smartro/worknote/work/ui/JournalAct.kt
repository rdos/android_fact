package ru.smartro.worknote.work.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.work.abs.ActNOAbst
import ru.smartro.worknote.work.PlatformEntity

class JournalAct : ActNOAbst(), JournalClickListener {
    private val viewModel: JournalViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal)
        supportActionBar!!.title = "Журнал"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val rvJournalAct = findViewById<RecyclerView>(R.id.rv_act_journal)

        viewModel.findPlatformsIsServed().let {
            rvJournalAct.adapter = JournalAdapter(it as ArrayList<PlatformEntity>)
        }
    }

    override fun logDetailClicked(item: PlatformEntity) {
        startActivity(Intent(this, LogDetailAct::class.java)
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