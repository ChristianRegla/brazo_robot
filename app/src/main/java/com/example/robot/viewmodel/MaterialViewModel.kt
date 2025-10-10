package com.example.robot.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.robot.data.MaterialRepository
import com.example.robot.data.NetworkConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MaterialViewModel(application: Application) : AndroidViewModel(application) {
    private val materialRepository = MaterialRepository()
    private val connectivityObserver = NetworkConnectivityObserver(application)

    private val _materialesRows = MutableStateFlow<List<List<String>>>(emptyList())
    val materialesRows: StateFlow<List<List<String>>> = _materialesRows

    // Propiedades para controlar el estado de carga
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Propiedades para controlar la conexión
    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected

    init {
        connectivityObserver.observe().onEach { isOnline ->
            _isConnected.value = isOnline
        }.launchIn(viewModelScope)

        loadMateriales()
    }

    // Función para cargar los materiales desde Firestore
    fun loadMateriales() {
        // Ponemos que está cargando
        _isLoading.value = true
        viewModelScope.launch {
            // Obtenemos los materiales desde el repositorio
            materialRepository.getMateriales()
                .catch { exception ->
                    _materialesRows.value = emptyList()
                    _isLoading.value = false
                }
                .collect { materialesList ->
                    _materialesRows.value = materialesList.map { item->
                        listOf(
                            item.color,
                            "${item.pesoGramos}g",
                            if (item.esMetal) "Sí" else "No",
                            item.categoria
                        )
                    }
                    _isLoading.value = false
                }
        }
    }
}