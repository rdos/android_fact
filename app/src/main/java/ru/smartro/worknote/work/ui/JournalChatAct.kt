package ru.smartro.worknote.work.ui

import android.app.Application
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.act_messager__rv_item.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.base.GenericRecyclerAdapter
import ru.smartro.worknote.awORKOLDs.base.ViewHolder
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.awORKOLDs.util.MyUtil.toStr
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.work.PlatformEntity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class JournalChatAct : ActNOAbst() {
    private val viewModel: JournalViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_journalchat)
        supportActionBar!!.title = "Журнал"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val rvJournalAct = findViewById<RecyclerView>(R.id.rv_act_journal)

        viewModel.findPlatformsIsServed().let {
            rvJournalAct.adapter = JournalAdapter(it as ArrayList<PlatformEntity>)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class JournalAdapter(items: java.util.ArrayList<PlatformEntity>
    ) : GenericRecyclerAdapter<PlatformEntity>(items) {

        override fun bind(item: PlatformEntity, holder: ViewHolder) {
            val date = Date(item.updateAt * 1000L)
            val dateFormat = SimpleDateFormat("HH:mm")
            val resultDate: String = dateFormat.format(date)
            val imgBefore = holder.itemView.findViewById<ImageView>(R.id.img_act_messager__media_before)
            if (item.beforeMedia.size > 0) {
                Glide.with(imgBefore)
                    .load(MyUtil.base64ToImage(item.beforeMedia[0]?.image))
                    .into(imgBefore)
                holder.itemView.tv_act_messager__media_before.text = "Фото(1 из ${item.beforeMedia.size}) до обслуживания"
            } else {
                holder.itemView.tv_act_messager__media_before.text = "beforeMedia пусто"
            }
            if (item.afterMedia.size > 0) {
                val imgAfter = holder.itemView.findViewById<ImageView>(R.id.img_act_messager__media_after)
                Glide.with(imgAfter)
                    .load(MyUtil.base64ToImage(item.afterMedia[0]?.image))
                    .into(imgAfter)
                holder.itemView.tv_act_messager__media_after.text = "Фото(1 из ${item.afterMedia.size}) после обслуживания"
            } else {
                holder.itemView.tv_act_messager__media_before.text = "afterMedia пусто"
            }

//            val llBehavior = holder.itemView.findViewById<ConstraintLayout>(R.id.ll_behavior)
//            val bottomSheetBehavior = BottomSheetBehavior.from(llBehavior)
////            bottomSheetBehavior.expandedOffset = 100

            holder.itemView.log_item_time.text = resultDate

            holder.itemView.log_item_title.text = item.address

            val failureComment = if (item.failureComment.isNullOrEmpty()) {
                "Пусто"
            } else {
                item.failureComment
            }

            holder.itemView.log_item_content.text =
                "кол-во контейнеров: ${item.containers.size} \n" +
                        "Обслужено: ${item.containers.filter { it.status != StatusEnum.NEW }.size}/${item.containers.size} \n" +
                        "Проблема: $failureComment \n"
            "Объем Подбора: ${item.volumePickup.toStr()}\n" +
                    "Статус сети: ${status(item.networkStatus!!)}"

            if (item.networkStatus!!)
                holder.itemView.log_item_status.setImageResource(R.drawable.ic_done)
            else
                holder.itemView.log_item_status.setImageResource(R.drawable.ic_clock)
        }

        private fun status(b: Boolean) =
            if (b)
                "Отправлено"
            else
                "Еще не отправлен"

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return super.onCreateViewHolder(parent, R.layout.act_messager__rv_item)
        }
    }
//
//    interface JournalClickListener {
//        fun logDetailClicked(item: PlatformEntity)
//    }

    class JournalViewModel(application: Application) : BaseViewModel(application) {

        fun findPlatformsIsServed(): List<PlatformEntity> {
            return baseDat.findPlatformsIsServed()
        }
    }
}