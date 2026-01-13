package com.coloridentifier.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Encja reprezentująca zapisany kolor w bazie danych Room.
 * Przechowuje informacje o kolorze włączając jego wartość RGB, HEX i nazwę.
 *
 * @property id Unikalny identyfikator koloru (generowany automatycznie)
 * @property name Nazwa koloru (np. "Lime Green")
 * @property hexCode Wartość koloru w formacie HEX (np. "#32CD32")
 * @property red Składowa czerwona koloru (0-255)
 * @property green Składowa zielona koloru (0-255)
 * @property blue Składowa niebieska koloru (0-255)
 * @property timestamp Znacznik czasu utworzenia koloru (w milisekundach)
 */
@Entity(tableName = "saved_colors")
data class SavedColor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val hexCode: String,
    val red: Int,
    val green: Int,
    val blue: Int,
    val timestamp: Long = System.currentTimeMillis()
)
