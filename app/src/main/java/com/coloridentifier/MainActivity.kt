package com.coloridentifier

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.coloridentifier.databinding.ActivityMainBinding

/**
 * MainActivity - główna aktywność aplikacji Color Identifier.
 * Zarządza nawigacją Bottom Navigation pomiędzy różnymi fragmentami aplikacji.
 * Używa Navigation Component do obsługi nawigacji między ekranami.
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    /**
     * Inicjalizacja aktywności.
     * Konfiguruje View Binding i Bottom Navigation z Navigation Component.
     *
     * @param savedInstanceState Stan zapisany aktywności
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicjalizacja View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Konfiguracja nawigacji
        setupNavigation()
    }
    
    /**
     * Konfiguruje Bottom Navigation z Navigation Component.
     * Łączy Bottom Navigation View z NavController dla automatycznej nawigacji.
     */
    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNavigation.setupWithNavController(navController)
    }
}
