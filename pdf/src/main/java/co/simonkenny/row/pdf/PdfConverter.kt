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

private const val FONT_SIZE_TITLE = 20.0f
private const val FONT_SIZE_INFO = 13.0f
private const val FONT_SIZE_BODY = 12.0f

private const val LINE_SPACING_BODY = 1.4f

class PdfConverter(
    private val context: Context,
    private val readerDoc: ReaderDoc
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
            document.open()
            with (readerDoc) {
                // metadata
                document.addTitle(title)
                // header
                document.add(Paragraph().apply {
                    add(Chunk(title,
                        FontFactory.getFont(FontFactory.TIMES, FONT_SIZE_TITLE, Font.BOLD)))
                })
                document.add(Paragraph("\n"))
                document.add(Paragraph().apply {
                    add(Chunk(url,
                        FontFactory.getFont(FontFactory.TIMES, FONT_SIZE_INFO, Font.UNDERLINE)))
                })
                document.add(Paragraph("\n"))
                attribution?.run {
                    document.add(Paragraph().apply {
                        add(Chunk(this@run,
                            FontFactory.getFont(FontFactory.TIMES, FONT_SIZE_INFO, Font.NORMAL)))
                    })
                    document.add(Paragraph("\n"))
                }
                publisher?.run {
                    document.add(Paragraph().apply {
                        add(Chunk(this@run,
                            FontFactory.getFont(FontFactory.TIMES, FONT_SIZE_INFO, Font.ITALIC)))
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
                            it.parseToPdfStyledParts()
                                .forEach { pdfStyledString ->
                                    add(Chunk(pdfStyledString.str,
                                        FontFactory.getFont(FontFactory.TIMES, FONT_SIZE_BODY, pdfStyledString.style)))
                                }
                            multipliedLeading = LINE_SPACING_BODY
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