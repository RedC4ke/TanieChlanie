package com.redc4ke.taniechlanie.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AdditionalUserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.*
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.data.viewmodels.ShopViewModel
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
import com.redc4ke.taniechlanie.databinding.ActivityMainBinding
import com.redc4ke.taniechlanie.ui.menu.MenuFragment
import com.redc4ke.taniechlanie.ui.popup.WelcomeFragment
import io.grpc.android.BuildConfig
import java.math.BigDecimal


class MainActivity : AppCompatActivity() {

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var shopViewModel: ShopViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences
    lateinit var menuFrag: MenuFragment
    lateinit var alcoObjectViewModel: AlcoObjectViewModel
    lateinit var faq: ArrayList<Map<String, String>>
    private val loginRC = 1
    private val auth = FirebaseAuth.getInstance()
    val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    var currentFragment = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Disable night theme (temporary)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //ANALYTICS DISABLED IN EXPERIMENTAL UNIT
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        prefs = getPreferences(MODE_PRIVATE)


        //NavHostFragment and navigation controller
        val host: NavHostFragment = supportFragmentManager
                .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = host.navController

        //Drawer setup
        setupNavigationMenu(navController)

        //Toolbar setup
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        mDrawerLayout = findViewById(R.id.drawer_layout)
        setSupportActionBar(toolbar)
        appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.profile_dest,
                        R.id.list_dest,
                        R.id.about_dest,
                        R.id.request_dest,
                        R.id.options_dest
                ),
                mDrawerLayout
        )
        setupActionBar(navController, appBarConfiguration)

        //Preferences stuff
        checkPrefs()

        //ViewModels
        alcoObjectViewModel = ViewModelProvider(this).get(AlcoObjectViewModel::class.java)
        shopViewModel = ViewModelProvider(this).get(ShopViewModel::class.java)
        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        //Firebase request (first from cache, then online)
        getFirebaseData()

        //Chceck current user
        auth.addAuthStateListener {
            userViewModel.login(this, it.currentUser)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Inflate the drawer
        val navView = findViewById<NavigationView>(R.id.navigation)
        if (navView == null) {
            menuInflater.inflate(R.menu.drawer_menu, menu)
            return true
        }

        //Drawer profile card
        val nameTV = findViewById<TextView?>(R.id.drawerNameTV)
        val loginTV = findViewById<TextView?>(R.id.drawerLoginTV)
        val avatarIV = findViewById<ImageView?>(R.id.drawerAvatarIV)
        val profileBT = findViewById<Button?>(R.id.drawerProfileBT)
        userViewModel.getUser().observe(this, { user ->
            if (user != null) {
                nameTV.text = user.displayName
                loginTV.text = getString(R.string.profile)
                profileBT.setOnClickListener {
                    this@MainActivity.findNavController(R.id.navHostFragment)
                        .navigate(R.id.profile_dest)
                    mDrawerLayout.closeDrawer(GravityCompat.START)
                }
                userViewModel.getUserUpdates().observe(this, {
                    setImage(this, "avatar", avatarIV, user.photoUrl)
                })
            } else {
                nameTV.text = getString(R.string.guest)
                loginTV.text = getString(R.string.login)
                avatarIV.setImageResource(R.drawable.ic_baseline_account_circle_24)
                profileBT.setOnClickListener {
                    login()
                }
            }
        })

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Login
        if (requestCode == loginRC) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                if (response!!.isNewUser) {
                    userViewModel.login(this, auth.currentUser, true)
                }
            } else if (response != null) {
                val message =
                    if (response.error?.errorCode == ErrorCodes.NO_NETWORK)
                        getString(R.string.err_no_connection)
                    else
                        getString(R.string.error, response.error?.errorCode.toString())
                Toast.makeText(
                        applicationContext,
                        message,
                        Toast.LENGTH_LONG).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getAlcoObject() {
        getTask("wines")
                .addOnCompleteListener {
                    getShopList()
                }
                .addOnSuccessListener {
                    it.forEach { document ->
                        val data = document.data
                        val output = mapOf<String, Any?>(
                                "id" to data["id"].toString().toInt(),
                                "name" to data["name"].toString(),
                                "volume" to data["volume"].toString().toInt(),
                                "voltage" to data["voltage"].toString().toBigDecimal(),
                                "categories" to data["categories"] as ArrayList<Int>,
                                "photo" to data["photo"] as String?
                        )
                        getPrices(output as MutableMap<String, Any?>)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext,
                            "Połączenie z bazą nieudane. " +
                                    "Sprawdź czy posiadasz dostęp do Internetu " +
                                    "i spróbuj ponownie.", Toast.LENGTH_LONG).show()
                }
    }

    private fun getPrices(input: MutableMap<String, Any?>) {
        val result: MutableMap<String, Any?> = input
        database.collection("prices").document(input["id"].toString()).get()
            .addOnSuccessListener { it ->
                val data = it.data
                val shopList = (data?.get("shop") as Map<String, Map<String, Any>>)
                val shopIds = mutableListOf<Int>()
                shopList.forEach {
                    shopIds.add(it.key.toInt())
                }
                result["shop"] = shopIds

                val priceList = arrayListOf<BigDecimal>()
                shopList.forEach {
                    val price = it.value["price"]?.toString()?.toBigDecimal()
                    if (price != null)
                        priceList.add(price)
                }
                result["price"] = priceList.minByOrNull { it }
                    ?: (0).toBigDecimal()

                val alcoObject = AlcoObject(
                        result["id"] as Int,
                        result["name"] as String,
                        result["price"] as BigDecimal,
                        result["volume"] as Int,
                        result["voltage"] as BigDecimal,
                        result["shop"] as ArrayList<Int>,
                        result["categories"] as ArrayList<Int>,
                        result["photo"] as String?
                )

                Log.d("FireBase", "Added: $alcoObject")
                alcoObjectViewModel.addObject(alcoObject)
                menuFrag.updateRV()

            }
    }

    private fun getShopList() {
        getTask("shops")
                .addOnCompleteListener {
                    getFaq()
                }
                .addOnSuccessListener {
                    it.forEach { document ->
                        val data = document.data
                        shopViewModel.add(Shop(data["id"].toString().toInt(), data["name"] as String))
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                }
    }

    private fun getFaq() {
        getTask("faq")
                .addOnCompleteListener {
                    getCategories()
                }
                .addOnSuccessListener {
                    val tempList: ArrayList<Map<String, String>> = arrayListOf()
                    it.forEach{ document ->
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
    }

    private fun getCategories() {
        getTask("categories")
                .addOnSuccessListener {
                    it.forEach { document ->
                        val id = document["id"].toString().toInt()
                        val name = document["name"] as String
                        val url = document["image"] as String

                        categoryViewModel.add(id, name, url, this)
                        Log.d("FireBase", "Added: $name")
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                }
    }

    private fun getTask(colName: String): Task<QuerySnapshot> {
        val collection: CollectionReference = database.collection(colName)

        return if (colName == "shops")
            collection.orderBy("id").get()
        else
            collection.orderBy("name").get()
    }

    private fun getFirebaseData() {
            getAlcoObject()
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

    private fun login() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )

        val layout = AuthMethodPickerLayout
            .Builder(R.layout.firebase_auth)
            .setEmailButtonId(R.id.loginMailBT)
            .setFacebookButtonId(R.id.loginFbBT)
            .build()

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setAuthMethodPickerLayout(layout)
                .setTheme(R.style.Theme_taniechlanie_AuthUI)
                .build(),
            loginRC
        )
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











