package com.coloridentifier.ui.palette

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coloridentifier.data.model.ColorPalette
import com.coloridentifier.data.model.SavedColor
import com.coloridentifier.databinding.ItemPaletteBinding
import com.coloridentifier.util.ColorUtils
import com.coloridentifier.viewmodel.ColorViewModel
import kotlinx.coroutines.launch

/**
 * Adapter dla RecyclerView wyświetlający palety kolorów.
 * Każda paleta pokazuje nazwę, liczbę kolorów oraz podgląd kolorów.
 */
class PaletteAdapter(
    private val colorViewModel: ColorViewModel,
    private val onDeleteClick: (ColorPalette) -> Unit,
    private val onShareClick: (ColorPalette, List<SavedColor>) -> Unit
) : ListAdapter<ColorPalette, PaletteAdapter.PaletteViewHolder>(PaletteDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaletteViewHolder {
        val binding = ItemPaletteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaletteViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: PaletteViewHolder, position: Int) {
        val palette = getItem(position)
        holder.bind(palette)
    }
    
    inner class PaletteViewHolder(
        private val binding: ItemPaletteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        /**
         * Binduje dane palety do widoku.
         *
         * @param palette Paleta do wyświetlenia
         */
        fun bind(palette: ColorPalette) {
            binding.paletteName.text = palette.name
            binding.colorCount.text = "${palette.colorIds.size} kolorów"
            
            // Pobranie kolorów i wyświetlenie podglądu
            if (binding.root.context is LifecycleOwner) {
                val lifecycleOwner = binding.root.context as LifecycleOwner
                lifecycleOwner.lifecycleScope.launch {
                    colorViewModel.getColorsByIds(palette.colorIds) { colors ->
                        displayColorsPreview(colors)
                        
                        // Obsługa przycisków
                        binding.shareButton.setOnClickListener {
                            onShareClick(palette, colors)
                        }
                    }
                }
            }
            
            binding.deleteButton.setOnClickListener {
                onDeleteClick(palette)
            }
        }
        
        /**
         * Wyświetla podgląd kolorów w palecie.
         *
         * @param colors Lista kolorów w palecie
         */
        private fun displayColorsPreview(colors: List<SavedColor>) {
            binding.colorsPreview.removeAllViews()
            
            colors.forEach { savedColor ->
                val colorView = View(binding.root.context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    ).apply {
                        (this as ViewGroup.MarginLayoutParams).weight = 1f
                    }
                    val colorInt = ColorUtils.rgbToColor(
                        savedColor.red,
                        savedColor.green,
                        savedColor.blue
                    )
                    setBackgroundColor(colorInt)
                }
                binding.colorsPreview.addView(colorView)
            }
        }
    }
}

/**
 * DiffUtil.Callback dla optymalizacji RecyclerView.
 */
class PaletteDiffCallback : DiffUtil.ItemCallback<ColorPalette>() {
    override fun areItemsTheSame(oldItem: ColorPalette, newItem: ColorPalette): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: ColorPalette, newItem: ColorPalette): Boolean {
        return oldItem == newItem
    }
}
