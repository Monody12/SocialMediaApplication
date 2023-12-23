package com.example.socialmediaapplication.ui.sofa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SofaViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Sofa Fragment"
    }
    val text: LiveData<String> = _text
}