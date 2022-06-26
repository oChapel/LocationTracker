package ua.com.foxminded.locationtrackera.ui.auth.reset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import ua.com.foxminded.locationtrackera.databinding.FragmentResetPasswordBinding
import ua.com.foxminded.locationtrackera.mvi.fragments.HostedFragment
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory
import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenEffect
import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenState
import ua.com.foxminded.locationtrackera.util.Utils

class ResetPasswordFragment : HostedFragment<
        ResetPasswordContract.View,
        ResetPasswordScreenState,
        ResetPasswordScreenEffect,
        ResetPasswordContract.ViewModel,
        ResetPasswordContract.Host>(),
    ResetPasswordContract.View, View.OnClickListener {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    override fun createModel(): ResetPasswordContract.ViewModel = ViewModelProvider(
        this, AuthViewModelFactory()
    ).get(ResetPasswordViewModel::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.resetPasswordBtn.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        model?.resetPassword(Utils.getTextFromEditText(binding.resetPasswordEditTextEmail))
    }

    override fun setProgressVisibility(isProgressVisible: Boolean) {
        setUpProgressBarVisibility(isProgressVisible)
    }

    override fun showToastMessage(idStringResource: Int) {
        Toast.makeText(context, idStringResource, Toast.LENGTH_LONG).show()
    }

    override fun showEmailError(emailError: Int) {
        binding.resetPasswordLayoutEmail.error =
            if (emailError == 0) null else getString(emailError)
    }

    private fun setUpProgressBarVisibility(isVisible: Boolean) {
        val progressBar: ProgressBar = binding.resetPasswordProgressBar
        val progressTargetAlpha = if (isVisible) 1f else 0f
        val shortAnimationDuration: Int = resources.getInteger(android.R.integer.config_shortAnimTime)
        if (progressTargetAlpha != progressBar.alpha) {
            progressBar.animate().alpha(progressTargetAlpha)
                .withStartAction(if (isVisible) Runnable { progressBar.visibility = View.VISIBLE } else null)
                .setDuration(shortAnimationDuration.toLong())
                .withEndAction(if (isVisible) null else Runnable { progressBar.visibility = View.INVISIBLE })
                .start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
