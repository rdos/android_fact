/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.smartro.worknote.presentation.came

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.App
import ru.smartro.worknote.R
import java.io.File
import java.util.*


val EXTENSION_WHITELIST = arrayOf("JPG")
//class GalleryFragment(p_id: Int) internal constructor()
class GalleryPhotoF : AFragment() {

    private lateinit var mediaList: MutableList<File>
    private val viewModel: PhotoViewModel by viewModel()


    /** Adapter class used to present a fragment containing one photo or video as a page */
    inner class MediaAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int = mediaList.size
        override fun getItem(position: Int): Fragment  {
            val textNumOfCount = "${position+1} из $count"
            return MediaAdapterFragment(mediaList[position], textNumOfCount)
        }
        override fun getItemPosition(obj: Any): Int = POSITION_NONE
    }

    fun getOutputD(): File {
        //todo:!!r_dos
        val basePhotoD = App.getAppliCation().getDPath("photo")
        val dirPath = basePhotoD + File.separator  + getArgumentName()
        val file = File(dirPath)
        if (!file.exists()) file.mkdirs()
        return file
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mark this as a retain fragment, so the lifecycle does not get restarted on config change
        retainInstance = true



        // Walk through all files in the root directory
        // We reverse the order of the list to present the last photos first
        mediaList = getFileList(getOutputD())?.sortedDescending()?.toMutableList() ?: mutableListOf()
    }



    override fun onGetLayout(): Int {
        return R.layout.f_gallery_photo
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val apibDelete =  view.findViewById<AppCompatImageButton>(R.id.apib_f_gallery_photo__delete)

        val viewPager = view.findViewById<ViewPager>(R.id.vp_f_gallery_photo)

        val apibBack = view.findViewById<AppCompatImageButton>(R.id.apib_f_gallery_photo__back)
        apibBack.setOnClickListener {
            navigateBack()
        }

        //Checking media files list
        if (mediaList.isEmpty()) {
            apibDelete.isEnabled = false
//            fragmentGalleryBinding.shareButton.isEnabled = false
        }

        // Populate the ViewPager and implement a cache of two media items
        viewPager.apply {
            offscreenPageLimit = 2
            adapter = MediaAdapter(childFragmentManager)
        }

        // Make sure that the cutout "safe area" avoids the screen notch if any
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Use extension method to pad "inside" view containing UI using display cutout's bounds
//            fragmentGalleryBinding.cutoutSafeArea.padWithDisplayCutout()
        }

        // Handle back button press


        // Handle share button press
//        fragmentGalleryBinding.shareButton.setOnClickListener {
//
//            mediaList.getOrNull(fragmentGalleryBinding.photoViewPager.currentItem)?.let { mediaFile ->
//
//                // Create a sharing intent
//                val intent = Intent().apply {
//                    // Infer media type from file extension
//                    val mediaType = MimeTypeMap.getSingleton()
//                            .getMimeTypeFromExtension(mediaFile.extension)
//                    // Get URI from our FileProvider implementation
//                    val uri = FileProvider.getUriForFile(
//                            view.context, BuildConfig.APPLICATION_ID + ".provider", mediaFile)
//                    // Set the appropriate intent extra, type, action and flags
//                    putExtra(Intent.EXTRA_STREAM, uri)
//                    type = mediaType
//                    action = Intent.ACTION_SEND
//                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//                }
//
//                // Launch the intent letting the user choose which app to share with
//                startActivity(Intent.createChooser(intent, getString(R.string.share_hint)))
//            }
//        }

        // Handle delete button press
        apibDelete.setOnClickListener {

            mediaList.getOrNull(viewPager.currentItem)?.let { imageEntity ->
                AlertDialog.Builder(view.context, android.R.style.Theme_Material_Dialog)
                    .setTitle("Подтвердите")
                    .setMessage(getString(R.string.warning_detele))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes) { _, _ ->
//                        viewModel.removeImageEntity(p_platformId, p_containerId, photoFor , imageEntity.md5)
                        mediaList.remove(imageEntity)
                        imageEntity.delete()
                        createCOSTFile(getOutputD())
                        viewPager.adapter?.notifyDataSetChanged()
                        if (mediaList.size <= 0) {
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

    companion object {
        private const val COST___EXTENSION = "cost."
        private const val FILE_NAME_KEY = "no_restorePhotoFileS"
        private const val NUM_OF_COUNT = "num_of_count"

        private fun createCOSTFile(outD: File) {
            createFile(outD, getCOSTFileName())
        }
        private fun createFile(baseFolder: File, fileName: String) {
            val costFL = File(baseFolder, fileName)
            val outputStream = costFL.outputStream()
            outputStream.use { it ->
                it.write(Int.MAX_VALUE)
            }
        }
        fun getCOSTFileName(): String {
            return COST___EXTENSION + FILE_NAME_KEY
        }

        //r_dos что такое Array<out File>? !!
        fun getFileList(outD: File): Array<File>? {
            // Get root directory of media from
            val rootDirectory = File(outD.absolutePath)
            val result = rootDirectory.listFiles { file ->
                EXTENSION_WHITELIST.contains(file.extension.toUpperCase(Locale.ROOT))
            }

            return result
        }

        fun isCostFileNotExist(outD: File): Boolean {
            val costFL = File(outD, getCOSTFileName())
            return !costFL.exists()
        }
//        fun create(image: ImageEntity, numOfCount: String) = MediaAdapterFragment().apply {
//            arguments = Bundle().apply {
//                putString(FILE_NAME_KEY, image.absolutePath)
//                putString(NUM_OF_COUNT, numOfCount)
//            }
//        }
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
