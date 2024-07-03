package com.example.landtech.presentation.ui.images

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.landtech.data.database.models.ImagesDb
import com.example.landtech.data.repository.LandtechRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImagesViewModel @Inject constructor(private val repository: LandtechRepository) :
    ViewModel() {

    private var _orderId = MutableLiveData<String?>(null)
    val orderId: LiveData<String?> = _orderId

    val images: LiveData<List<ImagesDb>?> = orderId.switchMap {
        if (it == null) MutableLiveData(null)
        else repository.getImages(it)
    }

    fun setOrderId(value: String) {
        _orderId.value = value
    }

    fun addImage(imageUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            orderId.value?.let {
                repository.addImage(ImagesDb(orderId = it, imageUri = imageUri))
            }
        }
    }

    fun removeImage(image: ImagesDb) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeImage(image)
        }
    }
}