package ua.com.foxminded.locationtrackera.ui.tracker.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import ua.com.foxminded.locationtrackera.mvi.HostedDialogFragment;
import ua.com.foxminded.locationtrackera.ui.tracker.TrackerFragment;
import ua.com.foxminded.locationtrackera.ui.tracker.dialog.state.TrackerDialogScreenEffect;
import ua.com.foxminded.locationtrackera.ui.tracker.dialog.state.TrackerDialogScreenState;

public class TrackerDialogFragment extends HostedDialogFragment<
        TrackerDialogContract.View,
        TrackerDialogScreenState,
        TrackerDialogScreenEffect,
        TrackerDialogContract.ViewModel,
        TrackerDialogContract.Host>
        implements TrackerDialogContract.View {

    private static final String ARG_TYPE = "arg_type";
    private static final String DIALOG_TEXT_STRING_ID = "dialog_text";
    private static final String NEGATIVE_BUTTON_TEXT_STRING_ID = "negative_button";
    private static final String POSITIVE_BUTTON_TEXT_STRING_ID = "positive_button";

    public static TrackerDialogFragment newInstance(
            int argType,
            int dialogText,
            int negativeButtonText,
            int positiveButtonText
    ) {
        final Bundle b = new Bundle();
        b.putInt(ARG_TYPE, argType);
        b.putInt(DIALOG_TEXT_STRING_ID, dialogText);
        b.putInt(NEGATIVE_BUTTON_TEXT_STRING_ID, negativeButtonText);
        b.putInt(POSITIVE_BUTTON_TEXT_STRING_ID, positiveButtonText);
        final TrackerDialogFragment f = new TrackerDialogFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    protected TrackerDialogContract.ViewModel createModel() {
        return null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int argType = getArguments().getInt(ARG_TYPE);
        final int message = getArguments().getInt(DIALOG_TEXT_STRING_ID);
        final int negativeButton = getArguments().getInt(NEGATIVE_BUTTON_TEXT_STRING_ID);
        final int positiveButton = getArguments().getInt(POSITIVE_BUTTON_TEXT_STRING_ID);

        return new AlertDialog.Builder(requireActivity())
                .setTitle(message)
                .setNegativeButton(negativeButton, (dialogInterface, which)
                        -> ((TrackerFragment) getParentFragment()).doNegativeButton(argType))
                .setPositiveButton(positiveButton, (dialogInterface, which)
                        -> ((TrackerFragment) getParentFragment()).doPositiveButton(argType))
                .create();
    }
}
