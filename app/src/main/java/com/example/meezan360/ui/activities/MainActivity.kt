package com.example.meezan360.ui.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import butterknife.ButterKnife
import butterknife.Unbinder
import com.example.meezan360.R
import com.example.meezan360.adapter.ExpListAdapter
import com.example.meezan360.databinding.ActivityMainBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.events.Event_Class
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.dialog.FingerprintPermissionDialog.FingerprintPermissionDialogFragment
import com.example.meezan360.utils.Constants
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.LoginViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : DockActivity() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var unbinder: Unbinder
        lateinit var navController: NavController
        lateinit var drawerLayout: DrawerLayout
        const val CAMERA_CODE: Int = 211
        const val GALLERY_CODE: Int = 212

    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var contentView: ConstraintLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    val END_SCALE = 0.7f
    private lateinit var actionBarMenu: Menu
    lateinit var imageDialog: Dialog
    lateinit var listDataChild: HashMap<String, List<String>>
    lateinit var listDataHeader: ArrayList<String>
    private val myViewModel: LoginViewModel by viewModel()
    private var logoutJob: Job? = null
    private var is_fingerprint_linked = false

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val sharedPreferences = getSharedPreferences("Meezan360", Context.MODE_PRIVATE)
        sharedPreferencesManager = SharedPreferencesManager(sharedPreferences)
        setContentView(binding.root)

        unbinder = ButterKnife.bind(this)
        initView()

        imageDialog = Dialog(this, R.style.DialogTheme)
        imageDialog.setContentView(R.layout.picture_dialog)
        handleAPIResponse()
    }

    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.nav_host_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        actionBarMenu = menu



        actionBarMenu.findItem(R.id.action_searches).setOnMenuItemClickListener {
//            if (SharedPrefKeyManager.get<Boolean>(Constants.IS_SHIFT) == true)
            val intent = Intent(this, MainFragActivity::class.java)
            startActivity(intent)

            true
        }



        return super.onCreateOptionsMenu(menu)
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun initView() {
        checkFingerprintSetup()
        drawerLayout = binding.drawerLayout
        setSupportActionBar(findViewById(R.id.toolBar))
        supportActionBar?.title = ""
        val titleTextView: TextView? = findViewById<TextView>(R.id.toolbar_title)


        contentView = binding.appBarMain.content
        navController = findNavController(R.id.nav_host_main)
        navController.setGraph(R.navigation.nav_graph)

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.sideLayout.userName.text = sharedPreferencesManager.getUserName()
        binding.sideLayout.userEmail.text = sharedPreferencesManager.getUserEmail()

        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)


        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = ContextCompat.getColor(this, R.color.purple_light)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        animateNavigationDrawer(drawerLayout)

        navigateToFragment(R.id.nav_home)
        prepareSideMenu()

        navController.addOnDestinationChangedListener { _, destination, _ ->

            val label = destination.label ?: getString(R.string.Home)
            titleTextView?.text = label
        }
    }

    private fun checkFingerprintSetup() {
        is_fingerprint_linked = false
        if (sharedPreferencesManager.get<Boolean>(Constants.IS_FINGERPRINT) == true) {
            is_fingerprint_linked = true
        }
    }

    private fun navigateToFragment(@IdRes id: Int, args: Bundle? = null) {

        closeDrawer()

        if (args != null) {
            navController.navigate(id, args)
            return
        }
        // navigation drawer
        navController.navigate(id)
    }

    fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun animateNavigationDrawer(drawerLayout: DrawerLayout) {

        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                // Scale the View based on current slide offset
                val diffScaledOffset: Float = slideOffset * (1 - END_SCALE)
                val offsetScale = 1 - diffScaledOffset
                contentView.scaleX = offsetScale
                contentView.scaleY = offsetScale


                // Translate the View, accounting for the scaled width
                val xOffset: Float = drawerView.width * slideOffset
                val xOffsetDiff: Float = contentView.width * diffScaledOffset / 2
                val xTranslation = xOffset - xOffsetDiff
                contentView.translationX = xTranslation

            }
        })
    }

    private fun prepareSideMenu() {

        listDataHeader = ArrayList<String>()
        listDataChild = HashMap<String, List<String>>()
        val icons = ArrayList<Int>()


        listDataHeader.add(Constants.DASHBOARD) //0

        listDataHeader.add(Constants.CHANGE_PASSWORD) //1

        if (is_fingerprint_linked) {
            listDataHeader.add(Constants.UNLINKED_FINGERPRINT) //2
        } else {
            listDataHeader.add(Constants.LINKED_FINGERPRINT) //2
        }

        listDataHeader.add(Constants.LOGOUT) //3


        val listAdapter = ExpListAdapter(
            this,
            listDataHeader,
            listDataChild,
            icons
        )



        binding.sideLayout.lvExp.setGroupIndicator(null)
        binding.sideLayout.lvExp.setChildIndicator(null)


        binding.sideLayout.userImage.setOnClickListener {
//            showImageDialog()
        }
        binding.sideLayout.lvExp.setAdapter(listAdapter as ExpListAdapter)


        binding.sideLayout.lvExp.setOnGroupClickListener { parent, v, groupPosition, id ->
            fragmentClickEvent(listDataHeader[groupPosition])
            true
        }
    }

    private fun showImageDialog() {
        imageDialog.window?.setBackgroundDrawableResource(R.color.zxing_transparent)
        val camera = imageDialog.findViewById<ImageView>(R.id.camera)
        val gallery = imageDialog.findViewById<ImageView>(R.id.gallery)
        camera.setOnClickListener {
            openCamera()
            imageDialog.dismiss()
        }
        gallery.setOnClickListener {
            openGallery()
            imageDialog.dismiss()
        }
        imageDialog.show()
    }

    private fun openCamera() {
        startActivityForResult(Intent().apply {
            action = MediaStore.ACTION_IMAGE_CAPTURE
        }, CAMERA_CODE)
    }

    private fun openGallery() {
        startActivityForResult(Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT

        }, GALLERY_CODE)
    }


    private fun fragmentClickEvent(itemString: String?) {

        when (itemString) {
            Constants.DASHBOARD -> {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                closeDrawer()
            }

            Constants.CHANGE_PASSWORD -> {
                navigateToFragment(R.id.action_dashboard_to_change_Password_Fragment)
            }

            Constants.LINKED_FINGERPRINT -> {
                EventBus.getDefault().post(Event_Class.linked_fingerprint(is_fingerprint_linked))
                closeDrawer()
            }

            Constants.UNLINKED_FINGERPRINT -> {
                EventBus.getDefault().post(Event_Class.linked_fingerprint(is_fingerprint_linked))
                closeDrawer()
            }


            Constants.LOGOUT -> {
                showLogOutAlert()
            }
        }
    }


    fun showLogOutAlert() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Logout")
        alertDialog.setMessage("Do you want to Logout?")
        alertDialog.setPositiveButton(
            "Yes"
        ) { dialog, which ->
            logout()
        }
        alertDialog.setNegativeButton(
            "No"
        ) { dialog: DialogInterface, which: Int -> dialog.cancel() }
        alertDialog.show()
    }

    private fun handleAPIResponse() {
        logoutJob = lifecycleScope.launch {
            myViewModel.logoutData.collect {
                hideProgressIndicator()
                when (it) {
                    is ResponseModel.Error -> {
                        this@MainActivity.handleErrorResponse(this@MainActivity, it)
                    }

                    is ResponseModel.Idle -> {

                    }

                    is ResponseModel.Loading -> {}

                    is ResponseModel.Success -> {
                        sharedPreferencesManager.logout()
                        it.message?.let { it1 -> showSuccessMessage(it1) }
                        val intent = Intent(this@MainActivity, LoginScreen::class.java)
                        startActivity(intent)
                    }
                }

            }
        }

    }

    private fun logout() {
        showProgressIndicator()
//        sharedPreferencesManager.clearSharedPreferences()
        myViewModel.viewModelScope.launch {
            myViewModel.logoutRequest()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFingerprintEvent(event: Event_Class.update_sidebar) {
        checkFingerprintSetup()
        prepareSideMenu()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

}