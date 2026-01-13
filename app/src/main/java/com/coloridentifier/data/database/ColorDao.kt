package com.coloridentifier.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.coloridentifier.data.model.SavedColor

/**
 * Data Access Object (DAO) dla encji SavedColor.
 * Definiuje operacje bazodanowe dla zapisanych kolorów.
 */
@Dao
interface ColorDao {
    
    /**
     * Wstawia nowy kolor do bazy danych.
     *
     * @param color Kolor do zapisania
     * @return ID wstawionego koloru
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(color: SavedColor): Long
    
    /**
     * Usuwa kolor z bazy danych.
     *
     * @param color Kolor do usunięcia
     */
    @Delete
    suspend fun delete(color: SavedColor)
    
    /**
     * Usuwa wszystkie kolory z bazy danych.
     */
    @Query("DELETE FROM saved_colors")
    suspend fun deleteAll()
    
    /**
     * Pobiera wszystkie zapisane kolory posortowane według znacznika czasu (malejąco).
     * Używa LiveData do automatycznego odświeżania UI przy zmianach w bazie.
     *
     * @return LiveData z listą wszystkich zapisanych kolorów
     */
    @Query("SELECT * FROM saved_colors ORDER BY timestamp DESC")
    fun getAllColors(): LiveData<List<SavedColor>>
    
    /**
     * Pobiera kolor po jego ID.
     *
     * @param id Identyfikator koloru
     * @return SavedColor lub null jeśli nie znaleziono
     */
    @Query("SELECT * FROM saved_colors WHERE id = :id")
    suspend fun getColorById(id: Long): SavedColor?
    
    /**
     * Pobiera listę kolorów na podstawie ich identyfikatorów.
     *
     * @param ids Lista identyfikatorów kolorów
     * @return Lista kolorów odpowiadających podanym ID
     */
    @Query("SELECT * FROM saved_colors WHERE id IN (:ids)")
    suspend fun getColorsByIds(ids: List<Long>): List<SavedColor>
}
