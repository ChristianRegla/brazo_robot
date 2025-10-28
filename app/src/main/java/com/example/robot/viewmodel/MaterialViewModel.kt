package com.example.robot.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.robot.data.MaterialRepository
import com.example.robot.data.NetworkConnectivityObserver
import com.example.robot.model.MaterialItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortableColumn {
    COLOR, PESO, ES_METAL, CATEGORIA
}

enum class SortDirection {
    ASC, DESC
}

enum class MetalFilterState {
    ALL, METAL, NON_METAL
}

class MaterialViewModel(application: Application) : AndroidViewModel(application) {
    private val materialRepository = MaterialRepository()
    private val connectivityObserver = NetworkConnectivityObserver(application)

    private val _materiales = MutableStateFlow<List<MaterialItem>>(emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _selectedItems = MutableStateFlow<Set<MaterialItem>>(emptySet())
    val selectedItems: StateFlow<Set<MaterialItem>> = _selectedItems

    private val _lastDeletedItems = MutableStateFlow<List<MaterialItem>>(emptyList())
    val lastDeletedItems: StateFlow<List<MaterialItem>> = _lastDeletedItems
    private var undoDismissJob: Job? = null

    private val _sortState = MutableStateFlow<Pair<SortableColumn, SortDirection>?>(null)
    val sortState: StateFlow<Pair<SortableColumn, SortDirection>?> = _sortState

    private val _colorFilter = MutableStateFlow<Set<String>>(emptySet())
    val colorFilter: StateFlow<Set<String>> = _colorFilter

    private val _isMetalFilter = MutableStateFlow(MetalFilterState.ALL)
    val isMetalFilter: StateFlow<MetalFilterState> = _isMetalFilter

    private val _categoryFilter = MutableStateFlow<Set<String>>(emptySet())
    val categoryFilter: StateFlow<Set<String>> = _categoryFilter

    val availableColors: StateFlow<List<String>> = _materiales.map { materiales ->
        val colors = materiales.map { it.color.ifBlank { "No hay color" } }.distinct().sorted()
        colors
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableCategories: StateFlow<List<String>> = _materiales.map { materiales ->
        val categories = materiales.map { it.categoria.ifBlank { "Sin categoría" } }.distinct().sorted()
        categories
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val filteredAndSortedMateriales: StateFlow<List<MaterialItem>> = combine(
        _materiales,
        _sortState,
        _colorFilter,
        _isMetalFilter,
        _categoryFilter
    ) { materials, sortState, colorFilter, isMetalFilter, categoryFilter ->
        val filtered = applyFilters(materials, colorFilter, isMetalFilter, categoryFilter)
        if (sortState == null) {
            filtered
        } else {
            applySort(filtered, sortState.first, sortState.second)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val sortedMateriales: StateFlow<List<MaterialItem>> = combine(
        _materiales,
        _sortState
    ) { materials, sortState ->
        if (sortState == null) {
            materials
        } else {
            applySort(materials, sortState.first, sortState.second)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        connectivityObserver.observe().onEach { isOnline ->
            _isConnected.value = isOnline
            if (isOnline && _materiales.value.isEmpty()) {
                fetchMateriales()
            }
        }.launchIn(viewModelScope)

        fetchMateriales()
    }

    fun updateSortColumn(column: SortableColumn) {
        val currentSort = _sortState.value
        val newSortState = when {
            currentSort?.first != column -> Pair(column, SortDirection.ASC)
            currentSort.second == SortDirection.ASC -> Pair(column, SortDirection.DESC)
            else -> null
        }
        _sortState.value = newSortState
    }

    fun toggleColorFilter(color: String) {
        val current = _colorFilter.value.toMutableSet()
        if (current.contains(color)) {
            current.remove(color)
        } else {
            current.add(color)
        }
        _colorFilter.value = current
    }

    fun setMetalFilter(state: MetalFilterState) {
        _isMetalFilter.value = state
    }

    fun toggleCategoryFilter(category: String) {
        val current = _categoryFilter.value.toMutableSet()
        if (current.contains(category)) {
            current.remove(category)
        } else {
            current.add(category)
        }
        _categoryFilter.value = current
    }

    fun clearFilters() {
        _colorFilter.value = emptySet()
        _isMetalFilter.value = MetalFilterState.ALL
        _categoryFilter.value = emptySet()
    }

    private fun applyFilters(
        list: List<MaterialItem>,
        colorFilter: Set<String>,
        isMetalFilter: MetalFilterState,
        categoryFilter: Set<String>
    ): List<MaterialItem> {
        return list.filter { item ->
            val colorMatch = colorFilter.isEmpty() ||
                    (item.color.ifBlank { "No hay color" } in colorFilter)

            val metalMatch = when (isMetalFilter) {
                MetalFilterState.ALL -> true
                MetalFilterState.METAL -> item.esMetal
                MetalFilterState.NON_METAL -> !item.esMetal
            }

            val categoryMatch = categoryFilter.isEmpty() ||
                    (item.categoria.ifBlank { "Sin categoría" } in categoryFilter)

            colorMatch && metalMatch && categoryMatch
        }
    }

    private fun applySort(list: List<MaterialItem>, column: SortableColumn, direction: SortDirection): List<MaterialItem> {
        val comparator: Comparator<MaterialItem> = when (column) {
            SortableColumn.COLOR -> compareBy { it.color }
            SortableColumn.PESO -> compareBy { it.pesoGramos }
            SortableColumn.ES_METAL -> compareBy { it.esMetal }
            SortableColumn.CATEGORIA -> compareBy { it.categoria }
        }
        return if (direction == SortDirection.ASC) {
            list.sortedWith(comparator)
        } else {
            list.sortedWith(comparator.reversed())
        }
    }

    fun fetchMateriales() {
        if (!_isConnected.value) {
            _isLoading.value = false
            _materiales.value = emptyList()
            return
        }
        _isLoading.value = true
        viewModelScope.launch {
            delay(2000)
            materialRepository.getMateriales()
                .catch { exception ->
                    _materiales.value = emptyList()
                    _isLoading.value = false
                }
                .collect { materialesList ->
                    _materiales.value = materialesList
                    _isLoading.value = false
                }
        }
    }

    fun toggleItemSelection(item: MaterialItem) {
        val currentSelection = _selectedItems.value.toMutableSet()
        if (item in currentSelection) {
            currentSelection.remove(item)
        } else {
            currentSelection.add(item)
        }
        _selectedItems.value = currentSelection
    }

    fun deselectAllItems() {
        _selectedItems.value = emptySet()
    }

    fun deleteSelectedItems(undoDurationMillis: Long) {
        viewModelScope.launch {
            val selectedIds = _selectedItems.value.map { it.id }
            val itemsToDelete = _materiales.value.filter { it.id in selectedIds }
            if (itemsToDelete.isNotEmpty()) {
                _lastDeletedItems.value = itemsToDelete
                println("VM: showUndoBar.value = true, lastDeletedItems = ${itemsToDelete.size}")
            }
            selectedIds.forEach { id ->
                materialRepository.deleteMaterialById(id)
            }
            deselectAllItems()

            if (itemsToDelete.isNotEmpty()) {
                startUndoDismissTimer(undoDurationMillis)
            }
        }
    }

    fun deleteSingleMaterial(material: MaterialItem, undoDurationMillis: Long) {
        viewModelScope.launch {
            materialRepository.deleteMaterialById(material.id)
            _lastDeletedItems.value = listOf(material)
            startUndoDismissTimer(undoDurationMillis)
        }
    }

    fun deleteAllMateriales(undoDurationMillis: Long) {
        viewModelScope.launch {
            val allItems = _materiales.value
            if (allItems.isNotEmpty()) {
                _lastDeletedItems.value = allItems
                materialRepository.clearMateriales()
                startUndoDismissTimer(undoDurationMillis)
            }
        }
    }

    private fun startUndoDismissTimer(durationMillis: Long) {
        undoDismissJob?.cancel()
        undoDismissJob = viewModelScope.launch {
            delay(durationMillis + 500L)
            dismissUndoAction()
        }
    }

    fun restoreLastDeletedItems() {
        undoDismissJob?.cancel()
        viewModelScope.launch {
            val itemsToRestore = _lastDeletedItems.value
            materialRepository.addMaterials(itemsToRestore)
            _lastDeletedItems.value = emptyList()
        }
    }

    fun dismissUndoAction() {
        _lastDeletedItems.value = emptyList()
    }
}