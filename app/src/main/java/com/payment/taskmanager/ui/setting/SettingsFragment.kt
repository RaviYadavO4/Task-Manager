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

       binding.lblChangeTheme.setOnClickListener {
           if (prefs.getDarkMode()=="Dark"){
               prefs.setDarkMode("Light")
               applyTheme(true)
           }else{
               prefs.setDarkMode("Dark")
               applyTheme(false)
           }
       }


    }

    private fun applyTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        // Recreate the activity for the theme change to take effect
        requireActivity().recreate()
    }

}