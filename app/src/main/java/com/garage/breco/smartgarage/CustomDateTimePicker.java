package com.garage.breco.smartgarage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;


/**
 * Created by stormset on 2016. 11. 07.
 */
public class CustomDateTimePicker extends DatePickerDialog {
    private Button OK;
    private OnTimeSetClickListener OnTimeSetClickListener;

    public static DatePickerDialog newInstance(OnDateSetListener callback, Calendar initialSelection) {
        DatePickerDialog ret = new DatePickerDialog();
        ret.initialize(callback, initialSelection);
        return ret;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = super.onCreateView(inflater, container, state);
        LinearLayout buttonContainer = view.findViewById(
                com.wdullaer.materialdatetimepicker.R.id.mdtp_done_background);
        View clearButton = inflater.inflate(R.layout.date_picker_dialog_time_button,
                buttonContainer, false);
        clearButton.setOnClickListener(new ClearClickListener());
        buttonContainer.addView(clearButton, 0);
        OK = buttonContainer.findViewById(R.id.mdtp_ok);
        return view;
    }

    public void setOnTimeSetClickListener(OnTimeSetClickListener listener) {
        OnTimeSetClickListener = listener;
    }

    public OnTimeSetClickListener getOnDateClearedListener() {
        return OnTimeSetClickListener;
    }

    public interface OnTimeSetClickListener {

        /**
         * @param view The view associated with this listener.
         */
        void onTimeSet(CustomDateTimePicker view);

    }

    private class ClearClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            tryVibrate();
            OK.performClick();
            OnTimeSetClickListener listener = getOnDateClearedListener();
            if (listener != null) {
                listener.onTimeSet(CustomDateTimePicker.this);
            }

        }
    }
}
