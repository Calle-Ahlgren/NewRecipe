package com.example.newrecipe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

public class TimeDialog extends AppCompatDialogFragment {
    private EditText hourInput;
    private EditText minuteInput;
    private TimeDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view  = inflater.inflate(R.layout.dialog_time_input, null);

        hourInput = view.findViewById(R.id.hour_input);
        minuteInput = view.findViewById(R.id.minute_input);

        builder.setView(view)
                .setTitle("Input Time")
                .setNegativeButton("Cancel", (dialog, which) -> Utility.hideKeyboardFrom(hourInput, hourInput.getContext()))
                .setPositiveButton("Ok", (dialog, which) -> {
                    String hours = hourInput.getText().toString();
                    String minutes = minuteInput.getText().toString();
                    int i = getArguments().getInt(AddNewRecipe.TIME_INPUT_FLAG);
                    listener.applyText(hours, minutes, i);
                    Utility.hideKeyboardFrom(hourInput, hourInput.getContext());
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (TimeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public interface TimeDialogListener {
        void applyText(String hours, String minutes, int timeInputFlag);
    }
}
