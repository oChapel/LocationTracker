package ua.com.foxminded.locationtrackera.ui.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.databinding.FragmentLoginBinding
import ua.com.foxminded.locationtrackera.mvi.fragments.HostedFragment
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory
import ua.com.foxminded.locationtrackera.ui.auth.login.state.LoginScreenEffect
import ua.com.foxminded.locationtrackera.ui.auth.login.state.LoginScreenState
import ua.com.foxminded.locationtrackera.util.SafeNavigation
import ua.com.foxminded.locationtrackera.util.Utils

open class LoginFragment : HostedFragment<
        LoginContract.View,
        LoginScreenState,
        LoginScreenEffect,
        LoginContract.ViewModel,
        LoginContract.Host>(),
    LoginContract.View, View.OnClickListener {

    protected var binding: FragmentLoginBinding? = null

    override fun createModel(): LoginContract.ViewModel =
        ViewModelProvider(this, AuthViewModelFactory()).get(LoginViewModel::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        this.binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.loginBtn?.setOnClickListener(this)
        binding?.signUpTxt?.setOnClickListener(this)
        binding?.forgotPasswordTxt?.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when {
            view === binding?.loginBtn -> {
                model?.login(
                    Utils.getTextFromEditText(binding?.loginEditTextEmail),
                    Utils.getTextFromEditText(binding?.loginEditTextPassword)
                )
            }
            view === binding?.signUpTxt -> {
                SafeNavigation.navigate(
                    binding?.root, R.id.loginFragment,
                    R.id.nav_from_loginFragment_to_registrationFragment
                )
            }
            view === binding?.forgotPasswordTxt -> {
                SafeNavigation.navigate(
                    binding?.root, R.id.loginFragment,
                    R.id.nav_from_loginFragment_to_resetPasswordFragment
                )
            }
        }
    }

    override fun setProgressVisibility(isProgressVisible: Boolean) {
        setUpProgressBarVisibility(isProgressVisible)
    }

    override fun proceedToNextScreen() {
        Toast.makeText(context, R.string.successful_login, Toast.LENGTH_SHORT).show()
        SafeNavigation.navigate(
            binding?.root, R.id.loginFragment,
            R.id.nav_from_loginFragment_to_trackerFragment
        )
    }

    override fun showFailureToastMessage() {
        Toast.makeText(context, R.string.login_failed, Toast.LENGTH_SHORT).show()
    }

    override fun showEmailAndPasswordError(emailError: Int, passwordError: Int) {
        binding?.loginLayoutEmail?.error = if (emailError == 0) null else getString(emailError)
        binding?.loginLayoutPassword?.error = if (passwordError == 0) null else getString(passwordError)
    }

    private fun setUpProgressBarVisibility(isVisible: Boolean) {
        val progressBar: ProgressBar? = binding?.loginProgressBar
        val progressTargetAlpha = if (isVisible) 1f else 0f
        val shortAnimationDuration: Int =
            resources.getInteger(android.R.integer.config_shortAnimTime)
        if (progressTargetAlpha != progressBar?.alpha) {
            progressBar?.animate()?.alpha(progressTargetAlpha)
                ?.withStartAction(if (isVisible) Runnable { progressBar.visibility = View.VISIBLE } else null)
                ?.setDuration(shortAnimationDuration.toLong())
                ?.withEndAction(if (isVisible) null else Runnable { progressBar.visibility = View.INVISIBLE })
                ?.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
