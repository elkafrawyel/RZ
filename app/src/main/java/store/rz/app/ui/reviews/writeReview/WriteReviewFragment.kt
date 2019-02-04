package store.rz.app.ui.reviews.writeReview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import store.rz.app.R
import store.rz.app.domain.observeEvent
import kotlinx.android.synthetic.main.write_review_fragment.*

class WriteReviewFragment : Fragment() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(WriteReviewViewModel::class.java) }
    private var adUuid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.write_review_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.writeState.observeEvent(this) { onWriteStateChanged(it) }

        arguments?.let {
            adUuid = WriteReviewFragmentArgs.fromBundle(it).adUuid

            if (adUuid == null)
                findNavController().navigateUp()
        }

        rateBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { ratingBar, fl, b ->
            viewModel.count = rateBar.rating.toInt()
        }
        confirmBtn.setOnClickListener { confirmReview() }
    }

    private fun onWriteStateChanged(state: WriteReviewViewModel.WriteUiState) {
        when (state) {

            WriteReviewViewModel.WriteUiState.Loading -> {
                loadingFl.visibility = View.VISIBLE
            }
            is WriteReviewViewModel.WriteUiState.Success -> {
                loadingFl.visibility = View.GONE
                showMessage(getString(R.string.success_send_review))

                findNavController().navigateUp()
            }
            is WriteReviewViewModel.WriteUiState.Error -> {
                loadingFl.visibility = View.GONE
                showMessage(state.message)
            }
            WriteReviewViewModel.WriteUiState.NoInternetConnection -> {
                loadingFl.visibility = View.GONE
                showMessage(getString(R.string.label_no_internet_connection))
            }
        }
    }

    private fun confirmReview() {
        if (writeReviewTiEt.text!!.isBlank()) {
            showMessage(getString(R.string.label_write_review_message))
        } else {
            if (viewModel.count == 0) {
                showMessage(getString(R.string.error_star_count_zero))
            } else {
                if (adUuid == null)
                    findNavController().navigateUp()
                viewModel.writeReview(
                    adUuid!!,
//                    "25ef9be5-cad6-4980-a360-16b442dbb478",
                    viewModel.count,
                    writeReviewTiEt.text.toString()
                )
            }
        }
    }

    private fun showMessage(message: String) {
        val snackBar = Snackbar.make(rootViewCl, message, Snackbar.LENGTH_LONG)
        val view = snackBar.view
        val textView = view.findViewById<View>(R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snackBar.show()
    }
}
