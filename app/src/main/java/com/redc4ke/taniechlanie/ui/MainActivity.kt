package com.redc4ke.taniechlanie.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.transition.TransitionInflater
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.redc4ke.taniechlanie.BuildConfig
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.AlcoViewModel
import com.redc4ke.taniechlanie.data.Shop
import com.redc4ke.taniechlanie.ui.menu.MenuFragment
import com.redc4ke.taniechlanie.ui.request.RequestFragment
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var prefs: SharedPreferences
    lateinit var menuFrag: MenuFragment
    lateinit var vm: AlcoViewModel
    lateinit var shopList: ArrayList<Shop>
    lateinit var faq: ArrayList<Map<String, String>>
    var currentFragment = 0
    val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        prefs = getSharedPreferences("com.redc4ke.taniechlanie", MODE_PRIVATE)

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

        //Preferences stuff
        checkPrefs()

        //Firebase request (first from cache, then online)
        getAlcoObject(vm)
        getShopList()
        getFaq()

        //Drawer setup
        setupNavigationMenu(navController)

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

    private fun getAlcoObject(viewm: AlcoViewModel, isOnline: Boolean = false) {
        getTask(isOnline, "wines")
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
                        data["shop"] as ArrayList<Int>?,
                        data["categories"] as ArrayList<Int>?,
                        data["photo"] as String?
                    )
                    tempList.add(alcoObject)
                    Log.d("FireBase", "Added: $alcoObject")
                }
                viewm.addAll(tempList)
                menuFrag.updateRV(viewm)
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext,
                    "Połączenie z bazą nieudane. " +
                            "Sprawdź czy posiadasz dostęp do Internetu " +
                            "i spróbuj ponownie.", Toast.LENGTH_LONG).show()
            }

        if (!isOnline) getAlcoObject(viewm, true)
    }

    private fun getShopList(isOnline: Boolean = false) {
        getTask(isOnline, "shops")
            .addOnSuccessListener {
                val tempList: ArrayList<Shop> = arrayListOf()
                it.forEach { document ->
                    val data = document.data
                    tempList.add(Shop(data["id"].toString().toInt(), data["name"] as String))
                }
                shopList = tempList
                Log.d("FireBase", "Added: $shopList")
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
            }
        if (!isOnline) getShopList(true)
    }

    private fun getFaq(isOnline: Boolean = false) {
        getTask(isOnline, "faq")
                .addOnSuccessListener {
                    val tempList: ArrayList<Map<String, String>> = arrayListOf()
                    it.forEach{document ->
                        val data = document.data
                        tempList.add(
                                mapOf(
                                        "question" to data["question"] as String,
                                        "answer" to data["answer"] as String)
                        )
                    }
                    faq = tempList
                    Log.d("FireBase", "Added: $faq")
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                }
        if (!isOnline) getFaq(true)
    }

    private fun getTask(isOnline: Boolean = false, colName: String): Task<QuerySnapshot> {
        if (isOnline) {
            database.enableNetwork()
        } else {
            database.disableNetwork()
        }
        val collection: CollectionReference = database.collection(colName)

        return if (colName == "shops")
            collection.orderBy("id").get()
        else
            collection.get()
    }

    private fun checkPrefs() {
        val key = "VERSION_CODE"
        val savedVersionCode = prefs.getInt(key, -1)
        val currentVersionCode = BuildConfig.VERSION_CODE

        when {
            savedVersionCode == -1 -> welcome()
            savedVersionCode == currentVersionCode -> return
            savedVersionCode < currentVersionCode -> welcome()
        }

        prefs.edit().putInt(key, currentVersionCode).apply()
    }

    private fun welcome() {
        //Display welcome fragment
        WelcomeFragment().show(supportFragmentManager, "welcome")
    }

    fun openBrowserFromTextView(view: View) {
        val url = view.tag as String
        openBrowser(url)
    }

    fun openBrowser(url: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.data = Uri.parse(url)

        startActivity(intent)
    }

}

fun setTransitions(fragment: Fragment, enter: Int?, exit: Int?) {
    val inflater = TransitionInflater.from(fragment.requireContext())
    val enterTrans =
            if (enter != null) inflater.inflateTransition(enter)
            else null
    val exitTrans =
            if (exit != null) inflater.inflateTransition(exit)
            else null
    fragment.apply {
        enterTransition = enterTrans
        exitTransition = exitTrans
        returnTransition = exitTrans
        allowEnterTransitionOverlap = true
        allowReturnTransitionOverlap = true
    }
}








