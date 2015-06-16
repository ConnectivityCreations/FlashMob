package com.stridera.connectivitycreations.flashmob.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.stridera.connectivitycreations.flashmob.R;

import org.droidparts.widget.ClearableEditText;

public class FiltersDialog extends DialogFragment {
    private static final String SEARCH_RADIUS = "SearchRadius";
    private static final String SEARCH_LOCATION = "SearchLocation";
    private static final int DEFAULT_SEARCH_RADIUS = 20;
    private static final int DEFAULT_MAX_SEARCH_RADIUS = 100;


    SharedPreferences settings;

    Switch sUseLocation;
    TextView tvLocationTitle;
    ClearableEditText cetLocation;
    TextView tvDistance;
    SeekBar sbDistance;
    Button btnOK;
    Button btnCancel;

    public interface FiltersChangedListener {
        void onFiltersChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filters, container);
        String title = getResources().getString(R.string.edit_filters);
        getDialog().setTitle(title);

        sUseLocation = (Switch) view.findViewById(R.id.sUseCurrentLocation);
        tvLocationTitle = (TextView) view.findViewById(R.id.tvFilterLocation);
        cetLocation = (ClearableEditText) view.findViewById(R.id.cetLocation);
        tvDistance = (TextView) view.findViewById(R.id.tvDistance);
        sbDistance = (SeekBar) view.findViewById(R.id.sbDistance);
        btnOK = (Button) view.findViewById(R.id.btnSave);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);

        settings = getActivity().getSharedPreferences("Settings", 0);
        String searchLocation = settings.getString(SEARCH_LOCATION, "");
        int searchRadius = settings.getInt(SEARCH_RADIUS, DEFAULT_SEARCH_RADIUS);

        if (searchLocation.isEmpty()) {
            sUseLocation.setChecked(true);
            enableLocation(false);
        } else {
            sUseLocation.setChecked(false);
            enableLocation(true);
            cetLocation.setText(searchLocation);
        }

        sbDistance.setMax(DEFAULT_MAX_SEARCH_RADIUS - 1);
        sbDistance.setProgress(searchRadius);
        sbDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int radius = progress + 1;
                tvDistance.setText(radius + " mi");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tvDistance.setText(searchRadius + " mi");

        sUseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableLocation(!sUseLocation.isChecked());
            }
        });

        btnOK.setOnClickListener(onOk);
        btnCancel.setOnClickListener(onCancel);

        return view;
    }

    View.OnClickListener onOk = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences.Editor editor = settings.edit();

            int radius = sbDistance.getProgress() + 1;
            editor.putInt(SEARCH_RADIUS, radius);

            if (sUseLocation.isChecked()) {
                editor.putString(SEARCH_LOCATION, "");
            } else {
                editor.putString(SEARCH_LOCATION, cetLocation.getText().toString());
            }
            editor.commit();

            FiltersChangedListener listener = (FiltersChangedListener) getActivity();
            listener.onFiltersChanged();
            dismiss();
        }
    };

    View.OnClickListener onCancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    public void enableLocation(boolean checked) {
        tvLocationTitle.setEnabled(checked);
        cetLocation.setEnabled(checked);
    }


}
