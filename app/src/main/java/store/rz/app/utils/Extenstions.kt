package store.rz.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import store.rz.app.RzApplication
import io.objectbox.BoxStore
import store.rz.app.R
import java.util.*

const val RC_CAPTURE_IMAGE = 1000
const val RC_IMAGES = 1001

fun RzApplication.getBoxStore(): BoxStore {
    return this.boxStore
}

fun Context.changeLanguage(lang: Constants.Language) {
    val locale = Locale(lang.value)
    Locale.setDefault(locale)
    val config = this.resources.configuration
    config.setLocale(locale)
    this.createConfigurationContext(config)
    this.resources.updateConfiguration(config, this.resources.displayMetrics)
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.toggle() {
    this.visibility = if(this.visibility == View.GONE) View.VISIBLE else View.GONE
}

fun Context.openPaypalLink() {
    // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
    val builder = CustomTabsIntent.Builder()
    // set toolbar color and/or setting custom actions before invoking build()
    // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
    val customTabsIntent = builder.build()
    // and launch the desired Url with CustomTabsIntent.launchUrl()
    customTabsIntent.launchUrl(this, Uri.parse(Constants.PAYPAL_URL))
}

fun Fragment.openGallery(allowMultiple: Boolean) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple)
    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.type = "image/*"
//    startActivityForResult(Intent.createChooser(intent, "اختر صورة"), RC_IMAGES)
    startActivityForResult(intent, RC_IMAGES)
}

fun Fragment.openCamera(): Uri? {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val photoURI = FileProvider.getUriForFile(
        requireContext(),
        "${requireContext().packageName}.provider",
        getImageTempFile()
    )
    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    startActivityForResult(intent, RC_CAPTURE_IMAGE)

    return photoURI
}