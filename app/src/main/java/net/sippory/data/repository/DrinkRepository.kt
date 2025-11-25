package net.sippory.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import net.sippory.data.dao.DrinkDao
import net.sippory.data.entity.DrinkEntity

class DrinkRepository(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) : DrinkDao {
    private val drinksCollection = firestore.collection("drinks")

    override suspend fun getAllDrinks(): List<DrinkEntity> {
        return try {
            val snapshot = drinksCollection.get().await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(DrinkEntity::class.java)
            }
        } catch (e: Exception) {
            println("Error fetching drinks: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getDrinksByName(searchTerm: String): List<DrinkEntity> {
        val editedSearchTerm = searchTerm.takeIf { it.isNotBlank() }?.replaceFirstChar { it.uppercaseChar() } ?: ""
        val endIndex = editedSearchTerm + "\uf8ff"

        return try {
            val snapshot =
                drinksCollection.orderBy("name").startAt(editedSearchTerm).endAt(endIndex).limit(5).get().await()
            snapshot.documents.mapNotNull { it.toObject<DrinkEntity>() }
        } catch (e: Exception) {
            println("Error fetching drinks by name: ${e.message}")
            emptyList()
        }
    }
}
