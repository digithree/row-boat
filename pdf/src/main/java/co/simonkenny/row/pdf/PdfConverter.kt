package co.simonkenny.row.pdf

import android.content.Context
import android.os.Environment
import co.simonkenny.row.readersupport.ReaderDoc
import com.lowagie.text.*
import com.lowagie.text.pdf.PdfWriter
import java.io.File
import java.util.*

private const val FONT_SIZE_TITLE = 20.0f
private const val FONT_SIZE_INFO = 14.0f
private const val FONT_SIZE_SUB_TITLE = 16.0f
private const val FONT_SIZE_BODY = 12.0f

class PdfConverter(
    private val context: Context,
    private val readerDoc: ReaderDoc
) {

    fun save(): File? {
        val document = Document()
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
                val paragraphs = body.toString().split("\n")
                paragraphs.forEach cont@{
                    if (it.isEmpty()) {
                        document.add(Paragraph("\n"))
                    } else {
                        document.add(Paragraph().apply {
                            add(Chunk(it,
                                FontFactory.getFont(FontFactory.TIMES, FONT_SIZE_BODY, Font.NORMAL)))
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
}