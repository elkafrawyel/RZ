package com.hmaserv.rz.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hmaserv.rz.domain.Action
import com.hmaserv.rz.domain.State
import com.hmaserv.rz.domain.observeEvent

abstract class TestBaseFragment<T : State, M, VM : TestViewModel<T, M>>(modelClassName: Class<VM>) : Fragment() {

    protected val viewModel : VM by lazy { ViewModelProviders.of(this).get(modelClassName) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(this, Observer { renderState(it) })
        viewModel.messages.observeEvent(this, ::showMessage)
    }

    abstract fun renderState(state: T)

    open fun showMessage(message: M) {
        if (message is String)
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun sendAction(action: Action) {
        viewModel.sendAction(action)
    }

}