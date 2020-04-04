package za.co.addcolour.tiltgame.helper

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object SharedPrefsHelper {

    private const val TAG = "SharedPrefsHelper"

    private const val PREFERENCES = "za.co.addcolour.tiltgame"

    private const val PREFS_COLOR_CODE = "PREFS_COLOR_CODE"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    }

    private fun getIntPreference(context: Context?, key: String): Int {
        return if (context == null) 0 else getSharedPreferences(context).getInt(key, 0)
    }

    private fun setIntPreference(context: Context?, key: String, value: Int) {

        if (context == null) return

        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)

        if (!editor.commit()) Log.e(TAG, "FAILED to store preference!")
    }

    fun setColorCode(context: Context, colorCode: Int) {
        setIntPreference(context, PREFS_COLOR_CODE, colorCode)
    }

    fun getColorCode(context: Context): Int {
        return getIntPreference(context, PREFS_COLOR_CODE)
    }
}