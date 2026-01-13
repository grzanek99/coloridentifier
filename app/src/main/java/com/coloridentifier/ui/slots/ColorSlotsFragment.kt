package com.coloridentifier.ui.slots

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.coloridentifier.databinding.FragmentColorSlotsBinding
import com.github.skydoves.colorpickerview.ColorPickerDialog
import com.github.skydoves.colorpickerview.listeners.ColorEnvelopeListener

/**
 * Fragment ze slotami kolorów.
 * Wyświetla 5 pionowych pustych slotów, które po kliknięciu otwierają dialog wyboru koloru.
 * Każdy slot wizualizuje wybrany kolor.
 */
class ColorSlotsFragment : Fragment() {
    
    private var _binding: FragmentColorSlotsBinding? = null
    private val binding get() = _binding!!
    
    private val slots = mutableListOf<View>()
    private val slotColors = mutableMapOf<Int, Int>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentColorSlotsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Inicjalizacja slotów
        slots.addAll(
            listOf(
                binding.slot1,
                binding.slot2,
                binding.slot3,
                binding.slot4,
                binding.slot5
            )
        )
        
        // Ustawienie listener dla każdego slotu
        slots.forEachIndexed { index, slot ->
            slot.setOnClickListener {
                showColorPickerDialog(index)
            }
        }
    }
    
    /**
     * Wyświetla dialog wyboru koloru dla określonego slotu.
     * Po wybraniu koloru aktualizuje wizualizację slotu.
     *
     * @param slotIndex Indeks slotu (0-4)
     */
    private fun showColorPickerDialog(slotIndex: Int) {
        ColorPickerDialog.Builder(requireContext())
            .setTitle("Wybierz kolor dla slotu ${slotIndex + 1}")
            .setPreferenceName("ColorPickerDialog")
            .setPositiveButton("Wybierz", ColorEnvelopeListener { envelope, _ ->
                val color = envelope.color
                slotColors[slotIndex] = color
                slots[slotIndex].setBackgroundColor(color)
            })
            .setNegativeButton("Anuluj") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)
            .setBottomSpace(12)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
