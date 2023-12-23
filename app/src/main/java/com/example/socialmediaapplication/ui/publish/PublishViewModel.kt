package com.example.socialmediaapplication.ui.my

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PublishViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Publish Fragment"
    }
    val text: LiveData<String> = _text
}