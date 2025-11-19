package com.example.glibicompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.glibicompose.data.GhibliCharacter
import com.example.glibicompose.data.GhibliFilm
import com.example.glibicompose.ui.components.CharactersDialog
import com.example.glibicompose.ui.components.InfoCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

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
            TopAppBar(
                title = { Text("Detail Film") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF003153),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)

        ) {
            item {
                // Cover Banner dengan Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    AsyncImage(
                        model = if (film.movieBanner.isNotEmpty()) film.movieBanner else film.image,
                        contentDescription = film.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                )
                            )
                    )

                    // Title over image
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = film.title,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = film.originalTitle,
                            fontSize = 18.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            item {
                // Info Cards
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Rating & Info Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Rating Card
                        InfoCard(
                            icon = Icons.Default.Star,
                            label = "Rating",
                            value = if (film.rtScore.isNotEmpty()) "${film.rtScore}/100" else "N/A",
                            modifier = Modifier.weight(1f)
                        )

                        // Year Card
                        InfoCard(
                            icon = Icons.Default.Person,
                            label = "Tahun",
                            value = film.releaseDate,
                            modifier = Modifier.weight(1f)
                        )

                        // Duration Card
                        InfoCard(
                            icon = Icons.Default.Star,
                            label = "Durasi",
                            value = if (film.runningTime.isNotEmpty()) "${film.runningTime} min" else "N/A",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Director Info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Director",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = film.director,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF003153)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Synopsis
                    Text(
                        text = "Sinopsis",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003153)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = film.description,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Characters Button
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
                                            } catch (e: Exception) {
                                                // Skip character if error
                                            }
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
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF003153)
                        )
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lihat Karakter Film",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // Characters Dialog
    if (showCharactersDialog) {
        CharactersDialog(
            characters = characters,
            isLoading = isLoadingCharacters,
            errorMessage = errorMessage,
            onDismiss = { showCharactersDialog = false }
        )
    }
}

// ========== PREVIEW ==========
@Preview(showBackground = true)
@Composable
fun FilmDetailScreenPreview() {
    FilmDetailScreen(
        film = GhibliFilm(
            id = "2baf70d1-42bb-4437-b551-e5fed5a87abe",
            title = "Spirited Away",
            originalTitle = "千と千尋の神隠し",
            description = "Spirited Away is an Oscar winning Japanese animated film about a ten year old girl who wanders away from her parents along a path that leads to a world ruled by strange and unusual monster-like animals. Her parents have been changed into pigs along with others inside a bathhouse full of these creatures. Will she ever see the world how it once was?",
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

@Preview(showBackground = true, name = "Film Without Banner")
@Composable
fun FilmDetailScreenNoBannerPreview() {
    FilmDetailScreen(
        film = GhibliFilm(
            id = "1",
            title = "My Neighbor Totoro",
            originalTitle = "となりのトトロ",
            description = "Two sisters move to the country with their father in order to be closer to their hospitalized mother, and discover the surrounding trees are inhabited by Totoros, magical spirits of the forest. When the youngest runs away from home, the older sister seeks help from the spirits to find her.",
            director = "Hayao Miyazaki",
            releaseDate = "1988",
            image = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/rtGDOeG9LzoerkDGZF9dnVeLppL.jpg",
            movieBanner = "",
            runningTime = "86",
            rtScore = "93"
        ),
        onBackClick = {}
    )
}

@Preview(showBackground = true, name = "Film dengan Judul Panjang")
@Composable
fun FilmDetailScreenLongTitlePreview() {
    FilmDetailScreen(
        film = GhibliFilm(
            id = "3",
            title = "Howl's Moving Castle",
            originalTitle = "ハウルの動く城",
            description = "When Sophie, a shy young woman, is cursed with an old body by a spiteful witch, her only chance of breaking the spell lies with a self-indulgent yet insecure young wizard and his companions in his legged, walking castle.",
            director = "Hayao Miyazaki",
            releaseDate = "2004",
            image = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/TkTPELv4kC3u1lZOOlPjWzZcCpB.jpg",
            movieBanner = "https://image.tmdb.org/t/p/original/6a8b0zjPfJCOf4mLdE5tZoJuhn9.jpg",
            runningTime = "119",
            rtScore = "87"
        ),
        onBackClick = {}
    )
}