package ru.smartro.worknote.ui.workFlow.waybillHead

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import ru.smartro.worknote.MainActivity
import ru.smartro.worknote.R
import ru.smartro.worknote.databinding.FragmentWaybillListBinding

import ru.smartro.worknote.ui.workFlow.waybillHead.dummy.DummyContent
import ru.smartro.worknote.ui.workFlow.waybillHead.dummy.DummyContent.DummyItem
import timber.log.Timber

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [WaybillFragment.OnListFragmentInteractionListener] interface.
 */
class WaybillFragment : Fragment() {


    private var listener: OnListFragmentInteractionListener? = null

    private lateinit var waybillHeadViewModel: WaybillHeadViewModel

    private var waybillAdapter: WaybillRecyclerViewAdapter? = null

    private lateinit var binding: FragmentWaybillListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        waybillHeadViewModel = ViewModelProvider(
            this,
            WaybillHeadViewModelFactory(requireActivity())
        )
            .get(WaybillHeadViewModel::class.java)

        binding = FragmentWaybillListBinding.inflate(inflater)

        waybillAdapter = WaybillRecyclerViewAdapter(DummyContent.ITEMS, listener)

        binding.root.findViewById<RecyclerView>(R.id.list).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = waybillAdapter
        }

        waybillHeadViewModel.authError.observe(viewLifecycleOwner, Observer {
            if (it) {
                requireActivity().setResult(Activity.RESULT_OK)
                val intent = Intent(this.context, MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        })

        val toolbar = requireActivity().container?.toolbar

        toolbar?.setNavigationOnClickListener {
            Timber.e("back!!!!")
            activity?.onBackPressed()
        }


        return binding.root
    }


    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: DummyItem?)
    }
}
