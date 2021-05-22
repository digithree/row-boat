package co.simonkenny.row

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import co.simonkenny.row.core.di.FakeDI
import java.net.MalformedURLException
import java.util.*


class MainActivity : AppCompatActivity() {

    private val articleRepo = FakeDI.instance.articleRepo
    private val settingsRepo = FakeDI.instance.settingsRepo
    private val airtableRepo = co.simonkenny.airtable.FakeDI.instance.airtableRepo

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var appNavigation: AppNavigation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(this, R.id.nav_host_fragment)

        appNavigation = AppNavigation(articleRepo, settingsRepo, airtableRepo, navController)

        // handle incoming URL, if any
        handleUrlIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // handle incoming URL, if any
        handleUrlIntent(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (item.onNavDestinationSelected(findNavController(this, R.id.nav_host_fragment))
                || super.onOptionsItemSelected(item))
    }

    override fun onStart() {
        super.onStart()
        appNavigation.register(this)
        settingsRepo.register(this)
    }

    override fun onStop() {
        super.onStop()
        appNavigation.unregister(this)
        settingsRepo.unregister(this)
    }

    // private helpers

    private fun handleUrlIntent(intent: Intent?): Boolean =
        with (findNavController(this, R.id.nav_host_fragment)) {
            intent?.let {
                if (it.action == Intent.ACTION_VIEW) {
                    try {
                        intent.data?.toString()?.run {
                            navigate(NavigationXmlDirections.articleAction(this))
                            return@with true
                        } ?: Log.e("MainActivity", "handleUrlIntent no URL data")
                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                        Log.e("MainActivity", "handleUrlIntent failed to parse URL")
                    }
                }
            }
            false
        }
}
