package ru.smartro.worknote.ui.workFlow.selectVehicle

import android.app.Activity
import android.content.Context
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
import kotlinx.android.synthetic.main.fragment_vehicle_list.*
import ru.smartro.worknote.R
import ru.smartro.worknote.databinding.FragmentVehicleListBinding
import ru.smartro.worknote.domain.models.VehicleModel
import ru.smartro.worknote.ui.login.LoginActivity
import timber.log.Timber


class VehicleFragment : Fragment() {

    private lateinit var vehicleViewModel: VehicleViewModel

    private lateinit var binding: FragmentVehicleListBinding

    private var vehicleAdapter: MyVehicleRecyclerViewAdapter? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Toast.makeText(
            activity, "FirstFragment.onActivityCreated()",
            Toast.LENGTH_LONG
        ).show()
        Timber.d("Fragment 1 \"onActivityCreated\"")


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(
            activity, "FirstFragment.onCreate()",
            Toast.LENGTH_LONG
        ).show()
        Timber.d("Fragment 1 \"onCreate\"")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Toast.makeText(
            activity, "FirstFragment.onCreateView()",
            Toast.LENGTH_LONG
        ).show()
        Timber.d("Fragment 1 \"onCreateView\"")


        binding = FragmentVehicleListBinding.inflate(inflater)
        vehicleViewModel = ViewModelProvider(this, VehicleViewModelFactory(requireActivity()))
            .get(VehicleViewModel::class.java)



        vehicleAdapter = MyVehicleRecyclerViewAdapter(
            onSelectListener = {
                vehicleViewModel.onChose(it)
            },
            onDeselectListener = {
                vehicleViewModel.onCancelChose()
            },
            viewModel = vehicleViewModel
        )

        binding.root.findViewById<RecyclerView>(R.id.list).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = vehicleAdapter
        }

        vehicleViewModel.vehicles.observe(
            viewLifecycleOwner,
            Observer<List<VehicleModel>> {
                it?.apply {
                    vehicleAdapter?.vehiclesModels = it
                }
            }
        )
        vehicleViewModel.onInit()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vehicleViewModel.state.observe(viewLifecycleOwner, Observer {
            //progress bar
            when (it) {
                is VehicleViewModel.State.SoftInProgress -> swipe_refresh_layout.isRefreshing = true
                else -> swipe_refresh_layout.isRefreshing = false

            }
            //button
            button3.isEnabled = vehicleViewModel.canConfirmChoice()

            //check boxes
            vehicleAdapter?.enabled = vehicleViewModel.canSelect()

            // work done
            when (it) {
                is VehicleViewModel.State.Done -> this.findNavController()
                    .navigate(R.id.action_nav_vehicle_to_waybillFragment)
            }
            //errors
            when (it) {
                is VehicleViewModel.State.Error.AuthError -> {
                    showFailed(R.string.app_auth_error)
                    requireActivity().setResult(Activity.RESULT_OK)
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is VehicleViewModel.State.Error.AppError -> {
                    showFailed(R.string.app_error)
                    requireActivity().setResult(Activity.RESULT_OK)
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is VehicleViewModel.State.Error.NetworkError -> {
                    showFailed(R.string.api_error_no_connection)
                }
                is VehicleViewModel.State.Error.NotFindError -> {
                    showFailed(R.string.api_error_not_find)
                }
            }

        })

        // refresh
        swipe_refresh_layout.setOnRefreshListener {
            if (vehicleViewModel.canRefresh()) {
                vehicleViewModel.onRefresh(true)
            }
        }

        //choice done
        button3.setOnClickListener {
            vehicleViewModel.onConfirmChoice()
        }


    }

    private fun showFailed(errorString: Int, arg: String? = null) {
        Toast.makeText(context, getString(errorString, arg), Toast.LENGTH_LONG).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Toast.makeText(
            activity, "FirstFragment.onAttach()",
            Toast.LENGTH_LONG
        ).show()
        Timber.d("Fragment 1 \"onAttach\"")
    }

    override fun onStart() {
        super.onStart()
        Toast.makeText(
            activity, "FirstFragment.onStart()",
            Toast.LENGTH_LONG
        ).show()
        Timber.d("Fragment 1 \"onStart\"")
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(
            activity, "FirstFragment.onResume()",
            Toast.LENGTH_LONG
        ).show()
        Timber.d("Fragment 1 \"onResume\"")
    }

    override fun onPause() {
        super.onPause()
        Toast.makeText(
            activity, "FirstFragment.onPause()",
            Toast.LENGTH_LONG
        ).show()
        Timber.d("Fragment 1 \"onPause\"")
    }

    override fun onStop() {
        super.onStop()
        Toast.makeText(
            activity, "FirstFragment.onStop()",
            Toast.LENGTH_LONG
        ).show()
        Timber.d("Fragment 1 \"onStop\"")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Toast.makeText(
            activity, "FirstFragment.onDestroyView()",
            Toast.LENGTH_LONG
        ).show()
        Timber.d("Fragment 1 \"onDestroyView\"")
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(
            activity, "FirstFragment.onDestroy()",
            Toast.LENGTH_LONG
        ).show()
        Timber.d("Fragment 1 \"onDestroy\"")
    }

    override fun onDetach() {
        super.onDetach()
        //mListener = null;
        Toast.makeText(
            activity, "FirstFragment.onDetach()",
            Toast.LENGTH_LONG
        ).show()
        Timber.d("Fragment 1 onDetach")
    }


}
