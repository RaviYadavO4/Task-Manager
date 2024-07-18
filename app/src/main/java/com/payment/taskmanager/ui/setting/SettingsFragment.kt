package com.payment.taskmanager.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import com.payment.taskmanager.R
import com.payment.taskmanager.databinding.FragmentSettingsBinding
import com.payment.taskmanager.persistence.Prefs
import org.koin.android.ext.android.inject


class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var _binding:FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val prefs:Prefs by inject()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isNightModeOn: Boolean = prefs.getDarkMode()
        binding.lblChangeTheme.setOnClickListener {
            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                prefs.setDarkMode(false)
                prefs.setFirstDarkMode(false)
                requireActivity().recreate()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                prefs.setDarkMode(true)
                prefs.setFirstDarkMode(false)
                requireActivity().recreate()

            }
        }

    }

}