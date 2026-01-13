package com.coloridentifier.data.repository

import androidx.lifecycle.LiveData
import com.coloridentifier.data.database.ColorDao
import com.coloridentifier.data.database.PaletteDao
import com.coloridentifier.data.model.ColorPalette
import com.coloridentifier.data.model.SavedColor

/**
 * Repository dla zarządzania operacjami na kolorach i paletach.
 * Pośredniczy między ViewModelami a warstwą bazodanową (DAOs).
 * Zapewnia abstrakcję dla źródła danych i upraszcza logikę w ViewModels.
 *
 * @property colorDao DAO dla operacji na kolorach
 * @property paletteDao DAO dla operacji na paletach
 */
class ColorRepository(
    private val colorDao: ColorDao,
    private val paletteDao: PaletteDao
) {
    
    // ===== OPERACJE NA KOLORACH =====
    
    /**
     * Pobiera wszystkie zapisane kolory jako LiveData.
     *
     * @return LiveData z listą wszystkich kolorów
     */
    val allColors: LiveData<List<SavedColor>> = colorDao.getAllColors()
    
    /**
     * Zapisuje kolor do bazy danych.
     *
     * @param color Kolor do zapisania
     * @return ID zapisanego koloru
     */
    suspend fun insertColor(color: SavedColor): Long {
        return colorDao.insert(color)
    }
    
    /**
     * Usuwa kolor z bazy danych.
     *
     * @param color Kolor do usunięcia
     */
    suspend fun deleteColor(color: SavedColor) {
        colorDao.delete(color)
    }
    
    /**
     * Usuwa wszystkie kolory z bazy danych.
     */
    suspend fun deleteAllColors() {
        colorDao.deleteAll()
    }
    
    /**
     * Pobiera kolor po jego ID.
     *
     * @param id Identyfikator koloru
     * @return SavedColor lub null
     */
    suspend fun getColorById(id: Long): SavedColor? {
        return colorDao.getColorById(id)
    }
    
    /**
     * Pobiera listę kolorów na podstawie listy identyfikatorów.
     *
     * @param ids Lista identyfikatorów kolorów
     * @return Lista kolorów
     */
    suspend fun getColorsByIds(ids: List<Long>): List<SavedColor> {
        return colorDao.getColorsByIds(ids)
    }
    
    // ===== OPERACJE NA PALETACH =====
    
    /**
     * Pobiera wszystkie palety kolorów jako LiveData.
     *
     * @return LiveData z listą wszystkich palet
     */
    val allPalettes: LiveData<List<ColorPalette>> = paletteDao.getAllPalettes()
    
    /**
     * Zapisuje paletę kolorów do bazy danych.
     *
     * @param palette Paleta do zapisania
     * @return ID zapisanej palety
     */
    suspend fun insertPalette(palette: ColorPalette): Long {
        return paletteDao.insert(palette)
    }
    
    /**
     * Usuwa paletę kolorów z bazy danych.
     *
     * @param palette Paleta do usunięcia
     */
    suspend fun deletePalette(palette: ColorPalette) {
        paletteDao.delete(palette)
    }
    
    /**
     * Usuwa wszystkie palety z bazy danych.
     */
    suspend fun deleteAllPalettes() {
        paletteDao.deleteAll()
    }
    
    /**
     * Pobiera paletę po jej ID.
     *
     * @param id Identyfikator palety
     * @return ColorPalette lub null
     */
    suspend fun getPaletteById(id: Long): ColorPalette? {
        return paletteDao.getPaletteById(id)
    }
}
