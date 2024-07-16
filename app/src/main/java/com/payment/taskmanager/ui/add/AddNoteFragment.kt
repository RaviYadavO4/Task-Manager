package com.payment.taskmanager.ui.add

import android.app.DatePickerDialog
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
import com.payment.taskmanager.databinding.FragmentAddnoteBinding
import org.koin.android.ext.android.inject
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AddNoteFragment : Fragment(R.layout.fragment_addnote), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentAddnoteBinding? = null
    private val binding get() = _binding!!

    private val viewModel:LiveChannelViewModel by inject()

    private var currentTime: String? = null
    var priority = "High"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE  = 123

    val priorities = listOf("High","Medium","Low")



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddnoteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        getLocation()



        initView()

        priorityChipGroup()

    }

    private fun initView() {

        binding.apply {

            // Date & Time
            val sdf = SimpleDateFormat("dd-MM-yyy", Locale.getDefault())
            currentTime = sdf.format(Date())

            tvDateTime.text = currentTime

            binding.lblDueDate.setOnClickListener {
                showDatePickerDialog()
            }

            // Done
            imgDone.setOnClickListener {
                val likesDao = TaskNote(
                    title = binding.etNoteTitle.text.toString(),
                    description = binding.etNoteDesc.text.toString(),
                    date = currentTime?:"",
                    dueDate = binding.lblDueDate.text.toString(),
                    level = priority,
                    status = "Pending",
                    location = if (binding.lblLocation.text.toString().isEmpty())"" else binding.lblLocation.text.toString(),
                )
                viewModel.updateUser(likesDao)

                findNavController().navigate(R.id.action_addNoteFragment_to_congratsFragment)
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


}