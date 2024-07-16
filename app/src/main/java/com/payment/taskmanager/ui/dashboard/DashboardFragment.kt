package com.payment.taskmanager.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.payment.taskmanager.R
import com.payment.taskmanager.data.db.entity.TaskNote
import com.payment.taskmanager.databinding.FragmentDashboardBinding
import com.payment.taskmanager.ui.add.LiveChannelViewModel
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LiveChannelViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var completedCount = 0
        var incompleteCount = 0
        viewModel.userComments?.observe(viewLifecycleOwner) {

            Log.v("TaskList---","---$it")

            for (task in it) {
                if (task.status == "Completed") {
                    completedCount++
                } else {
                    incompleteCount++
                }
            }

            val entries = ArrayList<PieEntry>()
            entries.add(PieEntry(completedCount.toFloat(), "Completed"))
            entries.add(PieEntry(incompleteCount.toFloat(), "Pending"))

            // Create dataset
            val dataSet = PieDataSet(entries, "Task Completion")
            dataSet.colors = listOf(Color.parseColor("#43A047"), Color.parseColor("#FFC300")) // Colors for completed and pending tasks

            // Create chart data and set it to chart
            val data = PieData(dataSet)
            binding.chart.data = data

            // Customize chart
            binding.chart.description.isEnabled = false
            binding.chart.invalidate()


            val pendingTasks = filterPendingTasksBeforeToday(it)

            Log.v("pendingTasks======","$pendingTasks")

            if (pendingTasks.isNotEmpty()) {
                showTasksInChart(binding.barChart, pendingTasks)
            }

        }



    }

  private  fun filterPendingTasksBeforeToday(jsonData: List<TaskNote>): List<TaskNote> {
        // Define the date format used in the JSON data
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


        // Get today's date
        val today = Date()

        // Filter tasks with due dates before today and status "Pending"
        return jsonData.filter { task ->
            val dueDate = dateFormat.parse(task.dueDate)
            dueDate.before(today) && task.status == "Pending"
        }
    }
    private fun showTasksInChart(barChart: BarChart, tasks: List<TaskNote>) {
        val entries = tasks.mapIndexed { index, task ->
            BarEntry(index.toFloat(), 1f)
        }

        val dataSet = BarDataSet(entries, "Due Date")
        dataSet.color = Color.RED
        val barData = BarData(dataSet)

        barChart.data = barData

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(tasks.map { it.dueDate })
        xAxis.granularity = 1f

        barChart.invalidate()
    }
}



