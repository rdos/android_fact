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

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LOG.debug("before")
        val view = AUFragment.onSetItemLayout(this, inflater, container, savedInstanceState)
        //        try {
//          это провал!!!
//        } try в имени это тру! VT, слышь)))
        return view
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

    protected fun navigateBack() {
        LOG.debug("before")
        AUFragment.showFragment(this)
    }

    protected fun navigate(navFragmentId: Int) {
        LOG.debug("before")
        AUFragment.showFragment(this, navFragmentId)
    }


    final fun navigateNext(navFragmentId: Int, argumentId: Int? = null, argumentName: String?= null) {
        AUFragment.showNextFragment(this, navFragmentId, argumentId, argumentName)
    }

    final override fun getArgSBundle(argumentId: Int, argumentName: String?): Bundle {
        val bundle = AUFragment.setFragmentVar(argumentId, argumentName)
        return bundle
    }

}