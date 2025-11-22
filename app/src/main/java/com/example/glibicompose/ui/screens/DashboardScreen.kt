package com.example.glibicompose.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.glibicompose.data.GhibliFilm
import com.example.glibicompose.ui.theme.GlibiComposeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.glibicompose.R
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

// Netflix-inspired Dark Theme
private val NetflixBlack = Color(0xFF141414)
private val NetflixRed = Color(0xFF9BBCFF)
private val NetflixDarkGray = Color(0xFF2F2F2F)
private val NetflixLightGray = Color(0xFFE8E8E8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    var films by remember { mutableStateOf<List<GhibliFilm>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedFilm by remember { mutableStateOf<GhibliFilm?>(null) }
    var showSearch by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Fetch films
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
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    // Group films by categories
    val featuredFilm = films.find { it.title.contains("My Neighbor Totoro", ignoreCase = true) } ?: films.firstOrNull()
    val topRated = films.sortedByDescending { it.rtScore.toIntOrNull() ?: 0 }.take(6)
    val miyazakiFilms = films.filter { it.director.contains("Miyazaki", ignoreCase = true) }
    val recentFilms = films.sortedByDescending { it.releaseDate }.take(6)
    // Show search screen if search is activated
    if (showSearch) {
        SearchScreen(
            onBackClick = { showSearch = false },
            onFilmClick = { film ->
                selectedFilm = film
                showSearch = false
            }
        )
        // Back button handling - kembali ke dashboard
        BackHandler {
            showSearch = false
        }
    }
    // Show detail screen if film is selected
    else if (selectedFilm != null) {
        FilmDetailScreen(
            film = selectedFilm!!,
            onBackClick = { selectedFilm = null }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Header with Logo and Search - PERBAIKAN DI SINI
                NetflixHeader(onSearchClick = { showSearch = true })

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = NetflixRed,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                    errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = "⚠️",
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Error: $errorMessage",
                                    color = NetflixLightGray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    films.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No films available",
                                color = NetflixLightGray,
                                fontSize = 16.sp
                            )
                        }
                    }
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Featured/Hero Section
                            featuredFilm?.let { film ->
                                FeaturedFilmSection(
                                    film = film,
                                    onClick = { selectedFilm = film }
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Top Rated Section
                            if (topRated.isNotEmpty()) {
                                FilmRowSection(
                                    title = stringResource(id= R.string.Top_rated),
                                    films = topRated,
                                    onFilmClick = { selectedFilm = it }
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            // Miyazaki Films Section
                            if (miyazakiFilms.isNotEmpty()) {
                                FilmRowSection(
                                    title = stringResource(id= R.string.hayao),
                                    films = miyazakiFilms,
                                    onFilmClick = { selectedFilm = it }
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            // Recent Films Section
                            if (recentFilms.isNotEmpty()) {
                                FilmRowSection(
                                    title = stringResource(id= R.string.rilis),
                                    films = recentFilms,
                                    onFilmClick = { selectedFilm = it }
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            // All Films Section
                            FilmRowSection(
                                title = stringResource(id=R.string.all),
                                films = films,
                                onFilmClick = { selectedFilm = it }
                            )

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

// PERBAIKAN: Tambahkan parameter onSearchClick
@Composable
fun NetflixHeader(onSearchClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "GHIBLI",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = NetflixRed,
            letterSpacing = 2.sp
        )

        Icon(
            Icons.Default.Search,
            contentDescription = "Search",

            modifier = Modifier
                .size(28.dp)
                .clickable { onSearchClick() }
        )
    }
}

@Composable
fun FeaturedFilmSection(film: GhibliFilm, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
    ) {
        // Background Image with Gradient
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = if (film.movieBanner.isNotEmpty()) film.movieBanner else film.image,
                contentDescription = film.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Bottom gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                NetflixBlack.copy(alpha = 0.7f),
                                NetflixBlack
                            ),
                            startY = 0f,
                            endY = 1500f
                        )
                    )
            )
        }

        // Content at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = film.title,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (film.rtScore.isNotEmpty()) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${film.rtScore}%",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Text(
                    text = film.releaseDate,
                    color = NetflixLightGray,
                    fontSize = 14.sp
                )

                if (film.runningTime.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${film.runningTime} min",
                        color = NetflixLightGray,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = film.description,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NetflixDarkGray.copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.more_info),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}


@Composable
fun FilmRowSection(
    title: String,
    films: List<GhibliFilm>,
    onFilmClick: (GhibliFilm) -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,

            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(films) { film ->
                NetflixFilmCard(
                    film = film,
                    onClick = { onFilmClick(film) }
                )
            }
        }
    }
}

@Composable
fun NetflixFilmCard(film: GhibliFilm, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = film.image,
                contentDescription = film.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Rating badge
            if (film.rtScore.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = NetflixRed
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(10.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = film.rtScore,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black

                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = film.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,

            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
fun DashboardScreenPreview() {
    GlibiComposeTheme {
        DashboardScreen()
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
fun NetflixFilmCardPreview() {
    GlibiComposeTheme {
        Box(
            modifier = Modifier
                .background(NetflixBlack)
                .padding(16.dp)
        ) {
            NetflixFilmCard(
                film = GhibliFilm(
                    id = "1",
                    title = "Spirited Away",
                    originalTitle = "千と千尋の神隠し",
                    description = "A young girl enters a world of spirits and must work to free her parents.",
                    director = "Hayao Miyazaki",
                    releaseDate = "2001",
                    image = "",
                    movieBanner = "",
                    runningTime = "125",
                    rtScore = "97"
                ),
                onClick = {}
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
fun FeaturedFilmSectionPreview() {
    GlibiComposeTheme {
        Box(modifier = Modifier.background(NetflixBlack)) {
            FeaturedFilmSection(
                film = GhibliFilm(
                    id = "1",
                    title = "Spirited Away",
                    originalTitle = "千と千尋の神隠し",
                    description = "During her family's move to the suburbs, a sullen 10-year-old girl wanders into a world ruled by gods, witches, and spirits, and where humans are changed into beasts.",
                    director = "Hayao Miyazaki",
                    releaseDate = "2001",
                    image = "",
                    movieBanner = "",
                    runningTime = "125",
                    rtScore = "97"
                ),
                onClick = {}
            )
        }
    }
}