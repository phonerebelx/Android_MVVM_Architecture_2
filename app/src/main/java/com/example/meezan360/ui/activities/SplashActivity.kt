package com.example.meezan360.ui.activities

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.example.meezan360.R
import com.example.meezan360.databinding.ActivitySplashBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.scottyab.rootbeer.RootBeer
import org.koin.android.ext.android.inject
class SplashActivity : DockActivity() {

    lateinit var binding: ActivitySplashBinding
    private val sharedPreferenceManager: SharedPreferencesManager by inject()

    private lateinit var gestureDetector: GestureDetectorCompat

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Initialize gesture detector
        gestureDetector = GestureDetectorCompat(this, MyGestureListener())

        if (sharedPreferenceManager.getToken() != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set onTouchListener to detect swipe-up gesture
        binding.root.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }


    // this gesture calculate your swipe up gesture
    inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onFling(
            downEvent: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e2 == null || downEvent == null) return false

            val diffY = e2.y - downEvent.y
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {

                val intent = Intent(this@SplashActivity, LoginScreen::class.java)
                val options = ActivityOptions.makeCustomAnimation(this@SplashActivity, R.anim.slide_up_in, R.anim.slide_up_out)
                startActivity(intent, options.toBundle())
                finish()
                return true
            }
            return false
        }
    }
}

//class SplashActivity : DockActivity() {
//
//    lateinit var binding: ActivitySplashBinding
//    private val sharedPreferenceManager: SharedPreferencesManager by inject()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivitySplashBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        if (sharedPreferenceManager.getToken() != null) {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//
//        binding.tvSwipe.setOnClickListener {
//            val intent = Intent(this, LoginScreen::class.java)
//            startActivity(intent)
//            finish()
//
//        }
//
//    }
//}