package ru.smartro.worknote.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import ru.smartro.worknote.databinding.ListItemSelectOrganisationBinding
import ru.smartro.worknote.domain.models.OrganisationModel


class OrganisationAdapter(val viewModel: OrganisationSelectViewModel) : ListAdapter<OrganisationModel, OrganisationAdapter.ViewHolder>(OrganisationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, viewModel)
    }


    class ViewHolder private constructor(val binding: ListItemSelectOrganisationBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: OrganisationModel, viewModel: OrganisationSelectViewModel) {
            binding.organisation = item
            binding.viewModel = viewModel
            binding.button.setOnClickListener {
                viewModel.currentOrganisationId.value = item.id
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSelectOrganisationBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }


}


class OrganisationDiffCallback: DiffUtil.ItemCallback<OrganisationModel>() {
    override fun areItemsTheSame(oldItem: OrganisationModel, newItem: OrganisationModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: OrganisationModel,
        newItem: OrganisationModel
    ): Boolean {
        return oldItem == newItem
    }

}