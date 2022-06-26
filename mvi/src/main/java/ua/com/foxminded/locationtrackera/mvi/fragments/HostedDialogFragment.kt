package ua.com.foxminded.locationtrackera.mvi.fragments

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import ua.com.foxminded.locationtrackera.mvi.states.ScreenEffect
import ua.com.foxminded.locationtrackera.mvi.states.ScreenState
import java.lang.reflect.ParameterizedType

abstract class HostedDialogFragment<
        VIEW : FragmentContract.View,
        STATE : ScreenState<VIEW>,
        ACTION : ScreenEffect<VIEW>,
        VIEW_MODEL : FragmentContract.ViewModel<STATE, ACTION>,
        HOST : FragmentContract.Host>
    : AppCompatDialogFragment(), FragmentContract.View, Observer<STATE>, LifecycleEventObserver {

    protected var model: VIEW_MODEL? = null
        private set

    protected var fragmentHost: HOST? = null
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentHost = try {
            context as HOST
        } catch (e: Throwable) {
            val hostClassName = ((javaClass.genericSuperclass as ParameterizedType)
                .actualTypeArguments[1] as Class<*>).canonicalName
            throw RuntimeException(
                "Activity must implement $hostClassName to attach ${this.javaClass.simpleName}", e
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setModel(createModel())
        lifecycle.addObserver(this)
        model?.getStateObservable()?.observe(this, this)
        model?.getEffectObservable()?.observe(this, { it.visit(this as VIEW) })
    }

    override fun onDetach() {
        super.onDetach()
        fragmentHost = null
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        model?.onStateChanged(event)

        if (lifecycle.currentState <= Lifecycle.State.DESTROYED) {
            lifecycle.removeObserver(this)
            model?.getEffectObservable()?.removeObservers(this)
            model?.getStateObservable()?.removeObservers(this)
        }
    }

    override fun onChanged(screenState: STATE) {
        screenState.visit(this as VIEW)
    }

    protected abstract fun createModel(): VIEW_MODEL?

    protected fun setModel(model: VIEW_MODEL?) {
        this.model = model
    }
}
