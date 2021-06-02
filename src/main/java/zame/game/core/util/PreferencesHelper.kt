package zame.game.core.util

import android.content.Context
import android.content.SharedPreferences
import zame.game.App

/**
 * Created by vinh on 12/17/17.
 */

class PreferencesHelper {

    private val mPref: SharedPreferences

    init {
        mPref = App.self.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun clear() {
        mPref.edit().clear().apply()
    }

    fun putValue(key: String, value: Any) {
        when (value) {
            is String -> mPref.edit().putString(key, value).apply()
            is Int -> mPref.edit().putInt(key, value).apply()
            is Boolean -> mPref.edit().putBoolean(key, value).apply()
            is Float -> mPref.edit().putFloat(key, value).apply()
            else -> Throwable("Put Object not support")
        }
    }

    fun getStringValue(key: String, default: String? = null): String? {
        return mPref.getString(key, default)
    }

    fun getIntValue(key: String, default: Int = 0): Int? {
        return mPref.getInt(key, default)
    }

    fun getFloatValue(key: String, default: Float = 0f): Float? {
        return mPref.getFloat(key, default)
    }

    fun getBooleanValue(key: String, default: Boolean = false): Boolean {
        return mPref.getBoolean(key, default)
    }

    fun remove(key: String) {
        mPref.edit().remove(key).apply()
    }

    companion object {
        val shared = PreferencesHelper()
        val PREF_FILE_NAME = "pixel_pref_file"
    }

}
