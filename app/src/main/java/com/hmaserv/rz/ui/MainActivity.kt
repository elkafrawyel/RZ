package com.hmaserv.rz.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hmaserv.rz.R
import java.util.*


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
    }

}
