package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode(val title: String, val description: String) {
    SYSTEM("System Default", "Follows device system appearance"),
    LIGHT("Light Mode", "Crisp Material 3 daylight palette"),
    DARK("Dark Mode", "Sleek obsidian night palette")
}

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs: SharedPreferences = application.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _dynamicColor = MutableStateFlow(loadDynamicColor())
    val dynamicColor: StateFlow<Boolean> = _dynamicColor.asStateFlow()

    private val _isThemeDialogVisible = MutableStateFlow(false)
    val isThemeDialogVisible: StateFlow<Boolean> = _isThemeDialogVisible.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    fun setDynamicColor(enabled: Boolean) {
        _dynamicColor.value = enabled
        prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
    }

    fun showThemeDialog() {
        _isThemeDialogVisible.value = true
    }

    fun hideThemeDialog() {
        _isThemeDialogVisible.value = false
    }

    val isDynamicColorSupported: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    private fun loadThemeMode(): AppThemeMode {
        val saved = prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name)
        return try {
            AppThemeMode.valueOf(saved ?: AppThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            AppThemeMode.SYSTEM
        }
    }

    private fun loadDynamicColor(): Boolean {
        return prefs.getBoolean(KEY_DYNAMIC_COLOR, true)
    }

    companion object {
        private const val KEY_THEME_MODE = "key_app_theme_mode"
        private const val KEY_DYNAMIC_COLOR = "key_dynamic_color_enabled"
    }
}
