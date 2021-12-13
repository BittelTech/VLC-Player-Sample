package com.ph.meshtv.tv.player.control

import androidx.fragment.app.Fragment
import com.ph.meshtv.tv.player.movie.model.MoviesItem
import com.ph.meshtv.tv.player.util.Utils.toArrayList


val indexMap = mutableMapOf<Fragment, Int>()
val sourceMap = mutableMapOf<Fragment, ArrayList<MoviesItem>>()

var Fragment.channelIndex: Int
    get() = indexMap[this] ?: 0
    set(value) {
        indexMap[this] = if (value < vlcMedia!!.size && value >= 0) value else if (value < 0) vlcMedia!!.size - 1 else 0
    }

var Fragment.vlcMedia: ArrayList<MoviesItem>?
    get() = sourceMap[this] ?: arrayListOf()
    set(value) {
        sourceMap[this] = value!!.sortedBy {
            it.movie_id
        }.toArrayList()
    }

