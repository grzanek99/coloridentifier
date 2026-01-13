package com.coloridentifier.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.coloridentifier.data.database.AppDatabase
import com.coloridentifier.data.model.SavedColor
import com.coloridentifier.data.repository.ColorRepository
import kotlinx.coroutines.launch

/**
 * ViewModel dla zarządzania operacjami na kolorach.
 * Zapewnia komunikację między UI a warstwą danych (Repository).
 * Przechowuje dane, które przetrwają zmiany konfiguracji (np. obrót ekranu).
 *
 * @property application Referencja do aplikacji (potrzebna do inicjalizacji bazy danych)
 */
class ColorViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ColorRepository
    
    /**
     * LiveData zawierające wszystkie zapisane kolory.
     * Automatycznie aktualizuje UI przy zmianach w bazie danych.
     */
    val allColors: LiveData<List<SavedColor>>
    
    init {
        // Inicjalizacja bazy danych i repository
        val database = AppDatabase.getDatabase(application)
        val colorDao = database.colorDao()
        val paletteDao = database.paletteDao()
        repository = ColorRepository(colorDao, paletteDao)
        allColors = repository.allColors
    }
    
    /**
     * Zapisuje nowy kolor do bazy danych.
     * Operacja wykonywana asynchronicznie w viewModelScope.
     *
     * @param color Kolor do zapisania
     */
    fun insertColor(color: SavedColor) = viewModelScope.launch {
        repository.insertColor(color)
    }
    
    /**
     * Usuwa kolor z bazy danych.
     * Operacja wykonywana asynchronicznie w viewModelScope.
     *
     * @param color Kolor do usunięcia
     */
    fun deleteColor(color: SavedColor) = viewModelScope.launch {
        repository.deleteColor(color)
    }
    
    /**
     * Usuwa wszystkie kolory z bazy danych.
     * Operacja wykonywana asynchronicznie w viewModelScope.
     */
    fun deleteAllColors() = viewModelScope.launch {
        repository.deleteAllColors()
    }
    
    /**
     * Pobiera kolor po jego ID.
     * Operacja wykonywana asynchronicznie w viewModelScope.
     *
     * @param id Identyfikator koloru
     * @param callback Funkcja zwrotna z wynikiem (SavedColor lub null)
     */
    fun getColorById(id: Long, callback: (SavedColor?) -> Unit) = viewModelScope.launch {
        val color = repository.getColorById(id)
        callback(color)
    }
    
    /**
     * Pobiera listę kolorów na podstawie ich identyfikatorów.
     * Operacja wykonywana asynchronicznie w viewModelScope.
     *
     * @param ids Lista identyfikatorów kolorów
     * @param callback Funkcja zwrotna z wynikiem (lista kolorów)
     */
    fun getColorsByIds(ids: List<Long>, callback: (List<SavedColor>) -> Unit) = viewModelScope.launch {
        val colors = repository.getColorsByIds(ids)
        callback(colors)
    }
}
