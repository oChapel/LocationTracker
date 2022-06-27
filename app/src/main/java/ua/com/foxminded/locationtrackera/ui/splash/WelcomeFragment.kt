package ua.com.foxminded.locationtrackera.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.databinding.FragmentWelcomeBinding
import ua.com.foxminded.locationtrackera.util.SafeNavigation

open class WelcomeFragment : Fragment(), View.OnClickListener {

    protected var binding: FragmentWelcomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        this.binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.welcomeRegisterBtn?.setOnClickListener(this)
        binding?.welcomeLoginTxt?.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when {
            view === binding?.welcomeRegisterBtn -> SafeNavigation.navigate(
                binding?.root, R.id.welcomeFragment,
                R.id.nav_from_welcomeFragment_to_registrationFragment
            )
            view === binding?.welcomeLoginTxt -> SafeNavigation.navigate(
                binding?.root, R.id.welcomeFragment,
                R.id.nav_from_welcomeFragment_to_loginFragment
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
