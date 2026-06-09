package com.jera.caracterisiticsv1.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.jera.caracterisiticsv1.data.database.entities.AchievementEntity
import com.jera.caracterisiticsv1.data.database.entities.ModelEntity
import com.jera.caracterisiticsv1.data.database.entities.UserProfileEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// ─── DTOs para Firestore ──────────────────────────────────────────────────────

data class FirestoreUserProfile(
    val uid: String = "",
    val username: String = "Driver",
    val email: String = "",
    val photoUrl: String = "",
    val level: Int = 1,
    val currentXp: Int = 0,
    val totalXp: Int = 0,
    val carsCollected: Int = 0,
    val totalCaptures: Int = 0,
    val isPublic: Boolean = true,
    val createdDate: Long = System.currentTimeMillis()
)

data class FirestoreGarageCar(
    val carId: String = "",
    val makeName: String = "",
    val modelName: String = "",
    val years: String = "",
    val probability: Double = 0.0,
    val imageUrl: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val capturedAt: Long = System.currentTimeMillis(),
    // Specs
    val specsBodyType: String = "",
    val specsFuelType: String = "",
    val specsEngineType: String = "",
    val specsPowerHp: String = "",
    val specsGearbox: String = "",
    val specsDriveWheels: String = "",
    val specsAcceleration: String = "",
    val specsTopSpeed: String = ""
)

data class FirestoreCapturedCar(
    val uid: String = "",
    val username: String = "",
    val photoUrl: String = "",
    val makeName: String = "",
    val modelName: String = "",
    val years: String = "",
    val imageUrl: String = "",
    val probability: Double = 0.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val capturedAt: Long = System.currentTimeMillis(),
    val isPublic: Boolean = true
)

data class FirestoreLeaderboardEntry(
    val uid: String = "",
    val username: String = "",
    val photoUrl: String = "",
    val level: Int = 1,
    val totalXp: Int = 0,
    val carsCollected: Int = 0
)

data class FriendData(
    val profile: FirestoreUserProfile,
    val showcase: List<FirestoreRepository.ShowcaseCar> = emptyList()
)

// ─── Repository ───────────────────────────────────────────────────────────────

@Singleton
class FirestoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // ── Colecciones ────────────────────────────────────────────────────────────
    private fun userDoc(uid: String) = firestore.collection("users").document(uid)
    private fun garageCol(uid: String) = userDoc(uid).collection("garage")
    private fun achievementsCol(uid: String) = userDoc(uid).collection("achievements")
    private val capturedCarsCol = firestore.collection("capturedCars")

    // ─────────────────────────────────────────────────────────────────────────
    // PERFIL DE USUARIO
    // ─────────────────────────────────────────────────────────────────────────

    /** Crea o actualiza el perfil del usuario en Firestore */
    suspend fun syncUserProfile(uid: String, profile: UserProfileEntity, email: String, photoUrl: String) {
        val data = mapOf(
            "uid" to uid,
            "username" to profile.username,
            "email" to email,
            "photoUrl" to photoUrl,
            "level" to profile.level,
            "currentXp" to profile.currentXp,
            "totalXp" to profile.totalXp,
            "carsCollected" to profile.carsCollected,
            "totalCaptures" to profile.totalCaptures,
            "isPublic" to true,
            "createdDate" to profile.createdDate
        )
        userDoc(uid).set(data, SetOptions.merge()).await()
    }

    /** Actualiza solo las estadísticas de juego (XP, nivel, capturas) */
    suspend fun updateUserStats(uid: String, level: Int, currentXp: Int, totalXp: Int, carsCollected: Int, totalCaptures: Int) {
        userDoc(uid).update(
            mapOf(
                "level" to level,
                "currentXp" to currentXp,
                "totalXp" to totalXp,
                "carsCollected" to carsCollected,
                "totalCaptures" to totalCaptures
            )
        ).await()
    }

    /** Obtiene el perfil de un usuario por uid (para ver garaje de otros) */
    suspend fun getUserProfile(uid: String): FirestoreUserProfile? {
        val snap = userDoc(uid).get().await()
        return if (snap.exists()) snap.toObject(FirestoreUserProfile::class.java) else null
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GARAJE
    // ─────────────────────────────────────────────────────────────────────────

    /** Sube un coche al garaje de Firestore cuando el usuario lo captura */
    suspend fun addCarToGarage(uid: String, car: ModelEntity) {
        val carId = car.id.toString()
        val data = FirestoreGarageCar(
            carId = carId,
            makeName = car.make_name ?: "",
            modelName = car.model_name ?: "",
            years = car.years ?: "",
            probability = car.probability ?: 0.0,
            imageUrl = car.imageUrl ?: "",
            latitude = car.latitude,
            longitude = car.longitude,
            capturedAt = System.currentTimeMillis(),
            specsBodyType = car.specsBodyType ?: "",
            specsFuelType = car.specsFuelType ?: "",
            specsEngineType = car.specsEngineType ?: "",
            specsPowerHp = car.specsPowerHp ?: "",
            specsGearbox = car.specsGearbox ?: "",
            specsDriveWheels = car.specsDriveWheels ?: "",
            specsAcceleration = car.specsAcceleration0100 ?: "",
            specsTopSpeed = car.specsTopSpeedKmh ?: ""
        )
        garageCol(uid).document(carId).set(data).await()
    }

    /** Obtiene todos los coches del garaje de un usuario (para ver garaje de otros) */
    suspend fun getGarageCars(uid: String): List<FirestoreGarageCar> {
        val snap = garageCol(uid).get().await()
        return snap.documents.mapNotNull { it.toObject(FirestoreGarageCar::class.java) }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LOGROS
    // ─────────────────────────────────────────────────────────────────────────

    /** Sincroniza un logro desbloqueado a Firestore */
    suspend fun unlockAchievement(uid: String, achievement: AchievementEntity) {
        achievementsCol(uid).document(achievement.achievementId).set(
            mapOf(
                "achievementId" to achievement.achievementId,
                "title" to achievement.title,
                "isUnlocked" to true,
                "unlockedDate" to achievement.unlockedDate,
                "progress" to achievement.progress
            ),
            SetOptions.merge()
        ).await()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MAPA SOCIAL — colección global capturedCars
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Publica un coche en el mapa global cuando tiene coordenadas GPS.
     * Solo se publica si tiene lat/lng válidos.
     */
    suspend fun publishCarToMap(uid: String, username: String, photoUrl: String, car: ModelEntity) {
        val lat = car.latitude ?: return
        val lng = car.longitude ?: return

        val data = FirestoreCapturedCar(
            uid = uid,
            username = username,
            photoUrl = photoUrl,
            makeName = car.make_name ?: "",
            modelName = car.model_name ?: "",
            years = car.years ?: "",
            imageUrl = car.imageUrl ?: "",
            probability = car.probability ?: 0.0,
            latitude = lat,
            longitude = lng,
            capturedAt = System.currentTimeMillis(),
            isPublic = true
        )
        // Usamos uid + carId como clave para evitar duplicados
        capturedCarsCol.document("${uid}_${car.id}").set(data).await()
    }

    /**
     * Obtiene todos los coches capturados por todos los usuarios con ubicación.
     * Se puede filtrar por bounding box en el futuro con queries compuestas.
     */
    suspend fun getAllCapturedCarsOnMap(): List<FirestoreCapturedCar> {
        val snap = capturedCarsCol
            .whereEqualTo("isPublic", true)
            .get()
            .await()
        return snap.documents.mapNotNull { it.toObject(FirestoreCapturedCar::class.java) }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SHOWCASE — expositor de 3 coches
    // ─────────────────────────────────────────────────────────────────────────

    data class ShowcaseCar(
        val carId: String = "",
        val makeName: String = "",
        val modelName: String = "",
        val years: String = "",
        val storageUrl: String = ""   // URL de Firebase Storage (foto del usuario)
    )

    /** Guarda el expositor (hasta 3 coches) en Firestore */
    suspend fun saveShowcase(uid: String, cars: List<ShowcaseCar>) {
        val data = cars.map { car ->
            mapOf(
                "carId" to car.carId,
                "makeName" to car.makeName,
                "modelName" to car.modelName,
                "years" to car.years,
                "storageUrl" to car.storageUrl
            )
        }
        userDoc(uid).update("showcase", data).await()
    }

    /** Obtiene el expositor de un usuario */
    suspend fun getShowcase(uid: String): List<ShowcaseCar> {
        val snap = userDoc(uid).get().await()
        @Suppress("UNCHECKED_CAST")
        val raw = snap.get("showcase") as? List<Map<String, Any>> ?: return emptyList()
        return raw.map { m ->
            ShowcaseCar(
                carId = m["carId"] as? String ?: "",
                makeName = m["makeName"] as? String ?: "",
                modelName = m["modelName"] as? String ?: "",
                years = m["years"] as? String ?: "",
                storageUrl = m["storageUrl"] as? String ?: ""
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FOLLOWING — sistema de amigos unilateral
    // ─────────────────────────────────────────────────────────────────────────

    /** Busca un usuario por email para poder seguirlo */
    suspend fun findUserByEmail(email: String): FirestoreUserProfile? {
        val snap = firestore.collection("users")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .await()
        return snap.documents.firstOrNull()?.toObject(FirestoreUserProfile::class.java)
    }

    /** Añade el uid de otro usuario al array following del usuario actual */
    suspend fun followUser(myUid: String, targetUid: String) {
        userDoc(myUid).update(
            "following", com.google.firebase.firestore.FieldValue.arrayUnion(targetUid)
        ).await()
    }

    /** Elimina el uid del array following */
    suspend fun unfollowUser(myUid: String, targetUid: String) {
        userDoc(myUid).update(
            "following", com.google.firebase.firestore.FieldValue.arrayRemove(targetUid)
        ).await()
    }

    /** Obtiene la lista de uids que sigo */
    suspend fun getFollowing(myUid: String): List<String> {
        val snap = userDoc(myUid).get().await()
        @Suppress("UNCHECKED_CAST")
        return snap.get("following") as? List<String> ?: emptyList()
    }

    /** Obtiene el perfil + expositor de todos mis amigos de una vez */
    suspend fun getFriendsData(followingUids: List<String>): List<FriendData> {
        if (followingUids.isEmpty()) return emptyList()
        return followingUids.mapNotNull { uid ->
            runCatching {
                val snap = userDoc(uid).get().await()
                if (!snap.exists()) return@mapNotNull null
                val profile = snap.toObject(FirestoreUserProfile::class.java) ?: return@mapNotNull null
                @Suppress("UNCHECKED_CAST")
                val rawShowcase = snap.get("showcase") as? List<Map<String, Any>> ?: emptyList()
                val showcase = rawShowcase.map { m ->
                    ShowcaseCar(
                        carId = m["carId"] as? String ?: "",
                        makeName = m["makeName"] as? String ?: "",
                        modelName = m["modelName"] as? String ?: "",
                        years = m["years"] as? String ?: "",
                        storageUrl = m["storageUrl"] as? String ?: ""
                    )
                }
                FriendData(profile = profile, showcase = showcase)
            }.getOrNull()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LEADERBOARD
    // ─────────────────────────────────────────────────────────────────────────

    /** Top 20 jugadores por XP total */
    suspend fun getLeaderboardByXp(): List<FirestoreLeaderboardEntry> {
        // Nota: no usamos whereEqualTo("isPublic", true) para evitar requerir
        // índice compuesto en Firestore. Todos los usuarios registrados son públicos.
        val snap = firestore.collection("users")
            .orderBy("totalXp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .await()
        return snap.documents.mapNotNull {
            val uid = it.getString("uid") ?: return@mapNotNull null
            val username = it.getString("username") ?: "Driver"
            val photoUrl = it.getString("photoUrl") ?: ""
            val level = it.getLong("level")?.toInt() ?: 1
            val totalXp = it.getLong("totalXp")?.toInt() ?: 0
            val carsCollected = it.getLong("carsCollected")?.toInt() ?: 0
            FirestoreLeaderboardEntry(uid, username, photoUrl, level, totalXp, carsCollected)
        }
    }

    /** Top 20 jugadores por coches coleccionados */
    suspend fun getLeaderboardByCollection(): List<FirestoreLeaderboardEntry> {
        val snap = firestore.collection("users")
            .orderBy("carsCollected", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .await()
        return snap.documents.mapNotNull {
            val uid = it.getString("uid") ?: return@mapNotNull null
            val username = it.getString("username") ?: "Driver"
            val photoUrl = it.getString("photoUrl") ?: ""
            val level = it.getLong("level")?.toInt() ?: 1
            val totalXp = it.getLong("totalXp")?.toInt() ?: 0
            val carsCollected = it.getLong("carsCollected")?.toInt() ?: 0
            FirestoreLeaderboardEntry(uid, username, photoUrl, level, totalXp, carsCollected)
        }
    }
}
