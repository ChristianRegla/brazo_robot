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

    private val _selectedItems = MutableStateFlow<Set<MaterialItem>>(emptySet())
    val selectedItems: StateFlow<Set<MaterialItem>> = _selectedItems


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

    fun toggleSelection(item: MaterialItem) {
        val currentSelection = _selectedItems.value.toMutableSet()
        if (item in currentSelection) {
            currentSelection.remove(item)
        } else {
            currentSelection.add(item)
        }
        _selectedItems.value = currentSelection
    }

    fun clearSelection() {
        _selectedItems.value = emptySet()
    }

    fun deleteSelectedItems() {
        viewModelScope.launch {
            val selectedIds = _selectedItems.value.map { it.id }
            selectedIds.forEach { id ->
                materialRepository.deleteMaterialById(id)
            }
            clearSelection()
        }
    }

    fun clearAllMateriales() {
        viewModelScope.launch {
            materialRepository.clearMateriales()
        }
    }

    fun deleteMaterial(material: MaterialItem) {
        viewModelScope.launch {
            materialRepository.deleteMaterialById(material.id)
        }
    }
}