package ru.smartro.worknote.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_log.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.base.GenericRecyclerAdapter
import ru.smartro.worknote.base.ViewHolder
import ru.smartro.worknote.service.database.entity.work_order.ContainerEntity
import ru.smartro.worknote.util.StatusEnum
import java.util.*


class LogDetailAdapter(
    items: ArrayList<ContainerEntity>
) : GenericRecyclerAdapter<ContainerEntity>(items) {

    override fun bind(item: ContainerEntity, holder: ViewHolder) {
        holder.itemView.log_item_title.text = item.number

        val statusString = when (item.status) {
            StatusEnum.NEW -> "Не обслужен"
            StatusEnum.SUCCESS -> "Обслужен"
            else -> "Проблема"
        }
//        val problemComment = if (item.failureComment.isNullOrEmpty()) {
//            "Пусто"
//        } else {
//            item.failureComment
//        }
        val comment = if (item.comment.isNullOrEmpty()) {
            "Пусто"
        } else {
            item.comment
        }
//        holder.itemView.log_item_content.text =
//            "Номер: ${item.number} \n" +
//                    "Статус: $statusString \n" +
//                    "Объем заполненности ${item.volume} \n" +
//                    "Комментарии: $comment \n" +
//                    "Проблема: $problemComment \n"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_log_detail)
    }

}
