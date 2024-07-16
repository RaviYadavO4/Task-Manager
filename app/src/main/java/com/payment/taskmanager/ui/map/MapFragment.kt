package com.payment.taskmanager.ui.map

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.*
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import com.payment.taskmanager.R
import com.payment.taskmanager.databinding.FragmentMapBinding
import com.payment.taskmanager.ui.util.view.FetchAddressIntentService
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {
    private  var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    // Location
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var mLastKnownLocation: Location? = null
    private lateinit var locationCallback: LocationCallback
    private val DEFAULT_ZOOM = 17f

    // Places
    private lateinit var placesClient: PlacesClient
    private lateinit var predictionList: List<AutocompletePrediction>

    // Views
    private lateinit var mapView: View



    // Variables
    private var addressOutput: String = ""
    private var addressResultCode: Int = 0
    private var isSupportedArea: Boolean = false
    private lateinit var currentMarkerPosition: LatLng

    // Receiving
    private var mApiKey: String = "AIzaSyBhvoEMjsFBGnOnrnqEP2o3H_5QHk6BDyU"
    private var mSupportedArea: Array<String> = emptyArray()
    private var mCountry: String = ""
    private var mLanguage: String = "en"

    companion object {
        private const val TAG = "MapFragment"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        receiveIntent()
        initMapsAndPlaces()
    }

    private fun initViews() {



        Handler().postDelayed({
            revealView(binding.icPin)
        }, 1000)

        binding.submitLocationButton.setOnClickListener {
            submitResultLocation()
        }
    }

    private fun receiveIntent() {
        val intent = requireArguments()

        if (intent.containsKey(SimplePlacePicker.API_KEY)) {
            mApiKey = intent.getString(SimplePlacePicker.API_KEY) ?: ""
        }

        if (intent.containsKey(SimplePlacePicker.COUNTRY)) {
            mCountry = intent.getString(SimplePlacePicker.COUNTRY) ?: ""
        }

        if (intent.containsKey(SimplePlacePicker.LANGUAGE)) {
            mLanguage = intent.getString(SimplePlacePicker.LANGUAGE) ?: "en"
        }

        if (intent.containsKey(SimplePlacePicker.SUPPORTED_AREAS)) {
            mSupportedArea = intent.getStringArray(SimplePlacePicker.SUPPORTED_AREAS) ?: emptyArray()
        }
    }

    private fun initMapsAndPlaces() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        Places.initialize(requireActivity(), mApiKey)
        placesClient = Places.createClient(requireContext())
        val token = AutocompleteSessionToken.newInstance()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapView = mapFragment.requireView()

        binding.searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {}

            override fun onSearchConfirmed(text: CharSequence?) {
                text?.let { requireActivity().startSearch(it.toString(), true, null, true) }
            }

            override fun onButtonClicked(buttonCode: Int) {
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    binding.searchBar.closeSearch()
                    binding.searchBar.clearSuggestions()
                }
            }
        })

        binding.searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val predictionsRequest = FindAutocompletePredictionsRequest.builder()
                    .setCountry(mCountry)
                    .setSessionToken(token)
                    .setQuery(s.toString())
                    .build()
                placesClient.findAutocompletePredictions(predictionsRequest)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val predictionsResponse = task.result
                            predictionsResponse?.let {
                                predictionList = predictionsResponse.autocompletePredictions
                                val suggestionsList = ArrayList<String>()
                                for (prediction in predictionList) {
                                    suggestionsList.add(prediction.getFullText(null).toString())
                                }
                                binding.searchBar.updateLastSuggestions(suggestionsList)
                                Handler().postDelayed({
                                    if (!binding.searchBar.isSuggestionsVisible) {
                                        binding.searchBar.showSuggestionsList()
                                    }
                                }, 1000)
                            }
                        } else {
                            Log.i(TAG, "Prediction fetching task unsuccessful")
                        }
                    }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchBar.setSuggestionsClickListener(object : SuggestionsAdapter.OnItemViewClickListener {
            override fun OnItemClickListener(position: Int, v: View?) {
                if (position >= predictionList.size) {
                    return
                }
                val selectedPrediction = predictionList[position]
                val suggestion = binding.searchBar.getLastSuggestions()[position].toString()
                binding.searchBar.setText(suggestion)

                Handler().postDelayed({
                    binding.searchBar.clearSuggestions()
                }, 1000)

                val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.hideSoftInputFromWindow(binding.searchBar.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)

                val placeId = selectedPrediction.placeId
                val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)

                val fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()
                placesClient.fetchPlace(fetchPlaceRequest)
                    .addOnSuccessListener { response: FetchPlaceResponse ->
                        val place = response.place
                        place.latLng?.let { latLng ->
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
                        }

                        binding.rippleBg.startRippleAnimation()
                        Handler().postDelayed({
                            binding.rippleBg.stopRippleAnimation()
                        }, 2000)
                    }
                    .addOnFailureListener { e: Exception ->
                        if (e is ApiException) {
                            e.printStackTrace()
                            val statusCode = e.statusCode
                            Log.i(TAG, "Place not found: ${e.message}")
                            Log.i(TAG, "Status code: $statusCode")
                        }
                    }
            }

            override fun OnItemDeleteListener(position: Int, v: View?) {}
        })
    }

    private fun submitResultLocation() {
        if (addressResultCode == SimplePlacePicker.FAILURE_RESULT || !isSupportedArea) {
            Toast.makeText(requireContext(), R.string.failed_select_location, Toast.LENGTH_SHORT).show()
        } else {

            val result = Bundle().apply {
                putString(SimplePlacePicker.SELECTED_ADDRESS,addressOutput )
                putString(SimplePlacePicker.LOCATION_LAT_EXTRA,currentMarkerPosition.latitude.toString())
                putString(SimplePlacePicker.LOCATION_LNG_EXTRA, currentMarkerPosition.longitude.toString())
            }
            parentFragmentManager.setFragmentResult(SimplePlacePicker.REQUEST_ADDRESS, result)

            findNavController().popBackStack()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isCompassEnabled = false

        if (mapView != null && mapView.findViewById<View>(Integer.parseInt("1")) != null) {
            val locationButton = (mapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(Integer.parseInt("2"))
            val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 60, 500)
        }

        val locationRequest = LocationRequest.create()
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            getDeviceLocation()
        }

        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(requireActivity(), 51)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }

        mMap.setOnMyLocationButtonClickListener {
            if (binding.searchBar.isSuggestionsVisible) {
                binding.searchBar.clearSuggestions()
            }
            if (binding.searchBar.isSearchOpened) {
                binding.searchBar.closeSearch()
            }
            false
        }

        mMap.setOnCameraIdleListener {
            binding.smallPin.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            Log.i(TAG, "Changing address")
            startIntentService()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        mFusedLocationProviderClient.lastLocation
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mLastKnownLocation = task.result
                    mLastKnownLocation?.let {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), DEFAULT_ZOOM))
                    } ?: run {
                        val locationRequest = LocationRequest.create()
                        locationRequest.interval = 1000
                        locationRequest.fastestInterval = 5000
                        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        locationCallback = object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                super.onLocationResult(locationResult)
                                val lastLocation = locationResult.lastLocation
                                if (lastLocation != null) {
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastLocation.latitude, lastLocation.longitude), DEFAULT_ZOOM))
                                }
                                mFusedLocationProviderClient.removeLocationUpdates(locationCallback)
                            }
                        }
                        mFusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null)
                    }
                } else {
                    Toast.makeText(requireContext(), "Unable to get last location", Toast.LENGTH_SHORT).show()
                }
            }
    }

    protected fun startIntentService() {
        currentMarkerPosition = mMap.cameraPosition.target
        val resultReceiver = AddressResultReceiver(Handler())
        val intent = Intent(requireActivity(), FetchAddressIntentService::class.java).apply {
            putExtra(SimplePlacePicker.RECEIVER, resultReceiver)
            putExtra(SimplePlacePicker.LOCATION_LAT_EXTRA, currentMarkerPosition.latitude)
            putExtra(SimplePlacePicker.LOCATION_LNG_EXTRA, currentMarkerPosition.longitude)
            putExtra(SimplePlacePicker.LANGUAGE, mLanguage)
        }
        requireActivity().startService(intent)
    }

    private fun updateUi() {
        binding.tvDisplayMarkerLocation.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        mMap.clear()
        if (addressResultCode == SimplePlacePicker.SUCCESS_RESULT) {
            if (isSupportedArea(mSupportedArea)) {
                addressOutput = addressOutput.replace("Unnamed Road,", "")
                    .replace("Unnamed Road،", "")
                    .replace("Unnamed Road New,", "")
                binding.smallPin.visibility = View.VISIBLE
                isSupportedArea = true
                binding.tvDisplayMarkerLocation.text = addressOutput
            } else {
                binding.smallPin.visibility = View.GONE
                isSupportedArea = false
                binding.tvDisplayMarkerLocation.text = getString(R.string.not_support_area)
            }
        } else if (addressResultCode == SimplePlacePicker.FAILURE_RESULT) {
            binding.smallPin.visibility = View.GONE
            binding.tvDisplayMarkerLocation.text = addressOutput
        }
    }

    private fun isSupportedArea(supportedAreas: Array<String>): Boolean {
        if (supportedAreas.isEmpty()) {
            return true
        }

        val addressSet = HashSet<String>()
        addressSet.addAll(supportedAreas)

        for (area in addressSet) {
            if (addressOutput.contains(area)) {
                return true
            }
        }
        return false
    }

    private fun revealView(view: View) {
        val cx = view.width / 2
        val cy = view.height / 2
        val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)
        view.visibility = View.VISIBLE
        anim.start()
    }

    inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            addressResultCode = resultCode
            if (resultData == null) {
                return
            }
            addressOutput = resultData.getString(SimplePlacePicker.RESULT_DATA_KEY) ?: ""

            updateUi()
        }
    }
}