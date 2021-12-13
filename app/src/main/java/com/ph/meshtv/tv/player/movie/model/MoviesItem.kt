package com.ph.meshtv.tv.player.movie.model

data class MoviesItem(
    val description: String,
    val full: String,
    val movie_id: Int,
    val poster: String,
    val rating: Int,
    val tag: ArrayList<Tag>,
    val title: String,
    val trailer: String
)