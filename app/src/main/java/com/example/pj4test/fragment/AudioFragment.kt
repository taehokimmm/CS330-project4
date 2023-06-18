package com.example.drive.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.drive.ProjectConfiguration
import com.example.drive.audioInference.SirenClassifier
import com.example.drive.databinding.FragmentAudioBinding
import com.example.drive.viewmodel.SharedViewModel

class AudioFragment : Fragment(), SirenClassifier.DetectorListener {
    private val TAG = "AudioFragment"

    private lateinit var sharedViewModel: SharedViewModel

    private var _fragmentAudioBinding: FragmentAudioBinding? = null

    private var prevRecordStatus = false

    private val fragmentAudioBinding
        get() = _fragmentAudioBinding!!

    // classifiers
    lateinit var sirenClassifier: SirenClassifier

    // views
    lateinit var snapView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentAudioBinding = FragmentAudioBinding.inflate(inflater, container, false)

        return fragmentAudioBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        snapView = fragmentAudioBinding.SnapView

        sirenClassifier = SirenClassifier()
        sirenClassifier.initialize(requireContext())
        sirenClassifier.setDetectorListener(this)

        sharedViewModel.getIsRecording().observe(viewLifecycleOwner) {
            if (it) {
                if (!prevRecordStatus) {
                    sirenClassifier.startRecording()
                    onResume()
                }
                prevRecordStatus = true
            } else {
                if (prevRecordStatus) {
                    onPause()
                    sirenClassifier.stopRecording()
                }
                snapView.text = "MIC OFF - NO EMERGENCY VEHICLE DETECTED"

                prevRecordStatus = false
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sirenClassifier.stopInferencing()
    }

    override fun onResume() {
        super.onResume()
        sirenClassifier.startInferencing()
    }

    override fun onResults(score: Float) {
        activity?.runOnUiThread {
            if (score > SirenClassifier.THRESHOLD) {
                snapView.text = "SIREN: YIELD TO EMERGENCY VEHICLE"
                snapView.setBackgroundColor(ProjectConfiguration.activeBackgroundColor)
                snapView.setTextColor(ProjectConfiguration.activeTextColor)
            } else {
                snapView.text = "NO SIREN: GOOD TO GO"
                snapView.setBackgroundColor(ProjectConfiguration.idleBackgroundColor)
                snapView.setTextColor(ProjectConfiguration.idleTextColor)
            }
        }
    }
}