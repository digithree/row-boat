package co.simonkenny.row

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import co.simonkenny.row.collection.AddToCollectionBottomSheetDialogFragment
import co.simonkenny.row.navigation.*

class AppNavigation(
    private val navController: NavController
) {

    private val broadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
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
            }
        }
    }

    private var fragmentManager: FragmentManager? = null

    private var isRegistered = false

    fun registerReceiver(activity: AppCompatActivity) {
        activity.registerReceiver(broadcastReceiver, IntentFilter().apply {
            listOf(DESTINATION_READER, DESTINATION_SEARCH, DIALOG_ADD_TO_COLLECTION)
                .forEach { addAction(it) }
        })
        fragmentManager = activity.supportFragmentManager
        isRegistered = true
    }

    fun unregisterReceiver(activity: AppCompatActivity) {
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