package store.rz.app.ui.reviews.writeReview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import store.rz.app.R
import kotlinx.android.synthetic.main.write_review_fragment.*
import store.rz.app.domain.Action
import store.rz.app.domain.State
import store.rz.app.ui.RzBaseFragment

class WriteReviewFragment :
    RzBaseFragment<State.WriteReviewState, String, WriteReviewViewModel>(WriteReviewViewModel::class.java) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.write_review_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            viewModel.adUuid = WriteReviewFragmentArgs.fromBundle(it).adUuid
        }

        confirmBtn.setOnClickListener { confirmReview() }
    }

    override fun renderState(state: State.WriteReviewState) {
        loadingFl.visibility = state.onViewLoadingVisibility
        if (state.goBack) {
            findNavController().navigateUp()
        }
    }

    private fun confirmReview() {
        when {
            writeReviewTiEt.text!!.isBlank() -> showMessage(getString(R.string.label_write_review_message))
            rateBar.rating.toInt() < 1 -> showMessage(getString(R.string.error_star_count_zero))
            else -> sendAction(Action.WriteReview(rateBar.rating.toInt(), writeReviewTiEt.text.toString()))
        }
    }
}
