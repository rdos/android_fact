package ru.smartro.worknote.ui.workFlow.waybillHead

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.fragment_waybill_list.*
import ru.smartro.worknote.R
import ru.smartro.worknote.databinding.FragmentWaybillListBinding
import ru.smartro.worknote.ui.login.LoginActivity
import timber.log.Timber

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [WaybillFragment.OnListFragmentInteractionListener] interface.
 */
class WaybillFragment : Fragment() {

    private lateinit var waybillHeadViewModel: WaybillHeadViewModel

    private lateinit var binding: FragmentWaybillListBinding

    private var waybillAdapter: WaybillRecyclerViewAdapter? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.d("WaybillFragment \"onActivityCreated\"")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("WaybillFragment \"onCreate\"")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("WaybillFragment \"onCreateView\"")
        waybillHeadViewModel = ViewModelProvider(
            this,
            WaybillHeadViewModelFactory(requireActivity())
        )
            .get(WaybillHeadViewModel::class.java)

        binding = FragmentWaybillListBinding.inflate(inflater)

        waybillAdapter = WaybillRecyclerViewAdapter(
            onSelectListener = {
                waybillHeadViewModel.onChose(it)
            },
            onDeselectListener = {
                waybillHeadViewModel.onCancelChose()
            },
            viewModel = waybillHeadViewModel
        )


        binding.root.findViewById<RecyclerView>(R.id.list).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = waybillAdapter
        }

        val toolbar = requireActivity().container?.toolbar

        toolbar?.setNavigationOnClickListener {
            Timber.e("back!!!!")
         //   activity?.onBackPressed()
        }

        waybillHeadViewModel.waybills.observe(
            viewLifecycleOwner,
            Observer {
                it?.apply {
                    waybillAdapter?.waybillHeadModels = it
                }
            }
        )
        initModelObservers()
        waybillHeadViewModel.onInit()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUiObservers()
    }


    private fun initModelObservers() {
        waybillHeadViewModel.vehicleNumber.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                (requireActivity() as AppCompatActivity).supportActionBar!!.title =
                    getString(R.string.way_bill)
            } else {
                (requireActivity() as AppCompatActivity).supportActionBar!!.title =
                    getString(R.string.way_bill_for, it)
            }
        })

        waybillHeadViewModel.state.observe(viewLifecycleOwner, Observer {
            //progress bar
            when (it) {
                is WaybillHeadViewModel.State.SoftInProgress -> swipe_refresh_layout.isRefreshing = true
                else -> swipe_refresh_layout.isRefreshing = false

            }
            //button
            button3.isEnabled = waybillHeadViewModel.canConfirmChoice()

            //check boxes
            waybillAdapter?.enabled = waybillHeadViewModel.canSelect()

            // work done
            when (it) {
                is WaybillHeadViewModel.State.Done -> {
                    this.findNavController()
                        .navigate(R.id.action_waybillFragment_to_workOrderFragment)
                    waybillHeadViewModel.onReset()
                }
            }

            //errors
            when (it) {
                is WaybillHeadViewModel.State.Error.AuthError -> {
                    showFailed(R.string.app_auth_error)
                    requireActivity().setResult(Activity.RESULT_OK)
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is WaybillHeadViewModel.State.Error.AppError -> {
                    showFailed(R.string.app_error)
                    requireActivity().setResult(Activity.RESULT_OK)
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is WaybillHeadViewModel.State.Error.NetworkError -> {
                    showFailed(R.string.api_error_no_connection)
                }
                is WaybillHeadViewModel.State.Error.NotFindError -> {
                    showFailed(R.string.api_error_not_find)
                }
            }

        })
    }

    private fun initUiObservers() {
        // refresh
        swipe_refresh_layout.setOnRefreshListener {
            if (waybillHeadViewModel.canRefresh()) {
                waybillHeadViewModel.onRefresh(true)
            }
        }

        //choice done
        button3.setOnClickListener {
            waybillHeadViewModel.onConfirmChoice()
        }
    }

    private fun showFailed(errorString: Int, arg: String? = null) {
        Toast.makeText(context, getString(errorString, arg), Toast.LENGTH_LONG).show()
    }
}
