package com.example.robot.model

import com.google.firebase.firestore.DocumentId

data class MaterialItem(
    @DocumentId val id: String = "",
    val color: String = "",
    val pesoGramos: Int = 0,
    val esMetal: Boolean = false,
    val categoria: String = ""
)