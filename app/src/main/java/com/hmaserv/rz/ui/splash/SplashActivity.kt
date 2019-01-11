package com.hmaserv.rz.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hmaserv.rz.ui.MainActivity
import com.hmaserv.rz.R
import com.hmaserv.rz.utils.changeLanguage

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        val splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        splashViewModel.getCurrentLanguage()
        splashViewModel.uiState.observe(this, Observer {
            when(it) {
                is SplashViewModel.SplashUiState.Success -> {
                    changeLanguage(it.language)
                    MainActivity.start(this)
                    finish()
                }
            }
        })
    }
}