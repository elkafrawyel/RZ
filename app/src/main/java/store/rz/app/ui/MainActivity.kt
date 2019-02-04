package store.rz.app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

}
