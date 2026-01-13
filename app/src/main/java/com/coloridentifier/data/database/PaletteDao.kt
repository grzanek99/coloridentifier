package com.coloridentifier.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.coloridentifier.data.model.ColorPalette

/**
 * Data Access Object (DAO) dla encji ColorPalette.
 * Definiuje operacje bazodanowe dla palet kolorów.
 */
@Dao
interface PaletteDao {
    
    /**
     * Wstawia nową paletę kolorów do bazy danych.
     *
     * @param palette Paleta do zapisania
     * @return ID wstawionej palety
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(palette: ColorPalette): Long
    
    /**
     * Usuwa paletę kolorów z bazy danych.
     *
     * @param palette Paleta do usunięcia
     */
    @Delete
    suspend fun delete(palette: ColorPalette)
    
    /**
     * Usuwa wszystkie palety z bazy danych.
     */
    @Query("DELETE FROM color_palettes")
    suspend fun deleteAll()
    
    /**
     * Pobiera wszystkie palety kolorów posortowane według znacznika czasu (malejąco).
     * Używa LiveData do automatycznego odświeżania UI przy zmianach w bazie.
     *
     * @return LiveData z listą wszystkich palet kolorów
     */
    @Query("SELECT * FROM color_palettes ORDER BY timestamp DESC")
    fun getAllPalettes(): LiveData<List<ColorPalette>>
    
    /**
     * Pobiera paletę po jej ID.
     *
     * @param id Identyfikator palety
     * @return ColorPalette lub null jeśli nie znaleziono
     */
    @Query("SELECT * FROM color_palettes WHERE id = :id")
    suspend fun getPaletteById(id: Long): ColorPalette?
}
