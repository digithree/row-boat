package co.simonkenny.row.settings

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.simonkenny.row.core.di.FakeDI
import co.simonkenny.row.coresettings.PdfSettingsData
import co.simonkenny.row.coresettings.SizeData
import co.simonkenny.row.coresettings.sizeDataFromOrdinal
import co.simonkenny.row.settings.databinding.FragSettingsBinding
import co.simonkenny.row.util.UiState

class SettingsFragment : Fragment() {

    private val settingsRepo = FakeDI.instance.settingsRepo

    private lateinit var binding: FragSettingsBinding

    private var uiUpdateFreeze: Boolean = false

    private val viewModel: SettingsViewModel by lazy {
        ViewModelProvider(requireActivity().viewModelStore, object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                if (modelClass == SettingsViewModel::class.java) {
                    return SettingsViewModel(settingsRepo) as T
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
                    if (!uiUpdateFreeze) viewModel.updatePdfSettings(
                        PdfSettingsData(
                            textSize = progress.sizeDataFromOrdinal()
                        )
                    ) }
            )
            sbSettingsPdfLineSpSize.setOnSeekBarChangeListener(
                SettingsOnSeekBarChangeListener { progress ->
                    if (!uiUpdateFreeze) viewModel.updatePdfSettings(
                        PdfSettingsData(
                            lineSpacingSize = progress.sizeDataFromOrdinal()
                        )
                    ) }
            )
            sbSettingsPdfMarginVertSize.setOnSeekBarChangeListener(
                SettingsOnSeekBarChangeListener { progress ->
                    if (!uiUpdateFreeze) viewModel.updatePdfSettings(
                        PdfSettingsData(
                            marginVertSize = progress.sizeDataFromOrdinal()
                        )
                    ) }
            )
            sbSettingsPdfMarginHorzSize.setOnSeekBarChangeListener(
                SettingsOnSeekBarChangeListener { progress ->
                    if (!uiUpdateFreeze) viewModel.updatePdfSettings(
                        PdfSettingsData(
                            marginHorzSize = progress.sizeDataFromOrdinal()
                        )
                    ) }
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.pdfSettingsData.observe(viewLifecycleOwner,
            Observer {
                when (it) {
                    is UiState.Success -> updateUi(it.data)
                    is UiState.Error -> {
                        it.exception.printStackTrace()
                        Toast.makeText(requireContext(), "Couldn't access settings", Toast.LENGTH_LONG).show()
                    }
                }
            })
        viewModel.init()
    }

    private fun updateUi(pdfSettingsData: PdfSettingsData) {
        with (pdfSettingsData) {
            with(binding) {
                checkNotNull(textSize)
                checkNotNull(lineSpacingSize)
                checkNotNull(marginVertSize)
                checkNotNull(marginHorzSize)

                uiUpdateFreeze = true

                sbSettingsPdfTextSize.progress =
                    textSize?.ordinal ?: SizeData.SIZE_MEDIUM.ordinal
                tvSettingsPdfTextSizeValue.text = textSize?.text(resources)
                tvSettingsPdfTextSizeValue.gravity =
                    textGravity(textSize ?: SizeData.SIZE_MEDIUM)

                sbSettingsPdfLineSpSize.progress = lineSpacingSize?.ordinal
                    ?: SizeData.SIZE_MEDIUM.ordinal
                tvSettingsPdfLineSpSizeValue.text = lineSpacingSize?.text(resources)
                tvSettingsPdfLineSpSizeValue.gravity = textGravity(
                    lineSpacingSize ?: SizeData.SIZE_MEDIUM
                )

                sbSettingsPdfMarginVertSize.progress = marginVertSize?.ordinal
                    ?: SizeData.SIZE_MEDIUM.ordinal
                tvSettingsPdfMarginVertSizeValue.text = marginVertSize?.text(resources)
                tvSettingsPdfMarginVertSizeValue.gravity = textGravity(
                    marginVertSize ?: SizeData.SIZE_MEDIUM
                )

                sbSettingsPdfMarginHorzSize.progress = marginHorzSize?.ordinal
                    ?: SizeData.SIZE_MEDIUM.ordinal
                tvSettingsPdfMarginHorzSizeValue.text = marginHorzSize?.text(resources)
                tvSettingsPdfMarginHorzSizeValue.gravity = textGravity(
                    marginHorzSize ?: SizeData.SIZE_MEDIUM
                )

                uiUpdateFreeze = false
            }
        }
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