package com.example.robot.data

import com.example.robot.model.MaterialItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MaterialRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getMateriales(): Flow<List<MaterialItem>> = callbackFlow {
        val listener = db.collection("materiales")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.isEmpty) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val materials = snapshot.toObjects(MaterialItem::class.java)
                trySend(materials)
            }
        awaitClose { listener.remove() }
    }

    suspend fun clearMateriales() {
        try {
            val collection = db.collection("materiales")
            val snapshot = collection.get().await()
            val batch = db.batch()
            for (document in snapshot.documents) {
                batch.delete(document.reference)
            }
            batch.commit().await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteMaterialById(materialId: String) {
        try {
            db.collection("materiales").document(materialId).delete().await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteMaterialsByIds(materialIds: List<String>) {
        try {
            val batch = db.batch()
            materialIds.forEach { id ->
                val docRef = db.collection("materiales").document(id)
                batch.delete(docRef)
            }
            batch.commit().await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun addMaterials(materials: List<MaterialItem>) {
        try {
            val batch = db.batch()
            materials.forEach { material ->
                val docRef = db.collection("materiales").document(material.id)
                batch.set(docRef, material)
            }
            batch.commit().await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Confirma un material específico en Firestore.
     * Esto le dice al ESP32 que puede mover el brazo.
     */
    suspend fun confirmMaterial(materialId: String) {
        try {
            db.collection("materiales").document(materialId)
                .update("confirmado", true)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Actualiza el estado del "Modo Automático" en Firestore.
     * El ESP32 leerá este documento.
     */
    suspend fun setModoAutomatico(estaActivo: Boolean) {
        try {
            val configDoc = db.collection("configuracion").document("robot")

            val configData = mapOf("modoAutomatico" to estaActivo)
            configDoc.set(configData).await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Lee el estado actual del "Modo Automático" desde Firestore.
     */
    fun getModoAutomatico(): Flow<Boolean> = callbackFlow {
        val configDoc = db.collection("configuracion").document("robot")

        val listener = configDoc.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.getBoolean("modoAutomatico") ?: true)
            } else {
                trySend(true)
            }
        }
        awaitClose { listener.remove() }
    }
}