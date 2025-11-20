package com.example.glibicompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.glibicompose.data.GhibliCharacter
import com.example.glibicompose.data.GhibliFilm
import com.example.glibicompose.ui.components.CharactersDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

private val PrimaryBlue = Color(0xFF1E398A)
private val SecondaryBlue = Color(0xFF5289CB)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmDetailScreen(
    film: GhibliFilm,
    onBackClick: () -> Unit
) {
    var showCharactersDialog by rememberSaveable { mutableStateOf(false) }
    var characters by rememberSaveable { mutableStateOf<List<GhibliCharacter>>(emptyList()) }
    var isLoadingCharacters by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(PrimaryBlue, SecondaryBlue)
                        )
                    )
            ) {
                TopAppBar(
                    title = {
                        Text(
                            "Detail Film",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, // WAJIB transparan
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                ) {
                    AsyncImage(
                        model = if (film.movieBanner.isNotEmpty()) film.movieBanner else film.image,
                        contentDescription = film.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.2f),
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Black.copy(alpha = 0.85f)
                                    ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = film.title,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 40.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = film.originalTitle,
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            icon = Icons.Default.Star,
                            value = if (film.rtScore.isNotEmpty()) film.rtScore else "N/A",
                            label = "Rating",
                            iconTint = Color(0xFFFFB800),
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            icon = Icons.Outlined.DateRange,
                            value = film.releaseDate,
                            label = "Year",
                            iconTint = Color(0xFF003153),
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            icon = Icons.Default.AccessTime,
                            value = if (film.runningTime.isNotEmpty()) "${film.runningTime}m" else "N/A",
                            label = "Duration",
                            iconTint = Color(0xFF003153),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF003153).copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF003153),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Director",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = film.director,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF003153)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "Synopsis",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        letterSpacing = 0.3.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Text(
                            text = film.description,
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            color = Color(0xFF4A4A4A),
                            modifier = Modifier.padding(20.dp),
                            letterSpacing = 0.2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            showCharactersDialog = true
                            scope.launch {
                                isLoadingCharacters = true
                                errorMessage = null
                                try {
                                    val charactersList = withContext(Dispatchers.IO) {
                                        val url = URL("https://ghibliapi.vercel.app/films/${film.id}")
                                        val response = url.readText()
                                        val jsonObject = org.json.JSONObject(response)
                                        val charactersArray = jsonObject.optJSONArray("people") ?: JSONArray()

                                        val list = mutableListOf<GhibliCharacter>()
                                        for (i in 0 until charactersArray.length()) {
                                            val charUrl = charactersArray.getString(i)
                                            try {
                                                val charResponse = URL(charUrl).readText()
                                                val charJson = org.json.JSONObject(charResponse)
                                                list.add(
                                                    GhibliCharacter(
                                                        id = charJson.getString("id"),
                                                        name = charJson.getString("name"),
                                                        gender = charJson.optString("gender", "Unknown"),
                                                        age = charJson.optString("age", "Unknown"),
                                                        eyeColor = charJson.optString("eye_color", "Unknown"),
                                                        hairColor = charJson.optString("hair_color", "Unknown")
                                                    )
                                                )
                                            } catch (_: Exception) { }
                                        }
                                        list
                                    }
                                    characters = charactersList
                                } catch (e: Exception) {
                                    errorMessage = "Gagal memuat karakter: ${e.message}"
                                } finally {
                                    isLoadingCharacters = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF003153)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "View Characters",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            letterSpacing = 0.3.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    if (showCharactersDialog) {
        CharactersDialog(
            characters = characters,
            isLoading = isLoadingCharacters,
            errorMessage = errorMessage,
            onDismiss = { showCharactersDialog = false }
        )
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilmDetailScreenPreview() {
    FilmDetailScreen(
        film = GhibliFilm(
            id = "2baf70d1-42bb-4437-b551-e5fed5a87abe",
            title = "Spirited Away",
            originalTitle = "千と千尋の神隠し",
            description = "Spirited Away is an Oscar winning Japanese animated film...",
            director = "Hayao Miyazaki",
            releaseDate = "2001",
            image = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/39wmItIWsg5sZMyRUHLkWBcuVCM.jpg",
            movieBanner = "https://image.tmdb.org/t/p/original/bSXfU4dwZyBA1vMmXvejdRXBvuF.jpg",
            runningTime = "125",
            rtScore = "97"
        ),
        onBackClick = {}
    )
}
