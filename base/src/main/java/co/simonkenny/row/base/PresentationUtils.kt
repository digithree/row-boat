package co.simonkenny.row.base

import android.os.Build
import android.text.Html

fun toHtml(htmlStr: String) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    Html.fromHtml(htmlStr, Html.FROM_HTML_MODE_LEGACY)
} else {
    Html.fromHtml(htmlStr)
}