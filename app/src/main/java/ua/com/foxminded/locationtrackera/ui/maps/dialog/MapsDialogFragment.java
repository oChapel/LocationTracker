package ua.com.foxminded.locationtrackera.ui.maps.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.mvi.HostedDialogFragment;
import ua.com.foxminded.locationtrackera.ui.maps.MapsFragment;
import ua.com.foxminded.locationtrackera.ui.maps.dialog.state.MapsDialogScreenEffect;
import ua.com.foxminded.locationtrackera.ui.maps.dialog.state.MapsDialogScreenState;

public class MapsDialogFragment extends HostedDialogFragment<
        MapsDialogContract.View,
        MapsDialogScreenState,
        MapsDialogScreenEffect,
        MapsDialogContract.ViewModel,
        MapsDialogContract.Host> implements MapsDialogContract.View {

    @Override
    protected MapsDialogContract.ViewModel createModel() {
        return null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.logout_dialog_message)
                .setNegativeButton(R.string.cancel, ((dialogInterface, which) -> dismiss()))
                .setPositiveButton(R.string.logout, (dialogInterface, which)
                        -> ((MapsFragment) getParentFragment()).doPositiveButton())
                .create();
    }
}
