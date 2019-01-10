package com.hmaserv.rz.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hmaserv.rz.ui.MainActivity
import com.hmaserv.rz.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        MainActivity.start(this)
        finish()
    }
}
