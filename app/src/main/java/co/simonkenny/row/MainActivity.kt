package co.simonkenny.row

import android.app.SearchManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import co.simonkenny.row.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navController = findNavController(this, R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.local_collection_fragment,
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
                    else -> navController.navigate(it.itemId)
                }
                true
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.visibility = if (destination.id == R.id.settings_fragment) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        // handle incoming search
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                navController.navigate(NavigationXmlDirections.searchAction(query))
            }
        }
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
        val navController = findNavController(this, R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}
