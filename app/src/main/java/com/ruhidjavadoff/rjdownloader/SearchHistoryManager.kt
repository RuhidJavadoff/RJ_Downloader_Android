package com.ruhidjavadoff.rjdownloader

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.LinkedList

object SearchHistoryManager {

    private const val PREFS_NAME = "rj_downloader_prefs"
    private const val KEY_SEARCH_HISTORY = "search_history"
    private const val MAX_HISTORY_SIZE = 10

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun addSearchQuery(context: Context, query: String) {
        if (query.isBlank()) return

        val prefs = getPreferences(context)
        val historyJson = prefs.getString(KEY_SEARCH_HISTORY, null)
        val gson = Gson()
        val type = object : TypeToken<LinkedList<String>>() {}.type

        val history: LinkedList<String> = if (historyJson != null) {
            gson.fromJson(historyJson, type)
        } else {
            LinkedList()
        }


        history.remove(query)

        history.addFirst(query)


        while (history.size > MAX_HISTORY_SIZE) {
            history.removeLast()
        }

        val newHistoryJson = gson.toJson(history)
        prefs.edit().putString(KEY_SEARCH_HISTORY, newHistoryJson).apply()
    }

    fun getRecentSearches(context: Context): List<String> {
        val prefs = getPreferences(context)
        val historyJson = prefs.getString(KEY_SEARCH_HISTORY, null)
        return if (historyJson != null) {
            val gson = Gson()
            val type = object : TypeToken<LinkedList<String>>() {}.type
            gson.fromJson(historyJson, type)
        } else {
            emptyList()
        }
    }

    fun clearSearchHistory(context: Context) {
        val prefs = getPreferences(context)
        prefs.edit().remove(KEY_SEARCH_HISTORY).apply()
    }
}
