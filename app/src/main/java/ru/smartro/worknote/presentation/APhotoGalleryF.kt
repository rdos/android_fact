package ru.smartro.worknote.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AF
import ru.smartro.worknote.log.todo.ImageInfoEntity
import java.io.File

//class GalleryFragment(p_id: Int) internal constructor()
class APhotoGalleryF : AF() {

    private lateinit var fileList: MutableList<File>

    private val sharedViewModel: VMPserve by activityViewModels()
    private val viewModel: VMGallery by viewModels()

    companion object {
        const val NAV_ID = R.id.GalleryPhotoF
    }

    override fun onGetLayout(): Int {
        return R.layout.f_gallery_photo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val directory = getArgumentName()
        LOG.debug("DIRECTORY ::: ${directory}")
        fileList = AppliCation().getDFileList(directory!!).sortedDescending().toMutableList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apibDelete =  view.findViewById<AppCompatImageButton>(R.id.apib_f_gallery_photo__delete)
        val viewPager = view.findViewById<ViewPager>(R.id.vp_f_gallery_photo)
        val apibBack = view.findViewById<AppCompatImageButton>(R.id.apib_f_gallery_photo__back)

        apibBack.setOnClickListener {
            navigateBack()
        }

        var mAdapter: MediaAdapter
        viewPager.offscreenPageLimit = 2

        val directory = getArgumentName()
        viewModel.calculateFileList(directory!!, sharedViewModel.getPlatformEntity())

        if (fileList.isEmpty()) {
            apibDelete.isEnabled = false
//            fragmentGalleryBinding.shareButton.isEnabled = false
        }

        viewModel.imageInfoList.observe(viewLifecycleOwner) { imageS ->
            mAdapter = MediaAdapter(childFragmentManager)
            viewPager.adapter = mAdapter
        }

        apibDelete.setOnClickListener {

            fileList.getOrNull(viewPager.currentItem)?.let { imageEntity ->
                AlertDialog.Builder(view.context, android.R.style.Theme_Material_Dialog)
                    .setTitle("Подтвердите")
                    .setMessage(getString(R.string.warning_detele))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes) { _, _ ->
                        fileList.remove(imageEntity)
//                        viewModel.removeImageInfoEntityByHash(imageEntity.nameWithoutExtension)
                        imageEntity.delete()
                        viewPager.adapter?.notifyDataSetChanged()
                        if (fileList.size <= 0) {
                            navigateBack()
                        }
                    }
                    .setNegativeButton(android.R.string.no, null)
                    .create().show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateBack()
    }


    /** Adapter class used to present a fragment containing one photo or video as a page */
    inner class MediaAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int = fileList.size
        override fun getItem(position: Int): Fragment {
            val textNumOfCount = "${position+1} из $count"
            return MediaAdapterFragment(fileList[position], textNumOfCount)
        }
        override fun getItemPosition(obj: Any): Int = POSITION_NONE
    }

    class MediaAdapterFragment(val image: File, val numOfCount: String) : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View {
            val view = inflater.inflate(R.layout.media_fragment, container, false)
            return view
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
//        val args = arguments ?: return
            val aptvNumOfCount = view.findViewById<AppCompatTextView>(R.id.aptv_media_fragment__num_of_count)
            aptvNumOfCount.text = numOfCount
            val imageView = view.findViewById<AppCompatImageView>(R.id.apiv_media_fragment)
//        val resource = args.getString(FILE_NAME_KEY)?.let { File(it) } ?: R.drawable.ic_photo
//        val bmp = image?.let { BitmapFactory.decodeByteArray(image, 0, it.size) }
//        val resource = args.getString(FILE_NAME_KEY)?.let { File(it) } ?: R.drawable.ic_photo
            Glide.with(view).load(image).into(imageView)
        }


    }
}
