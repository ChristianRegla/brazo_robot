package com.example.robot.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.robot.data.MaterialRepository
import com.example.robot.data.NetworkConnectivityObserver
import com.example.robot.model.MaterialItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MaterialViewModel(application: Application) : AndroidViewModel(application) {
    private val materialRepository = MaterialRepository()
    private val connectivityObserver = NetworkConnectivityObserver(application)

    private val _materiales = MutableStateFlow<List<MaterialItem>>(emptyList())
    val materiales: StateFlow<List<MaterialItem>> = _materiales

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected

    init {
        connectivityObserver.observe().onEach { isOnline ->
            _isConnected.value = isOnline
        }.launchIn(viewModelScope)

        loadMateriales()
    }

    fun loadMateriales() {
        _isLoading.value = true
        viewModelScope.launch {
            materialRepository.getMateriales()
                .catch { exception ->
                    _materiales.value = emptyList()
                    _isLoading.value = false
                }
                .collect { materialesList ->
                    _materiales.value = materialesList
                    _isLoading.value = false
                }
        }
    }

    fun clearAllMateriales() {
        viewModelScope.launch {
            materialRepository.clearMateriales()
        }
    }
}