package ru.smartro.worknote.work.ui

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.awORKOLDs.extensions.FLAGS_FULLSCREEN
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
        supportActionBar?.isHideOnContentScrollEnabled = false
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        val cameraFragment = CameraFragment(photoFor, platformId, containerId)
        supportFragmentManager.beginTransaction().run {
            this.replace(R.id.fragment_container, cameraFragment)
            this.addToBackStack(null)
            this.commit()
        }
    }

    override fun onResume() {
        super.onResume()
        hostLayout.postDelayed({ hostLayout.systemUiVisibility = FLAGS_FULLSCREEN }, IMMERSIVE_FLAG_TIMEOUT)
    }

    companion object {
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
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