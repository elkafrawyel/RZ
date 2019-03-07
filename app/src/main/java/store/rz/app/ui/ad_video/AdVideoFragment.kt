package store.rz.app.ui.ad_video

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.MediaController
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_ad_video.*

import store.rz.app.R
import store.rz.app.ui.MainActivity

class AdVideoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ad_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val videoUrl = AdVideoFragmentArgs.fromBundle(it).videoUrl

            playAdVideo(videoUrl)

            adVideoPlayer.setOnCompletionListener {
                replayAdVideoMbtn.visibility = View.VISIBLE
                backAdVideoMbtn.visibility = View.VISIBLE
                replayAdVideoMbtn.setOnClickListener {
                    adVideoPlayer.start()
                    replayAdVideoMbtn.visibility = View.GONE
                    backAdVideoMbtn.visibility = View.GONE

                    (requireActivity() as MainActivity).goNormal()
                }
                backAdVideoMbtn.setOnClickListener {
                    closeVideo()
                    (requireActivity() as MainActivity).goNormal()
                }

            }
        }

        (requireActivity() as MainActivity).goLandScape()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).goNormal()

    }
    private fun closeVideo() {
        findNavController().navigateUp()
    }

    private fun playAdVideo(videoUrl: String) {
        adVideoPlayer.keepScreenOn = true
        adVideoPlayer.setVideoURI(Uri.parse(videoUrl))
        adVideoPlayer.setOnPreparedListener {
            loadinPb.visibility = View.GONE
            adVideoPlayer.start()
            adVideoPlayer.setMediaController(MediaController(requireContext())) }

    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onStart() {
        super.onStart()
        hideSystemUI()
    }

    fun hideSystemUI() {
        val decorView = requireActivity().window.decorView
        decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
}
