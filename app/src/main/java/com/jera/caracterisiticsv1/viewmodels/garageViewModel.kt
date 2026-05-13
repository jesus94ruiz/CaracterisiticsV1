package com.jera.caracterisiticsv1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jera.caracterisiticsv1.data.domain.model.CarModel
import com.jera.caracterisiticsv1.repository.CameraRepository
import com.jera.caracterisiticsv1.repository.DatabaseRepository
import com.jera.caracterisiticsv1.ui.components.BrandInfo
import com.jera.caracterisiticsv1.ui.components.BrandLogoProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class GarageSortOrder { A_Z, Z_A, MOST_CARS, LEAST_CARS }
enum class CarSortOrder    { NAME_AZ, NAME_ZA, YEAR_DESC, YEAR_ASC, PROB_DESC }

@HiltViewModel
class GarageViewModel @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    // ── Base data ─────────────────────────────────────────────────────────────
    private val _models = MutableStateFlow<List<CarModel>>(emptyList())
    val models: StateFlow<List<CarModel>> = _models

    // ── Brand view state ──────────────────────────────────────────────────────
    private val _selectedBrand   = MutableStateFlow<String?>(null)
    val selectedBrand: StateFlow<String?> = _selectedBrand

    private val _brandSearchQuery = MutableStateFlow("")
    val brandSearchQuery: StateFlow<String> = _brandSearchQuery

    private val _brandSortOrder   = MutableStateFlow(GarageSortOrder.A_Z)
    val brandSortOrder: StateFlow<GarageSortOrder> = _brandSortOrder

    // ── Car view state ────────────────────────────────────────────────────────
    private val _carSearchQuery = MutableStateFlow("")
    val carSearchQuery: StateFlow<String> = _carSearchQuery

    private val _carSortOrder = MutableStateFlow(CarSortOrder.NAME_AZ)
    val carSortOrder: StateFlow<CarSortOrder> = _carSortOrder

    // ── Derived: brands list ──────────────────────────────────────────────────
    val brands: StateFlow<List<BrandInfo>> = combine(
        _models, _brandSearchQuery, _brandSortOrder
    ) { cars, query, sort ->
        val grouped = cars.groupBy { it.make_name.trim() }
        var list = grouped.map { (name, group) ->
            BrandInfo(
                name     = name,
                carCount = group.size,
                logoRes  = BrandLogoProvider.getLogoRes(name)
            )
        }
        // Filtrar por búsqueda
        if (query.isNotBlank()) {
            list = list.filter { it.name.contains(query.trim(), ignoreCase = true) }
        }
        // Ordenar
        list = when (sort) {
            GarageSortOrder.A_Z        -> list.sortedBy   { it.name.lowercase() }
            GarageSortOrder.Z_A        -> list.sortedByDescending { it.name.lowercase() }
            GarageSortOrder.MOST_CARS  -> list.sortedByDescending { it.carCount }
            GarageSortOrder.LEAST_CARS -> list.sortedBy   { it.carCount }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Derived: cars for selected brand ─────────────────────────────────────
    val carsForSelectedBrand: StateFlow<List<CarModel>> = combine(
        _models, _selectedBrand, _carSearchQuery, _carSortOrder
    ) { cars, brand, query, sort ->
        if (brand == null) return@combine emptyList()
        var list = cars.filter { it.make_name.trim().equals(brand.trim(), ignoreCase = true) }
        if (query.isNotBlank()) {
            list = list.filter { car ->
                car.model_name.contains(query, ignoreCase = true) ||
                car.make_name.contains(query, ignoreCase = true)  ||
                (car.specsBodyType?.contains(query, ignoreCase = true) == true) ||
                (car.specsFuelType?.contains(query, ignoreCase = true) == true) ||
                (car.specsEngineType?.contains(query, ignoreCase = true) == true)
            }
        }
        list = when (sort) {
            CarSortOrder.NAME_AZ   -> list.sortedBy   { it.model_name.lowercase() }
            CarSortOrder.NAME_ZA   -> list.sortedByDescending { it.model_name.lowercase() }
            CarSortOrder.YEAR_DESC -> list.sortedByDescending { it.years }
            CarSortOrder.YEAR_ASC  -> list.sortedBy   { it.years }
            CarSortOrder.PROB_DESC -> list.sortedByDescending { it.probability }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Init ──────────────────────────────────────────────────────────────────
    init {
        getAllCarModels()
    }

    fun getAllCarModels() {
        viewModelScope.launch {
            _models.value = databaseRepository.getModelsFromDatabase()
        }
    }

    // ── Brand actions ─────────────────────────────────────────────────────────
    fun selectBrand(brandName: String) {
        _selectedBrand.value = brandName
        _carSearchQuery.value = ""
        _carSortOrder.value   = CarSortOrder.NAME_AZ
    }

    fun clearSelectedBrand() {
        _selectedBrand.value  = null
        _carSearchQuery.value = ""
    }

    fun setBrandSearchQuery(query: String) { _brandSearchQuery.value = query }
    fun setBrandSortOrder(order: GarageSortOrder) { _brandSortOrder.value = order }

    // ── Car actions ───────────────────────────────────────────────────────────
    fun setCarSearchQuery(query: String) { _carSearchQuery.value = query }
    fun setCarSortOrder(order: CarSortOrder) { _carSortOrder.value = order }
}
