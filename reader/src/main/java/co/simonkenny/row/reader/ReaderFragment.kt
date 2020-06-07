package co.simonkenny.row.reader

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import co.simonkenny.row.base.CustomTypefaceSpan
import co.simonkenny.row.core.UiState
import co.simonkenny.row.core.article.Article
import co.simonkenny.row.core.di.FakeDI
import co.simonkenny.row.navigation.Navigate
import co.simonkenny.row.reader.databinding.FragReaderBinding
import java.text.SimpleDateFormat
import java.util.*

class ReaderFragment : Fragment() {

    private val articleRepo = FakeDI.instance.articleRepo

    private val dataFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    private lateinit var binding: FragReaderBinding
    private val args: ReaderFragmentArgs by navArgs()

    private val viewModel: ReaderViewModel by lazy {
        ViewModelProvider(requireActivity().viewModelStore, object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                if (modelClass == ReaderViewModel::class.java) {
                    return ReaderViewModel(articleRepo) as T
                }
                throw IllegalArgumentException("Cannot create ViewMode of class ${modelClass.canonicalName}")
            }
        }).get(ReaderViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_reader, container, false)
        with (binding) {
            // set typefaces
            val typefaceBold = Typeface.createFromAsset(context?.assets, "fonts/LibreBaskerville-Bold.ttf")
            val typefaceRegular = Typeface.createFromAsset(context?.assets, "fonts/LibreBaskerville-Regular.ttf")
            val typefaceMono = Typeface.createFromAsset(context?.assets, "fonts/RobotoMono-Regular.ttf")
            tvReaderTitle.typeface = typefaceBold
            tvReaderUrl.typeface = typefaceMono
            tvReaderAttribution.typeface = typefaceRegular
            tvReaderPublisher.typeface = typefaceRegular
            tvReaderWelcomeTitle.typeface = typefaceBold
            tvReaderWelcomeBody.typeface = typefaceRegular
            // enable auto-html links
            tvReaderPublisher.movementMethod = LinkMovementMethod.getInstance()
            tvReaderBody.movementMethod = LinkMovementMethod.getInstance()
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.article.observe(viewLifecycleOwner,
            Observer { processObserved(it) })
        Log.d("ReaderFragment", "URL: $args.url")
        when {
            args.url.isNotBlank() -> {
                viewModel.fetchArticle(args.url)
                showWelcomeState(false)
            }
            viewModel.article.value != null ->
                processObserved(requireNotNull(viewModel.article.value))
            else -> showWelcomeState(true)
        }
        requireActivity().invalidateOptionsMenu()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        with (menu) {
            findItem(R.id.action_add_to_collection).isVisible =
                args.url.isNotBlank() || viewModel.article.value != null
            findItem(R.id.action_search).isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (item.itemId == R.id.action_add_to_collection) {
            if (args.url.isNotBlank()) {
                Navigate.addToCollection(requireContext(), args.url)
            } else if (viewModel.article.value != null) {
                getUrlFromObserved(viewModel.article.value)?.run {
                    Navigate.addToCollection(requireContext(), this)
                } ?: Log.e("ReaderFragment", "Cant share URL")
            } else Log.e("ReaderFragment", "Cant share URL")
            true
        } else super.onOptionsItemSelected(item)

    private fun getUrlFromObserved(observed: UiState<Article>?): String? =
        when (observed) {
            is UiState.Success -> observed.data.url
            else -> null
        }

    private fun processObserved(observed: UiState<Article>) {
        when (observed) {
            is UiState.Success -> {
                binding.pbReader.isGone = true
                showWelcomeState(false)
                setData(observed.data)
            }
            is UiState.Error -> {
                binding.pbReader.isGone = true
                showWelcomeState(false)
                observed.exception.printStackTrace()
                Toast.makeText(context, "Error fetching article for URL ${args.url}", Toast.LENGTH_SHORT).show()
            }
            is UiState.Loading -> {
                binding.pbReader.isVisible = true
                showWelcomeState(false)
            }
        }
    }

    private fun setData(article: Article) {
        with (binding) {
            with (article) {
                tvReaderTitle.text = title ?: "NO TITLE"
                tvReaderUrl.text = SpannableStringBuilder(url).apply {
                    setSpan(UnderlineSpan(), 0, length, 0)
                }
                tvReaderUrl.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
                }
                val dateFormatted: String? = date?.run {
                    dataFormatter.format(Date(this))
                }
                tvReaderAttribution.text = if (attribution != null && dateFormatted != null) {
                    "Attribution: $attribution at $dateFormatted"
                } else if (attribution != null) {
                    "Attribution: $attribution"
                } else if (dateFormatted != null) {
                    "At $dateFormatted"
                } else ""
                    .also { tvReaderAttribution.isGone = it.isBlank() }
                publisher?.run {
                    tvReaderPublisher.text = SpannableStringBuilder().apply {
                        append("Search the Media Bias/Fact Check for ")
                        append(toHtml("<a href=\"https://mediabiasfactcheck.com/?s=" + this@run + "\">" + this@run + "</a>"))
                    }
                } ?: run { tvReaderPublisher.isGone = true }
                body?.run {
                    // first clean up html of excessive whitespace
                    val cleanedBody = this
                        .replace("\t", "")
                        .replace(Regex.fromLiteral("(?m)(^ *| +(?= |$))"), "")
                        .replace(Regex.fromLiteral("(?m)^$([\r\n]+?)(^$[\r\n]+?^)+"), "$1")
                    // convert HTML to Spanned, which is marked up with Spans
                    val html: Spanned = toHtml(cleanedBody)
                    // load our three custom typefaces
                    val typefaceBold = Typeface.createFromAsset(context?.assets, "fonts/LibreBaskerville-Bold.ttf")
                    val typefaceItalic = Typeface.createFromAsset(context?.assets, "fonts/LibreBaskerville-Italic.ttf")
                    val typefaceRegular = Typeface.createFromAsset(context?.assets, "fonts/LibreBaskerville-Regular.ttf")
                    // list of new spans (as small info data class) and old spans which they will replace
                    val newSpansInfo = mutableListOf<SpanReplacementInfo>()
                    val oldSpans = mutableListOf<StyleSpan>()
                    // convert old spans to new span info, but don't do anything to the Spanned just yet
                    for (span in html.getSpans(0, html.length, StyleSpan::class.java)) {
                        val typeface = when (span.style) {
                            Typeface.BOLD -> typefaceBold
                            Typeface.ITALIC, Typeface.BOLD_ITALIC -> typefaceItalic
                            else -> typefaceRegular
                        }
                        newSpansInfo.add(SpanReplacementInfo(
                            CustomTypefaceSpan(typeface),
                            html.getSpanStart(span),
                            html.getSpanEnd(span),
                            html.getSpanFlags(span)
                        ))
                    }
                    // create a SpannableStringBuilder, as Spanned is just an interface and we're not
                    // guarenteed what the implementation will be
                    val builder = SpannableStringBuilder(html)
                    // remove the old spans
                    for (span in oldSpans) {
                        builder.removeSpan(span)
                    }
                    // set regular typeface over entire document, as default
                    builder.setSpan(CustomTypefaceSpan(typefaceRegular), 0, builder.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    // then set each specific CustomTypefaceSpan to replace the old StyleSpan
                    for (spanInfo in newSpansInfo) {
                        builder.setSpan(spanInfo.customTypefaceSpan, spanInfo.start, spanInfo.end, spanInfo.flags)
                    }
                    // finally, set to TextView
                    tvReaderBody.text = builder
                } ?: run { tvReaderBody.text = "No Article Body" }
            }
        }
    }

    private fun showWelcomeState(show: Boolean) {
        with (binding) {
            llReaderWelcome.isVisible = show
            svReaderContent.isGone = show
            if (show) {
                pbReader.isVisible = false
            }
        }
    }

    private fun toHtml(htmlStr: String) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(htmlStr, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(htmlStr)
    }

    private data class SpanReplacementInfo(
        val customTypefaceSpan: CustomTypefaceSpan,
        val start: Int,
        val end: Int,
        val flags: Int
    )
}