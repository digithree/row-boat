package co.simonkenny.row

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import co.simonkenny.row.base.Patterns
import co.simonkenny.row.core.di.FakeDI
import co.simonkenny.row.databinding.ActivityMainBinding
import java.net.MalformedURLException
import java.util.*

class MainActivity : AppCompatActivity() {

    private val articleRepo = FakeDI.instance.articleRepo
    private val settingsRepo = FakeDI.instance.settingsRepo

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var appNavigation: AppNavigation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navController = findNavController(this, R.id.nav_host_fragment)

        appNavigation = AppNavigation(articleRepo, settingsRepo, navController)

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.settings_fragment,
            R.id.collection_fragment,
            R.id.search_fragment,
            R.id.reader_fragment
        ))

        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomNav.apply {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.search_action -> navController.navigate(NavigationXmlDirections.searchAction(""))
                    R.id.article_action -> navController.navigate(NavigationXmlDirections.articleAction(""))
                    R.id.settings_action -> navController.navigate(R.id.settings_navigation)
                    else -> navController.navigate(it.itemId)
                }
                true
            }
        }

        // handle incoming search or URL, if any
        handleSearchIntent(intent)
        handleUrlIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // handle incoming search or URL, if any
        handleSearchIntent(intent)
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
        return if (item.itemId == R.id.action_search) {
            onSearchRequested()
            true
        } else {
            (item.onNavDestinationSelected(findNavController(this, R.id.nav_host_fragment))
                    || super.onOptionsItemSelected(item))
        }
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

    private fun handleSearchIntent(intent: Intent?): Boolean =
        with (findNavController(this, R.id.nav_host_fragment)) {
            intent?.let {
                when (it.action) {
                    Intent.ACTION_SEARCH -> {
                        intent.getStringExtra(SearchManager.QUERY)?.run {
                            // first check search query for !url bang
                            if (trim().toLowerCase(Locale.US).startsWith("!url")) {
                                val parts = trim().split(" ")
                                if (parts.size == 2) {
                                    navigate(NavigationXmlDirections.articleAction(parts[1]))
                                    return@with true
                                }
                            }
                            // next, see if query matches web url link pattern
                            val matcher = Patterns.AUTOLINK_WEB_URL.matcher(this)
                            if (matcher.find()) {
                                navigate(NavigationXmlDirections.articleAction(this))
                            } else {
                                // otherwise, treat as search query
                                navigate(NavigationXmlDirections.searchAction(this))
                            }
                            return@with true
                        }
                    }
                    else -> Log.d("MainActivity", "handleSearchIntent nothing to handle")
                }
            }
            false
        }

    private fun handleUrlIntent(intent: Intent?): Boolean =
        with (findNavController(this, R.id.nav_host_fragment)) {
            intent?.let {
                when (it.action) {
                    Intent.ACTION_VIEW -> {
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
                    else -> Log.d("MainActivity", "handleSearchIntent nothing to handle")
                }
            }
            false
        }
}
