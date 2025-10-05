package com.example.robot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robot.model.MaterialItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MaterialViewModel : ViewModel() {
    private val _materiales = MutableStateFlow<List<MaterialItem>>(emptyList())
    val materiales: StateFlow<List<MaterialItem>> = _materiales

    private val db = FirebaseFirestore.getInstance()

    fun loadMateriales() {
        viewModelScope.launch {
            db.collection("materiales")
                .get()
                .addOnSuccessListener { result ->
                    val lista = result.map { doc ->
                        MaterialItem(
                            color = doc.getString("color") ?: "",
                            pesoGramos = doc.getLong("pesoGramos")?.toInt() ?: 0,
                            esMetal = doc.getBoolean("esMetal") ?: false,
                            categoria = doc.getString("categoria") ?: ""
                        )
                    }
                    _materiales.value = lista
                }
                .addOnFailureListener { exception ->
                    // Manejar el error
                }
        }
    }
}