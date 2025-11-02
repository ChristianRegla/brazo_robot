package com.example.robot.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robot.data.MaterialRepository
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

object UndoDuration {
    const val SHORT = 3000
    const val MEDIUM = 5000
    const val LONG = 10000
}

class SettingsViewModel(context: Context) : ViewModel() {

    private val dataStore = context.dataStore
    private val materialRepository = MaterialRepository()

    private val UNIT_TYPE_KEY = stringPreferencesKey("unit_type")
    private val THEME_PREFERENCE_KEY = stringPreferencesKey("theme_preference")
    private val HAPTIC_FEEDBACK_KEY = booleanPreferencesKey("haptic_feedback_enabled")
    private val UNDO_DURATION_KEY = intPreferencesKey("undo_duration_millis")
    private val CONFIRM_DELETE_SELECTED_KEY = booleanPreferencesKey("confirm_delete_selected")
    private val CONFIRM_DELETE_ALL_KEY = booleanPreferencesKey("confirm_delete_all")

    val modoAutomatico: StateFlow<Boolean> = materialRepository.getModoAutomatico()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun setModoAutomatico(estaActivo: Boolean) {
        viewModelScope.launch {
            materialRepository.setModoAutomatico(estaActivo)
        }
    }

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

    val hapticFeedbackEnabled: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[HAPTIC_FEEDBACK_KEY] ?: true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val undoDurationMillis: StateFlow<Int> = dataStore.data
        .map { preferences ->
            preferences[UNDO_DURATION_KEY] ?: UndoDuration.MEDIUM
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UndoDuration.MEDIUM
        )

    val confirmDeleteSelected: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[CONFIRM_DELETE_SELECTED_KEY] ?: true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val confirmDeleteAll: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[CONFIRM_DELETE_ALL_KEY] ?: true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
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

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { settings ->
                settings[HAPTIC_FEEDBACK_KEY] = enabled
            }
        }
    }

    fun setUndoDurationMillis(durationMillis: Int) {
        viewModelScope.launch {
            dataStore.edit { settings ->
                settings[UNDO_DURATION_KEY] = durationMillis
            }
        }
    }

    fun setConfirmDeleteSelected(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { settings ->
                settings[CONFIRM_DELETE_SELECTED_KEY] = enabled
            }
        }
    }

    fun setConfirmDeleteAll(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { settings ->
                settings[CONFIRM_DELETE_ALL_KEY] = enabled
            }
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            dataStore.edit { settings ->
                settings.remove(UNIT_TYPE_KEY)
                settings.remove(THEME_PREFERENCE_KEY)
                settings.remove(HAPTIC_FEEDBACK_KEY)
                settings.remove(UNDO_DURATION_KEY)
                settings.remove(CONFIRM_DELETE_SELECTED_KEY)
                settings.remove(CONFIRM_DELETE_ALL_KEY)
            }
        }
    }
}