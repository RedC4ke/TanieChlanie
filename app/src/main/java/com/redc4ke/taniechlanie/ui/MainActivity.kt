package com.redc4ke.taniechlanie.ui

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.bumptech.glide.Glide
import com.firebase.ui.auth.*
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.*
import com.redc4ke.taniechlanie.data.viewmodels.*
import com.redc4ke.taniechlanie.databinding.ActivityMainBinding
import com.redc4ke.taniechlanie.ui.MainActivity.Utility.longToast
import com.redc4ke.taniechlanie.ui.popup.WelcomeFragment
import io.grpc.android.BuildConfig
import kotlin.math.sign


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var shopViewModel: ShopViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var faqViewModel: FaqViewModel
    private lateinit var filterViewModel: FilterViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences
    lateinit var alcoObjectViewModel: AlcoObjectViewModel
    private val auth = FirebaseAuth.getInstance()
    private val firestoreRef = FirebaseFirestore.getInstance()

    //Sign in
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    //Prefs
    object Preferences {
        var roundedmr = false
    }

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
                R.id.list_dest,
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
        filterViewModel = ViewModelProvider(this)[FilterViewModel::class.java]

        //Firebase request
        getFirebaseData()

        // Chceck current user
        auth.addAuthStateListener {
            userViewModel.login(it.currentUser, false, object : RequestListener {
                override fun onComplete(resultCode: Int) {
                    if (resultCode != RequestListener.SUCCESS) {
                        longToast(applicationContext, getString(R.string.toast_error))
                    }
                }
            })
        }

        //Setup the filter
        alcoObjectViewModel.getAll().observe(this) {
            filterViewModel.setAlcoObjectList(it)
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
        userViewModel.getUser().observe(this) { user ->
            if (user != null) {
                nameTV.text = user.displayName
                loginTV.text = getString(R.string.profile)
                profileBT.setOnClickListener {
                    this@MainActivity.findNavController(R.id.alcoListNH)
                        .navigate(R.id.profile_dest)
                    mDrawerLayout.closeDrawer(GravityCompat.START)
                }
                userViewModel.getAvatarUrl().observe(this) {
                    Glide.with(this).load(it).into(avatarIV)
                }
            } else {
                avatarIV.setImageResource(R.drawable.ic_baseline_account_circle_24)
                nameTV.text = getString(R.string.guest)
                loginTV.text = getString(R.string.login)
                profileBT.setOnClickListener {
                    login()
                }
            }
        }

        return true
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

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse

        if (result.resultCode == Activity.RESULT_OK) {

            // Testing TODO() remove
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //    val name = "xxx"
            //    val descriptionText = "xxx"
            //    val importance = NotificationManager.IMPORTANCE_DEFAULT
            //    val channel = NotificationChannel("CH1", name, importance).apply {
            //        description = descriptionText
            //    }
            //    // Register the channel with the system
            //    val notificationManager: NotificationManager =
            //        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //    notificationManager.createNotificationChannel(channel)
            //}
//
            //val intent = Intent(this, MainActivity::class.java)
            //val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
//
            //val welcomeNF = NotificationCompat.Builder(this, "CH1")
            //    .setSmallIcon(R.drawable.notification_default)
            //    .setContentTitle("xxx")
            //    .setContentText("ęąśćż")
            //    .setPriority(NotificationCompat.PRIORITY_HIGH)
            //    .setContentIntent(pendingIntent)
            //    .setAutoCancel(true)
//
            //with (NotificationManagerCompat.from(this)) {
            //    notify(0, welcomeNF.build())
            //}

            userViewModel.login(auth.currentUser, response!!.isNewUser, object : RequestListener {
                override fun onComplete(resultCode: Int) {
                    if (resultCode != RequestListener.SUCCESS) {
                        longToast(applicationContext, getString(R.string.toast_error))
                    }
                }
            })
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

    fun getFirebaseData() {
        alcoObjectViewModel.fetch(firestoreRef, object : RequestListener {
            override fun onComplete(resultCode: Int) {
                if (resultCode != RequestListener.SUCCESS) {
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
            object : RequestListener {
                override fun onComplete(resultCode: Int) {
                    if (resultCode != RequestListener.SUCCESS) {
                        Toast.makeText(
                            baseContext,
                            getString(R.string.connection_error),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
        shopViewModel.fetch(firestoreRef, object : RequestListener {
            override fun onComplete(resultCode: Int) {
                if (resultCode != RequestListener.SUCCESS) {
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
        val savedVersionCode = prefs.getInt("VERSION_CODE", -1)
        val currentVersionCode = BuildConfig.VERSION_CODE

        when {
            savedVersionCode == -1 -> welcome()
            savedVersionCode == currentVersionCode -> return
            savedVersionCode < currentVersionCode -> welcome()
        }

        prefs.edit().putInt("VERSION_CODE", currentVersionCode).apply()

        Preferences.roundedmr = prefs.getBoolean("rounded_mR", false)
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

        val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setAuthMethodPickerLayout(layout)
                .setTheme(R.style.Theme_taniechlanie_AuthUI)
                .build()

        signInLauncher.launch(signInIntent)
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

    object Utility {
        fun longToast(context: Context, string: String) {
            Toast.makeText(context, string, Toast.LENGTH_LONG).show()
        }
    }
}











