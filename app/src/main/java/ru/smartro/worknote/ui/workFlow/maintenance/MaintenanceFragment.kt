package ru.smartro.worknote.ui.workFlow.maintenance

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.maintenance_fragment.view.*
import ru.smartro.worknote.databinding.MaintenanceFragmentBinding
import timber.log.Timber

class MaintenanceFragment : Fragment() {

    companion object {
        fun newInstance() = MaintenanceFragment()
    }

    private val ELEMENT_WIDTH = 240
    private lateinit var binding: MaintenanceFragmentBinding

    private var platformId: Int? = null

    private lateinit var viewModel: MaintenanceViewModel

    private var maintenanceRecyclerViewAdapter: MaintenanceRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        platformId = MaintenanceFragmentArgs.fromBundle(requireArguments()).platformId
        binding = MaintenanceFragmentBinding.inflate(inflater)
        maintenanceRecyclerViewAdapter = MaintenanceRecyclerViewAdapter()
        viewModel = ViewModelProvider(this, MaintenanceViewModelFactory(requireActivity()))
            .get(MaintenanceViewModel::class.java)
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val columns = width / ELEMENT_WIDTH

        binding.root.list.apply {
            adapter = maintenanceRecyclerViewAdapter
            layoutManager = GridLayoutManager(context, columns)
        }

        val toolbar = requireActivity().container?.toolbar

        toolbar?.setNavigationOnClickListener {
            Timber.e("back!!!!")
        }
        viewModel.containers.observe(viewLifecycleOwner, Observer {
            it?.apply {
                maintenanceRecyclerViewAdapter!!.containers = it
            }
        })
        viewModel.onInit(platformId!!)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // TODO: Use the ViewModel
    }

}
