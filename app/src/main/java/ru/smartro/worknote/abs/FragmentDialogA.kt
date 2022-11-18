package ru.smartro.worknote.abs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ru.smartro.worknote.LOG

abstract class FragmentDialogA : DialogFragment(), FragmentAI {

    init {
        LOG.info( "init AbstractDialog")
    }
    final override fun getAct() = requireActivity() as AAct

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LOG.debug("before")
        val view = AUF.onSetItemLayout(this, inflater, container, savedInstanceState)
        //        try {
//          это провал!!!
//        } try в имени это тру! VT, слышь)))
        return view
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AUF.onViewCreated(this, view, savedInstanceState)
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

    protected fun navigate(navFragmentId: Int) {
        LOG.debug("before")
        AUF.showFragment(this, navFragmentId)
    }

    protected fun navigateNext(navFragmentId: Int, argumentId: Int? = null, argumentName: String?= null) {
        AUF.showNextFragment(this, navFragmentId, argumentId, argumentName)
    }

    protected fun navigateBack() {
        LOG.debug("before")
        AUF.showFragment(this)
    }
    protected fun navigateBack(navFragmentId: Int) {
        LOG.debug("before")
        AUF.showFragment(this, navFragmentId)
    }

    protected fun navigateClose() {
        LOG.debug("before")
        getAct().finish()
    }

    final override fun getArgSBundle(argumentId: Int, argumentName: String?): Bundle {
        val bundle = AUF.setFragmentVar(argumentId, argumentName)
        return bundle
    }

}