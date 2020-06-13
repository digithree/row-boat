package co.simonkenny.row.settings

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.simonkenny.row.settings.databinding.FragSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var binding: FragSettingsBinding

    private var uiUpdateFreeze: Boolean = false

    private val viewModel: SettingsViewModel by lazy {
        ViewModelProvider(requireActivity().viewModelStore, object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                if (modelClass == SettingsViewModel::class.java) {
                    return SettingsViewModel(requireActivity().getPreferences(Context.MODE_PRIVATE)) as T
                }
                throw IllegalArgumentException("Cannot create ViewMode of class ${modelClass.canonicalName}")
            }
        }).get(SettingsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_settings, container, false)
        with (binding) {
            sbSettingsPdfTextSize.setOnSeekBarChangeListener(
                SettingsOnSeekBarChangeListener { progress ->
                    if (!uiUpdateFreeze) viewModel.updatePdfSettings(PdfSettingsData(textSize = progress.sizeDataFromOrdinal())) }
            )
            sbSettingsPdfLineSpSize.setOnSeekBarChangeListener(
                SettingsOnSeekBarChangeListener { progress ->
                    if (!uiUpdateFreeze) viewModel.updatePdfSettings(PdfSettingsData(lineSpacingSize = progress.sizeDataFromOrdinal())) }
            )
            sbSettingsPdfMarginVertSize.setOnSeekBarChangeListener(
                SettingsOnSeekBarChangeListener { progress ->
                    if (!uiUpdateFreeze) viewModel.updatePdfSettings(PdfSettingsData(marginVertSize = progress.sizeDataFromOrdinal())) }
            )
            sbSettingsPdfMarginHorzSize.setOnSeekBarChangeListener(
                SettingsOnSeekBarChangeListener { progress ->
                    if (!uiUpdateFreeze) viewModel.updatePdfSettings(PdfSettingsData(marginHorzSize = progress.sizeDataFromOrdinal())) }
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.pdfSettingsData.observe(viewLifecycleOwner,
            Observer {
                with (it) {
                    with (binding) {
                        checkNotNull(textSize)
                        checkNotNull(lineSpacingSize)
                        checkNotNull(marginVertSize)
                        checkNotNull(marginHorzSize)

                        uiUpdateFreeze = true

                        sbSettingsPdfTextSize.progress = textSize.ordinal
                        tvSettingsPdfTextSizeValue.text = textSize.text(resources)
                        tvSettingsPdfTextSizeValue.gravity = textGravity(textSize)

                        sbSettingsPdfLineSpSize.progress = lineSpacingSize.ordinal
                        tvSettingsPdfLineSpSizeValue.text = lineSpacingSize.text(resources)
                        tvSettingsPdfLineSpSizeValue.gravity = textGravity(lineSpacingSize)

                        sbSettingsPdfMarginVertSize.progress = marginVertSize.ordinal
                        tvSettingsPdfMarginVertSizeValue.text = marginVertSize.text(resources)
                        tvSettingsPdfMarginVertSizeValue.gravity = textGravity(marginVertSize)

                        sbSettingsPdfMarginHorzSize.progress = marginHorzSize.ordinal
                        tvSettingsPdfMarginHorzSizeValue.text = marginHorzSize.text(resources)
                        tvSettingsPdfMarginHorzSizeValue.gravity = textGravity(marginHorzSize)

                        uiUpdateFreeze = false
                    }
                }
            })
        viewModel.init()
    }

    private fun textGravity(sizeData: SizeData) =
        when (sizeData.ordinal) {
            0 -> Gravity.START
            1 -> Gravity.CENTER
            else -> Gravity.END
        }

    class SettingsOnSeekBarChangeListener(
        private val f: (progress: Int) -> Unit
    ): SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            f(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            // ignored
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            // ignored
        }
    }
}