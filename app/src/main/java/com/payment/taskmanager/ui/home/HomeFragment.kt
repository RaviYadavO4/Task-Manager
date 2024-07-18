package com.payment.taskmanager.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.payment.taskmanager.R
import com.payment.taskmanager.data.db.entity.TaskNote
import com.payment.taskmanager.databinding.FragmentHomeBinding
import com.payment.taskmanager.persistence.Prefs
import com.payment.taskmanager.ui.add.LiveChannelViewModel
import org.koin.android.ext.android.inject


class HomeFragment : Fragment(R.layout.fragment_home),HomeNoteAdapter.OnReportClickListener {

    private val viewModel: LiveChannelViewModel by inject()
    private lateinit var homeNoteAdapter: HomeNoteAdapter

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val prefs:Prefs by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        val searchIcon: ImageView = binding.searchView.findViewById(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(resources.getColor(R.color.black))

        binding.fabCreateNoteBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addNoteFragment)
        }

        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                homeNoteAdapter.filter.filter(newText)
                return true
            }

        })






    }

    private fun initView() {
        observer()
        homeNoteAdapter = HomeNoteAdapter(this)
        val layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        binding.recyclerView.apply {
            this.layoutManager = layoutManager
            adapter = homeNoteAdapter
        }


    }

    private fun observer() {

        viewModel.userComments?.observe(viewLifecycleOwner) {
            homeNoteAdapter.setTask(it)
        }

    }

    override fun onInvoiceClick(position: Int, reports: TaskNote) {
        val bundle = bundleOf("data" to reports)
        findNavController().navigate(R.id.action_homeFragment_to_updateNoteFragment,bundle)
    }

    override fun deleteTask(position: Int, reports: TaskNote) {
        deleteTask(reports)
    }

    override fun completeTask(position: Int, reports: TaskNote) {
        viewModel.updateUser(
            reports.apply {
                status = "Completed"
            }
        )
    }

    private fun deleteTask(reports: TaskNote){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete")
        builder.setMessage("Are you sure delete task")
        builder.setPositiveButton("OK") { dialog, which ->
            viewModel.deleteUser(reports.uniqueSlug)
            dialog.dismiss()
            findNavController().navigate(R.id.action_homeFragment_to_congratsFragment)

        }
        builder.setNegativeButton("Cancel") { dialog, which ->
        }
        val dialog = builder.create()
        dialog.show()

    }


}