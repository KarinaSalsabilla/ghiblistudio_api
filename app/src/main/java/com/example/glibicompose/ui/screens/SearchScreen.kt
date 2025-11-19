package com.example.glibicompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.glibicompose.data.GhibliFilm
import com.example.glibicompose.ui.components.FilmCard
import com.example.glibicompose.ui.theme.GlibiComposeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var films by remember { mutableStateOf<List<GhibliFilm>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedFilm by remember { mutableStateOf<GhibliFilm?>(null) }
    val scope = rememberCoroutineScope()

    // Fetch films saat pertama kali load
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val filmsList = withContext(Dispatchers.IO) {
                    val url = URL("https://ghibliapi.vercel.app/films")
                    val response = url.readText()
                    val jsonArray = JSONArray(response)
                    val list = mutableListOf<GhibliFilm>()
                    for (i in 0 until jsonArray.length()) {
                        val film = jsonArray.getJSONObject(i)
                        list.add(
                            GhibliFilm(
                                id = film.getString("id"),
                                title = film.getString("title"),
                                originalTitle = film.getString("original_title"),
                                description = film.getString("description"),
                                director = film.getString("director"),
                                releaseDate = film.getString("release_date"),
                                image = film.getString("image"),
                                movieBanner = film.optString("movie_banner", ""),
                                runningTime = film.optString("running_time", ""),
                                rtScore = film.optString("rt_score", "")
                            )
                        )
                    }
                    list
                }
                films = filmsList
            } catch (e: Exception) {
                errorMessage = "Gagal memuat data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Filter films berdasarkan search query
    val filteredFilms = films.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.director.contains(searchQuery, ignoreCase = true)
    }

    // Show detail screen if film is selected
    if (selectedFilm != null) {
        FilmDetailScreen(
            film = selectedFilm!!,
            onBackClick = { selectedFilm = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Studio Ghibli Films",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF003153),
                        titleContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Cari film atau director...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color(0xFF003153),
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    singleLine = true
                )

                // Content
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF003153))
                        }
                    }
                    errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    filteredFilms.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isEmpty()) "Tidak ada film"
                                else "Tidak ditemukan hasil untuk \"$searchQuery\"",
                                color = Color.Gray
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredFilms) { film ->
                                FilmCard(
                                    film = film,
                                    onClick = { selectedFilm = film }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilmCard(film: GhibliFilm, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Film Image
            AsyncImage(
                model = film.image,
                contentDescription = film.title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Film Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = film.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003153),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = film.originalTitle,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Director: ",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = film.director,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0066CC)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Release: ${film.releaseDate}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    GlibiComposeTheme {
        SearchScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun FilmCardPreview() {
    GlibiComposeTheme {
        FilmCard(
            GhibliFilm(
                id = "1",
                title = "Spirited Away",
                originalTitle = "千と千尋の神隠し",
                description = "A young girl enters a world of spirits and must work to free her parents.",
                director = "Hayao Miyazaki",
                releaseDate = "2001",
                image = ""
            )
        )
    }
}