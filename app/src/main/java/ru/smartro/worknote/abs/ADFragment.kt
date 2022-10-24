package ru.smartro.worknote.abs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ru.smartro.worknote.LOG

abstract class ADFragment : DialogFragment(), IAFragment {

    init {
        LOG.info( "init AbstractDialog")
    }
    final override fun getAct() = requireActivity() as AAct

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LOG.debug("before")
        val view = AUFragment.onSetItemLayout(this, inflater, container, savedInstanceState)
        //        try {
//          это провал!!!
//        } try в имени это тру! VT, слышь)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AUFragment.onViewCreated(this, view, savedInstanceState)
        LOG.debug("onViewCreated")
    }

    final override fun onDestroy() {
        super.onDestroy()
        LOG.debug("before")
    }

    final override fun onDetach() {
        super.onDetach()
        LOG.debug("before")
    }

    fun navigateBack(navFragmentId: Int) {
        LOG.debug("before")
        //todo:???        dismiss() vs
        AUFragment.showLastFragment(this, navFragmentId)
    }

    fun navigateBack() {
        LOG.debug("before")
        //todo:???        dismiss() vs
        AUFragment.showLastFragment(this)
    }
}