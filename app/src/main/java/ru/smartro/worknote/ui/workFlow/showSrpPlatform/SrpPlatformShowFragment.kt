package ru.smartro.worknote.ui.workFlow.showSrpPlatform

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.databinding.FragmentSrpPlatformShowListBinding
import timber.log.Timber

class SrpPlatformShowFragment : Fragment() {

    private lateinit var binding: FragmentSrpPlatformShowListBinding

    private var srpPlatformRecyclerViewAdapter: SrpPlatformRecyclerViewAdapter? = null

    private lateinit var srpPlatformShowViewModel: SrpPlatformShowViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        srpPlatformShowViewModel = ViewModelProvider(
            this,
            SrpPlatformShowViewModelFactory(requireActivity())
        ).get(SrpPlatformShowViewModel::class.java)
        binding = FragmentSrpPlatformShowListBinding.inflate(inflater)
        srpPlatformRecyclerViewAdapter = SrpPlatformRecyclerViewAdapter()
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

        srpPlatformShowViewModel.platforms.observe(
            viewLifecycleOwner,
            Observer {
                Timber.e("refreshed platforms")
                it?.apply {
                    srpPlatformRecyclerViewAdapter!!.platforms = it
                }
            }
        )
        val workOrderId = SrpPlatformShowFragmentArgs.fromBundle(requireArguments()).workOrderId
        srpPlatformShowViewModel.onRefresh(workOrderId)

        return binding.root
    }

}
