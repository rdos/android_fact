package ru.smartro.worknote.ui.workFlow.selectVehicle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_vehicle_list.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.domain.models.VehicleModel

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [VehicleFragment.OnListFragmentInteractionListener] interface.
 */
class VehicleFragment : Fragment() {

    private var listener: OnListFragmentInteractionListener? = null

    private val vehicleViewModel: VehicleViewModel by lazy {
        ViewModelProvider(this, VehicleViewModelFactory(requireActivity()))
            .get(VehicleViewModel::class.java)

    }

    private var vehicleAdapter: MyVehicleRecyclerViewAdapter? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vehicleViewModel.vehicles.observe(
            viewLifecycleOwner,
            Observer<List<VehicleModel>> {
                it?.apply {
                    vehicleAdapter?.vehiclesModels = it
                }
            }
        )

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        vehicleViewModel.refresh()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vehicle_list, container, false)
        vehicleAdapter = MyVehicleRecyclerViewAdapter(listener)

        view.list.layoutManager = LinearLayoutManager(context)
        view.list.adapter = vehicleAdapter

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            //       throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
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
        fun onListFragmentInteraction(item: VehicleModel?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            VehicleFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
