package com.example.landtech.presentation.ui.audio_record

import androidx.lifecycle.ViewModel

class AudioRecordViewModel : ViewModel() {
    var orderId: String? = null
        private set

    fun setOrderId(id: String) {
        orderId = id
    }
}