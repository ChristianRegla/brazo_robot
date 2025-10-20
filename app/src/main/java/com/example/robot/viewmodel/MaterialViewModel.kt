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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortableColumn {
    COLOR, PESO, ES_METAL, CATEGORIA
}

enum class SortDirection {
    ASC, DESC
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


    val sortedMateriales: StateFlow<List<MaterialItem>> = combine(
        _materiales,
        _sortState
    ) { materials, sortState ->
        if (sortState == null) {
            materials
        } else {
            applySort(materials, sortState.first, sortState.second)
        }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        connectivityObserver.observe().onEach { isOnline ->
            _isConnected.value = isOnline
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
        _isLoading.value = true
        viewModelScope.launch {
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

    fun deleteSelectedItems() {
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
                startUndoDismissTimer()
            }
        }
    }

    fun deleteSingleMaterial(material: MaterialItem) {
        viewModelScope.launch {
            materialRepository.deleteMaterialById(material.id)
            _lastDeletedItems.value = listOf(material)
            startUndoDismissTimer()
        }
    }

    fun deleteAllMateriales() {
        viewModelScope.launch {
            val allItems = _materiales.value
            if (allItems.isNotEmpty()) {
                _lastDeletedItems.value = allItems
                materialRepository.clearMateriales()
                startUndoDismissTimer()
            }
        }
    }

    private fun startUndoDismissTimer() {
        undoDismissJob?.cancel()
        undoDismissJob = viewModelScope.launch {
            delay(4500)
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