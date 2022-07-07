package ru.smartro.worknote.work.cam

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import ru.smartro.worknote.App
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.awORKOLDs.util.PhotoTypeEnum
import java.io.File

private const val IMMERSIVE_FLAG_TIMEOUT = 500L

class CameraAct : ActNOAbst() {
    private var photoFor = 0
    private lateinit var hostLayout: FrameLayout
    private var platformId = 0
    private var containerId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        supportActionBar?.hide()
        intent.let {
            photoFor = it.getIntExtra("photoFor", 0)
        }

        hostLayout = findViewById(R.id.fragment_container)
        when (photoFor) {
            PhotoTypeEnum.forBeforeMedia -> {
                platformId = intent.getIntExtra("platform_id", 0)
                supportActionBar?.title = getString(R.string.service_before)
            }
            PhotoTypeEnum.forAfterMedia -> {
                platformId = intent.getIntExtra("platform_id", 0)
                supportActionBar?.title = getString(R.string.service_after)
            }
            PhotoTypeEnum.forPlatformProblem -> {
                platformId = intent.getIntExtra("platform_id", 0)
                supportActionBar?.title = getString(R.string.problem_on_point)
            }
            PhotoTypeEnum.forContainerFailure -> {
                containerId = intent.getIntExtra("container_id", 0)
                platformId = intent.getIntExtra("platform_id", 0)
                supportActionBar?.title = getString(R.string.problem_container)
            }
            PhotoTypeEnum.forContainerBreakdown -> {
                containerId = intent.getIntExtra("container_id", 0)
                platformId = intent.getIntExtra("platform_id", 0)
                supportActionBar?.title = "Поломка контейнера"
            }
            PhotoTypeEnum.forServedKGO -> {
                platformId = intent.getIntExtra("platform_id", 0)
//                supportActionBar?.title = getString(R.string.kgo)
                // TODO: 14.01.2022 r_dos!!!
                supportActionBar?.hide()
            }
            PhotoTypeEnum.forRemainingKGO -> {
                // TODO: 14.01.2022 r_dos!!!
                platformId = intent.getIntExtra("platform_id", 0)
//                supportActionBar?.title = getString(R.string.kgo)
                supportActionBar?.hide()
            }
            PhotoTypeEnum.forPlatformPickupVolume -> {
                platformId = intent.getIntExtra("platform_id", 0)
                supportActionBar?.title = getString(R.string.service_pickup_volume)
            }
        }
        val APhotoFragment = APhotoFragment(photoFor, platformId, containerId)
        supportFragmentManager.beginTransaction().run {
            this.replace(R.id.fragment_container, APhotoFragment)
            this.addToBackStack(null)
            this.commit()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    companion object {
        fun getOutputFL(): File {
            val dirPath = App.getAppliCation().filesDir.absolutePath
            val file = File(dirPath)
            if (!file.exists()) file.mkdirs()
            return file
        }
    }

    override fun onBackPressed() {
        setResult(404)
        finish()
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