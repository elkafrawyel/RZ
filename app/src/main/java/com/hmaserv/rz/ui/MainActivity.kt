package com.hmaserv.rz.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hmaserv.rz.R
import com.hmaserv.rz.utils.Constants
import com.hmaserv.rz.utils.changeLanguage

class MainActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) {
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            context.startActivity(mainActivityIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        changeLanguage(Constants.Language.DEFAULT)
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

}
