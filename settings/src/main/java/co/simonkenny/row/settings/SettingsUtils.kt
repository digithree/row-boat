package co.simonkenny.row.settings

import android.content.res.Resources
import co.simonkenny.row.coresettings.SizeData

fun SizeData.text(resources: Resources) =
    when (ordinal) {
        0 -> resources.getString(R.string.settings_size_small)
        1 -> resources.getString(R.string.settings_size_medium)
        else -> resources.getString(R.string.settings_size_large)
    }