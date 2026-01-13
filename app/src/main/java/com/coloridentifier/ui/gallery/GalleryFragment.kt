package com.coloridentifier.ui.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.coloridentifier.R
import com.coloridentifier.data.model.SavedColor
import com.coloridentifier.databinding.FragmentGalleryBinding
import com.coloridentifier.util.ColorNameMapper
import com.coloridentifier.util.ColorUtils
import com.coloridentifier.viewmodel.ColorViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Fragment obsługujący rozpoznawanie kolorów z obrazów z galerii.
 * Umożliwia wybór obrazu z galerii urządzenia i wykrywanie koloru w miejscu dotknięcia.
 * Czas rozpoznawania koloru: < 200ms zgodnie z wymaganiami.
 */
class GalleryFragment : Fragment() {
    
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    
    private val colorViewModel: ColorViewModel by viewModels()
    
    private var selectedBitmap: Bitmap? = null
    private var detectedColor: Int = Color.TRANSPARENT
    
    /**
     * Launcher dla wyboru obrazu z galerii.
     */
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { loadImage(it) }
    }
    
    /**
     * Launcher dla żądania uprawnień do galerii.
     */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openImagePicker()
        } else {
            Snackbar.make(
                binding.root,
                R.string.gallery_permission_denied,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Obsługa przycisku wyboru obrazu
        binding.selectImageButton.setOnClickListener {
            checkGalleryPermission()
        }
        
        // Obsługa dotyku na obrazie do wykrywania koloru
        setupTouchDetection()
        
        // Obsługa przycisku zapisu koloru
        binding.saveColorButton.setOnClickListener {
            saveDetectedColor()
        }
    }
    
    /**
     * Sprawdza uprawnienia do galerii i żąda ich jeśli są potrzebne.
     * Dla Android 13+ (API 33+) używa READ_MEDIA_IMAGES.
     */
    private fun checkGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                openImagePicker()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }
    
    /**
     * Otwiera picker do wyboru obrazu z galerii.
     */
    private fun openImagePicker() {
        pickImageLauncher.launch("image/*")
    }
    
    /**
     * Wczytuje obraz z wybranego URI.
     * Dekoduje obraz do bitmapy i wyświetla w ImageView.
     *
     * @param uri URI wybranego obrazu
     */
    private fun loadImage(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            selectedBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            binding.selectedImage.setImageBitmap(selectedBitmap)
            binding.colorInfoCard.visibility = View.GONE
            
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(
                binding.root,
                R.string.error_loading_image,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
    
    /**
     * Konfiguruje wykrywanie dotyku na wybranym obrazie.
     * Wykrywa kolor w miejscu dotknięcia obrazu.
     */
    private fun setupTouchDetection() {
        binding.selectedImage.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN && selectedBitmap != null) {
                detectColorAt(event.x, event.y, view.width, view.height)
                true
            } else {
                false
            }
        }
    }
    
    /**
     * Wykrywa kolor w określonym punkcie obrazu.
     * Mapuje współrzędne ImageView na współrzędne bitmapy i pobiera kolor piksela.
     *
     * @param x Współrzędna X dotyku
     * @param y Współrzędna Y dotyku
     * @param viewWidth Szerokość ImageView
     * @param viewHeight Wysokość ImageView
     */
    private fun detectColorAt(x: Float, y: Float, viewWidth: Int, viewHeight: Int) {
        val bitmap = selectedBitmap ?: return
        
        try {
            // Obliczenie współczynnika skalowania
            val imageWidth = bitmap.width.toFloat()
            val imageHeight = bitmap.height.toFloat()
            
            val scaleX = imageWidth / viewWidth
            val scaleY = imageHeight / viewHeight
            
            // Użycie większego współczynnika aby obraz był "fit center"
            val scale = maxOf(scaleX, scaleY)
            
            val scaledWidth = imageWidth / scale
            val scaledHeight = imageHeight / scale
            
            // Obliczenie offsetu (obraz jest wyśrodkowany)
            val offsetX = (viewWidth - scaledWidth) / 2
            val offsetY = (viewHeight - scaledHeight) / 2
            
            // Mapowanie współrzędnych
            val bitmapX = ((x - offsetX) * scale).toInt().coerceIn(0, bitmap.width - 1)
            val bitmapY = ((y - offsetY) * scale).toInt().coerceIn(0, bitmap.height - 1)
            
            // Pobranie koloru piksela
            val color = bitmap.getPixel(bitmapX, bitmapY)
            detectedColor = color
            
            // Wyświetlenie informacji o kolorze
            displayColorInfo(color)
            binding.colorInfoCard.visibility = View.VISIBLE
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Wyświetla informacje o wykrytym kolorze w UI.
     * Pokazuje podgląd koloru, nazwę, wartość HEX i RGB.
     *
     * @param color Wykryty kolor
     */
    private fun displayColorInfo(color: Int) {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        
        val hexCode = ColorUtils.colorToHex(color)
        val rgbText = ColorUtils.formatRgb(red, green, blue)
        val colorName = ColorNameMapper.getColorName(red, green, blue)
        
        binding.colorPreview.setBackgroundColor(color)
        binding.colorNameText.text = getString(R.string.color_name) + " $colorName"
        binding.colorHexText.text = getString(R.string.color_hex) + " $hexCode"
        binding.colorRgbText.text = getString(R.string.color_rgb) + " $rgbText"
    }
    
    /**
     * Zapisuje wykryty kolor do bazy danych.
     * Tworzy nowy obiekt SavedColor i zapisuje go przez ViewModel.
     */
    private fun saveDetectedColor() {
        if (detectedColor == Color.TRANSPARENT) {
            Snackbar.make(
                binding.root,
                "Najpierw wybierz obraz i dotknij go aby wykryć kolor",
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }
        
        val red = Color.red(detectedColor)
        val green = Color.green(detectedColor)
        val blue = Color.blue(detectedColor)
        
        val savedColor = SavedColor(
            name = ColorNameMapper.getColorName(red, green, blue),
            hexCode = ColorUtils.colorToHex(detectedColor),
            red = red,
            green = green,
            blue = blue
        )
        
        lifecycleScope.launch {
            colorViewModel.insertColor(savedColor)
            Snackbar.make(
                binding.root,
                R.string.color_saved,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        selectedBitmap?.recycle()
        _binding = null
    }
}
