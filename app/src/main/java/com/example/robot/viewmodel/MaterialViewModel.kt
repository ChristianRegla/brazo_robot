package com.example.robot.viewmodel

import androidx.lifecycle.ViewModel
import com.example.robot.model.MaterialItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MaterialViewModel : ViewModel() {
    private val _materiales = MutableStateFlow<List<MaterialItem>>(emptyList())
    val materiales: StateFlow<List<MaterialItem>> = _materiales

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val db = FirebaseFirestore.getInstance()

    fun loadMateriales() {
        _isLoading.value = true
        db.collection("materiales")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    _materiales.value = emptyList()
                    _isLoading.value = false
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val lista = snapshot.documents.map { doc ->
                        MaterialItem(
                            color = doc.getString("color") ?: "",
                            pesoGramos = doc.getLong("pesoGramos")?.toInt() ?: 0,
                            esMetal = doc.getBoolean("esMetal") ?: false,
                            categoria = doc.getString("categoria") ?: ""
                        )
                    }
                    _materiales.value = lista
                }
                _isLoading.value = false
            }
    }
}