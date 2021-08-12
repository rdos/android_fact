package ru.smartro.worknote.ui.platform_service

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_service.*
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.ViewPagerAdapter
import ru.smartro.worknote.ui.camera.SlideCameraFragment
import ru.smartro.worknote.util.PhotoTypeEnum


class ServiceActivity : AppCompatActivity()  {
    private var platformId = 0
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)
        intent.let {
            platformId = it.getIntExtra("platform_id", 0)
        }
        initViewPager()
    }

    fun nextPage() {
        service_vp.currentItem = currentPosition + 1
    }

    private fun initViewPager() {
        val beforePhotoCamera = SlideCameraFragment(photoFor = PhotoTypeEnum.forBeforeMedia, platformId = platformId, containerId = 0)
        val platformService = PlatformServiceFragment(platformId = platformId)
        val afterPhotoCamera = SlideCameraFragment(photoFor = PhotoTypeEnum.forAfterMedia, platformId = platformId, containerId = 0)
        val fragments = arrayListOf(beforePhotoCamera, platformService, afterPhotoCamera)
        val adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        service_vp.offscreenPageLimit = 1
        service_vp.adapter = adapter

        service_vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(newPosition: Int) {
                adapter.getItem(newPosition).onResumeFragment()
                adapter.getItem(currentPosition).onPauseFragment()
                currentPosition = newPosition

                when (newPosition) {
                    2 -> {
                        service_next.isVisible = false
                        service_back.isVisible = true
                    }
                    1 -> {
                        service_next.isVisible = true
                        service_back.isVisible = true
                    }
                    0 -> {
                        service_next.isVisible = true
                        service_back.isVisible = false
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })

        service_back.setOnClickListener {
            service_vp.currentItem = currentPosition - 1
        }

        service_next.setOnClickListener {
            service_vp.currentItem = currentPosition + 1
        }
    }


    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)

    }

}