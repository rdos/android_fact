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
        platformId = intent.getIntExtra("platform_id", 0)
//        hostLayout = findViewById(R.id.fragment_container)

//        val beforeMediaPhotoF = BeforeMediaPhotoF()
//        beforeMediaPhotoF.addArgument(platformId)
//        supportFragmentManager.beginTransaction().run {
//            this.replace(R.id.fragment_container, beforeMediaPhotoF)
//            this.addToBackStack(null)
//            this.commitAllowingStateLoss()
//        }
    }

    override fun onResume() {
        super.onResume()
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