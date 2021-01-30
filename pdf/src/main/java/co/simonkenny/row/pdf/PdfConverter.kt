package co.simonkenny.row.pdf

import android.content.Context
import android.graphics.Typeface
import android.os.Environment
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import co.simonkenny.row.readersupport.ReaderDoc
import com.lowagie.text.*
import com.lowagie.text.pdf.PdfWriter
import java.io.File
import java.util.*


class PdfConverter(
    private val context: Context,
    private val readerDoc: ReaderDoc,
    private val pdfExportConfig: PdfExportConfig
) {

    fun save(): File? {
        // connect file output stream to document
        val outputFilename = readerDoc.title
            .replace("[^a-zA-Z0-9]", "")
            .replace("|", "")
            .replace(" ", "-")
            .toLowerCase(Locale.US)
        val outputFile: File
        try {
            outputFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                "$outputFilename.pdf")
            makeDocument(outputFile)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return outputFile
    }

    private fun makeDocument(file: File) {
        val document = Document()
        PdfWriter.getInstance(document, file.outputStream())
        try {
            document.setMargins(
                pdfExportConfig.horzMargins,
                pdfExportConfig.horzMargins,
                pdfExportConfig.vertMargins,
                pdfExportConfig.vertMargins
            )
            document.open()
            with (readerDoc) {
                // metadata
                document.addTitle(title)
                // header
                document.add(Paragraph().apply {
                    multipliedLeading = pdfExportConfig.lineSpacingTitle
                    add(Chunk(title,
                        FontFactory.getFont(FontFactory.TIMES, pdfExportConfig.fontSizeTitle, Font.BOLD)))
                })
                document.add(Paragraph("\n"))
                document.add(Paragraph().apply {
                    multipliedLeading = pdfExportConfig.lineSpacingInfo
                    add(Anchor(url,
                        FontFactory.getFont(FontFactory.TIMES, pdfExportConfig.fontSizeInfo, Font.UNDERLINE))
                            .apply {
                                reference = this@with.url
                                name = this@with.url
                            }
                    )
                })
                document.add(Paragraph("\n"))
                attribution?.run {
                    document.add(Paragraph().apply {
                        multipliedLeading = pdfExportConfig.lineSpacingInfo
                        add(Chunk(this@run,
                            FontFactory.getFont(FontFactory.TIMES, pdfExportConfig.fontSizeInfo, Font.NORMAL)))
                    })
                    document.add(Paragraph("\n"))
                }
                publisher?.run {
                    document.add(Paragraph().apply {
                        multipliedLeading = pdfExportConfig.lineSpacingInfo
                        add(Chunk(this@run,
                            FontFactory.getFont(FontFactory.TIMES, pdfExportConfig.fontSizeInfo, Font.ITALIC)))
                    })
                    document.add(Paragraph("\n"))
                }
                document.add(Paragraph("\n"))
                val paragraphs = body.splitByNewlines()
                paragraphs.forEach cont@{
                    if (it.isEmpty()) {
                        document.add(Paragraph("\n"))
                    } else {
                        document.add(Paragraph().apply {
                            multipliedLeading = pdfExportConfig.lineSpacingBody
                            it.parseToPdfStyledParts()
                                .forEach { pdfStyledString ->
                                    add(Chunk(pdfStyledString.str,
                                        FontFactory.getFont(FontFactory.TIMES, pdfExportConfig.fontSizeBody, pdfStyledString.style)))
                                }
                        })
                    }
                }
            }
            // finally
            document.close()
        } catch (e: DocumentException) {
            e.printStackTrace()
            error { "Couldn't write to document" }
        }
    }

    private fun Spanned.splitByNewlines(): List<Spanned> =
        mutableListOf<Spanned>().apply {
            var i = 0
            while (i < this@splitByNewlines.length) {
                val nextNewlineIdx = indexOf('\n', i)
                if (i == nextNewlineIdx) {
                    add(SpannableString(""))
                } else {
                    add(subSequence(i, nextNewlineIdx) as Spanned)
                }
                i = nextNewlineIdx + 1
            }
        }.toList()

    // TODO : support more spans, like UnderlineSpan, URLSpan, SuperscriptSpan, SubscriptSpan,
    //          StrikethroughSpan, QuoteSpan
    private fun Spanned.parseToPdfStyledParts(): List<PdfStyledString> =
        mutableListOf<PdfStyledString>().apply {
            val spans: List<Pair<Int, StyleSpan>> = getSpans(0, length, StyleSpan::class.java)
                .map { Pair(getSpanStart(it), it as StyleSpan) }
                .sortedBy { it.first }
            var spanIdx = 0
            var strIdx = 0
            while (spanIdx < spans.size && strIdx < this@parseToPdfStyledParts.length) {
                if (strIdx < spans[spanIdx].first) {
                    add(PdfStyledString(substring(strIdx, spans[spanIdx].first)))
                    strIdx = spans[spanIdx].first
                } else {
                    add(PdfStyledString(
                        substring(spans[spanIdx].first, getSpanEnd(spans[spanIdx].second)),
                        when (spans[spanIdx].second.style) {
                            Typeface.BOLD -> Font.BOLD
                            Typeface.ITALIC -> Font.ITALIC
                            Typeface.BOLD_ITALIC -> Font.BOLDITALIC
                            else -> Font.NORMAL
                        }
                    ))
                    strIdx = getSpanEnd(spans[spanIdx].second)
                    spanIdx++
                }
            }
            if (strIdx < this@parseToPdfStyledParts.length) {
                add(PdfStyledString(substring(strIdx, this@parseToPdfStyledParts.length)))
            }
        }

    data class PdfStyledString(
        val str: String,
        val style: Int = Font.NORMAL
    )
}