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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemePreference {
    SYSTEM, LIGHT, DARK
}

class SettingsViewModel(context: Context) : ViewModel() {

    private val dataStore = context.dataStore

    private val UNIT_TYPE_KEY = stringPreferencesKey("unit_type")
    private val THEME_PREFERENCE_KEY = stringPreferencesKey("theme_preference")

    val unitType = dataStore.data
        .map { preferences ->
            val unitTypeName = preferences[UNIT_TYPE_KEY] ?: UnitType.GRAMS.name
            try {
                UnitType.valueOf(unitTypeName)
            } catch (e: IllegalArgumentException) {
                UnitType.GRAMS
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UnitType.GRAMS
        )

    val themePreference: StateFlow<ThemePreference> = dataStore.data
        .map { preferences ->
            val themeName = preferences[THEME_PREFERENCE_KEY] ?: ThemePreference.SYSTEM.name
            try {
                ThemePreference.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                ThemePreference.SYSTEM
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemePreference.SYSTEM
        )

    fun setUnitType(unitType: UnitType) {
        viewModelScope.launch {
            dataStore.edit { settings ->
                settings[UNIT_TYPE_KEY] = unitType.name
            }
        }
    }

    fun setThemePreference(themePreference: ThemePreference) {
        viewModelScope.launch {
            dataStore.edit { settings ->
                settings[THEME_PREFERENCE_KEY] = themePreference.name
            }
        }
    }
}