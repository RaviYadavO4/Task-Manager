package com.payment.taskmanager.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.payment.taskmanager.R
import com.payment.taskmanager.data.db.entity.TaskNote
import com.payment.taskmanager.databinding.ViewHolderItemTaskBinding

class HomeNoteAdapter (
    private val listener: OnReportClickListener
) : RecyclerView.Adapter<HomeNoteAdapter.TaskViewHolder>() , Filterable {

    private val reportsList: MutableList<TaskNote> = mutableListOf()
    private val providerListFiltered: MutableList<TaskNote> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemBinding = ViewHolderItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(itemBinding, listener)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(position, reportsList[position])
    }

    override fun getItemCount(): Int = reportsList.size

    fun setTask(listItems: List<TaskNote>) {
        reportsList.clear()
        providerListFiltered.clear()
        reportsList.addAll(listItems)
        providerListFiltered.addAll(listItems)
        notifyDataSetChanged()
    }


    class TaskViewHolder(
        private val binding: ViewHolderItemTaskBinding,
        private val listener: OnReportClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, report: TaskNote) {
            binding.lblTitle.text = report.title
            binding.lblDecription.text = report.description
            binding.lblPriority.text = "Priority - ${report.level}"
            binding.lblLocation.text = report.location
            binding.lblStatus.text = report.status
            binding.lblDate.text = report.dueDate
            
            binding.lblPriority.apply {
                if (report.level.equals("High")){
                    setTextColor(resources.getColor(R.color.Red))
                }else if (report.level.equals("Medium")){
                    setTextColor(resources.getColor(R.color.yellow_900))
                }else{
                    setTextColor(resources.getColor(R.color.green))
                }
            }
            binding.lblStatus.apply {
                 if (report.status.equals("pending")){
                    setTextColor(resources.getColor(R.color.yellow_900))
                }else{
                    setTextColor(resources.getColor(R.color.green))
                }
            }

            binding.completedCheckbox.isChecked = report.status!="Pending"

            binding.btnEdit.setOnClickListener {
                listener.onInvoiceClick(position,report)
            }

            binding.btnDelete.setOnClickListener {
                listener.deleteTask(position,report)
            }

            binding.completedCheckbox.setOnClickListener {
                listener.completeTask(position,report)
            }


        }
    }


    interface OnReportClickListener {
        fun onInvoiceClick(position: Int, reports: TaskNote)
        fun deleteTask(position: Int, reports: TaskNote)
        fun completeTask(position: Int, reports: TaskNote)
    }

    override fun getFilter(): Filter {
        val filter = object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterResults = FilterResults()

                if (p0.isNullOrEmpty()) {
                    filterResults.values = providerListFiltered
                    filterResults.count = providerListFiltered.size
                } else {
                    val searchChar = p0.toString().lowercase()
                    val filteredResults = ArrayList<TaskNote>()

                    for (provider in providerListFiltered) {
                        if (provider.title.lowercase().contains(searchChar) || provider.description.lowercase().contains(searchChar)) {
                            filteredResults.add(provider)
                        }
                    }

                    filterResults.values = filteredResults
                    filterResults.count = filteredResults.size
                }

                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                reportsList.clear()
                reportsList.addAll(p1!!.values as List<TaskNote>)
                notifyDataSetChanged()
            }
        }

        return filter
    }

}