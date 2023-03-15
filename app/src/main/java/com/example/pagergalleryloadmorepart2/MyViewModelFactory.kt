package com.example.pagergalleryloadmorepart2

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyViewModelFactory(application: Application, param: String, accessTime: Int) : ViewModelProvider.Factory {
    private val mApplication: Application
    private val currentKey: String
    private var accessTime: Int = 0


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GalleryViewModel(mApplication, currentKey, accessTime) as T
    }

    init {
        mApplication = application
        this.currentKey = param
        this.accessTime = accessTime
    }

}