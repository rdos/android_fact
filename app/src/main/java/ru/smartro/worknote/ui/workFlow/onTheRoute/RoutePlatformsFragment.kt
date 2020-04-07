package ru.smartro.worknote.ui.workFlow.onTheRoute

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.databinding.FragmentRoutePlatformsListBinding
import timber.log.Timber


class RoutePlatformsFragment : Fragment() {

    private lateinit var binding: FragmentRoutePlatformsListBinding

    private var srpPlatformRecyclerViewAdapter: RoutePlatformRecyclerViewAdapter? = null

    private lateinit var routePlatformShowViewModel: RoutePlatformShowViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoutePlatformsListBinding.inflate(inflater)
        routePlatformShowViewModel = ViewModelProvider(
            this,
            RoutePlatformShowViewModelFactory(requireActivity())
        ).get(RoutePlatformShowViewModel::class.java)

        srpPlatformRecyclerViewAdapter =
            RoutePlatformRecyclerViewAdapter(routePlatformShowViewModel) {
                val actionDetails = RoutePlatformsFragmentDirections
                    .actionRoutePlatformsFragmentToMaintenanceFragment(it.id)
                this.findNavController()
                    .navigate(actionDetails)
            }

        binding.root.findViewById<RecyclerView>(R.id.list).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = srpPlatformRecyclerViewAdapter
        }
        binding.list.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )
        val toolbar = requireActivity().container?.toolbar

        toolbar?.setNavigationOnClickListener {
            Timber.e("back!!!!")
        }

        routePlatformShowViewModel.platforms.observe(
            viewLifecycleOwner,
            Observer {
                it?.apply {
                    srpPlatformRecyclerViewAdapter!!.platforms = it
                }
            }
        )

        routePlatformShowViewModel.onInit()

        return binding.root
    }
}
