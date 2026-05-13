package com.jera.caracterisiticsv1.repository

import android.util.Log
import com.jera.caracterisiticsv1.data.CarSpecsDataSource
import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsTrimDetail
import javax.inject.Inject

class CarSpecsRepository @Inject constructor(
    private val dataSource: CarSpecsDataSource
) {

    companion object {
        private const val TAG = "CarSpecsRepository"
    }

    /**
     * Flujo completo: makeName + modelName + year → CarSpecsTrimDetail
     * 1. getMakeId por nombre
     * 2. getModelId por makeId + nombre
     * 3. Filtrar generación por año
     * 4. Coger primer trim disponible
     * 5. Obtener specs completas del trim
     */
    suspend fun fetchCarSpecs(
        makeName: String,
        modelName: String,
        year: Int
    ): CarSpecsTrimDetail? {
        return try {
            // 1. Buscar makeId
            val makeId = findMakeId(makeName) ?: run {
                Log.w(TAG, "Make not found: $makeName")
                return null
            }

            // 2. Buscar modelId
            val modelId = findModelId(makeId, modelName) ?: run {
                Log.w(TAG, "Model not found: $modelName for makeId=$makeId")
                return null
            }

            // 3. Buscar generación por año
            val generationId = findGenerationId(modelId, year) ?: run {
                Log.w(TAG, "Generation not found for year=$year, modelId=$modelId")
                return null
            }

            // 4. Obtener primer trim de la generación
            val trimId = findFirstTrimId(generationId) ?: run {
                Log.w(TAG, "No trims found for generationId=$generationId")
                return null
            }

            // 5. Obtener specs del trim
            val detail = dataSource.getTrimDetail(trimId)
            if (detail == null) Log.w(TAG, "TrimDetail null for trimId=$trimId")
            Log.d(TAG, "TrimDetail for trimId=$trimId, $detail")
            detail

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching car specs", e)
            null
        }
    }

    private suspend fun findMakeId(makeName: String): String? {
        // Primero intentamos filtrar por nombre exacto vía query param (más eficiente)
        val filteredResponse = dataSource.getMakes(makeName)
        if (filteredResponse.isSuccessful) {
            val filtered = filteredResponse.body() ?: emptyList()
            Log.d(TAG, "getMakes(name=$makeName) returned ${filtered.size} results")
            val matched = filtered.firstOrNull { it.name.equals(makeName, ignoreCase = true) }
                ?: filtered.firstOrNull { it.name.contains(makeName, ignoreCase = true) }
                ?: filtered.firstOrNull { makeName.contains(it.name, ignoreCase = true) }
            if (matched != null) {
                Log.d(TAG, "Make '$makeName' matched to: ${matched.name} (id=${matched.id})")
                return matched.id
            }
        }
        // Fallback: listado completo
        val allResponse = dataSource.getMakes(null)
        if (!allResponse.isSuccessful) {
            Log.w(TAG, "getMakes (all) failed: ${allResponse.code()}")
            return null
        }
        val makes = allResponse.body() ?: emptyList()
        Log.d(TAG, "getMakes (all) returned ${makes.size} results")
        val matched = makes.firstOrNull { it.name.equals(makeName, ignoreCase = true) }
            ?: makes.firstOrNull { it.name.contains(makeName, ignoreCase = true) }
            ?: makes.firstOrNull { makeName.contains(it.name, ignoreCase = true) }
        Log.d(TAG, "Make '$makeName' fallback matched to: ${matched?.name} (id=${matched?.id})")
        return matched?.id
    }

    private suspend fun findModelId(makeId: String, modelName: String): String? {
        val response = dataSource.getModels(makeId, null)
        if (!response.isSuccessful) return null
        val models = response.body() ?: emptyList()
        return models.firstOrNull { it.name.equals(modelName, ignoreCase = true) }?.id
            ?: models.firstOrNull { it.name.contains(modelName, ignoreCase = true) }?.id
            ?: models.firstOrNull()?.id
    }

    private suspend fun findGenerationId(modelId: String, year: Int): String? {
        val response = dataSource.getGenerations(modelId)
        if (!response.isSuccessful) return null
        val generations = response.body() ?: emptyList()
        // Buscar generación cuyo rango incluye el año
        val matching = generations.filter { gen ->
            val begin = gen.yearBegin ?: 0
            val end = gen.yearEnd ?: Int.MAX_VALUE
            year in begin..end
        }
        return matching.firstOrNull()?.id
            ?: generations.minByOrNull { gen ->
                // Si no hay coincidencia exacta, coger la más cercana
                val begin = gen.yearBegin ?: year
                kotlin.math.abs(begin - year)
            }?.id
    }

    private suspend fun findFirstTrimId(generationId: String): String? {
        val response = dataSource.getTrims(generationId)
        if (!response.isSuccessful) return null
        return response.body()?.firstOrNull()?.id
    }
}
