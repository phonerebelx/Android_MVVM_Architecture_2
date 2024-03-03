package com.example.meezan360.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.meezan360.R
import com.example.meezan360.ui.fragments.SearchFragment.SearchFragment

class MainFragActivity : DockActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_frag)
        setSupportActionBar(findViewById(R.id.tbMainFrag))

        supportActionBar?.title = ""
        val titleTextView: TextView? = findViewById<TextView>(R.id.toolbar_title)
        titleTextView?.text = "Search"

        val searchFragment = SearchFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.llSearchFragment, searchFragment)
            commit()
        }
        val toolbar = findViewById<Toolbar>(R.id.tbMainFrag)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }
}