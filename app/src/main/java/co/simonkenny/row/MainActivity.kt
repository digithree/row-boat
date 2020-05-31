package co.simonkenny.row

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.util.PatternsCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import co.simonkenny.row.base.Patterns
import co.simonkenny.row.databinding.ActivityMainBinding
import java.net.MalformedURLException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navController = findNavController(this, R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.collection_fragment,
            R.id.search_fragment,
            R.id.reader_fragment
        ))

        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomNav.apply {
            // can't use this as setOnNavigationItemSelectedListener needed below replaces it
            //setupWithNavController(navController)

            // it doesn't look like there's a way to get this for free, have to call searchAction
            // manually
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.search_action -> navController.navigate(NavigationXmlDirections.searchAction(""))
                    R.id.article_action -> navController.navigate(NavigationXmlDirections.articleAction(""))
                    else -> navController.navigate(it.itemId)
                }
                true
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.isVisible = destination.id != R.id.settings_fragment
        }

        // handle incoming search or URL, if any
        if (!handleSearchIntent(intent) && !handleUrlIntent(intent)) {
            navController.navigate(NavigationXmlDirections.searchAction(""))
        }
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
        (menu?.findItem(R.id.app_bar_search)?.actionView as SearchView).run {
            setSearchableInfo((getSystemService(Context.SEARCH_SERVICE) as SearchManager)
                .getSearchableInfo(componentName))
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(this, R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
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
