package com.example.drive.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drive.fragment.AudioFragment
import com.example.drive.fragment.CameraFragment

class SharedViewModel : ViewModel() {
    private val isRecording = MutableLiveData<Boolean>()

    fun setIsRecording(record: Boolean) {
        isRecording.value = record
    }

    fun getIsRecording(): LiveData<Boolean> {
        return isRecording
    }

}