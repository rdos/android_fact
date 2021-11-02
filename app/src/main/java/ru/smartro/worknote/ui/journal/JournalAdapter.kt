package ru.smartro.worknote.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_log.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.base.GenericRecyclerAdapter
import ru.smartro.worknote.base.ViewHolder
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import ru.smartro.worknote.util.MyUtil.toStr
import ru.smartro.worknote.util.StatusEnum
import java.text.SimpleDateFormat
import java.util.*


class JournalAdapter(items: ArrayList<PlatformEntity>
) : GenericRecyclerAdapter<PlatformEntity>(items) {

    override fun bind(item: PlatformEntity, holder: ViewHolder) {
        val date = Date(item.updateAt * 1000L)
        val dateFormat = SimpleDateFormat("HH:mm")
        val resultDate: String = dateFormat.format(date)

        holder.itemView.log_item_time.text = resultDate

        holder.itemView.log_item_title.text = item.address

        val failureComment = if (item.failureComment.isNullOrEmpty()) {
            "Пусто"
        } else {
            item.failureComment
        }

        holder.itemView.log_item_content.text =
            "Общее кол-во контейнеров: ${item.containers.size} \n" +
                    "Обслужено кол-во контейнеров: ${item.containers.filter { it.status != StatusEnum.NEW }.size}/${item.containers.size} \n" +
                    "Проблема: $failureComment \n" +
                    "Объем КГО: ${item.volumeKGO.toStr()}\n" +
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
        return super.onCreateViewHolder(parent, R.layout.item_log)
    }

}

interface JournalClickListener {
    fun logDetailClicked(item: PlatformEntity)
}