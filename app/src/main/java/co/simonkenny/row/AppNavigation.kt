package co.simonkenny.row

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.navigation.NavController
import co.simonkenny.row.navigation.ARG_QUERY
import co.simonkenny.row.navigation.ARG_URL
import co.simonkenny.row.navigation.DESTINATION_READER
import co.simonkenny.row.navigation.DESTINATION_SEARCH

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
            }
        }
    }

    private var isRegistered = false

    fun registerReceiver(activity: Activity) {
        activity.registerReceiver(broadcastReceiver, IntentFilter().apply {
            listOf(DESTINATION_READER, DESTINATION_SEARCH).forEach { addAction(it) }
        })
        isRegistered = true
    }

    fun unregisterReceiver(activity: Activity) {
        if (isRegistered) {
            try {
                activity.unregisterReceiver(broadcastReceiver)
            } catch (e: RuntimeException) {
                e.printStackTrace()
                Log.e("AppNavigation", "Couldn't unregister receiver")
            }
        }
    }
}