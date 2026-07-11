package com.example

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.StarBorder
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.JameelNooriNastaleeq
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Enum to manage active app screens
enum class Screen {
    HOME, SETTINGS, PREVIEW, LIBRARY
}

class MainActivity : ComponentActivity() {

    private lateinit var sharedPrefs: SharedPreferences

    override fun attachBaseContext(newBase: Context) {
        var context = newBase
        try {
            // Read saved language and apply it as the locale context on startup
            val prefs = newBase.getSharedPreferences("shabiq_prefs", Context.MODE_PRIVATE)
            val lang = prefs.getString("lang", "ur") ?: "ur"
            val locale = Locale(lang)
            Locale.setDefault(locale)
            val config = Configuration(newBase.resources.configuration)
            config.setLocale(locale)
            config.setLayoutDirection(locale)
            context = newBase.createConfigurationContext(config)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sharedPrefs = getSharedPreferences("shabiq_prefs", Context.MODE_PRIVATE)

        // Preload typeface through native OS resource loader to prevent Compose lazy loading crashes
        try {
            val tf = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.jameel_noori_nastaleeq_real)
            if (tf != null) {
                com.example.ui.theme.JameelNooriNastaleeq = FontFamily(tf)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            MyApplicationTheme(darkTheme = true, dynamicColor = false) {
                var currentScreen by remember { mutableStateOf(Screen.HOME) }
                
                // Form States
                var hadithText by remember { mutableStateOf(sharedPrefs.getString("last_hadith", "") ?: "") }
                var referenceText by remember { mutableStateOf(sharedPrefs.getString("last_ref", "") ?: "") }
                var selectedTemplate by remember { mutableStateOf(sharedPrefs.getInt("selected_template", 0)) }
                var selectedSize by remember { mutableStateOf(PosterSize.STANDARD_PORTRAIT) }
                
                // Favorites and Recents States
                var favoriteTemplates by remember { mutableStateOf(loadFavorites(sharedPrefs)) }
                var recentTemplates by remember { mutableStateOf(loadRecents(sharedPrefs)) }
                
                // Library States
                var libraryFiles by remember { mutableStateOf<List<File>>(emptyList()) }
                var isLoadingLibrary by remember { mutableStateOf(false) }
                
                // Settings States
                var madrasahName by remember { mutableStateOf(sharedPrefs.getString("madrasah_name", "") ?: "") }
                var mohtamimName by remember { mutableStateOf(sharedPrefs.getString("mohtamim_name", "") ?: "") }
                var logoPath by remember { mutableStateOf(sharedPrefs.getString("logo_path", null)) }
                var appLanguage by remember { mutableStateOf(sharedPrefs.getString("lang", "ur") ?: "ur") }
                var hijriOffset by remember { mutableStateOf(sharedPrefs.getInt("hijri_offset", 0)) }

                // Poster State
                var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
                var isGenerating by remember { mutableStateOf(false) }

                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                fun refreshLibrary() {
                    scope.launch(Dispatchers.IO) {
                        isLoadingLibrary = true
                        val libraryDir = File(context.filesDir, "library")
                        val files = if (libraryDir.exists()) {
                            libraryDir.listFiles()?.filter { it.isFile && it.name.endsWith(".jpg") }
                                ?.sortedByDescending { it.lastModified() } ?: emptyList()
                        } else {
                            emptyList()
                        }
                        withContext(Dispatchers.Main) {
                            libraryFiles = files
                            isLoadingLibrary = false
                        }
                    }
                }

                // Save form text changes to persist draft state
                LaunchedEffect(hadithText, referenceText) {
                    sharedPrefs.edit().apply {
                        putString("last_hadith", hadithText)
                        putString("last_ref", referenceText)
                        apply()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Global Non-Editable App Footer shown only when not previewing the poster
                        if (currentScreen != Screen.PREVIEW) {
                            AppFooter(
                                onCallDeveloper = {
                                    try {
                                        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:03439106616")
                                        }
                                        context.startActivity(dialIntent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Could not open dialer", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.background,
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "screen_transition"
                        ) { screen ->
                            when (screen) {
                                Screen.HOME -> {
                                    HomeScreen(
                                        hadithText = hadithText,
                                        onHadithChange = { hadithText = it },
                                        referenceText = referenceText,
                                        onReferenceChange = { referenceText = it },
                                        selectedTemplate = selectedTemplate,
                                        onSelectTemplate = {
                                            selectedTemplate = it
                                            sharedPrefs.edit().putInt("selected_template", it).apply()
                                        },
                                        selectedSize = selectedSize,
                                        onSizeChange = { selectedSize = it },
                                        appLanguage = appLanguage,
                                        onLanguageChange = { lang ->
                                            if (lang != appLanguage) {
                                                sharedPrefs.edit().putString("lang", lang).apply()
                                                appLanguage = lang
                                                // Recreate Activity to apply new locale strings instantly
                                                recreate()
                                            }
                                        },
                                        favoriteTemplates = favoriteTemplates,
                                        onFavoriteToggle = { id ->
                                            val updated = favoriteTemplates.toMutableSet()
                                            if (updated.contains(id)) updated.remove(id) else updated.add(id)
                                            favoriteTemplates = updated
                                            saveFavorites(sharedPrefs, updated)
                                        },
                                        recentTemplates = recentTemplates,
                                        onNavigateLibrary = {
                                            refreshLibrary()
                                            currentScreen = Screen.LIBRARY
                                        },
                                        onGenerate = {
                                            if (hadithText.isBlank()) {
                                                Toast.makeText(context, context.getString(R.string.hadith_required), Toast.LENGTH_SHORT).show()
                                                return@HomeScreen
                                            }
                                            if (referenceText.isBlank()) {
                                                Toast.makeText(context, context.getString(R.string.reference_required), Toast.LENGTH_SHORT).show()
                                                return@HomeScreen
                                            }
                                            
                                            isGenerating = true
                                            scope.launch(Dispatchers.Default) {
                                                val bitmap = generatePosterBitmap(
                                                    context = context,
                                                    hadithArabic = hadithText,
                                                    referenceUrdu = referenceText,
                                                    madrasahName = madrasahName,
                                                    mohtamimName = mohtamimName,
                                                    logoPath = logoPath,
                                                    templateId = selectedTemplate,
                                                    posterSize = selectedSize
                                                )
                                                
                                                // Automatically save every generated JPG
                                                saveBitmapToLibrary(context, bitmap)
                                                saveBitmapToGallery(context, bitmap)
                                                
                                                withContext(Dispatchers.Main) {
                                                     // Update recent templates
                                                     recentTemplates = addTemplateToRecents(sharedPrefs, selectedTemplate)
                                                     generatedBitmap = bitmap
                                                     isGenerating = false
                                                     currentScreen = Screen.PREVIEW
                                                }
                                            }
                                        },
                                        onNavigateSettings = { currentScreen = Screen.SETTINGS }
                                    )
                                }
                                Screen.SETTINGS -> {
                                    SettingsScreen(
                                        madrasahName = madrasahName,
                                        mohtamimName = mohtamimName,
                                        logoPath = logoPath,
                                        hijriOffset = hijriOffset,
                                        onSave = { newMadrasah, newMohtamim, newLogoUri, isLogoCleared, newHijriOffset ->
                                            scope.launch(Dispatchers.IO) {
                                                val editor = sharedPrefs.edit()
                                                
                                                // Save madrasah name
                                                madrasahName = newMadrasah
                                                editor.putString("madrasah_name", newMadrasah)
                                                
                                                // Save mohtamim name
                                                mohtamimName = newMohtamim
                                                editor.putString("mohtamim_name", newMohtamim)

                                                // Save hijri offset
                                                hijriOffset = newHijriOffset
                                                editor.putInt("hijri_offset", newHijriOffset)
                                                
                                                // Handle logo
                                                if (isLogoCleared) {
                                                    logoPath = null
                                                    editor.remove("logo_path")
                                                    
                                                    // Purge logo files
                                                    context.filesDir.listFiles()?.forEach { file ->
                                                        if (file.name.startsWith("madrasah_logo_")) {
                                                            file.delete()
                                                        }
                                                    }
                                                } else if (newLogoUri != null) {
                                                    val localPath = saveLogoToInternalStorage(context, newLogoUri)
                                                    if (localPath != null) {
                                                        logoPath = localPath
                                                        editor.putString("logo_path", localPath)
                                                    }
                                                }
                                                
                                                editor.apply()
                                                
                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(context, "ترتیبات کامیابی سے محفوظ ہو گئیں", Toast.LENGTH_SHORT).show()
                                                    currentScreen = Screen.HOME
                                                }
                                            }
                                        },
                                        onResetSettings = {
                                            madrasahName = ""
                                            mohtamimName = ""
                                            logoPath = null
                                            hijriOffset = 0
                                            sharedPrefs.edit().apply {
                                                remove("madrasah_name")
                                                remove("mohtamim_name")
                                                remove("logo_path")
                                                remove("hijri_offset")
                                                apply()
                                            }
                                            // Purge secure sandbox logo files as well
                                            scope.launch(Dispatchers.IO) {
                                                context.filesDir.listFiles()?.forEach { file ->
                                                    if (file.name.startsWith("madrasah_logo_")) {
                                                        file.delete()
                                                    }
                                                }
                                            }
                                        },
                                        onBack = { currentScreen = Screen.HOME }
                                    )
                                }
                                Screen.PREVIEW -> {
                                    PreviewScreen(
                                        bitmap = generatedBitmap,
                                        onDownload = {
                                            generatedBitmap?.let { bmp ->
                                                val uri = saveBitmapToGallery(context, bmp)
                                                if (uri != null) {
                                                    Toast.makeText(context, context.getString(R.string.download_success), Toast.LENGTH_LONG).show()
                                                } else {
                                                    Toast.makeText(context, context.getString(R.string.download_failed), Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        },
                                        onShare = {
                                            generatedBitmap?.let { bmp ->
                                                shareBitmap(context, bmp)
                                            }
                                        },
                                        onHome = { currentScreen = Screen.HOME }
                                    )
                                }
                                Screen.LIBRARY -> {
                                    LibraryScreen(
                                        files = libraryFiles,
                                        isLoading = isLoadingLibrary,
                                        onBack = { currentScreen = Screen.HOME },
                                        onShare = { file -> shareLibraryFile(context, file) },
                                        onDelete = { file ->
                                            file.delete()
                                            refreshLibrary()
                                            Toast.makeText(context, context.getString(R.string.delete_success), Toast.LENGTH_SHORT).show()
                                        },
                                        onRefresh = { refreshLibrary() }
                                    )
                                }
                            }
                        }

                        if (isGenerating) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .clickable(enabled = false) {},
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(R.string.generate_poster),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        androidx.compose.material3.CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// HOME SCREEN COMPOSABLE
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    hadithText: String,
    onHadithChange: (String) -> Unit,
    referenceText: String,
    onReferenceChange: (String) -> Unit,
    selectedTemplate: Int,
    onSelectTemplate: (Int) -> Unit,
    selectedSize: PosterSize,
    onSizeChange: (PosterSize) -> Unit,
    appLanguage: String,
    onLanguageChange: (String) -> Unit,
    favoriteTemplates: Set<Int>,
    onFavoriteToggle: (Int) -> Unit,
    recentTemplates: List<Int>,
    onNavigateLibrary: () -> Unit,
    onGenerate: () -> Unit,
    onNavigateSettings: () -> Unit
) {
    var showTemplateDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Custom Styled App Bar with Logo and Theme Colors
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.img_shabiq_logo),
                        contentDescription = "App Icon",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.5.sp
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = onNavigateLibrary,
                    modifier = Modifier.testTag("library_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "My Library",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = onNavigateSettings,
                    modifier = Modifier.testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Canva-style Welcome Hero Banner
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "خوش آمدید",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "شبیق اسٹوڈیو میں آپ کا استقبال ہے! نیچے دیئے گئے خانوں میں مواد لکھیں اور سیکنڈوں میں خوبصورت اسلامی ڈیزائن تیار کریں۔",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontSize = 13.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Right,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Language Selection Bar
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                    
                    LanguagePill(
                        label = "اردو",
                        isSelected = appLanguage == "ur",
                        onClick = { onLanguageChange("ur") }
                    )
                    LanguagePill(
                        label = "العربية",
                        isSelected = appLanguage == "ar",
                        onClick = { onLanguageChange("ar") }
                    )
                    LanguagePill(
                        label = "English",
                        isSelected = appLanguage == "en",
                        onClick = { onLanguageChange("en") }
                    )
                }
            }

            // Input 1: Arabic Hadith
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.hadith_hint),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = hadithText,
                        onValueChange = onHadithChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("hadith_input"),
                        placeholder = { Text("قَالَ رَسُولُ اللَّهِ ﷺ: ...", color = Color.Gray.copy(alpha = 0.5f)) },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 18.sp,
                            textAlign = TextAlign.Right,
                            fontFamily = FontFamily.Serif
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = Color.Black.copy(alpha = 0.2f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 6
                    )
                }
            }

            // Input 2: Urdu/Other Reference
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.reference_hint),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = referenceText,
                        onValueChange = onReferenceChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reference_input"),
                        placeholder = { Text("صحیح بخاری: 3461", color = Color.Gray.copy(alpha = 0.5f)) },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            textAlign = TextAlign.Right
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = Color.Black.copy(alpha = 0.2f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                }
            }

            // Design Template Selection Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "ڈیزائن کا انتخاب / Choose Design Template",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.2f))
                            .clickable { showTemplateDialog = true }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val thumbRes = when (selectedTemplate) {
                            0 -> R.drawable.img_bg_emerald
                            1 -> R.drawable.img_bg_indigo
                            else -> R.drawable.img_bg_cream
                        }
                        Image(
                            painter = painterResource(thumbRes),
                            contentDescription = "Selected Template",
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            val currentTemplate = TemplateRegistry.templates.find { it.id == selectedTemplate }
                                ?: TemplateRegistry.templates.first()
                            Text(
                                text = currentTemplate.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "کلک کر کے دوسرا تھیم منتخب کریں",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Choose",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Poster Size Section Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "پوسٹر کا سائز / Poster Size",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PosterSize.values().forEach { sizeOption ->
                            val isSizeSelected = selectedSize == sizeOption
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSizeSelected) MaterialTheme.colorScheme.primary
                                        else Color.Black.copy(alpha = 0.15f)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSizeSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { onSizeChange(sizeOption) }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = when (sizeOption) {
                                            PosterSize.STANDARD_PORTRAIT -> "Standard"
                                            PosterSize.STORY_PORTRAIT -> "Story"
                                            PosterSize.A4_PRINT -> "A4 Print"
                                        },
                                        color = if (isSizeSelected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = when (sizeOption) {
                                            PosterSize.STANDARD_PORTRAIT -> "1080x1350"
                                            PosterSize.STORY_PORTRAIT -> "1080x1920"
                                            PosterSize.A4_PRINT -> "300 DPI"
                                        },
                                        color = if (isSizeSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Actions section
            Spacer(modifier = Modifier.height(4.dp))

            // Primary Generate Design Button (Canva styling)
            Button(
                onClick = onGenerate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("generate_design_button"),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.generate_poster),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Template Selector Dialog
    if (showTemplateDialog) {
        Dialog(onDismissRequest = { showTemplateDialog = false }) {
            val dialogCategories = listOf("Favorites", "Recents") + TemplateRegistry.CATEGORIES
            var activeCategory by remember { mutableStateOf("Favorites") }
            val filteredTemplates = remember(activeCategory, favoriteTemplates, recentTemplates) {
                when (activeCategory) {
                    "Favorites" -> {
                        TemplateRegistry.templates.filter { favoriteTemplates.contains(it.id) }
                    }
                    "Recents" -> {
                        recentTemplates.mapNotNull { id -> TemplateRegistry.templates.find { it.id == id } }
                    }
                    else -> {
                        TemplateRegistry.templates.filter { it.category == activeCategory }
                    }
                }
            }
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.select_template_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Categories smooth gallery
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(dialogCategories) { cat ->
                            val label = when (cat) {
                                "Favorites" -> stringResource(R.string.favorites)
                                "Recents" -> stringResource(R.string.recents)
                                else -> cat
                            }
                            CategoryPill(
                                label = label,
                                isSelected = activeCategory == cat,
                                onClick = { activeCategory = cat }
                            )
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.padding(bottom = 16.dp))

                    // 1500 Template Options filtered by category
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (filteredTemplates.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.no_posters_found),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            items(filteredTemplates) { template ->
                                TemplateItem(
                                    template = template,
                                    isSelected = selectedTemplate == template.id,
                                    isFavorite = favoriteTemplates.contains(template.id),
                                    onFavoriteToggle = { onFavoriteToggle(template.id) },
                                    onClick = {
                                        onSelectTemplate(template.id)
                                        showTemplateDialog = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        text = stringResource(android.R.string.cancel),
                        onClick = { showTemplateDialog = false },
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun LanguagePill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
fun CategoryPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
        )
    }
}

@Composable
fun TemplateItem(
    template: PosterTemplate,
    isSelected: Boolean,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (template.bgResId != null) {
                Image(
                    painter = painterResource(template.bgResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(template.bgStartColor), Color(template.bgEndColor))
                            )
                        )
                        .border(1.dp, Color(template.goldColor).copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "شبیق",
                        color = Color(template.textColor),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = template.name,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp
                )
                Text(
                    text = template.category,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
            
            // Favorite star toggle
            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier.testTag("favorite_button_${template.id}")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Toggle Favorite",
                    tint = if (isFavorite) Color(0xFFFFD700) else Color.Gray.copy(alpha = 0.6f)
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// ----------------------------------------------------
// SETTINGS SCREEN COMPOSABLE
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    madrasahName: String,
    mohtamimName: String,
    logoPath: String?,
    hijriOffset: Int,
    onSave: (String, String, Uri?, Boolean, Int) -> Unit,
    onResetSettings: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var draftMadrasahName by remember { mutableStateOf(madrasahName) }
    var draftMohtamimName by remember { mutableStateOf(mohtamimName) }
    var draftLogoPath by remember { mutableStateOf(logoPath) }
    var draftLogoUri by remember { mutableStateOf<Uri?>(null) }
    var isLogoCleared by remember { mutableStateOf(false) }
    var draftHijriOffset by remember { mutableStateOf(hijriOffset) }

    // ActivityResultLauncher for Photo Picker (Zero Permissions Required on Android 13+)
    val pickLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            draftLogoUri = uri
            isLogoCleared = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.settings_title),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // "Only three editable options:" Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.editable_options_title),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 14.sp
                    )
                }
            }

            // Option 1: Madrasah Name
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1. " + stringResource(R.string.madrasah_name),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = draftMadrasahName,
                        onValueChange = { draftMadrasahName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("madrasah_name_input"),
                        placeholder = { Text("جامعہ اسلامیہ ...", color = Color.Gray.copy(alpha = 0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        singleLine = true
                    )
                }
            }

            // Option 2: Mohtamim Name
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2. " + stringResource(R.string.mohtamim_name),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = draftMohtamimName,
                        onValueChange = { draftMohtamimName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mohtamim_name_input"),
                        placeholder = { Text("حضرت مولانا ...", color = Color.Gray.copy(alpha = 0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
),
                        singleLine = true
                    )
                }
            }

            // Option 3: Change Logo from Gallery
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3. " + stringResource(R.string.change_logo),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Logo Preview if exists or selected
                    val logoModel = if (isLogoCleared) null else (draftLogoUri ?: if (!draftLogoPath.isNullOrEmpty() && File(draftLogoPath!!).exists()) File(draftLogoPath!!) else null)
                    if (logoModel != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = logoModel,
                                contentDescription = "Logo Thumbnail",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Logo Active",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (draftLogoUri != null) "Selected from Gallery (Draft)" else "Saved in Secure App Storage",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            IconButton(onClick = {
                                draftLogoUri = null
                                draftLogoPath = null
                                isLogoCleared = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Logo",
                                    tint = Color.Red.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.logo_not_set),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                fontStyle = FontStyle.Italic
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Select/Change Logo Button
                    Button(
                        onClick = {
                            pickLogoLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("change_logo_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.change_logo),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Option 4: Hijri Date Correction (Moon Sighting Offset)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "4. ہجری تاریخ درست کریں / Hijri Correction",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "اگر ہجری تاریخ میں ایک یا دو دن کا فرق ہو تو یہاں سے درست کریں (مثلاً چاند کے حساب سے)۔",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(-2, -1, 0, 1, 2).forEach { offset ->
                            val label = when {
                                offset < 0 -> "$offset دن"
                                offset > 0 -> "+$offset دن"
                                else -> "عام (0)"
                            }
                            val isSelected = draftHijriOffset == offset
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                    .clickable { draftHijriOffset = offset }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Option 5: Reset Settings
            var showResetDialog by remember { mutableStateOf(false) }

            if (showResetDialog) {
                AlertDialog(
                    onDismissRequest = { showResetDialog = false },
                    title = { 
                        Text(
                            text = stringResource(R.string.reset_settings), 
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ) 
                    },
                    text = { Text(text = stringResource(R.string.reset_confirm)) },
                    confirmButton = {
                        Button(
                            onClick = {
                                showResetDialog = false
                                onResetSettings()
                                draftMadrasahName = ""
                                draftMohtamimName = ""
                                draftLogoPath = null
                                draftLogoUri = null
                                isLogoCleared = false
                                draftHijriOffset = 0
                                Toast.makeText(context, context.getString(R.string.reset_success), Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text(text = "Yes")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showResetDialog = false }) {
                            Text(text = "No")
                        }
                    }
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "5. " + stringResource(R.string.reset_settings),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left side: Reset (Outlined red button, compact width)
                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("reset_settings_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ری سیٹ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        // Right side: Save (Larger green filled button, 40% wider)
                        Button(
                            onClick = {
                                onSave(draftMadrasahName, draftMohtamimName, draftLogoUri, isLogoCleared, draftHijriOffset)
                            },
                            modifier = Modifier
                                .weight(1.4f)
                                .height(48.dp)
                                .testTag("save_settings_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Save",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// PREVIEW SCREEN COMPOSABLE
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    bitmap: Bitmap?,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    onHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF04291D)) // Explicit dark emerald background for immersive viewer experience
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.preview),
                    fontFamily = JameelNooriNastaleeq,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFFD4AF37) // Beautiful Shabiq Islamic Gold
                )
            },
            navigationIcon = {
                IconButton(onClick = onHome) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFD4AF37)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Immersive poster presentation box with ZERO white padding
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Generated Hadith Poster",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, Color(0xFFD4AF37).copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF04291D)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Rendering Failed",
                            color = Color.Red,
                            fontFamily = JameelNooriNastaleeq
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row: Share JPG ("شیئر کریں"), Save JPG ("محفوظ کریں")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Share JPG (Outlined Button: "شیئر کریں" with gold/yellow border and theme-matching text)
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("share_button"),
                    shape = RoundedCornerShape(27.dp),
                    border = BorderStroke(2.dp, Color(0xFFD4AF37)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFD4AF37)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFFD4AF37)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "شیئر کریں",
                        fontFamily = JameelNooriNastaleeq,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFFD4AF37)
                    )
                }

                // Save JPG (Primary Green Button: "محفوظ کریں" in Shabiq brand green)
                Button(
                    onClick = onDownload,
                    modifier = Modifier
                        .weight(1.2f) // slightly wider for primary emphasis
                        .height(54.dp)
                        .testTag("download_button"),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32) // Shabiq primary poster green
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "محفوظ کریں",
                        fontFamily = JameelNooriNastaleeq,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }
            }

            // Generate Another (Third Button: "ایک اور بنائیں" in Gold accent)
            Button(
                onClick = onHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(bottom = 8.dp)
                    .testTag("generate_another_button"),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD4AF37), // Rich gold color matching branding
                    contentColor = Color(0xFF04291D) // Dark text for elegant contrast
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF04291D)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ایک اور بنائیں",
                    fontFamily = JameelNooriNastaleeq,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF04291D)
                )
            }
        }
    }
}

// ----------------------------------------------------
// DYNAMIC COMPONENT HELPERS & EXPORTERS
// ----------------------------------------------------

@Composable
fun AppFooter(onCallDeveloper: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.rights_reserved),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = stringResource(R.string.developer_name),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(3.dp))
            
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onCallDeveloper() }
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call Developer",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.developer_phone),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    color: Color
) {
    Text(
        text = text.uppercase(),
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

// Persist Selected Logo into Local Sandbox Directory (avoid permissions expiring!)
fun saveLogoToInternalStorage(context: Context, sourceUri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
        val fileName = "madrasah_logo_${System.currentTimeMillis()}.png"
        val file = File(context.filesDir, fileName)

        // Purge pre-existing logo file to free space
        context.filesDir.listFiles()?.forEach {
            if (it.name.startsWith("madrasah_logo_")) {
                it.delete()
            }
        }

        val outputStream = FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Helper functions for precise, high-fidelity Islamic and Gregorian date localization
private fun getWeekdayName(dayOfWeek: Int, lang: String): String {
    return when (lang) {
        "ur" -> when (dayOfWeek) {
            1 -> "پیر"
            2 -> "منگل"
            3 -> "بدھ"
            4 -> "جمعرات"
            5 -> "جمعہ"
            6 -> "ہفتہ"
            7 -> "اتوار"
            else -> ""
        }
        "ar" -> when (dayOfWeek) {
            1 -> "الإثنين"
            2 -> "الثلاثاء"
            3 -> "الأربعاء"
            4 -> "الخميس"
            5 -> "الجمعة"
            6 -> "السبت"
            7 -> "الأحد"
            else -> ""
        }
        else -> when (dayOfWeek) {
            1 -> "MONDAY"
            2 -> "TUESDAY"
            3 -> "WEDNESDAY"
            4 -> "THURSDAY"
            5 -> "FRIDAY"
            6 -> "SATURDAY"
            7 -> "SUNDAY"
            else -> ""
        }
    }
}

private fun getGregMonthName(monthValue: Int, lang: String): String {
    return when (lang) {
        "ur" -> when (monthValue) {
            1 -> "جنوری"
            2 -> "فروری"
            3 -> "مارچ"
            4 -> "اپریل"
            5 -> "مئی"
            6 -> "جون"
            7 -> "جولائی"
            8 -> "اگست"
            9 -> "ستمبر"
            10 -> "اکتوبر"
            11 -> "نومبر"
            12 -> "دسمبر"
            else -> ""
        }
        "ar" -> when (monthValue) {
            1 -> "يناير"
            2 -> "فبراير"
            3 -> "مارس"
            4 -> "أبريل"
            5 -> "مايو"
            6 -> "يونيو"
            7 -> "يوليو"
            8 -> "أغسطس"
            9 -> "سبتمبر"
            10 -> "أكتوبر"
            11 -> "نوفمبر"
            12 -> "ديسمبر"
            else -> ""
        }
        else -> when (monthValue) {
            1 -> "JANUARY"
            2 -> "FEBRUARY"
            3 -> "MARCH"
            4 -> "APRIL"
            5 -> "MAY"
            6 -> "JUNE"
            7 -> "JULY"
            8 -> "AUGUST"
            9 -> "SEPTEMBER"
            10 -> "OCTOBER"
            11 -> "NOVEMBER"
            12 -> "DECEMBER"
            else -> ""
        }
    }
}

private fun getHijriMonthName(monthValue: Int, lang: String): String {
    return when (lang) {
        "ur" -> when (monthValue) {
            1 -> "محرم"
            2 -> "صفر"
            3 -> "ربیع الاول"
            4 -> "ربیع الثانی"
            5 -> "جمادی الاول"
            6 -> "جمادی الثانی"
            7 -> "رجب"
            8 -> "شعبان"
            9 -> "رمضان"
            10 -> "شوال"
            11 -> "ذی القعدہ"
            12 -> "ذی الحجہ"
            else -> ""
        }
        "ar" -> when (monthValue) {
            1 -> "محرم"
            2 -> "صفر"
            3 -> "ربيع الأول"
            4 -> "ربيع الآخر"
            5 -> "جمادى الأولى"
            6 -> "جمادى الآخرة"
            7 -> "رجب"
            8 -> "شعبان"
            9 -> "رمضان"
            10 -> "شوال"
            11 -> "ذو القعدة"
            12 -> "ذو الحجة"
            else -> ""
        }
        else -> when (monthValue) {
            1 -> "Muharram"
            2 -> "Safar"
            3 -> "Rabi' al-Awwal"
            4 -> "Rabi' ath-Thani"
            5 -> "Jumada al-Awwal"
            6 -> "Jumada ath-Thani"
            7 -> "Rajab"
            8 -> "Sha'ban"
            9 -> "Ramadan"
            10 -> "Shawwal"
            11 -> "Dhu al-Qi'dah"
            12 -> "Dhu al-Hijjah"
            else -> ""
        }
    }
}

private class PosterTheme(
    val mainBackgroundRes: Int,
    val rightPanelBgColor: Int,
    val cardBgColor: Int,
    val strokeColor: Int,
    val badgeBgColor: Int,
    val badgeTextColor: Int,
    val headerBannerBgColor: Int,
    val headerBannerStrokeColor: Int,
    val cardTextColor: Int,
    val cardAccentColor: Int,
    val hadithTextColor: Int,
    val referencePillBgColor: Int,
    val referencePillStrokeColor: Int,
    val referencePillTextColor: Int,
    val dividerColor: Int,
    val organizerTextColor: Int,
    val madrasahTextColor: Int,
    val hadithCardBgColor: Int,
    val hadithCardStrokeColor: Int
)

// Native JPG Poster Compiler from R.layout.activity_poster
@SuppressLint("NewApi")
fun generatePosterBitmap(
    context: Context,
    hadithArabic: String,
    referenceUrdu: String,
    madrasahName: String,
    mohtamimName: String,
    logoPath: String?,
    templateId: Int,
    posterSize: PosterSize
): Bitmap {
    val width = posterSize.width
    val height = posterSize.height
    
    // Create the target bitmap
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Inflate our XML layout
    val inflater = LayoutInflater.from(context)
    val view = inflater.inflate(R.layout.activity_poster, null)

    // Layout params for explicit size
    view.layoutParams = ViewGroup.LayoutParams(width, height)

    // Find and populate Left Content Area views
    val tvHadith = view.findViewById<TextView>(R.id.tv_hadith)
    val tvReference = view.findViewById<TextView>(R.id.tv_reference)
    val tvMadrasah = view.findViewById<TextView>(R.id.tv_madrasah)
    val tvOrganizer = view.findViewById<TextView>(R.id.tv_organizer)
    val ivLogo = view.findViewById<ImageView>(R.id.iv_logo)
    val tvLogoFallback = view.findViewById<TextView>(R.id.tv_logo_fallback)

    // Find and populate Date Panel views
    val tvHijriMonth = view.findViewById<TextView>(R.id.tv_hijri_month)
    val tvHijriDay = view.findViewById<TextView>(R.id.tv_hijri_day)
    val tvHijriYear = view.findViewById<TextView>(R.id.tv_hijri_year)
    val tvGregMonth = view.findViewById<TextView>(R.id.tv_greg_month)
    val tvGregDay = view.findViewById<TextView>(R.id.tv_greg_day)
    val tvGregYear = view.findViewById<TextView>(R.id.tv_greg_year)
    val tvDayName = view.findViewById<TextView>(R.id.tv_day_name)

    // Find card header views for dynamic localization
    val tvHijriMonthHeader = view.findViewById<TextView>(R.id.tv_hijri_month_header)
    val tvHijriDayHeader = view.findViewById<TextView>(R.id.tv_hijri_day_header)
    val tvGregMonthHeader = view.findViewById<TextView>(R.id.tv_greg_month_header)
    val tvGregDayHeader = view.findViewById<TextView>(R.id.tv_greg_day_header)

    // Set Madrasah Name
    val sharedPrefs = context.getSharedPreferences("shabiq_prefs", Context.MODE_PRIVATE)
    val currentLang = sharedPrefs.getString("lang", "ur") ?: "ur"
    val defaultMadrasah = when (currentLang) {
        "ar" -> "الجامعة الإسلامية لدار العلوم"
        "ur" -> "جامعہ اسلامیہ دارالعلوم"
        else -> "Jamia Islamia Darul Uloom"
    }
    tvMadrasah.text = madrasahName.ifEmpty { defaultMadrasah }

    // Set Card Header Labels
    val labelIslamicMonth = when (currentLang) {
        "ar" -> "الشهر الهجري"
        "ur" -> "اسلامی مہینہ"
        else -> "ISLAMIC MONTH"
    }
    val labelIslamicDate = when (currentLang) {
        "ar" -> "التاريخ الهجري"
        "ur" -> "اسلامی تاریخ"
        else -> "ISLAMIC DATE"
    }
    val labelGregMonth = when (currentLang) {
        "ar" -> "الشهر الميلادي"
        "ur" -> "انگریزی مہینہ"
        else -> "ENGLISH MONTH"
    }
    val labelGregDate = when (currentLang) {
        "ar" -> "التاريخ الميلادي"
        "ur" -> "انگریزی تاریخ"
        else -> "ENGLISH DATE"
    }

    tvHijriMonthHeader.text = labelIslamicMonth
    tvHijriDayHeader.text = labelIslamicDate
    tvGregMonthHeader.text = labelGregMonth
    tvGregDayHeader.text = labelGregDate

    // Set Organizer Name Only (No prefix labels)
    val nameToShow = mohtamimName.ifEmpty {
        when (currentLang) {
            "ar" -> "فضيلة الشيخ محمد شرف الدین"
            "ur" -> "حضرت مولانا مفتی محمد شرف الدین"
            else -> "Maulana Muhammad Sharafuddin"
        }
    }
    val cleanName = nameToShow
        .replace("مہتمم", "")
        .replace("سرپرست", "")
        .replace("المهتمم", "")
        .replace("Mohtamim", "")
        .replace(":", "")
        .replace("-", "")
        .trim()
    tvOrganizer.text = cleanName

    // Set Hadith Text and Reference
    val finalHadith = hadithArabic.ifEmpty { "قَالَ رَسُولُ اللَّهِ ﷺ: بَلِّغُوا عَنِّي وَلَوْ آيَةً" }
    val finalRef = referenceUrdu.ifEmpty { "صحیح بخاری: 3461" }
    tvHadith.text = finalHadith
    tvReference.text = finalRef

    // Handle Logo Loading
    var logoBmp: Bitmap? = null
    if (!logoPath.isNullOrEmpty()) {
        try {
            logoBmp = BitmapFactory.decodeFile(logoPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    if (logoBmp != null) {
        ivLogo.setImageBitmap(logoBmp)
        ivLogo.visibility = View.VISIBLE
        tvLogoFallback.visibility = View.GONE
    } else {
        // Use default logo image if available, else fallback text
        var fallbackBmp: Bitmap? = null
        try {
            fallbackBmp = BitmapFactory.decodeResource(context.resources, R.drawable.img_shabiq_logo)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (fallbackBmp != null) {
            ivLogo.setImageBitmap(fallbackBmp)
            ivLogo.visibility = View.VISIBLE
            tvLogoFallback.visibility = View.GONE
        } else {
            ivLogo.visibility = View.GONE
            tvLogoFallback.visibility = View.VISIBLE
        }
    }

    // Dynamic Dates Setup using manual mapping for perfect formatting & timezone sync via IslamicDateHelper
    val hijriOffset = sharedPrefs.getInt("hijri_offset", 0)
    val dateData = IslamicDateHelper.calculateDatePanelData(context, hijriOffset, currentLang)

    tvHijriMonth.text = dateData.hijriMonth
    tvHijriDay.text = dateData.hijriDay
    tvHijriYear.text = dateData.hijriYear

    tvGregMonth.text = dateData.gregMonth
    tvGregDay.text = dateData.gregDay
    tvGregYear.text = dateData.gregYear

    tvDayName.text = dateData.dayName
    val tvDayNameEnglish = view.findViewById<TextView>(R.id.tv_day_name_english)
    if (tvDayNameEnglish != null) {
        tvDayNameEnglish.text = dateData.dayNameEnglish
        tvDayNameEnglish.typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL)
    }

    // Helper to detect if a text block is primary Urdu vs Arabic
    fun isUrduText(text: String): Boolean {
        val urduSpecific = charArrayOf('ٹ', 'ڈ', 'ڑ', 'ں', 'ے', 'پ', 'چ', 'ژ', 'گ', 'ک', 'ی', 'ھ')
        val arabicSpecific = charArrayOf('ة', 'ي', 'ك')
        var urduScore = 0
        var arabicScore = 0
        for (char in text) {
            if (char in urduSpecific) urduScore += 2
            if (char in arabicSpecific) arabicScore += 2
            if (char.code in 0x064B..0x0652) {
                arabicScore += 1
            }
        }
        return urduScore >= arabicScore
    }

    // Apply custom Nastaleeq font to all text views programmatically for beautiful Urdu calligraphy
    try {
        val customTypeface = androidx.core.content.res.ResourcesCompat.getFont(context, R.font.jameel_noori_nastaleeq_real)
        if (customTypeface != null) {
            // Font switching based on text script as requested
            if (isUrduText(finalHadith)) {
                tvHadith.typeface = customTypeface
            } else {
                tvHadith.typeface = android.graphics.Typeface.create("serif", android.graphics.Typeface.BOLD)
            }
            
            tvReference.typeface = customTypeface
            tvMadrasah.typeface = customTypeface
            tvOrganizer.typeface = customTypeface
            tvHijriMonth.typeface = customTypeface
            tvHijriDay.typeface = customTypeface
            tvHijriYear.typeface = customTypeface
            tvGregMonth.typeface = customTypeface
            tvGregDay.typeface = customTypeface
            tvGregYear.typeface = customTypeface
            tvDayName.typeface = customTypeface
            view.findViewById<TextView>(R.id.tv_greeting_banner)?.typeface = customTypeface
            view.findViewById<TextView>(R.id.tv_brand_lbl)?.typeface = customTypeface
            view.findViewById<TextView>(R.id.tv_lesson_title)?.typeface = customTypeface
            view.findViewById<TextView>(R.id.tv_today_label)?.typeface = customTypeface
            view.findViewById<TextView>(R.id.tv_day_badge_text)?.typeface = customTypeface
            view.findViewById<TextView>(R.id.tv_logo_fallback)?.typeface = customTypeface
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }


    // Layout scaling scaling factors for pixel-perfect rendering across standard/high-def modes
    val scale = width.toFloat() / 1080f
    fun scalePx(valDp: Float): Int = (valDp * scale).toInt()

    // Dynamic thematic styling configuration based on selected Template ID
    val theme = when (templateId) {
        1 -> { // Midnight Indigo
            PosterTheme(
                mainBackgroundRes = R.drawable.img_bg_indigo,
                rightPanelBgColor = android.graphics.Color.parseColor("#0B0E21"),
                cardBgColor = android.graphics.Color.parseColor("#171B3E"),
                strokeColor = android.graphics.Color.parseColor("#4B5E91"), // muted rose gold/silver
                badgeBgColor = android.graphics.Color.parseColor("#D0D5EE"),
                badgeTextColor = android.graphics.Color.parseColor("#0B0E21"),
                headerBannerBgColor = android.graphics.Color.parseColor("#0F1332"),
                headerBannerStrokeColor = android.graphics.Color.parseColor("#4B5E91"),
                cardTextColor = android.graphics.Color.parseColor("#FFFFFF"),
                cardAccentColor = android.graphics.Color.parseColor("#CBD2FA"),
                hadithTextColor = android.graphics.Color.parseColor("#0F1332"),
                referencePillBgColor = android.graphics.Color.parseColor("#171B3E"),
                referencePillStrokeColor = android.graphics.Color.parseColor("#4B5E91"),
                referencePillTextColor = android.graphics.Color.parseColor("#CBD2FA"),
                dividerColor = android.graphics.Color.parseColor("#4B5E91"),
                organizerTextColor = android.graphics.Color.parseColor("#0F1332"),
                madrasahTextColor = android.graphics.Color.parseColor("#CBD2FA"),
                hadithCardBgColor = android.graphics.Color.parseColor("#F5F7FF"), // clean white backing
                hadithCardStrokeColor = android.graphics.Color.parseColor("#CBD2FA")
            )
        }
        2 -> { // Beige Minimalist (Cream)
            PosterTheme(
                mainBackgroundRes = R.drawable.img_bg_cream,
                rightPanelBgColor = android.graphics.Color.parseColor("#2D1F16"),
                cardBgColor = android.graphics.Color.parseColor("#4A3728"),
                strokeColor = android.graphics.Color.parseColor("#C5A059"),
                badgeBgColor = android.graphics.Color.parseColor("#C5A059"),
                badgeTextColor = android.graphics.Color.parseColor("#2D1F16"),
                headerBannerBgColor = android.graphics.Color.parseColor("#3E2A1C"),
                headerBannerStrokeColor = android.graphics.Color.parseColor("#C5A059"),
                cardTextColor = android.graphics.Color.parseColor("#FFF8F0"),
                cardAccentColor = android.graphics.Color.parseColor("#F5E6CC"),
                hadithTextColor = android.graphics.Color.parseColor("#2B1E16"),
                referencePillBgColor = android.graphics.Color.parseColor("#4A3728"),
                referencePillStrokeColor = android.graphics.Color.parseColor("#C5A059"),
                referencePillTextColor = android.graphics.Color.parseColor("#F5E6CC"),
                dividerColor = android.graphics.Color.parseColor("#C5A059"),
                organizerTextColor = android.graphics.Color.parseColor("#2B1E16"),
                madrasahTextColor = android.graphics.Color.parseColor("#F5E6CC"),
                hadithCardBgColor = android.graphics.Color.parseColor("#FCFAF5"), // solid warm cream backing
                hadithCardStrokeColor = android.graphics.Color.parseColor("#E6D7BD")
            )
        }
        else -> { // 0 - Emerald Royal (Default)
            PosterTheme(
                mainBackgroundRes = R.drawable.img_bg_emerald,
                rightPanelBgColor = android.graphics.Color.parseColor("#00150E"),
                cardBgColor = android.graphics.Color.parseColor("#013A24"),
                strokeColor = android.graphics.Color.parseColor("#4D9E7E"),
                badgeBgColor = android.graphics.Color.parseColor("#D4AF37"),
                badgeTextColor = android.graphics.Color.parseColor("#011F15"),
                headerBannerBgColor = android.graphics.Color.parseColor("#012B1E"),
                headerBannerStrokeColor = android.graphics.Color.parseColor("#D4AF37"),
                cardTextColor = android.graphics.Color.parseColor("#FFFFFF"),
                cardAccentColor = android.graphics.Color.parseColor("#FFF1AC"),
                hadithTextColor = android.graphics.Color.parseColor("#011F15"),
                referencePillBgColor = android.graphics.Color.parseColor("#0D241C"),
                referencePillStrokeColor = android.graphics.Color.parseColor("#D4AF37"),
                referencePillTextColor = android.graphics.Color.parseColor("#FFF1AC"),
                dividerColor = android.graphics.Color.parseColor("#D4AF37"),
                organizerTextColor = android.graphics.Color.parseColor("#011F15"),
                madrasahTextColor = android.graphics.Color.parseColor("#FFF1AC"),
                hadithCardBgColor = android.graphics.Color.parseColor("#FCFDF9"), // solid cool cream/mint backing
                hadithCardStrokeColor = android.graphics.Color.parseColor("#C8D9D2")
            )
        }
    }

    // Apply main background resource dynamically
    val posterRoot = view.findViewById<View>(R.id.poster_root)
    posterRoot.setBackgroundResource(theme.mainBackgroundRes)

    // Left Area: Header Banner
    val headerBannerContainer = view.findViewById<View>(R.id.header_banner_container)
    val headerBg = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(theme.headerBannerBgColor)
        setStroke(scalePx(4f), theme.headerBannerStrokeColor)
        cornerRadii = floatArrayOf(
            scalePx(36f).toFloat(), scalePx(36f).toFloat(), // top-left
            scalePx(8f).toFloat(), scalePx(8f).toFloat(),   // top-right
            scalePx(8f).toFloat(), scalePx(8f).toFloat(),   // bottom-left
            scalePx(36f).toFloat(), scalePx(36f).toFloat()  // bottom-right
        )
    }
    headerBannerContainer.background = headerBg
    tvMadrasah.setTextColor(theme.madrasahTextColor)

    // Left Area: Beautiful Card Frame/Backing for Hadith text to make it stand out like real graphic design
    val hadithBg = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(theme.hadithCardBgColor)
        setStroke(scalePx(3f), theme.hadithCardStrokeColor)
        cornerRadius = scalePx(24f).toFloat()
    }
    tvHadith.background = hadithBg
    tvHadith.setPadding(scalePx(40f), scalePx(46f), scalePx(40f), scalePx(46f))
    tvHadith.setTextColor(theme.hadithTextColor)

    // Left Area: Reference Pill
    val referenceBg = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(theme.referencePillBgColor)
        setStroke(scalePx(2f), theme.referencePillStrokeColor)
        cornerRadius = scalePx(30f).toFloat()
    }
    tvReference.background = referenceBg
    tvReference.setTextColor(theme.referencePillTextColor)

    // Left Area: Divider & Organizer Text
    val dividerBottom = view.findViewById<View>(R.id.divider_bottom)
    dividerBottom.setBackgroundColor(theme.dividerColor)
    tvOrganizer.setTextColor(theme.organizerTextColor)

    // Right Area: Date Panel Sidebar Container Background
    val datePanelContainer = view.findViewById<View>(R.id.date_panel_container)
    val panelBg = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(theme.rightPanelBgColor)
        setStroke(scalePx(2f), theme.strokeColor)
        cornerRadius = scalePx(24f).toFloat()
    }
    datePanelContainer.background = panelBg

    // Right Area: Greeting Banner top
    val tvGreetingBanner = view.findViewById<TextView>(R.id.tv_greeting_banner)
    tvGreetingBanner.background = headerBg
    tvGreetingBanner.setTextColor(theme.cardTextColor)

    // Right Area: The 4 stacked date cards styling
    val cardBg = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(theme.cardBgColor)
        setStroke(scalePx(2f), theme.strokeColor)
        cornerRadius = scalePx(18f).toFloat()
    }
    // Apply backgrounds to the new 5-card layout
    view.findViewById<View>(R.id.card_day_container)?.background = cardBg
    view.findViewById<View>(R.id.card_hijri_month_container)?.background = cardBg
    view.findViewById<View>(R.id.card_greg_month_container)?.background = cardBg

    // Style the year and day boxes side-by-side with the badge background
    val boxBg = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(theme.badgeBgColor)
        setStroke(scalePx(2f), theme.strokeColor)
        cornerRadius = scalePx(14f).toFloat()
    }
    view.findViewById<View>(R.id.box_hijri_year)?.background = boxBg
    view.findViewById<View>(R.id.box_hijri_day)?.background = boxBg
    view.findViewById<View>(R.id.box_greg_year)?.background = boxBg
    view.findViewById<View>(R.id.box_greg_day)?.background = boxBg

    // Also background-style any compatibility views so old lookups don't break
    view.findViewById<View>(R.id.card_greg_container)?.background = cardBg
    view.findViewById<View>(R.id.card_hijri_container)?.background = cardBg
    view.findViewById<View>(R.id.card_lesson_container)?.background = cardBg
    view.findViewById<View>(R.id.tv_day_badge_container)?.background = boxBg
    view.findViewById<View>(R.id.tv_greg_day_container)?.background = boxBg
    view.findViewById<View>(R.id.tv_hijri_day_container)?.background = boxBg
    view.findViewById<View>(R.id.tv_signature_day_container)?.background = boxBg

    // Text colors
    tvDayName.setTextColor(theme.cardTextColor)
    tvDayNameEnglish?.setTextColor(theme.cardAccentColor)
    
    tvHijriMonth.setTextColor(theme.cardTextColor)
    tvHijriDay.setTextColor(theme.badgeTextColor)
    tvHijriYear.setTextColor(theme.badgeTextColor)

    tvGregMonth.setTextColor(theme.cardTextColor)
    tvGregDay.setTextColor(theme.badgeTextColor)
    tvGregYear.setTextColor(theme.badgeTextColor)

    val tvTodayLabel = view.findViewById<TextView>(R.id.tv_today_label)
    tvTodayLabel?.setTextColor(theme.badgeTextColor)
    val tvDayBadgeText = view.findViewById<TextView>(R.id.tv_day_badge_text)
    tvDayBadgeText?.setTextColor(theme.badgeTextColor)
    val tvBrandLbl = view.findViewById<TextView>(R.id.tv_brand_lbl)
    tvBrandLbl?.setTextColor(theme.cardAccentColor)
    val tvLessonTitle = view.findViewById<TextView>(R.id.tv_lesson_title)
    tvLessonTitle?.setTextColor(theme.cardTextColor)

    // Dynamic text auto-scaling for Hadith Text View in raw PX
    val hadithLength = finalHadith.length
    val baseTextSize = when {
        hadithLength < 50 -> 48f
        hadithLength < 100 -> 40f
        hadithLength < 150 -> 34f
        hadithLength < 220 -> 28f
        else -> 24f
    }
    
    tvHadith.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, baseTextSize * scale)
    tvReference.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 24f * scale)
    tvMadrasah.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 34f * scale)
    tvOrganizer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 34f * scale)
    
    // Explicitly scale other TextViews in Date Panel to ensure exact proportion in the final exported poster!
    tvHijriMonth.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 32f * scale)
    tvHijriDay.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 26f * scale)
    tvHijriYear.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 18f * scale)
    tvGregMonth.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 32f * scale)
    tvGregDay.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 26f * scale)
    tvGregYear.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 18f * scale)
    tvDayName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 32f * scale)
    tvDayNameEnglish?.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 18f * scale)
    tvDayBadgeText.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 20f * scale)
    tvTodayLabel.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 26f * scale)
    tvBrandLbl.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 18f * scale)
    tvLessonTitle.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 32f * scale)

    // Measure and Layout the view hierarchy explicitly with width and height specifications
    val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
    val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
    view.measure(widthSpec, heightSpec)
    view.layout(0, 0, width, height)

    // Draw the view onto our bitmap canvas
    view.draw(canvas)

    return bitmap
}

// MediaStore Exporter for JPG Output (Android 10+ compatible with zero permissions, plus pre-Q and fallback modes)
fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Uri? {
    val filename = "SHABIQ_Poster_${System.currentTimeMillis()}.jpg"
    val contentResolver = context.contentResolver
    var savedUri: Uri? = null

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ (Q): MediaStore with relative path is the correct way
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/SHABIQ")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
            val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            savedUri = contentResolver.insert(collection, values)
            if (savedUri != null) {
                contentResolver.openOutputStream(savedUri).use { outStream ->
                    if (outStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                    }
                }
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentResolver.update(savedUri, values, null, null)
            }
        } else {
            // Pre-Android 10: Save to Pictures/SHABIQ public directory and scan it
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val shabiqDir = File(picturesDir, "SHABIQ")
            if (!shabiqDir.exists()) {
                shabiqDir.mkdirs()
            }
            val file = File(shabiqDir, filename)
            FileOutputStream(file).use { outStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            }
            
            // Register file with MediaStore so it appears in Gallery
            val values = ContentValues().apply {
                @Suppress("DEPRECATION")
                put(MediaStore.Images.Media.DATA, file.absolutePath)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.TITLE, filename)
            }
            savedUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            
            // Trigger scanner broadcast for older devices
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = Uri.fromFile(file)
            context.sendBroadcast(mediaScanIntent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        // Fallback to MediaStore.Images.Media.insertImage if everything else fails
        try {
            val title = "SHABIQ_Poster_${System.currentTimeMillis()}"
            val savedUriStr = MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap,
                title,
                "SHABIQ Islamic Hadith Poster"
            )
            if (savedUriStr != null) {
                savedUri = Uri.parse(savedUriStr)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    return savedUri
}

// Share Poster Action
fun shareBitmap(context: Context, bitmap: Bitmap) {
    try {
        val cachePath = File(context.cacheDir, "shared_images")
        cachePath.mkdirs()
        val file = File(cachePath, "shabiq_hadith_poster.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }

        val fileUri = FileProvider.getUriForFile(
            context,
            "com.aistudio.shabiq.wrtgpx.fileprovider",
            file
        )

        if (fileUri != null) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "image/jpeg"
            }
            val chooser = Intent.createChooser(shareIntent, context.getString(R.string.share)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Sharing failed", Toast.LENGTH_SHORT).show()
    }
}

// Helper functions for Favorites, Recents, and My Library

fun loadFavorites(sharedPrefs: SharedPreferences): Set<Int> {
    val favsStr = sharedPrefs.getString("favorite_templates_ids", "") ?: ""
    if (favsStr.isEmpty()) return emptySet()
    return favsStr.split(",").mapNotNull { it.toIntOrNull() }.toSet()
}

fun saveFavorites(sharedPrefs: SharedPreferences, favorites: Set<Int>) {
    val favsStr = favorites.joinToString(",")
    sharedPrefs.edit().putString("favorite_templates_ids", favsStr).apply()
}

fun loadRecents(sharedPrefs: SharedPreferences): List<Int> {
    val recentsStr = sharedPrefs.getString("recent_templates_ids", "") ?: ""
    if (recentsStr.isEmpty()) return emptyList()
    return recentsStr.split(",").mapNotNull { it.toIntOrNull() }
}

fun saveRecents(sharedPrefs: SharedPreferences, recents: List<Int>) {
    val recentsStr = recents.take(10).joinToString(",")
    sharedPrefs.edit().putString("recent_templates_ids", recentsStr).apply()
}

fun addTemplateToRecents(sharedPrefs: SharedPreferences, templateId: Int): List<Int> {
    val current = loadRecents(sharedPrefs).toMutableList()
    current.remove(templateId)
    current.add(0, templateId)
    val trimmed = current.take(10)
    saveRecents(sharedPrefs, trimmed)
    return trimmed
}

fun saveBitmapToLibrary(context: Context, bitmap: Bitmap): File? {
    return try {
        val libraryDir = File(context.filesDir, "library")
        if (!libraryDir.exists()) {
            libraryDir.mkdirs()
        }
        val file = File(libraryDir, "SHABIQ_Poster_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun shareLibraryFile(context: Context, file: File) {
    try {
        val fileUri = FileProvider.getUriForFile(
            context,
            "com.aistudio.shabiq.wrtgpx.fileprovider",
            file
        )
        if (fileUri != null) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "image/jpeg"
            }
            val chooser = Intent.createChooser(shareIntent, context.getString(R.string.share_jpg)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Sharing failed", Toast.LENGTH_SHORT).show()
    }
}

fun loadThumbnail(filePath: String, targetWidth: Int = 300, targetHeight: Int = 300): Bitmap? {
    return try {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(filePath, options)
        var inSampleSize = 1
        if (options.outWidth > targetWidth || options.outHeight > targetHeight) {
            val halfWidth = options.outWidth / 2
            val halfHeight = options.outHeight / 2
            while (halfWidth / inSampleSize >= targetWidth && halfHeight / inSampleSize >= targetHeight) {
                inSampleSize *= 2
            }
        }
        options.inJustDecodeBounds = false
        options.inSampleSize = inSampleSize
        BitmapFactory.decodeFile(filePath, options)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    files: List<File>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onShare: (File) -> Unit,
    onDelete: (File) -> Unit,
    onRefresh: () -> Unit
) {
    val context = LocalContext.current
    var showDeleteDialogForFile by remember { mutableStateOf<File?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredFiles = remember(files, searchQuery) {
        if (searchQuery.isBlank()) {
            files
        } else {
            files.filter { file ->
                val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date(file.lastModified()))
                dateStr.contains(searchQuery)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.library_title),
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("library_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh, modifier = Modifier.testTag("library_refresh_button")) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("library_search_input"),
                placeholder = { Text(stringResource(R.string.search_date_hint), color = Color.Gray.copy(alpha = 0.5f)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                singleLine = true
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
            } else if (filteredFiles.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_posters_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredFiles, key = { it.absolutePath }) { file ->
                        val thumbnailBitmap = remember(file) {
                            loadThumbnail(file.absolutePath, 300, 300)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.75f)
                                .testTag("library_item_${file.name}"),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (thumbnailBitmap != null) {
                                    Image(
                                        bitmap = thumbnailBitmap.asImageBitmap(),
                                        contentDescription = "Saved Poster",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Gray.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PhotoLibrary,
                                            contentDescription = null,
                                            tint = Color.Gray
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(Color.Black.copy(alpha = 0.6f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = { onShare(file) },
                                            modifier = Modifier.testTag("share_library_item_${file.name}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Share Poster",
                                                tint = Color.White
                                            )
                                        }

                                        IconButton(
                                            onClick = { showDeleteDialogForFile = file },
                                            modifier = Modifier.testTag("delete_library_item_${file.name}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Poster",
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialogForFile != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialogForFile = null },
            title = { Text(text = stringResource(R.string.delete_title)) },
            text = { Text(text = stringResource(R.string.delete_confirm)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialogForFile?.let { onDelete(it) }
                        showDeleteDialogForFile = null
                    },
                    modifier = Modifier.testTag("delete_confirm_yes")
                ) {
                    Text(text = stringResource(R.string.yes))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialogForFile = null },
                    modifier = Modifier.testTag("delete_confirm_no")
                ) {
                    Text(text = stringResource(R.string.no))
                }
            }
        )
    }
}

