package com.ph.meshtv.tv.player.util

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ph.meshtv.tv.player.movie.model.MoviesItem
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.reflect.Type

object Utils {

    fun getJsonFromAssets(context: Context, fileName: String?): String? {

        val jsonString: String = try {
            val `is` = context.assets.open(fileName!!)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return jsonString
    }


    fun getMoviesAPI(context : Context): ArrayList<MoviesItem> {

        val jsonFileString = getJsonFromAssets(context, "movies.json")
        val gson = Gson()
        val listUserType: Type = object : TypeToken<List<MoviesItem?>?>() {}.type

        return gson.fromJson<List<MoviesItem>>(jsonFileString, listUserType).toArrayList()
    }



    fun readFile(desc: String): String {
        var result = "<![CDATA[<p>..</p>]]"
        try {
            val file = File(Uri.parse(desc).path!!)
            if (file.exists())
                result = FileInputStream(file).bufferedReader().use { it.readText() }
        }catch (e : Exception){
            e.printStackTrace()
        }
        return result
    }



    fun <T> List<T>.toArrayList(): ArrayList<T> {
        return ArrayList(this)
    }

}