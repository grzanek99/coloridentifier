package com.coloridentifier.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coloridentifier.data.model.ColorPalette
import com.coloridentifier.data.model.ColorPaletteConverters
import com.coloridentifier.data.model.SavedColor

/**
 * Główna baza danych aplikacji Color Identifier.
 * Używa Room do lokalnego przechowywania kolorów i palet.
 * Singleton pattern zapewnia jedną instancję bazy dla całej aplikacji.
 */
@Database(
    entities = [SavedColor::class, ColorPalette::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ColorPaletteConverters::class)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Zwraca DAO dla operacji na kolorach.
     *
     * @return Instancja ColorDao
     */
    abstract fun colorDao(): ColorDao
    
    /**
     * Zwraca DAO dla operacji na paletach.
     *
     * @return Instancja PaletteDao
     */
    abstract fun paletteDao(): PaletteDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Pobiera instancję bazy danych (Singleton).
         * Tworzy nową instancję jeśli nie istnieje.
         *
         * @param context Kontekst aplikacji
         * @return Instancja AppDatabase
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "color_identifier_database"
                )
                    .fallbackToDestructiveMigration() // W produkcji użyj migracji
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
