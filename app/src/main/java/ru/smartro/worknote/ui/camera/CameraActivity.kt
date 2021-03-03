package ru.smartro.worknote.ui.camera

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import ru.smartro.worknote.R
import ru.smartro.worknote.extensions.FLAGS_FULLSCREEN
import ru.smartro.worknote.service.database.entity.way_task.WayPointEntity
import ru.smartro.worknote.util.PhotoTypeEnum
import java.io.File

const val KEY_EVENT_ACTION = "key_event_action"
const val KEY_EVENT_EXTRA = "key_event_extra"
private const val IMMERSIVE_FLAG_TIMEOUT = 500L

class CameraActivity : AppCompatActivity() {
    private var photoFor = 0
    private lateinit var hostLayout: FrameLayout
    private lateinit var wayPoint: WayPointEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        intent.let {
            wayPoint = Gson().fromJson(it.getStringExtra("wayPoint"), WayPointEntity::class.java)
            photoFor = it.getIntExtra("photoFor", 0)
        }

        hostLayout = findViewById(R.id.fragment_container)
        val cameraFragment = CameraFragment(photoFor, wayPoint)
        supportFragmentManager.beginTransaction().run {
            this.replace(R.id.fragment_container, cameraFragment)
            this.addToBackStack(null)
            this.commit()
        }
        when (photoFor) {
            PhotoTypeEnum.forBeforeMedia -> {
                supportActionBar?.title = getString(R.string.service_before)
            }
            PhotoTypeEnum.forAfterMedia -> {
                supportActionBar?.title = getString(R.string.service_after)
            }
            PhotoTypeEnum.forProblemPoint -> {
                supportActionBar?.title = getString(R.string.service_before)
            }
            PhotoTypeEnum.forProblemContainer -> {
                supportActionBar?.title = getString(R.string.problem_container)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hostLayout.postDelayed({ hostLayout.systemUiVisibility = FLAGS_FULLSCREEN }, IMMERSIVE_FLAG_TIMEOUT)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                val intent = Intent(KEY_EVENT_ACTION).apply {
                    putExtra(KEY_EVENT_EXTRA, keyCode)
                }
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
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
        Log.d("CameraActivity", "onBackPressed: ignored")
    }
}