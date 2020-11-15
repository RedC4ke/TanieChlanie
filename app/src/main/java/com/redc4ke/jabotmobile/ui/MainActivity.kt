package com.redc4ke.jabotmobile.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.redc4ke.jabotmobile.R
import com.redc4ke.jabotmobile.data.menu.AlcoObject
import com.redc4ke.jabotmobile.data.menu.AlcoViewModel
import com.redc4ke.jabotmobile.ui.menu.MenuFragment
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var mDrawerLayout: DrawerLayout
    lateinit var menuFrag: MenuFragment
    lateinit var vm: AlcoViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        //NavHostFragment and navigation controller
        val host: NavHostFragment = supportFragmentManager
                .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = host.navController

        //Toolbar setup
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        mDrawerLayout = findViewById(R.id.drawer_layout)
        setSupportActionBar(toolbar)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.list_dest,
                R.id.about_dest,
                R.id.request_dest,
                R.id.options_dest
            ),
            mDrawerLayout
        )
        setupActionBar(navController, appBarConfiguration)

        vm = this.run {
            ViewModelProvider(this).get(AlcoViewModel::class.java)
        }


        //Firebase request (first from cache, then online)
        firebaseToRV(vm, false)
        firebaseToRV(vm, true)

        //Drawer setup
        setupNavigationMenu(navController)

        //Calls the welcome dialog fragment
        WelcomeFragment().show(supportFragmentManager, "welcome")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Inflate the drawer
        val navView = findViewById<NavigationView>(R.id.navigation)
        if (navView == null) {
            menuInflater.inflate(R.menu.drawer_menu, menu)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.navHostFragment))
                || super.onOptionsItemSelected(item)
    }

    private fun setupNavigationMenu(navController: NavController) {
        val sideNavView = findViewById<NavigationView>(R.id.navigation)
        sideNavView?.setupWithNavController(navController)
    }

    private fun setupActionBar(
        navController: NavController,
        appBarConfiguration: AppBarConfiguration
    ) {
        // This allows NavigationUI to decide what label to show in the action bar
        // By using appBarConfig, it will also determine whether to
        // show the up arrow or drawer menu icon
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Allows NavigationUI to support proper up navigation or the drawer layout
        // drawer menu, depending on the situation
        return findNavController(R.id.navHostFragment).navigateUp(appBarConfiguration)
    }

    override fun onBackPressed() {
        //Close drawer on back pressed if open
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun firebaseToRV(viewm: AlcoViewModel, isOnline: Boolean = false) {
        firebaseTask(isOnline)
                .addOnSuccessListener {
                    val tempList: MutableList<AlcoObject> = mutableListOf()
                    it.forEach { document ->

                        val data = document.data
                        val alcoObject = AlcoObject(
                            data["id"]?.toString()?.toInt(),
                            data["name"]?.toString(),
                            ((data["price"] as Map<*, *>)["min"] ?: error("Mapa sie zjebala"))
                                .toString().toFloat(),
                            ((data["price"] as Map<*, *>)["max"] ?: error("Mapa sie zjebala"))
                                .toString().toFloat(),
                            (data["price"] as Map<*, *>)["promo"]
                                ?.toString()?.toFloat(),
                            data["volume"].toString().toInt(),
                            (data["voltage"].toString().toFloat() * 100),
                            data["shop"] as ArrayList<String>?,
                            data["categories"] as ArrayList<Int>?
                        )
                        tempList.add(alcoObject)
                        Log.d("FireBase", "Added: $alcoObject")
                    }
                    viewm.addAll(tempList)
                    menuFrag.updateRV(viewm)
                }
                .addOnFailureListener {
                }

    }

}

private fun firebaseTask(isOnline: Boolean = false): Task<QuerySnapshot> {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val collection: CollectionReference
    if (isOnline) {
        db.enableNetwork()
    } else {
        db.disableNetwork()
    }
    collection = db.collection("wines")
    return collection.get()
}






