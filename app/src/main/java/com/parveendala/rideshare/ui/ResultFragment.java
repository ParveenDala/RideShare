package com.parveendala.rideshare.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.parveendala.rideshare.R;
import com.parveendala.rideshare.utils.Constants;

/************************
 * Parveen Dala
 * 25th May, 2019
 * BikePool - Bengaluru
 */
public class ResultFragment extends Fragment {

    private boolean isValid;
    private int initialDistance;
    private int combinedDistance;

    private ImageView ivIcon;
    private TextView tvResult, tvInitial, tvCombined, tvDifference;

    public ResultFragment() {
    }

    public static ResultFragment newInstance(boolean isValid, int initialDistance, int combinedDistance) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.IS_VALID, isValid);
        args.putInt(Constants.INITIAL, initialDistance);
        args.putInt(Constants.COMBINED, combinedDistance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isValid = getArguments().getBoolean(Constants.IS_VALID, false);
            initialDistance = getArguments().getInt(Constants.INITIAL, 0);
            combinedDistance = getArguments().getInt(Constants.COMBINED, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.result_layout, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        ivIcon = view.findViewById(R.id.icon);
        tvResult = view.findViewById(R.id.result);
        tvInitial = view.findViewById(R.id.initial_distance);
        tvCombined = view.findViewById(R.id.combined_distance);
        tvDifference = view.findViewById(R.id.difference);
        setResult();
    }

    public void setResult() {
        try {
            if (isAdded()) {
                if (isValid) {
                    ivIcon.setImageResource(R.drawable.svg_done);
                    ivIcon.setBackgroundResource(R.drawable.shape_round_green);
                    tvResult.setText(getString(R.string.accept_ride_text));
                } else {
                    ivIcon.setImageResource(R.drawable.svg_close);
                    ivIcon.setBackgroundResource(R.drawable.shape_round_red);
                    tvResult.setText(getString(R.string.reject_ride_text));
                }
                tvInitial.setText(String.valueOf(initialDistance / 1000.0));
                tvCombined.setText(String.valueOf(combinedDistance / 1000.0));
                tvDifference.setText(String.valueOf((combinedDistance - initialDistance) / 1000.0));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateResult(boolean isValid, int initialDistance, int combinedDistance) {
        this.isValid = isValid;
        this.initialDistance = initialDistance;
        this.combinedDistance = combinedDistance;
        setResult();
    }
}