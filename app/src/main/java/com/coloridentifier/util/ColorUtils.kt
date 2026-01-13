package com.coloridentifier.util

import android.graphics.Color

/**
 * Klasa narzędziowa zawierająca funkcje pomocnicze do pracy z kolorami.
 * Zawiera metody konwersji między formatami kolorów oraz operacje na kolorach.
 */
object ColorUtils {
    
    /**
     * Konwertuje kolor Int na String w formacie HEX (#RRGGBB).
     *
     * @param color Kolor w formacie Int (ARGB)
     * @return String reprezentujący kolor w formacie HEX
     */
    fun colorToHex(color: Int): String {
        return String.format("#%06X", 0xFFFFFF and color)
    }
    
    /**
     * Konwertuje wartości RGB na kolor Int.
     *
     * @param red Składowa czerwona (0-255)
     * @param green Składowa zielona (0-255)
     * @param blue Składowa niebieska (0-255)
     * @return Kolor w formacie Int (ARGB)
     */
    fun rgbToColor(red: Int, green: Int, blue: Int): Int {
        return Color.rgb(red, green, blue)
    }
    
    /**
     * Konwertuje String HEX na kolor Int.
     *
     * @param hex String w formacie HEX (z lub bez #)
     * @return Kolor w formacie Int (ARGB)
     */
    fun hexToColor(hex: String): Int {
        val cleanHex = if (hex.startsWith("#")) hex else "#$hex"
        return Color.parseColor(cleanHex)
    }
    
    /**
     * Formatuje wartości RGB jako String.
     *
     * @param red Składowa czerwona (0-255)
     * @param green Składowa zielona (0-255)
     * @param blue Składowa niebieska (0-255)
     * @return String w formacie "R, G, B"
     */
    fun formatRgb(red: Int, green: Int, blue: Int): String {
        return "$red, $green, $blue"
    }
    
    /**
     * Formatuje wartości RGB z koloru Int jako String.
     *
     * @param color Kolor w formacie Int (ARGB)
     * @return String w formacie "R, G, B"
     */
    fun formatRgb(color: Int): String {
        return formatRgb(Color.red(color), Color.green(color), Color.blue(color))
    }
    
    /**
     * Sprawdza czy kolor jest jasny (używane do określenia koloru tekstu).
     *
     * @param color Kolor w formacie Int (ARGB)
     * @return true jeśli kolor jest jasny, false w przeciwnym przypadku
     */
    fun isColorLight(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 
                           0.587 * Color.green(color) + 
                           0.114 * Color.blue(color)) / 255
        return darkness < 0.5
    }
}
