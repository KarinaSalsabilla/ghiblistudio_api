package com.example.glibicompose.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun FilmCard(
    film: GhibliFilm,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                modifier = Modifier.fillMaxWidth()
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
@Composable
@Preview
fun FilmCardPreview() {
    FilmCard(
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
