package com.example.application

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class viewModel: ViewModel() {
    val resumeCountdownEvent = MutableLiveData<Boolean>()
    fun resumeCountdown(){
        resumeCountdownEvent.value = true
    }

}
