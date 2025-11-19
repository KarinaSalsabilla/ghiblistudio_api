package com.example.glibicompose.data

data class GhibliFilm(
    val id: String,
    val title: String,
    val originalTitle: String,
    val description: String,
    val director: String,
    val releaseDate: String,
    val image: String,
    val movieBanner: String = "",
    val runningTime: String = "",
    val rtScore: String = ""
)