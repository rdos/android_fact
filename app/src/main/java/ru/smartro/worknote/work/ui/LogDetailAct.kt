package ru.smartro.worknote.work.ui

import android.os.Bundle
import android.view.MenuItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst

class LogDetailAct : ActNOAbst() {
    private val viewModel: JournalViewModel by viewModel()
    private var platformId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal)
        supportActionBar!!.title = "Контейнеры"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initViews()
    }

    private fun initViews() {
        intent.let {
            platformId = it.getIntExtra("platform_id", 0)
        }
//        viewModel.findAllContainerInPlatform(platformId).let {
//            log_rv.adapter = LogDetailAdapter(it as ArrayList<ContainerEntity>)
//        }
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