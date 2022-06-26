package ua.com.foxminded.locationtrackera.ui.tracker.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import ua.com.foxminded.locationtrackera.mvi.fragments.HostedDialogFragment
import ua.com.foxminded.locationtrackera.ui.tracker.TrackerFragment
import ua.com.foxminded.locationtrackera.ui.tracker.dialog.state.TrackerDialogScreenEffect
import ua.com.foxminded.locationtrackera.ui.tracker.dialog.state.TrackerDialogScreenState

class TrackerDialogFragment : HostedDialogFragment<
        TrackerDialogContract.View,
        TrackerDialogScreenState,
        TrackerDialogScreenEffect,
        TrackerDialogContract.ViewModel,
        TrackerDialogContract.Host>(),
    TrackerDialogContract.View {

    override fun createModel(): TrackerDialogContract.ViewModel? {
        return null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val argType: Int = arguments!!.getInt(ARG_TYPE)
        val message: Int = arguments!!.getInt(DIALOG_TEXT_STRING_ID)
        val negativeButton: Int = arguments!!.getInt(NEGATIVE_BUTTON_TEXT_STRING_ID)
        val positiveButton: Int = arguments!!.getInt(POSITIVE_BUTTON_TEXT_STRING_ID)
        return AlertDialog.Builder(requireActivity())
            .setTitle(message)
            .setNegativeButton(negativeButton) { _: DialogInterface?, _: Int ->
                (parentFragment as TrackerFragment).doNegativeButton(argType)
            }
            .setPositiveButton(positiveButton) { _: DialogInterface?, _: Int ->
                (parentFragment as TrackerFragment).doPositiveButton(argType)
            }
            .create()
    }

    companion object {
        private const val ARG_TYPE = "arg_type"
        private const val DIALOG_TEXT_STRING_ID = "dialog_text"
        private const val NEGATIVE_BUTTON_TEXT_STRING_ID = "negative_button"
        private const val POSITIVE_BUTTON_TEXT_STRING_ID = "positive_button"

        fun newInstance(
            argType: Int,
            dialogText: Int,
            negativeButtonText: Int,
            positiveButtonText: Int
        ): TrackerDialogFragment {
            val b = Bundle()
            b.putInt(ARG_TYPE, argType)
            b.putInt(DIALOG_TEXT_STRING_ID, dialogText)
            b.putInt(NEGATIVE_BUTTON_TEXT_STRING_ID, negativeButtonText)
            b.putInt(POSITIVE_BUTTON_TEXT_STRING_ID, positiveButtonText)
            val f = TrackerDialogFragment()
            f.arguments = b
            return f
        }
    }
}
