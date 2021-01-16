package co.simonkenny.row.util

import android.content.res.Configuration
import android.content.res.Resources

fun isDarkMode(resources: Resources): Boolean {
    val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return currentNightMode == Configuration.UI_MODE_NIGHT_YES
}