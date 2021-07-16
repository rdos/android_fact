package ru.smartro.worknote.base

import androidx.fragment.app.Fragment
import ru.smartro.worknote.util.FragmentLifecycle

open class BaseFragment : Fragment(), FragmentLifecycle {
    override fun onPauseFragment() {

    }

    override fun onResumeFragment() {

    }
}