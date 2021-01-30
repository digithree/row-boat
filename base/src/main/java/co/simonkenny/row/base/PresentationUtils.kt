package co.simonkenny.row.base

import android.os.Build
import android.text.Html
import android.text.Spanned

fun String.fromHtml(): Spanned =
    with(cleanHtml()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(this)
        }
    }

fun String.cleanHtml() =
    replace("\t", "")
        .replace(Regex.fromLiteral("(?m)(^ *| +(?= |$))"), "")
        .replace(Regex.fromLiteral("(?m)^$([\r\n]+?)(^$[\r\n]+?^)+"), "$1")
