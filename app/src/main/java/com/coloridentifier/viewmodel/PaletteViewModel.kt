package com.coloridentifier.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.coloridentifier.data.database.AppDatabase
import com.coloridentifier.data.model.ColorPalette
import com.coloridentifier.data.repository.ColorRepository
import kotlinx.coroutines.launch

/**
 * ViewModel dla zarządzania operacjami na paletach kolorów.
 * Zapewnia komunikację między UI a warstwą danych (Repository).
 * Przechowuje dane, które przetrwają zmiany konfiguracji (np. obrót ekranu).
 *
 * @property application Referencja do aplikacji (potrzebna do inicjalizacji bazy danych)
 */
class PaletteViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ColorRepository
    
    /**
     * LiveData zawierające wszystkie palety kolorów.
     * Automatycznie aktualizuje UI przy zmianach w bazie danych.
     */
    val allPalettes: LiveData<List<ColorPalette>>
    
    init {
        // Inicjalizacja bazy danych i repository
        val database = AppDatabase.getDatabase(application)
        val colorDao = database.colorDao()
        val paletteDao = database.paletteDao()
        repository = ColorRepository(colorDao, paletteDao)
        allPalettes = repository.allPalettes
    }
    
    /**
     * Zapisuje nową paletę kolorów do bazy danych.
     * Operacja wykonywana asynchronicznie w viewModelScope.
     *
     * @param palette Paleta do zapisania
     */
    fun insertPalette(palette: ColorPalette) = viewModelScope.launch {
        repository.insertPalette(palette)
    }
    
    /**
     * Usuwa paletę kolorów z bazy danych.
     * Operacja wykonywana asynchronicznie w viewModelScope.
     *
     * @param palette Paleta do usunięcia
     */
    fun deletePalette(palette: ColorPalette) = viewModelScope.launch {
        repository.deletePalette(palette)
    }
    
    /**
     * Usuwa wszystkie palety z bazy danych.
     * Operacja wykonywana asynchronicznie w viewModelScope.
     */
    fun deleteAllPalettes() = viewModelScope.launch {
        repository.deleteAllPalettes()
    }
    
    /**
     * Pobiera paletę po jej ID.
     * Operacja wykonywana asynchronicznie w viewModelScope.
     *
     * @param id Identyfikator palety
     * @param callback Funkcja zwrotna z wynikiem (ColorPalette lub null)
     */
    fun getPaletteById(id: Long, callback: (ColorPalette?) -> Unit) = viewModelScope.launch {
        val palette = repository.getPaletteById(id)
        callback(palette)
    }
}
