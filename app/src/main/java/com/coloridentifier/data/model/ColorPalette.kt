package com.coloridentifier.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

/**
 * Encja reprezentująca paletę kolorów w bazie danych Room.
 * Paleta zawiera grupę zapisanych kolorów z wspólną nazwą.
 *
 * @property id Unikalny identyfikator palety (generowany automatycznie)
 * @property name Nazwa palety kolorów
 * @property colorIds Lista identyfikatorów kolorów należących do palety
 * @property timestamp Znacznik czasu utworzenia palety (w milisekundach)
 */
@Entity(tableName = "color_palettes")
@TypeConverters(ColorPaletteConverters::class)
data class ColorPalette(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorIds: List<Long>,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Konwertery typu dla Room Database.
 * Pozwalają na zapisywanie i odczytywanie list identyfikatorów kolorów.
 */
class ColorPaletteConverters {
    /**
     * Konwertuje listę Long na String do zapisu w bazie danych.
     * Elementy są oddzielone przecinkami.
     *
     * @param list Lista identyfikatorów do konwersji
     * @return String zawierający identyfikatory oddzielone przecinkami
     */
    @TypeConverter
    fun fromList(list: List<Long>): String {
        return list.joinToString(",")
    }

    /**
     * Konwertuje String na listę Long przy odczycie z bazy danych.
     * Rozdziela elementy po przecinku i konwertuje na Long.
     *
     * @param value String zawierający identyfikatory oddzielone przecinkami
     * @return Lista identyfikatorów jako Long
     */
    @TypeConverter
    fun toList(value: String): List<Long> {
        return if (value.isEmpty()) {
            emptyList()
        } else {
            value.split(",").map { it.toLong() }
        }
    }
}
