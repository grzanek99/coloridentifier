package com.coloridentifier.ui.picker

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.coloridentifier.R
import com.coloridentifier.data.model.SavedColor
import com.coloridentifier.databinding.FragmentColorPickerBinding
import com.coloridentifier.util.ColorNameMapper
import com.coloridentifier.util.ColorUtils
import com.coloridentifier.viewmodel.ColorViewModel
import com.github.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Fragment z okrągłą paletą RGB do wyboru koloru.
 * Wykorzystuje bibliotekę ColorPickerView z suwakami jasności i przezroczystości.
 * Wyświetla wybrany kolor w czasie rzeczywistym.
 */
class ColorPickerFragment : Fragment() {
    
    private var _binding: FragmentColorPickerBinding? = null
    private val binding get() = _binding!!
    
    private val colorViewModel: ColorViewModel by viewModels()
    
    private var currentColor: Int = Color.RED
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentColorPickerBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupColorPicker()
        
        // Obsługa przycisku zapisu koloru
        binding.saveColorButton.setOnClickListener {
            saveSelectedColor()
        }
    }
    
    /**
     * Konfiguruje ColorPickerView i podłącza suwaki.
     * Nasłuchuje zmian koloru i aktualizuje UI w czasie rzeczywistym.
     */
    private fun setupColorPicker() {
        // Podłączenie suwaków do ColorPickerView
        binding.colorPickerView.attachBrightnessSlider(binding.brightnessSlider)
        binding.colorPickerView.attachAlphaSlider(binding.alphaSlider)
        
        // Nasłuchiwanie zmian koloru
        binding.colorPickerView.setColorListener(ColorEnvelopeListener { envelope, _ ->
            currentColor = envelope.color
            updateColorInfo(envelope.color)
        })
        
        // Ustawienie początkowego koloru
        binding.colorPickerView.selectByHsvColor(Color.RED)
    }
    
    /**
     * Aktualizuje wyświetlane informacje o wybranym kolorze.
     *
     * @param color Wybrany kolor
     */
    private fun updateColorInfo(color: Int) {
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
     * Zapisuje wybrany kolor do bazy danych.
     */
    private fun saveSelectedColor() {
        val red = Color.red(currentColor)
        val green = Color.green(currentColor)
        val blue = Color.blue(currentColor)
        
        val savedColor = SavedColor(
            name = ColorNameMapper.getColorName(red, green, blue),
            hexCode = ColorUtils.colorToHex(currentColor),
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
        _binding = null
    }
}
