package com.example.robot.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robot.model.UnitType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsViewModel(context: Context) : ViewModel() {

    private val dataStore = context.dataStore

    private val UNIT_TYPE_KEY = stringPreferencesKey("unit_type")

    val unitType = dataStore.data
        .map { preferences ->
            val unitTypeName = preferences[UNIT_TYPE_KEY] ?: UnitType.GRAMS.name
            UnitType.valueOf(unitTypeName)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UnitType.GRAMS
        )

    fun setUnitType(unitType: UnitType) {
        viewModelScope.launch {
            dataStore.edit { settings ->
                settings[UNIT_TYPE_KEY] = unitType.name
            }
        }
    }
}