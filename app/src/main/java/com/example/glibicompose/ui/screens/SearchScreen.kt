package com.example.glibicompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.glibicompose.R
import com.example.glibicompose.data.GhibliFilm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

private val NetflixBlack = Color(0xFF141414)
private val NetflixRed = Color(0xFFE50914)
private val NetflixDarkGray = Color(0xFFB8BEC9)
private val NetflixLightGray = Color(0xFFCBCBCB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit = {},
    onFilmClick: (GhibliFilm) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var films by remember { mutableStateOf<List<GhibliFilm>>(emptyList()) }
    var filteredFilms by remember { mutableStateOf<List<GhibliFilm>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Fetch films on first load
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
                filteredFilms = filmsList
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    // Filter films based on search query - HANYA HURUF DEPAN
    LaunchedEffect(searchQuery) {
        filteredFilms = if (searchQuery.isEmpty()) {
            films
        } else {
            films.filter { film ->
                val query = searchQuery.trim()
                film.title.startsWith(query, ignoreCase = true) ||
                        film.originalTitle.startsWith(query, ignoreCase = true) ||
                        film.director.startsWith(query, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Search Header
            Surface(
                modifier = Modifier.fillMaxWidth(),

                ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",

                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onBackClick() }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Search TextField
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                stringResource(R.string.search_placeholder),
                                color = Color.Black
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = NetflixLightGray
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color.Unspecified,

                                    modifier = Modifier.clickable {
                                        searchQuery = ""
                                    }
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = NetflixDarkGray,
                            unfocusedContainerColor = NetflixDarkGray,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = NetflixRed,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = { focusManager.clearFocus() }
                        )
                    )
                }
            }

//            Divider(color = NetflixDarkGray, thickness = 1.dp)

            // Content
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
                            Text(text = "⚠️", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Error: $errorMessage",
//                                color = NetflixLightGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                filteredFilms.isEmpty() && searchQuery.isNotEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "🔍",
                                fontSize = 64.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.no_results, searchQuery),

                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.try_different_keywords),
//                                color = NetflixLightGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                searchQuery.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = NetflixDarkGray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.search_placeholder),
                                color = NetflixLightGray,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            val resultText = pluralStringResource(
                                R.plurals.results_count,
                                filteredFilms.size,
                                filteredFilms.size
                            )

                            Text(text = resultText)

                        }

                        items(filteredFilms) { film ->
                            SearchResultItem(
                                film = film,
                                onClick = { onFilmClick(film) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(film: GhibliFilm, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(NetflixDarkGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        // Film Image
        AsyncImage(
            model = film.image,
            contentDescription = film.title,
            modifier = Modifier
                .width(80.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Film Details
        Column(
            modifier = Modifier
                .weight(1f)
                .height(120.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = film.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
//                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = film.releaseDate,

                        fontSize = 12.sp
                    )

                    if (film.rtScore.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${film.rtScore}%",
//                            color = NetflixLightGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Text(
                text = film.description,
//                color = NetflixLightGray,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
        }
    }
}