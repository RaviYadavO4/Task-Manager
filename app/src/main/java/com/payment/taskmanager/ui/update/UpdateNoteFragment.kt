package com.payment.taskmanager.ui.update

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.chip.Chip
import com.payment.taskmanager.R
import com.payment.taskmanager.data.db.entity.TaskNote
import com.payment.taskmanager.databinding.FragmentUpdatenoteBinding
import com.payment.taskmanager.ui.add.LiveChannelViewModel
import com.payment.taskmanager.ui.map.SimplePlacePicker
import org.koin.android.ext.android.inject
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class UpdateNoteFragment : Fragment(R.layout.fragment_updatenote) , EasyPermissions.PermissionCallbacks{


    private var _binding:FragmentUpdatenoteBinding?= null
    private val binding get() = _binding!!

    var priority = "High"
    var status_ = "Pending"

    val priorities = listOf("High","Medium","Low")
    val states = listOf("Pending","Completed")


    private val viewModel: LiveChannelViewModel by inject()

    private var currentTime: String? = null

    var selectedColor = "#3e434e"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE  = 123



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdatenoteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        initView()



        requestLocationPermission()

    }

    private fun initView() {

        parentFragmentManager.setFragmentResultListener(SimplePlacePicker.REQUEST_ADDRESS, this) { key, bundle ->
            binding.lblAddress.text = bundle.getString(SimplePlacePicker.SELECTED_ADDRESS)
        }


        val data = requireArguments().getParcelable<TaskNote>("data")

         data?.apply {
             binding.etNoteTitle.setText(title)
             binding.etNoteDesc.setText(description)
             binding.lblDueDate.setText(dueDate)
             binding.lblAddress.setText(location)
             priority = level
             status_ = status

             priorityChipGroup()
             statesChipGroup()
         }

        binding.lblDueDate.setOnClickListener {
            showDatePickerDialog()
        }



        binding.apply {

            colorView.setBackgroundColor(Color.parseColor(selectedColor))

            // Date & Time
            val sdf = SimpleDateFormat("dd-MM-yyy", Locale.getDefault())
            currentTime = sdf.format(Date())

            tvDateTime.text = currentTime


            binding.lblAddress.setOnClickListener {

                val bundle = bundleOf(
                    SimplePlacePicker.API_KEY to "AIzaSyBhvoEMjsFBGnOnrnqEP2o3H_5QHk6BDyU"
                )
                findNavController().navigate(R.id.action_updateNoteFragment_to_mapFragment,bundle)
            }

            // Done
            imgDone.setOnClickListener {
                if (valide()){
                val taskDao = TaskNote(
                    uniqueSlug = data?.uniqueSlug?:0,
                    title = binding.etNoteTitle.text.toString(),
                    description = binding.etNoteDesc.text.toString(),
                    date = data?.date?:"",
                    dueDate = binding.lblDueDate.text.toString(),
                    level = priority,
                    status = status_,
                    location = if (binding.lblAddress.text.toString().isEmpty())"" else binding.lblAddress.text.toString(),
                )
                viewModel.updateUser(taskDao)
                findNavController().navigate(R.id.action_updateNoteFragment_to_congratsFragment)
            }
            }

            // Back Button
            imgBack.setOnClickListener {
                findNavController().popBackStack()
            }



        }






    }




    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }

    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            this,
            "This app needs to access your location in order to show it on the map",
            LOCATION_PERMISSION_REQUEST_CODE,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                binding.lblDueDate.setText(SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun priorityChipGroup(){

        priorities.forEach {
            val chip = layoutInflater.inflate(
                R.layout.layout_chip_choice,
                binding.priorityChipGroup,
                false
            ) as Chip

            with(chip) {
                text = "${it}"
                chipStrokeWidth = 1.0F
                isCheckable = true

                binding.priorityChipGroup.addView(this)

                isChecked = chip.text.toString().contains(priority)


                setOnClickListener{it2->

                    priority = chip.text.toString()

                }



            }
        }
    }
    private fun statesChipGroup(){

        states.forEach {
            val chip = layoutInflater.inflate(
                R.layout.layout_chip_choice,
                binding.statusChipGroup,
                false
            ) as Chip

            with(chip) {
                text = "${it}"
                chipStrokeWidth = 1.0F
                isCheckable = true

                binding.statusChipGroup.addView(this)

                isChecked = chip.text.toString().contains(status_)


                setOnClickListener{it2->

                    status_ = chip.text.toString()

                }



            }
        }
    }

    private fun valide():Boolean{
        if (binding.etNoteTitle.text.toString().isNullOrEmpty()){
            Toast.makeText(requireContext(), "Please enter title", Toast.LENGTH_SHORT).show()
            return false
        }else if (binding.etNoteDesc.text.toString().isNullOrEmpty()){
            Toast.makeText(requireContext(), "Please enter discription", Toast.LENGTH_SHORT).show()
            return false
        }else if (binding.lblDueDate.text.toString().isNullOrEmpty()){
            Toast.makeText(requireContext(), "Please select date", Toast.LENGTH_SHORT).show()
            return false
        }else if (binding.lblAddress.text.toString().isNullOrEmpty()){
            Toast.makeText(requireContext(), "Please select address", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

}