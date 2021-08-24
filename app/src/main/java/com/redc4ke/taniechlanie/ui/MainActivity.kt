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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.*
import com.redc4ke.taniechlanie.data.viewmodels.*
import com.redc4ke.taniechlanie.databinding.ActivityMainBinding
import com.redc4ke.taniechlanie.ui.popup.WelcomeFragment
import io.grpc.android.BuildConfig


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var shopViewModel: ShopViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var faqViewModel: FaqViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences
    lateinit var alcoObjectViewModel: AlcoObjectViewModel
    private val loginRC = 1
    private val auth = FirebaseAuth.getInstance()
    private val firestoreRef = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Crete cache directory
        if (!cacheDir.exists()) cacheDir.mkdirs()

        //Disable night theme (temporary)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //ANALYTICS DISABLED IN EXPERIMENTAL UNIT
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        prefs = getPreferences(MODE_PRIVATE)


        //NavHostFragment and navigation controller
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.alcoListNH) as NavHostFragment
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
                R.id.favourite_dest,
                R.id.about_dest,
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
        faqViewModel = ViewModelProvider(this)[FaqViewModel::class.java]

        //Firebase request
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
                    this@MainActivity.findNavController(R.id.alcoListNH)
                        .navigate(R.id.profile_dest)
                    mDrawerLayout.closeDrawer(GravityCompat.START)
                }
                userViewModel.getAvatarUrl().observe(this, {
                    setImage(
                        this,
                        "avatar",
                        avatarIV,
                        it
                    )
                })
            } else {
                avatarIV.setImageResource(R.drawable.ic_baseline_account_circle_24)
                nameTV.text = getString(R.string.guest)
                loginTV.text = getString(R.string.login)
                profileBT.setOnClickListener {
                    login()
                }
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.alcoListNH))
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
        return findNavController(R.id.alcoListNH).navigateUp(appBarConfiguration)
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
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun getFirebaseData() {
        alcoObjectViewModel.fetch(firestoreRef, object : FirebaseListener {
            override fun onComplete(resultCode: Int) {
                if (resultCode != FirebaseListener.SUCCESS) {
                    Toast.makeText(
                        baseContext,
                        getString(R.string.connection_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        categoryViewModel.fetch(
            firestoreRef,
            applicationContext.filesDir,
            object : FirebaseListener {
                override fun onComplete(resultCode: Int) {
                    if (resultCode != FirebaseListener.SUCCESS) {
                        Toast.makeText(
                            baseContext,
                            getString(R.string.connection_error),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
        shopViewModel.fetch(firestoreRef, object : FirebaseListener {
            override fun onComplete(resultCode: Int) {
                if (resultCode != FirebaseListener.SUCCESS) {
                    Toast.makeText(
                        baseContext,
                        getString(R.string.connection_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        faqViewModel.fetch(firestoreRef)
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

    fun login() {
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











