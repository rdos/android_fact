package ru.smartro.worknote.presentation

import android.app.Application
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.smartro.worknote.R
import ru.smartro.worknote.Snull
import ru.smartro.worknote.andPOintD.ANOFragment
import ru.smartro.worknote.andPOintD.AViewModel
import ru.smartro.worknote.andPOintD.BaseAdapter
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.work.PlatformEntity
import java.text.SimpleDateFormat
import java.util.*

class JournalChatF : ANOFragment() {

    private var mAdapter: JournalChatAdapter? = null
    private val viewModel: JournalChatViewModel by viewModels()

    override fun onGetLayout(): Int {
        return R.layout.act_journalchat
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getAct().supportActionBar?.hide()

        val platformSIsServed = viewModel.findPlatformsIsServed()
        val acibGotoBack = view.findViewById<AppCompatImageButton>(R.id.acib__act_journalchat__gotoback)
        acibGotoBack.setOnClickListener{
            findNavController().popBackStack()
        }
        val svFilterAddress = view.findViewById<AppCompatEditText>(R.id.sv__act_journalchat__filteraddress).apply {
            removeTextChangedListener(null)
            addTextChangedListener {
                mAdapter?.filteredList(it.toString())
            }
            clearFocus()
        }

        mAdapter = JournalChatAdapter(platformSIsServed)
//        svFilterAddress.setOnQueryTextListener(this)
        val rvJournalAct = view.findViewById<RecyclerView>(R.id.rv_act_journal)
        rvJournalAct.adapter = mAdapter
    }

    inner class JournalChatAdapter(items: List<PlatformEntity>) : BaseAdapter<PlatformEntity, JournalChatAdapter.JournalChatHolder>(items) {
        override fun onGetLayout(): Int {
            return R.layout.act_messager__rv_item
        }

        override fun onGetViewHolder(view: View): JournalChatHolder {
            return JournalChatHolder(view)
        }

        override fun bind(item: PlatformEntity, holder: JournalChatHolder) {
            val date = Date(item.updateAt * 1000L)
            val dateFormat = SimpleDateFormat("HH:mm")
            val resultDate: String = dateFormat.format(date)
            val imgBefore = holder.itemView.findViewById<ImageView>(R.id.img_act_messager__media_before)
            if (item.getBeforeMediaSize() > 0) {
                Glide.with(imgBefore)
                    .load(MyUtil.base64ToImage(item.beforeMedia[0]?.image))
                    .into(imgBefore)
                holder.tv_act_messager__media_before.text = "Фото до:"
            } else {
                imgBefore.visibility = View.INVISIBLE
                holder.tv_act_messager__media_before.visibility = View.INVISIBLE
            }
            if (item.getAfterMediaSize() > 0) {
                val imgAfter = holder.itemView.findViewById<ImageView>(R.id.img_act_messager__media_after)
                Glide.with(imgAfter)
                    .load(MyUtil.base64ToImage(item.afterMedia[0]?.image))
                    .into(imgAfter)
                holder.tv_act_messager__media_after.text = "Фото после:"
            } else {
                holder.img_act_messager__media_after.visibility = View.INVISIBLE
                holder.tv_act_messager__media_after.visibility = View.INVISIBLE
            }

            if (item.getFailureMediaSize() > 0) {
                val textBefore = holder.tv_act_messager__media_before
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

            holder.log_item_time.text = resultDate

            holder.log_item_title.text = item.address

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

            holder.log_item_content.text = serveStatus + pickupVolume + networkStatus

            if (item.networkStatus!!)
                holder.log_item_status.setImageResource(R.drawable.ic_done)
            else
                holder.log_item_status.setImageResource(R.drawable.ic_clock)
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
            // TODO: !r_dos
            if(queryText == Snull) {
                super.reset()
                return
            }
            setQueryText(queryText)
            val mItemsAfter = filter(super.getItemsForFilter(), queryText)
            super.set(mItemsAfter)
        }
        inner class JournalChatHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tv_act_messager__media_before: AppCompatTextView by lazy {
                view.findViewById(R.id.tv_act_messager__media_before)
            }

            val tv_act_messager__media_after: AppCompatTextView by lazy {
                view.findViewById(R.id.tv_act_messager__media_before)
            }
            val img_act_messager__media_after: AppCompatImageView by lazy {
                view.findViewById(R.id.img_act_messager__media_after)
            }

            val log_item_time: AppCompatTextView by lazy {
                view.findViewById(R.id.log_item_time)
            }
            val log_item_title: AppCompatTextView by lazy {
                view.findViewById(R.id.log_item_title)
            }
            val log_item_content: AppCompatTextView by lazy {
                view.findViewById(R.id.log_item_content)
            }
            val log_item_status: AppCompatImageView by lazy {
                view.findViewById(R.id.log_item_status)
            }
//        TODO("Not yet implemented")
        }
    }

//
//    interface JournalClickListener {
//        fun logDetailClicked(item: PlatformEntity)
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
    }

    class JournalChatViewModel(app: Application) : AViewModel(app) {

        fun findPlatformsIsServed(): List<PlatformEntity> {
            return database.findPlatformsIsServed()
        }
    }
}