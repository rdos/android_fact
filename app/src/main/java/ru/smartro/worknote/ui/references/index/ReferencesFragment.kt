package ru.smartro.worknote.ui.references.index

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import ru.smartro.worknote.R
import ru.smartro.worknote.databinding.FragmentReferencesIndexBinding

class ReferencesFragment : Fragment() {

    private lateinit var referencesViewModel: ReferencesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        referencesViewModel =
            ViewModelProviders.of(this).get(ReferencesViewModel::class.java)
        val binding: FragmentReferencesIndexBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_references_index, container, false
        )

        binding.containerTypes.setOnClickListener {
            it.findNavController().navigate(R.id.action_nav_references_to_conitainerTypeFragment)
        }
        return binding.root
    }
}