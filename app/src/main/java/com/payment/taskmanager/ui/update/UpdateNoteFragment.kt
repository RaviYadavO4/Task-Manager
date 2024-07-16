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
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.chip.Chip
import com.payment.taskmanager.R
import com.payment.taskmanager.data.db.entity.TaskNote
import com.payment.taskmanager.databinding.FragmentUpdatenoteBinding
import com.payment.taskmanager.ui.add.LiveChannelViewModel
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



        getLocation()

    }

    private fun initView() {


        val data = requireArguments().getParcelable<TaskNote>("data")

         data?.apply {
             binding.etNoteTitle.setText(title)
             binding.etNoteDesc.setText(description)
             binding.etNoteDesc.setText(description)
             binding.lblDueDate.setText(dueDate)
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

            // Done
            imgDone.setOnClickListener {
                val taskDao = TaskNote(
                    uniqueSlug = data?.uniqueSlug?:0,
                    title = binding.etNoteTitle.text.toString(),
                    description = binding.etNoteDesc.text.toString(),
                    date = data?.date?:"",
                    dueDate = binding.lblDueDate.text.toString(),
                    level = priority,
                    status = status_,
                    location = if (binding.lblLocation.text.toString().isEmpty())"" else binding.lblLocation.text.toString(),
                )
                viewModel.updateUser(taskDao)
                findNavController().navigate(R.id.action_updateNoteFragment_to_congratsFragment)
            }

            // Back Button
            imgBack.setOnClickListener {
                findNavController().popBackStack()
            }



        }






    }


    private fun getLocation() {
        if (hasLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->

                    if (location != null) {
                        getAddress(location)
                    } else {
                        Toast.makeText(requireContext(), "No location found", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            requestLocationPermission()
        }
    }

    private fun getAddress(location: Location) {
        val geocoder = Geocoder(requireActivity(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                val addressText = "${address.getAddressLine(0)}"
                binding.lblLocation.text = addressText
            } else {
                binding.lblLocation.text = "Address not found"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.lblLocation.text = "Cannot get address"
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
            getLocation()
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

}