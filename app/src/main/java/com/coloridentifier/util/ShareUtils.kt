package com.coloridentifier.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.FileProvider
import com.coloridentifier.data.model.SavedColor
import java.io.File
import java.io.FileOutputStream

/**
 * Klasa narzędziowa do udostępniania kolorów i palet.
 * Zawiera funkcje do kopiowania kolorów do schowka i udostępniania jako obrazy.
 */
object ShareUtils {
    
    /**
     * Kopiuje tekst do schowka systemowego.
     *
     * @param context Kontekst aplikacji
     * @param label Etykieta dla danych w schowku
     * @param text Tekst do skopiowania
     */
    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }
    
    /**
     * Tworzy kwadratową grafikę przedstawiającą kolor z wartościami HEX i RGB.
     * Grafika zawiera kolorowy prostokąt oraz tekst z informacjami o kolorze.
     *
     * @param color Kolor do wyświetlenia
     * @param name Nazwa koloru
     * @param hexCode Wartość HEX koloru
     * @param rgbText Wartość RGB koloru w formacie tekstu
     * @return Bitmap z grafiką koloru
     */
    fun createColorBitmap(
        color: Int,
        name: String,
        hexCode: String,
        rgbText: String
    ): Bitmap {
        val size = 800
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Białe tło
        canvas.drawColor(Color.WHITE)
        
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        
        // Prostokąt z kolorem (70% wysokości)
        val colorHeight = (size * 0.7f).toInt()
        paint.color = color
        canvas.drawRect(0f, 0f, size.toFloat(), colorHeight.toFloat(), paint)
        
        // Tekst z informacjami
        val textY = colorHeight + 50f
        paint.color = Color.BLACK
        paint.textSize = 40f
        paint.textAlign = Paint.Align.CENTER
        
        // Nazwa koloru
        canvas.drawText(name, size / 2f, textY, paint)
        
        // HEX
        paint.textSize = 50f
        canvas.drawText(hexCode, size / 2f, textY + 80, paint)
        
        // RGB
        paint.textSize = 35f
        canvas.drawText("RGB: $rgbText", size / 2f, textY + 140, paint)
        
        return bitmap
    }
    
    /**
     * Udostępnia kolor jako grafikę.
     * Tworzy tymczasowy plik z obrazem i uruchamia intent udostępniania.
     *
     * @param context Kontekst aplikacji
     * @param color Kolor do udostępnienia
     * @param name Nazwa koloru
     * @param hexCode Wartość HEX koloru
     * @param rgbText Wartość RGB koloru
     */
    fun shareColorAsImage(
        context: Context,
        color: Int,
        name: String,
        hexCode: String,
        rgbText: String
    ) {
        val bitmap = createColorBitmap(color, name, hexCode, rgbText)
        
        // Zapisywanie do pliku tymczasowego
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "color_${System.currentTimeMillis()}.png")
        
        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            
            // Utworzenie URI i udostępnienie
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(shareIntent, "Udostępnij kolor"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Tworzy grafikę przedstawiającą paletę kolorów.
     * Każdy kolor zajmuje równą część szerokości.
     *
     * @param colors Lista kolorów w palecie
     * @param paletteName Nazwa palety
     * @return Bitmap z paletą kolorów
     */
    fun createPaletteBitmap(colors: List<SavedColor>, paletteName: String): Bitmap {
        val width = 1000
        val height = 600
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Białe tło
        canvas.drawColor(Color.WHITE)
        
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        
        // Nazwa palety na górze
        paint.color = Color.BLACK
        paint.textSize = 50f
        paint.textAlign = Paint.Align.CENTER
        val textBounds = Rect()
        paint.getTextBounds(paletteName, 0, paletteName.length, textBounds)
        canvas.drawText(paletteName, width / 2f, 70f, paint)
        
        // Rysowanie kolorów
        if (colors.isNotEmpty()) {
            val colorWidth = width.toFloat() / colors.size
            val colorHeight = height - 150f
            val startY = 100f
            
            colors.forEachIndexed { index, savedColor ->
                val color = ColorUtils.rgbToColor(savedColor.red, savedColor.green, savedColor.blue)
                paint.color = color
                val left = index * colorWidth
                canvas.drawRect(left, startY, left + colorWidth, startY + colorHeight, paint)
                
                // Dodanie etykiety z nazwą koloru
                paint.color = if (ColorUtils.isColorLight(color)) Color.BLACK else Color.WHITE
                paint.textSize = 25f
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText(
                    savedColor.hexCode,
                    left + colorWidth / 2,
                    startY + colorHeight / 2,
                    paint
                )
            }
        }
        
        return bitmap
    }
    
    /**
     * Udostępnia paletę kolorów jako grafikę.
     *
     * @param context Kontekst aplikacji
     * @param colors Lista kolorów w palecie
     * @param paletteName Nazwa palety
     */
    fun sharePaletteAsImage(
        context: Context,
        colors: List<SavedColor>,
        paletteName: String
    ) {
        val bitmap = createPaletteBitmap(colors, paletteName)
        
        // Zapisywanie do pliku tymczasowego
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "palette_${System.currentTimeMillis()}.png")
        
        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            
            // Utworzenie URI i udostępnienie
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(shareIntent, "Udostępnij paletę"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
