package com.example.robot.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.lifecycle.AndroidViewModel
import com.example.robot.model.MaterialItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MaterialViewModel(application: Application) : AndroidViewModel(application) {
    // Propiedades para almacenar los datos
    private val _materiales = MutableStateFlow<List<MaterialItem>>(emptyList())
    val materiales: StateFlow<List<MaterialItem>> = _materiales

    // Propiedades para controlar el estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Propiedades para controlar la conexión
    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected

    // Instancia de Firestore
    private val db = FirebaseFirestore.getInstance()

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
        // Obtenemos los materiales de Firestore
        db.collection("materiales")
            // addSnapshotListener para obtener los cambios en tiempo real
            .addSnapshotListener { snapshot, exception ->
                // Si hay un error, mostramos los materiales vacíos y paramos
                if (exception != null) {
                    _materiales.value = emptyList()
                    _isLoading.value = false
                    return@addSnapshotListener
                }
                // Si no hay error, obtenemos los materiales y los mostramos
                if (snapshot != null) {
                    val lista = snapshot.documents.map { doc ->
                        MaterialItem(
                            color = doc.getString("color") ?: "",
                            pesoGramos = doc.getLong("pesoGramos")?.toInt() ?: 0,
                            esMetal = doc.getBoolean("esMetal") ?: false,
                            categoria = doc.getString("categoria") ?: ""
                        )
                    }
                    // Actualizamos los materiales
                    _materiales.value = lista
                }
                // Paramos de cargar
                _isLoading.value = false
            }
    }
}