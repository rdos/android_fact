package ru.smartro.worknote.ui.workFlow.workorder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.fragment_work_order_list.*
import ru.smartro.worknote.R
import ru.smartro.worknote.databinding.FragmentWorkOrderListBinding
import ru.smartro.worknote.ui.login.LoginActivity
import timber.log.Timber

class WorkOrderFragment : Fragment() {

    private lateinit var binding: FragmentWorkOrderListBinding

    private var workOrderAdapter: WorkOrderRecyclerViewAdapter? = null

    private lateinit var workOrderViewModel: WorkOrderViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("WorkOrderFragment \"onCreate\"")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("WorkOrderFragment \"onCreateView\"")
        workOrderViewModel = ViewModelProvider(
            this,
            WorkOrderViewModelFactory(requireActivity())
        )
            .get(WorkOrderViewModel::class.java)
        binding = FragmentWorkOrderListBinding.inflate(inflater)
        workOrderAdapter = WorkOrderRecyclerViewAdapter(
            onSelectListener = {
                workOrderViewModel.onChose(it)
            },
            onDeselectListener = {
                workOrderViewModel.onCancelChose()
            },
            viewModel = workOrderViewModel,
            onClickInfo = {
                val actionDetail = WorkOrderFragmentDirections
                    .actionWorkOrderFragmentToSrpPlatformShowFragment(it.srpId)
                this.findNavController()
                    .navigate(actionDetail)
            }
        )

        binding.root.findViewById<RecyclerView>(R.id.list).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = workOrderAdapter
        }

        val toolbar = requireActivity().container?.toolbar

        toolbar?.setNavigationOnClickListener {
            Timber.e("back!!!!")
        }

        workOrderViewModel.workOrders.observe(
            viewLifecycleOwner,
            Observer {
                it?.apply {
                    workOrderAdapter?.workOrders = it
                }
            }
        )
        workOrderViewModel.onInit()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("WorkOrderFragment \"onViewCreated\"")
        workOrderViewModel.state.observe(viewLifecycleOwner, Observer {
            //progress bar
            when (it) {
                is WorkOrderViewModel.State.SoftInProgress -> swipe_refresh_layout.isRefreshing =
                    true
                else -> swipe_refresh_layout.isRefreshing = false

            }
            //button
            button3.isEnabled = workOrderViewModel.canConfirmChoice()


            //check boxes
            workOrderAdapter?.enabled = workOrderViewModel.canSelect()

            // work done
            when (it) {
                is WorkOrderViewModel.State.Done -> {
                    this.findNavController()
                        .navigate(R.id.action_waybillFragment_to_workOrderFragment)
                    workOrderViewModel.onReset()
                }
            }

            //errors
            when (it) {
                is WorkOrderViewModel.State.Error.AuthError -> {
                    showFailed(R.string.app_auth_error)
                    requireActivity().setResult(Activity.RESULT_OK)
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is WorkOrderViewModel.State.Error.AppError -> {
                    showFailed(R.string.app_error)
                    requireActivity().setResult(Activity.RESULT_OK)
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is WorkOrderViewModel.State.Error.NetworkError -> {
                    showFailed(R.string.api_error_no_connection)
                }
                is WorkOrderViewModel.State.Error.NotFindError -> {
                    showFailed(R.string.api_error_not_find)
                }
            }

        })

        // refresh
        swipe_refresh_layout.setOnRefreshListener {
            if (workOrderViewModel.canRefresh()) {
                workOrderViewModel.onRefresh(true)
            }
        }

        //choice done
        button3.setOnClickListener {
            workOrderViewModel.onConfirmChoice()
        }


    }

    private fun showFailed(errorString: Int, arg: String? = null) {
        Toast.makeText(context, getString(errorString, arg), Toast.LENGTH_LONG).show()
    }


    override fun onDetach() {
        Timber.d("WorkOrderFragment \"onDetach\"")
        super.onDetach()
    }
}
