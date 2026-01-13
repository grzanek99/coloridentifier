# Color Identifier - Aplikacja Android

Kompletna aplikacja mobilna do rozpoznawania kolorów na Androidzie, napisana w Kotlinie z wykorzystaniem architektury MVVM.

## Funkcjonalności

### 1. Rozpoznawanie kolorów
- **Z kamery**: Wykrywanie kolorów w czasie rzeczywistym z podglądu kamery (CameraX)
- **Z galerii**: Wybór obrazu z galerii i wykrywanie koloru przez dotyk
- **Czas rozpoznawania**: < 200ms

### 2. Informacje o kolorze
- Nazwa koloru po polsku (np. "Czerwony", "Limonkowy")
- Wartość HEX (np. "#FF0000")
- Wartość RGB (np. "255, 0, 0")
- Podgląd koloru

### 3. Paleta RGB
- Okrągła paleta HSV do wyboru koloru
- Suwak jasności (BrightnessSlideBar)
- Suwak przezroczystości (AlphaSlideBar)
- Wybór koloru w czasie rzeczywistym

### 4. Sloty kolorów
- 5 pionowych slotów do przechowywania kolorów
- Dialog wyboru koloru dla każdego slotu
- Wizualizacja wybranych kolorów

### 5. Zarządzanie kolorami
- Zapisywanie kolorów do lokalnej bazy danych (Room)
- Lista zapisanych kolorów (RecyclerView)
- Swipe-to-delete do usuwania kolorów
- Tworzenie palet z zaznaczonych kolorów
- Przeglądanie i usuwanie palet

### 6. Udostępnianie
- Kopiowanie wartości HEX do schowka
- Kopiowanie wartości RGB do schowka
- Udostępnianie koloru jako obraz (bitmap)
- Udostępnianie palety kolorów jako obraz

## Technologie

- **Język**: Kotlin
- **Min SDK**: API 26 (Android 8.0)
- **Target SDK**: API 33
- **Architektura**: MVVM
- **Baza danych**: Room (offline storage)
- **Nawigacja**: Navigation Component + Bottom Navigation
- **UI**: Material Design 3
- **Biblioteki**:
  - ColorPickerView 2.3.0 (skydoves)
  - CameraX
  - Room Database
  - Kotlin Coroutines
  - AndroidX Lifecycle

## Struktura projektu

```
app/src/main/java/com/coloridentifier/
├── MainActivity.kt                  # Główna aktywność z bottom navigation
├── data/
│   ├── database/
│   │   ├── AppDatabase.kt          # Konfiguracja Room DB
│   │   ├── ColorDao.kt             # DAO dla kolorów
│   │   └── PaletteDao.kt           # DAO dla palet
│   ├── model/
│   │   ├── SavedColor.kt           # Model koloru
│   │   └── ColorPalette.kt         # Model palety
│   └── repository/
│       └── ColorRepository.kt       # Repository pattern
├── ui/
│   ├── camera/
│   │   └── CameraFragment.kt       # Rozpoznawanie z kamery
│   ├── gallery/
│   │   └── GalleryFragment.kt      # Rozpoznawanie z galerii
│   ├── picker/
│   │   └── ColorPickerFragment.kt  # Paleta RGB
│   ├── slots/
│   │   └── ColorSlotsFragment.kt   # 5 slotów kolorów
│   ├── saved/
│   │   ├── SavedColorsFragment.kt  # Lista kolorów i palet
│   │   └── SavedColorsAdapter.kt   # Adapter RecyclerView
│   └── palette/
│       └── PaletteAdapter.kt        # Adapter dla palet
├── util/
│   ├── ColorNameMapper.kt          # Mapowanie RGB na nazwy
│   ├── ColorUtils.kt               # Narzędzia konwersji kolorów
│   └── ShareUtils.kt               # Udostępnianie i kopiowanie
└── viewmodel/
    ├── ColorViewModel.kt            # ViewModel dla kolorów
    └── PaletteViewModel.kt          # ViewModel dla palet
```

## Budowanie projektu

### Wymagania
- Android Studio Giraffe (2023.2.1) lub nowszy
- Android SDK 33
- JDK 17
- Gradle 8.2

### Instrukcje
1. Sklonuj repozytorium
2. Otwórz projekt w Android Studio
3. Synchronizuj Gradle (`File > Sync Project with Gradle Files`)
4. Podłącz urządzenie Android lub uruchom emulator
5. Uruchom aplikację (`Run > Run 'app'`)

## Uprawnienia

Aplikacja wymaga następujących uprawnień:
- `CAMERA` - dostęp do kamery urządzenia
- `READ_MEDIA_IMAGES` (Android 13+) - dostęp do galerii
- `READ_EXTERNAL_STORAGE` (Android 12 i niższe) - dostęp do galerii

Wszystkie uprawnienia są wymagane w runtime zgodnie z najlepszymi praktykami Android.

## Komentarze

Cały kod jest szczegółowo skomentowany po polsku zgodnie z wymaganiami:
- Opis każdej klasy/interfejsu
- Dokumentacja funkcji z parametrami i wartościami zwracanymi
- Komentarze inline dla skomplikowanej logiki

## Zgodność z wymaganiami

✅ Kotlin jako język główny  
✅ Min SDK API 26  
✅ Architektura MVVM  
✅ Room Database (offline)  
✅ ColorPickerView library  
✅ CameraX  
✅ Material Design 3  
✅ Bottom Navigation  
✅ Runtime permissions  
✅ Wszystkie funkcjonalności zaimplementowane  
✅ Komentarze po polsku  

## Licencja

Ten projekt został stworzony jako aplikacja demonstracyjna.
