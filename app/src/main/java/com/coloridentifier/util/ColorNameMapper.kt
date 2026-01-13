package com.coloridentifier.util

import android.graphics.Color
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Klasa odpowiedzialna za mapowanie wartości RGB kolorów na ich nazwy.
 * Implementuje algorytm obliczania odległości euklidesowej w przestrzeni RGB
 * do znalezienia najbliższej nazwanej barwy.
 */
object ColorNameMapper {
    
    /**
     * Zdefiniowane kolory bazowe z nazwami.
     * Kolekcja zawiera najpopularniejsze kolory HTML/CSS.
     */
    private val colorNames = mapOf(
        // Czerwone
        Color.rgb(255, 0, 0) to "Czerwony",
        Color.rgb(220, 20, 60) to "Karmazynowy",
        Color.rgb(255, 99, 71) to "Pomidorowy",
        Color.rgb(255, 69, 0) to "Czerwono-pomarańczowy",
        Color.rgb(178, 34, 34) to "Ceglasty",
        
        // Różowe
        Color.rgb(255, 192, 203) to "Różowy",
        Color.rgb(255, 105, 180) to "Ciemnoróżowy",
        Color.rgb(255, 20, 147) to "Głęboki różowy",
        Color.rgb(219, 112, 147) to "Bladoczerwony",
        
        // Pomarańczowe
        Color.rgb(255, 165, 0) to "Pomarańczowy",
        Color.rgb(255, 140, 0) to "Ciemnopomarańczowy",
        Color.rgb(255, 127, 80) to "Koralowy",
        Color.rgb(255, 160, 122) to "Jasny łososiowy",
        Color.rgb(250, 128, 114) to "Łososiowy",
        
        // Żółte
        Color.rgb(255, 255, 0) to "Żółty",
        Color.rgb(255, 215, 0) to "Złoty",
        Color.rgb(255, 255, 224) to "Jasny żółty",
        Color.rgb(255, 250, 205) to "Cytrynowy",
        Color.rgb(240, 230, 140) to "Khaki",
        
        // Zielone
        Color.rgb(0, 255, 0) to "Zielony",
        Color.rgb(0, 128, 0) to "Ciemnozielony",
        Color.rgb(50, 205, 50) to "Limonkowy",
        Color.rgb(144, 238, 144) to "Jasnozielony",
        Color.rgb(152, 251, 152) to "Bladozielony",
        Color.rgb(34, 139, 34) to "Leśny zielony",
        Color.rgb(0, 100, 0) to "Ciemny zielony",
        Color.rgb(124, 252, 0) to "Trawiasty",
        Color.rgb(127, 255, 0) to "Chartreuse",
        Color.rgb(173, 255, 47) to "Zielono-żółty",
        Color.rgb(0, 255, 127) to "Wiosenny",
        Color.rgb(0, 250, 154) to "Wiosenno-zielony",
        
        // Niebieskie
        Color.rgb(0, 0, 255) to "Niebieski",
        Color.rgb(0, 0, 139) to "Ciemnoniebieski",
        Color.rgb(0, 0, 205) to "Średni niebieski",
        Color.rgb(135, 206, 235) to "Niebiesko-błękitny",
        Color.rgb(173, 216, 230) to "Jasnoniebieski",
        Color.rgb(176, 224, 230) to "Bladoróżowy",
        Color.rgb(70, 130, 180) to "Stalowo niebieski",
        Color.rgb(100, 149, 237) to "Chaber",
        Color.rgb(30, 144, 255) to "Dodger niebieski",
        Color.rgb(0, 191, 255) to "Głęboki niebiesko-błękitny",
        Color.rgb(135, 206, 250) to "Jasny niebiesko-błękitny",
        
        // Fioletowe/Purpurowe
        Color.rgb(128, 0, 128) to "Purpurowy",
        Color.rgb(75, 0, 130) to "Indygo",
        Color.rgb(138, 43, 226) to "Fioletowy",
        Color.rgb(147, 112, 219) to "Średni purpurowy",
        Color.rgb(216, 191, 216) to "Ostem",
        Color.rgb(221, 160, 221) to "Śliwkowy",
        Color.rgb(238, 130, 238) to "Fiołkowy",
        Color.rgb(218, 112, 214) to "Orchidea",
        Color.rgb(255, 0, 255) to "Magenta",
        
        // Brązowe
        Color.rgb(165, 42, 42) to "Brązowy",
        Color.rgb(139, 69, 19) to "Siodłowo brązowy",
        Color.rgb(160, 82, 45) to "Siena",
        Color.rgb(210, 105, 30) to "Czekoladowy",
        Color.rgb(205, 133, 63) to "Peru",
        Color.rgb(244, 164, 96) to "Piaskowy",
        Color.rgb(222, 184, 135) to "Bursztynowy",
        Color.rgb(210, 180, 140) to "Jasnobrązowy",
        
        // Szare/Czarne/Białe
        Color.rgb(0, 0, 0) to "Czarny",
        Color.rgb(255, 255, 255) to "Biały",
        Color.rgb(128, 128, 128) to "Szary",
        Color.rgb(192, 192, 192) to "Srebrny",
        Color.rgb(169, 169, 169) to "Ciemnoszary",
        Color.rgb(211, 211, 211) to "Jasnoszary",
        Color.rgb(220, 220, 220) to "Gainsboro",
        Color.rgb(245, 245, 245) to "Biała dymka",
        Color.rgb(105, 105, 105) to "Przygaszony szary",
        
        // Cyjanowe/Turkusowe
        Color.rgb(0, 255, 255) to "Cyjan",
        Color.rgb(0, 206, 209) to "Ciemny turkus",
        Color.rgb(64, 224, 208) to "Turkusowy",
        Color.rgb(72, 209, 204) to "Średni turkus",
        Color.rgb(175, 238, 238) to "Jasny turkus",
        Color.rgb(127, 255, 212) to "Aquamarine",
        Color.rgb(102, 205, 170) to "Średni aquamarine",
        
        // Inne
        Color.rgb(128, 128, 0) to "Oliwkowy",
        Color.rgb(0, 128, 128) to "Morski",
        Color.rgb(0, 139, 139) to "Ciemny cyjan",
        Color.rgb(95, 158, 160) to "Kadetowy niebieski",
        Color.rgb(240, 248, 255) to "Alice blue",
        Color.rgb(230, 230, 250) to "Lawendowy",
        Color.rgb(255, 240, 245) to "Lawendoworóżowy"
    )
    
    /**
     * Znajduje najbliższą nazwę koloru dla podanego koloru RGB.
     * Używa odległości euklidesowej w przestrzeni RGB do znalezienia najbliższego dopasowania.
     *
     * @param red Składowa czerwona (0-255)
     * @param green Składowa zielona (0-255)
     * @param blue Składowa niebieska (0-255)
     * @return Nazwa najbliższego koloru
     */
    fun getColorName(red: Int, green: Int, blue: Int): String {
        var minDistance = Double.MAX_VALUE
        var closestColorName = "Nieznany"
        
        // Sprawdzanie specjalnych przypadków
        if (red == green && green == blue) {
            return when {
                red < 30 -> "Czarny"
                red > 240 -> "Biały"
                red < 100 -> "Ciemnoszary"
                red < 180 -> "Szary"
                else -> "Jasnoszary"
            }
        }
        
        // Obliczanie odległości euklidesowej do wszystkich zdefiniowanych kolorów
        for ((color, name) in colorNames) {
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)
            
            // Wzór odległości euklidesowej w przestrzeni RGB
            val distance = sqrt(
                (red - r).toDouble().pow(2.0) +
                (green - g).toDouble().pow(2.0) +
                (blue - b).toDouble().pow(2.0)
            )
            
            if (distance < minDistance) {
                minDistance = distance
                closestColorName = name
            }
        }
        
        return closestColorName
    }
    
    /**
     * Znajduje najbliższą nazwę koloru dla podanego koloru.
     *
     * @param color Kolor w formacie Int (ARGB)
     * @return Nazwa najbliższego koloru
     */
    fun getColorName(color: Int): String {
        return getColorName(Color.red(color), Color.green(color), Color.blue(color))
    }
}
