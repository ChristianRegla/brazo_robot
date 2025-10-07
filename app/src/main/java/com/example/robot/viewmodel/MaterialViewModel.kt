package com.example.robot.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.robot.data.MaterialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MaterialViewModel(application: Application) : AndroidViewModel(application) {
    private val materialRepository = MaterialRepository()

    private val _materialesRows = MutableStateFlow<List<List<String>>>(emptyList())
    val materialesRows: StateFlow<List<List<String>>> = _materialesRows

    // Propiedades para controlar el estado de carga
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Propiedades para controlar la conexión
    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected

    // Instancia del ConnectivityManager
    private val connectivityManager =
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Callback para la conexión
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) { _isConnected.value = true }
        override fun onLost(network: Network) { _isConnected.value = false }
    }

    // Registro del callback
    init {
        val request = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
        loadMateriales()
    }

    // Desregistro del callback
    override fun onCleared() {
        super.onCleared()
        connectivityManager.unregisterNetworkCallback(networkCallback)
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