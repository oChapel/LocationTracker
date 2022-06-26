package ua.com.foxminded.locationtrackera.ui.auth.registration

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.databinding.FragmentRegistrationBinding
import ua.com.foxminded.locationtrackera.mvi.fragments.HostedFragment
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenEffect
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenState
import ua.com.foxminded.locationtrackera.util.SafeNavigation
import ua.com.foxminded.locationtrackera.util.Utils

open class RegistrationFragment : HostedFragment<
        RegistrationContract.View,
        RegistrationScreenState,
        RegistrationScreenEffect,
        RegistrationContract.ViewModel,
        RegistrationContract.Host>(),
    RegistrationContract.View, View.OnClickListener {

    private var _binding: FragmentRegistrationBinding? = null
    protected val binding get() = _binding!!

    override fun createModel(): RegistrationContract.ViewModel =
        ViewModelProvider(this, AuthViewModelFactory()).get(RegistrationViewModel::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registerBtn.setOnClickListener(this)
        binding.registerLogInTxt.setOnClickListener(this)
        val watcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                model?.registrationDataChanged(
                    Utils.getTextFromEditText(binding.registerEditTextUserName),
                    Utils.getTextFromEditText(binding.registerEditTextUserEmail),
                    Utils.getTextFromEditText(binding.registerEditTextPassword)
                )
            }
        }
        binding.registerEditTextUserName.addTextChangedListener(watcher)
        binding.registerEditTextUserEmail.addTextChangedListener(watcher)
        binding.registerEditTextPassword.addTextChangedListener(watcher)
    }

    override fun onClick(view: View) {
        if (view === binding.registerBtn) {
            model?.registerUser(
                Utils.getTextFromEditText(binding.registerEditTextUserName),
                Utils.getTextFromEditText(binding.registerEditTextUserEmail),
                Utils.getTextFromEditText(binding.registerEditTextPassword)
            )
        } else if (view === binding.registerLogInTxt) {
            SafeNavigation.navigate(
                binding.root, R.id.registrationFragment,
                R.id.nav_from_registrationFragment_to_loginFragment
            )
        }
    }

    override fun setProgressVisibility(isProgressVisible: Boolean) {
        setUpProgressBarVisibility(isProgressVisible)
    }

    override fun showErrors(usernameError: Int, emailError: Int, passwordError: Int) {
        binding.registerLayoutUserName.error = if (usernameError == 0) null else getString(usernameError)
        binding.registerLayoutEmail.error = if (emailError == 0) null else getString(emailError)
        binding.registerLayoutPassword.error = if (passwordError == 0) null else getString(passwordError)
    }

    override fun proceedToNextScreen() {
        Toast.makeText(context, R.string.successful_registration, Toast.LENGTH_SHORT).show()
        SafeNavigation.navigate(
            binding.root, R.id.registrationFragment,
            R.id.nav_from_registrationFragment_to_trackerFragment
        )
    }

    override fun showFailureToastMessage(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
    }

    private fun setUpProgressBarVisibility(isVisible: Boolean) {
        val progressBar: ProgressBar = binding.registerProgressBar
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
