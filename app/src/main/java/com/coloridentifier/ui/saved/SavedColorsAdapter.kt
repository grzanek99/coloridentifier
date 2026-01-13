package com.coloridentifier.ui.saved

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coloridentifier.R
import com.coloridentifier.data.model.SavedColor
import com.coloridentifier.databinding.ItemSavedColorBinding
import com.coloridentifier.util.ColorUtils

/**
 * Adapter dla RecyclerView wyświetlający zapisane kolory.
 * Obsługuje tryb wyboru dla tworzenia palet oraz menu kontekstowe dla każdego koloru.
 */
class SavedColorsAdapter(
    private val onColorClick: (SavedColor) -> Unit,
    private val onColorLongClick: (SavedColor) -> Unit,
    private val onDeleteClick: (SavedColor) -> Unit,
    private val onShareClick: (SavedColor) -> Unit,
    private val onCopyHexClick: (SavedColor) -> Unit,
    private val onCopyRgbClick: (SavedColor) -> Unit,
    private val onShareAsImageClick: (SavedColor) -> Unit
) : ListAdapter<SavedColor, SavedColorsAdapter.ColorViewHolder>(ColorDiffCallback()) {
    
    private var isSelectionMode = false
    private val selectedColors = mutableSetOf<Long>()
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = ItemSavedColorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ColorViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = getItem(position)
        holder.bind(color)
    }
    
    /**
     * Ustawia tryb wyboru kolorów.
     *
     * @param enabled true aby włączyć tryb wyboru, false aby wyłączyć
     */
    fun setSelectionMode(enabled: Boolean) {
        isSelectionMode = enabled
        selectedColors.clear()
        notifyDataSetChanged()
    }
    
    /**
     * Zwraca listę zaznaczonych kolorów.
     *
     * @return Lista zaznaczonych kolorów
     */
    fun getSelectedColors(): List<SavedColor> {
        return currentList.filter { selectedColors.contains(it.id) }
    }
    
    /**
     * Pobiera kolor na określonej pozycji.
     *
     * @param position Pozycja w liście
     * @return SavedColor na tej pozycji
     */
    fun getColorAt(position: Int): SavedColor {
        return getItem(position)
    }
    
    inner class ColorViewHolder(
        private val binding: ItemSavedColorBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        /**
         * Binduje dane koloru do widoku.
         *
         * @param color Kolor do wyświetlenia
         */
        fun bind(color: SavedColor) {
            val colorInt = Color.rgb(color.red, color.green, color.blue)
            
            binding.colorPreview.setBackgroundColor(colorInt)
            binding.colorName.text = color.name
            binding.colorHex.text = "HEX: ${color.hexCode}"
            binding.colorRgb.text = "RGB: ${ColorUtils.formatRgb(color.red, color.green, color.blue)}"
            
            // Obsługa trybu wyboru
            if (isSelectionMode) {
                binding.selectionCheckbox.visibility = View.VISIBLE
                binding.moreButton.visibility = View.GONE
                binding.selectionCheckbox.isChecked = selectedColors.contains(color.id)
                
                binding.root.setOnClickListener {
                    if (selectedColors.contains(color.id)) {
                        selectedColors.remove(color.id)
                    } else {
                        selectedColors.add(color.id)
                    }
                    binding.selectionCheckbox.isChecked = selectedColors.contains(color.id)
                }
            } else {
                binding.selectionCheckbox.visibility = View.GONE
                binding.moreButton.visibility = View.VISIBLE
                
                binding.root.setOnClickListener {
                    onColorClick(color)
                }
                
                binding.root.setOnLongClickListener {
                    onColorLongClick(color)
                    true
                }
            }
            
            // Menu kontekstowe
            binding.moreButton.setOnClickListener { view ->
                showPopupMenu(view, color)
            }
        }
        
        /**
         * Wyświetla menu kontekstowe dla koloru.
         *
         * @param view Widok anchor dla menu
         * @param color Kolor dla którego wyświetlane jest menu
         */
        private fun showPopupMenu(view: View, color: SavedColor) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.color_item_menu, popup.menu)
            
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_copy_hex -> {
                        onCopyHexClick(color)
                        true
                    }
                    R.id.action_copy_rgb -> {
                        onCopyRgbClick(color)
                        true
                    }
                    R.id.action_share_image -> {
                        onShareAsImageClick(color)
                        true
                    }
                    R.id.action_delete -> {
                        onDeleteClick(color)
                        true
                    }
                    else -> false
                }
            }
            
            popup.show()
        }
    }
}

/**
 * DiffUtil.Callback dla optymalizacji RecyclerView.
 */
class ColorDiffCallback : DiffUtil.ItemCallback<SavedColor>() {
    override fun areItemsTheSame(oldItem: SavedColor, newItem: SavedColor): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: SavedColor, newItem: SavedColor): Boolean {
        return oldItem == newItem
    }
}
