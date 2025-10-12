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
}