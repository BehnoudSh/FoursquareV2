package ir.behnoudsh.aroundus.data.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class Prefs(context: Context) {

    companion object {
        private const val PREFS_FILENAME = "aroundus_prefs"
        private const val KEY_MY_LOCATION_LAT = "myLocationLat"
        private const val KEY_MY_LOCATION_LONG = "myLocationLong"
        private const val KEY_OFFSET = "offset"
        private const val KEY_LAST_UPDATED = "lastUpdated"
    }

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    var myLocationLat: String
        get() = sharedPrefs.getString(KEY_MY_LOCATION_LAT, "") ?: ""
        set(value) = sharedPrefs.edit { putString(KEY_MY_LOCATION_LAT, value) }

    var myLocationLong: String
        get() = sharedPrefs.getString(KEY_MY_LOCATION_LONG, "") ?: ""
        set(value) = sharedPrefs.edit { putString(KEY_MY_LOCATION_LONG, value) }


    var offset: Int
        get() = sharedPrefs.getInt(KEY_OFFSET, 0) ?: 0
        set(value) = sharedPrefs.edit { putInt(KEY_OFFSET, value) }

    var lastUpdated: Long
        get() = sharedPrefs.getLong(KEY_LAST_UPDATED, 0) ?: 0
        set(value) = sharedPrefs.edit { putLong(KEY_LAST_UPDATED, value) }

}
