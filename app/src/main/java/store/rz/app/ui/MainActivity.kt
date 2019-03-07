package store.rz.app.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import store.rz.app.R
import store.rz.app.utils.Constants
import store.rz.app.utils.changeLanguage

class MainActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context, launchType: Constants.LaunchType) {
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            mainActivityIntent.putExtra(Constants.NOTIFICATION_TARGET, launchType.name)
            context.startActivity(mainActivityIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        changeLanguage(Constants.Language.DEFAULT)
    }

    // 4 fun for ad video

    fun goLandScape() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    fun goNormal(){
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

}
