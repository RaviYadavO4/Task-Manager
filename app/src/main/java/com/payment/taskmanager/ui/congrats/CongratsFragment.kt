package com.payment.taskmanager.ui.congrats

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.payment.taskmanager.R
import com.payment.taskmanager.databinding.FragmentCongratsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CongratsFragment : Fragment(R.layout.fragment_congrats) {

    private var _binding:FragmentCongratsBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCongratsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup MediaPlayer
        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.congrats_sound)
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
        }
        mediaPlayer.start()


        binding.lottieAnimationView.setAnimation(R.raw.transaction_added_lottie)
        binding.lottieAnimationView.loop(true)
        binding.lottieAnimationView.playAnimation()


        GlobalScope.launch(Dispatchers.Main) {
            delay(1500) // Suspend coroutine for 2 seconds
            findNavController().popBackStack(R.id.homeFragment,false)
        }
    }

}