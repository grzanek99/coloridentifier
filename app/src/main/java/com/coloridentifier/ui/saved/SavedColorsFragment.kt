package com.coloridentifier.ui.saved

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.coloridentifier.R
import com.coloridentifier.data.model.ColorPalette
import com.coloridentifier.databinding.FragmentSavedColorsBinding
import com.coloridentifier.ui.palette.PaletteAdapter
import com.coloridentifier.viewmodel.ColorViewModel
import com.coloridentifier.viewmodel.PaletteViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

/**
 * Fragment wyświetlający zapisane kolory i palety kolorów.
 * Zawiera dwie zakładki: kolory i palety.
 * Umożliwia usuwanie kolorów przez przesunięcie (swipe to delete),
 * tworzenie palet z zaznaczonych kolorów oraz zarządzanie paletami.
 */
class SavedColorsFragment : Fragment() {
    
    private var _binding: FragmentSavedColorsBinding? = null
    private val binding get() = _binding!!
    
    private val colorViewModel: ColorViewModel by viewModels()
    private val paletteViewModel: PaletteViewModel by viewModels()
    
    private lateinit var colorsAdapter: SavedColorsAdapter
    private lateinit var palettesAdapter: PaletteAdapter
    
    private var isSelectionMode = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedColorsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupAdapters()
        setupRecyclerViews()
        setupObservers()
        setupTabs()
        setupFab()
        setupMenu()
    }
    
    /**
     * Inicjalizuje adaptery dla RecyclerView.
     */
    private fun setupAdapters() {
        colorsAdapter = SavedColorsAdapter(
            onColorClick = { /* Możliwość rozszerzenia */ },
            onColorLongClick = { /* Możliwość rozszerzenia */ },
            onDeleteClick = { color ->
                colorViewModel.deleteColor(color)
                Snackbar.make(binding.root, R.string.color_deleted, Snackbar.LENGTH_SHORT).show()
            },
            onShareClick = { color ->
                // Implementacja udostępniania
            },
            onCopyHexClick = { color ->
                com.coloridentifier.util.ShareUtils.copyToClipboard(
                    requireContext(),
                    "HEX",
                    color.hexCode
                )
                Snackbar.make(binding.root, R.string.hex_copied, Snackbar.LENGTH_SHORT).show()
            },
            onCopyRgbClick = { color ->
                val rgbText = com.coloridentifier.util.ColorUtils.formatRgb(
                    color.red,
                    color.green,
                    color.blue
                )
                com.coloridentifier.util.ShareUtils.copyToClipboard(
                    requireContext(),
                    "RGB",
                    rgbText
                )
                Snackbar.make(binding.root, R.string.rgb_copied, Snackbar.LENGTH_SHORT).show()
            },
            onShareAsImageClick = { color ->
                val rgbText = com.coloridentifier.util.ColorUtils.formatRgb(
                    color.red,
                    color.green,
                    color.blue
                )
                val colorInt = Color.rgb(color.red, color.green, color.blue)
                com.coloridentifier.util.ShareUtils.shareColorAsImage(
                    requireContext(),
                    colorInt,
                    color.name,
                    color.hexCode,
                    rgbText
                )
            }
        )
        
        palettesAdapter = PaletteAdapter(
            colorViewModel = colorViewModel,
            onDeleteClick = { palette ->
                showDeletePaletteDialog(palette)
            },
            onShareClick = { palette, colors ->
                com.coloridentifier.util.ShareUtils.sharePaletteAsImage(
                    requireContext(),
                    colors,
                    palette.name
                )
            }
        )
    }
    
    /**
     * Konfiguruje RecyclerView dla kolorów i palet.
     */
    private fun setupRecyclerViews() {
        binding.colorsRecyclerView.adapter = colorsAdapter
        binding.palettesRecyclerView.adapter = palettesAdapter
        
        // Swipe to delete dla kolorów
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false
            
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val color = colorsAdapter.getColorAt(position)
                colorViewModel.deleteColor(color)
                Snackbar.make(binding.root, R.string.color_deleted, Snackbar.LENGTH_SHORT).show()
            }
            
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
        
        itemTouchHelper.attachToRecyclerView(binding.colorsRecyclerView)
    }
    
    /**
     * Konfiguruje obserwatory LiveData dla kolorów i palet.
     */
    private fun setupObservers() {
        colorViewModel.allColors.observe(viewLifecycleOwner) { colors ->
            colorsAdapter.submitList(colors)
            binding.emptyText.visibility = if (colors.isEmpty()) View.VISIBLE else View.GONE
        }
        
        paletteViewModel.allPalettes.observe(viewLifecycleOwner) { palettes ->
            palettesAdapter.submitList(palettes)
        }
    }
    
    /**
     * Konfiguruje zakładki (kolory/palety).
     */
    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.colorsRecyclerView.visibility = View.VISIBLE
                        binding.palettesRecyclerView.visibility = View.GONE
                        binding.createPaletteFab.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
                    }
                    1 -> {
                        binding.colorsRecyclerView.visibility = View.GONE
                        binding.palettesRecyclerView.visibility = View.VISIBLE
                        binding.createPaletteFab.visibility = View.GONE
                    }
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    
    /**
     * Konfiguruje FAB do tworzenia palet.
     */
    private fun setupFab() {
        binding.createPaletteFab.setOnClickListener {
            val selectedColors = colorsAdapter.getSelectedColors()
            if (selectedColors.isEmpty()) {
                Snackbar.make(
                    binding.root,
                    R.string.no_colors_selected,
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                showCreatePaletteDialog(selectedColors.map { it.id })
            }
        }
    }
    
    /**
     * Konfiguruje menu toolbar.
     */
    private fun setupMenu() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_delete_all -> {
                    showDeleteAllDialog()
                    true
                }
                R.id.action_select_mode -> {
                    toggleSelectionMode()
                    true
                }
                else -> false
            }
        }
    }
    
    /**
     * Przełącza tryb wyboru kolorów.
     */
    private fun toggleSelectionMode() {
        isSelectionMode = !isSelectionMode
        colorsAdapter.setSelectionMode(isSelectionMode)
        binding.createPaletteFab.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
    }
    
    /**
     * Wyświetla dialog tworzenia palety.
     */
    private fun showCreatePaletteDialog(colorIds: List<Long>) {
        val input = EditText(requireContext())
        input.hint = getString(R.string.enter_palette_name)
        
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.create_palette)
            .setView(input)
            .setPositiveButton(R.string.yes) { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    val palette = ColorPalette(
                        name = name,
                        colorIds = colorIds
                    )
                    lifecycleScope.launch {
                        paletteViewModel.insertPalette(palette)
                        Snackbar.make(
                            binding.root,
                            R.string.palette_created,
                            Snackbar.LENGTH_SHORT
                        ).show()
                        toggleSelectionMode()
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    /**
     * Wyświetla dialog potwierdzenia usunięcia wszystkich kolorów.
     */
    private fun showDeleteAllDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_all)
            .setMessage(R.string.confirm_delete_all)
            .setPositiveButton(R.string.yes) { _, _ ->
                colorViewModel.deleteAllColors()
                Snackbar.make(
                    binding.root,
                    R.string.all_colors_deleted,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
    
    /**
     * Wyświetla dialog potwierdzenia usunięcia palety.
     */
    private fun showDeletePaletteDialog(palette: ColorPalette) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_color)
            .setMessage(R.string.confirm_delete_palette)
            .setPositiveButton(R.string.yes) { _, _ ->
                paletteViewModel.deletePalette(palette)
                Snackbar.make(
                    binding.root,
                    R.string.palette_deleted,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
