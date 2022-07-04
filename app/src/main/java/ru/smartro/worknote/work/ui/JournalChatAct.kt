package ru.smartro.worknote.work.ui

import android.app.Application
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.act_messager__rv_item.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.Snull
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.andPOintD.BaseAdapter
import ru.smartro.worknote.andPOintD.ViewHolder
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.work.PlatformEntity
import java.text.SimpleDateFormat
import java.util.*

class JournalChatAct : ActNOAbst() , SearchView.OnQueryTextListener{
    private var mAdapter: JournalChatAdapter? = null
    private val viewModel: JournalViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_journalchat)
        supportActionBar!!.hide()
        val platformSIsServed = viewModel.findPlatformsIsServed()

        val acibGotoBack = findViewById<AppCompatImageButton>(R.id.acib__act_journalchat__gotoback)
        acibGotoBack.setOnClickListener{
            onBackPressed()
        }
        val svFilter = findViewById<SearchView>(R.id.sv__act_journalchat__filter)

        mAdapter = JournalChatAdapter(platformSIsServed)
        svFilter.setOnQueryTextListener(this)
        val rvJournalAct = findViewById<RecyclerView>(R.id.rv_act_journal)
        rvJournalAct.adapter = mAdapter
    }

    inner class JournalChatAdapter(items: List<PlatformEntity>) : BaseAdapter<PlatformEntity>(items) {

        fun filter(platformList: List<PlatformEntity>, filterText: String): List<PlatformEntity> {
            val query = filterText.lowercase()
            val filteredModeList = platformList.filter {
                try {
//                    it.javaClass.getField("address")
                    val text = it.address?.lowercase()
                    var res = true
                    text?.let {
                        res = (text.startsWith(query) || (text.contains(query)))
                    }
                    res
                } catch (ex: Exception) {
                    true
                }
            }
            //            val sYsTEM = mutableListOf<Vehicle>()
            return filteredModeList
        }

        fun filteredList(queryText: String) {
            logSentry(queryText)
            // TODO: !R_dos
            if(queryText == Snull) {
                super.reset()
                return
            }
            val mItemsAfter = filter(super.getItems(), queryText)
            super.set(mItemsAfter)
        }

        override fun bind(item: PlatformEntity, holder: ViewHolder) {
            val date = Date(item.updateAt * 1000L)
            val dateFormat = SimpleDateFormat("HH:mm")
            val resultDate: String = dateFormat.format(date)
            val imgBefore = holder.itemView.findViewById<ImageView>(R.id.img_act_messager__media_before)
            if (item.beforeMedia.size > 0) {
                Glide.with(imgBefore)
                    .load(MyUtil.base64ToImage(item.beforeMedia[0]?.image))
                    .into(imgBefore)
                holder.itemView.tv_act_messager__media_before.text = "Фото до:"
            } else {
                imgBefore.visibility = View.INVISIBLE
                holder.itemView.tv_act_messager__media_before.visibility = View.INVISIBLE
            }
            if (item.afterMedia.size > 0) {
                val imgAfter = holder.itemView.findViewById<ImageView>(R.id.img_act_messager__media_after)
                Glide.with(imgAfter)
                    .load(MyUtil.base64ToImage(item.afterMedia[0]?.image))
                    .into(imgAfter)
                holder.itemView.tv_act_messager__media_after.text = "Фото после:"
            } else {
                holder.itemView.findViewById<ImageView>(R.id.img_act_messager__media_after).visibility = View.INVISIBLE
                holder.itemView.tv_act_messager__media_after.visibility = View.INVISIBLE
            }

            if (item.failureMedia.size > 0) {
                val textBefore = holder.itemView.tv_act_messager__media_before
                Glide.with(imgBefore)
                    .load(MyUtil.base64ToImage(item.failureMedia[0]?.image))
                    .into(imgBefore)
                textBefore.text = "Фото невывоза:"
                textBefore.visibility = View.VISIBLE
                imgBefore.visibility = View.VISIBLE
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


            // search for:
            // failureComment

            val serveStatus = "Обслужено: ${item.containers.filter { it.status != StatusEnum.NEW }.size}/${item.containers.size}\n"
            val pickupVolume = if(item.volumePickup != null) "Объем Подбора: ${item.volumePickup}\n" else ""
            val networkStatus = getNetworkStatusText(item)

            holder.itemView.log_item_content.text = serveStatus + pickupVolume + networkStatus

            if (item.networkStatus!!)
                holder.itemView.log_item_status.setImageResource(R.drawable.ic_done)
            else
                holder.itemView.log_item_status.setImageResource(R.drawable.ic_clock)
        }

        private fun getNetworkStatusText(platform: PlatformEntity): String {
            var result = "Статус сети: "
            result += if (platform.networkStatus!!) {
                "Отправлено"
            } else {
                "Еще не отправлен"
            }
            return result
        }
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

    /** ********************************************************************************************
     * ЗДЕСЬ интерфейсы interface*/
    override fun onQueryTextSubmit(query: String?): Boolean {
        val result = false
        log("svFilter:::onQueryTextSubmit. result=${result} query=${query}")
        return result
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val res = true
        if (newText.isNullOrEmpty()) {
            mAdapter!!.filteredList(Snull)
            log("svFilter:::onQueryTextChange.result=${res} newText=${newText}")
            return res
        }
        mAdapter?.let {
            mAdapter!!.filteredList(newText.toString())
        }
        log("svFilter:::onQueryTextChange.result=${res} newText=${newText}")
        return res
    }
}