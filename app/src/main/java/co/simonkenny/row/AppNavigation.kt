package co.simonkenny.row

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import co.simonkenny.airtable.AirtableExportHandler
import co.simonkenny.airtable.AirtableRepo
import co.simonkenny.row.collection.AddToCollectionBottomSheetDialogFragment
import co.simonkenny.row.core.article.ArticleRepo
import co.simonkenny.row.coresettings.SettingsRepo
import co.simonkenny.row.navigation.*
import co.simonkenny.row.pdf.PdfExportHandler
import co.simonkenny.row.util.RegisterableWithActivity

class AppNavigation(
    private val articleRepo: ArticleRepo,
    private val settingsRepo: SettingsRepo,
    private val airtableRepo: AirtableRepo,
    private val navController: NavController
): RegisterableWithActivity() {

    private val broadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context == null) return
            when (intent?.action) {
                DESTINATION_READER -> navController.navigate(NavigationXmlDirections
                    .articleAction(intent.getStringExtra(ARG_URL) ?: ""))
                DESTINATION_SEARCH -> navController.navigate(NavigationXmlDirections
                    .searchAction(intent.getStringExtra(ARG_QUERY) ?: ""))
                DIALOG_ADD_TO_COLLECTION -> fragmentManager?.run {
                    AddToCollectionBottomSheetDialogFragment
                        .newInstance(
                            requireNotNull(intent.getStringExtra(ARG_URL)),
                            intent.getStringExtra(ARG_TITLE)
                        )
                        .show(this@run,
                            AddToCollectionBottomSheetDialogFragment::class.java.name)
                }
                ACTION_EXPORT_TO_PDF -> PdfExportHandler(
                    context,
                    requireNotNull(intent.getStringExtra(ARG_URL)),
                    articleRepo,
                    settingsRepo
                )
                ACTION_UPLOAD_TO_AIRTABLE -> AirtableExportHandler(
                    context,
                    requireNotNull(intent.getStringExtra(ARG_URL)),
                    articleRepo,
                    settingsRepo,
                    airtableRepo
                )
            }
        }
    }

    private var fragmentManager: FragmentManager? = null

    override fun register(activity: AppCompatActivity) {
        super.register(activity)
        activity.registerReceiver(broadcastReceiver, IntentFilter().apply {
            NAVIGATION_ENDPOINTS.forEach { addAction(it) }
        })
        fragmentManager = activity.supportFragmentManager
    }

    override fun unregister(activity: AppCompatActivity) {
        super.unregister(activity)
        if (isRegistered) {
            try {
                activity.unregisterReceiver(broadcastReceiver)
            } catch (e: RuntimeException) {
                e.printStackTrace()
                Log.e("AppNavigation", "Couldn't unregister receiver")
            }
        }
        fragmentManager = null
    }
}