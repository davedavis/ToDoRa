package io.davedavis.todora

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
//            with (sharedPrefs.edit()) {
//                putString(getString(R.string.prefs_jira_url_key), null)
//                apply()
//            }


        setContentView(R.layout.activity_main)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)


        val navView: NavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_backlog,
                R.id.nav_create,
                R.id.settings
            ), drawerLayout
        )


        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)

    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }
//
//    // https://stackoverflow.com/questions/59586207/androidx-preferences-and-navigation
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.show_backlog ->  HomeViewModel.
//            R.id.show_selected -> Timber.i("Selected Selected")
//            else -> Timber.i("Something Else Selected")
//        }
//
//        // If the settings item is selected, navigate to the settings fragment.
//        val navController = findNavController(R.id.nav_host_fragment)
//        return NavigationUI.onNavDestinationSelected(item, navController)
//                || super.onOptionsItemSelected(item)
//
//
//    }

    // Need to define a method on the activity to open the drawer so we can use the overflow for
    // filtering and not overwrite the onOptionsSelected calls.
    fun openDrawer() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.openDrawer(Gravity.LEFT)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}