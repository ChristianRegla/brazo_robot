package com.example.robot.viewmodel

import androidx.lifecycle.ViewModel
import com.example.robot.model.UnitType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSettingsViewModel : ViewModel() {

    private val _unitType = MutableStateFlow(UnitType.GRAMS)
    val unitType: StateFlow<UnitType> = _unitType.asStateFlow()

    fun setUnitType(unitType: UnitType) {
        _unitType.value = unitType
    }
}