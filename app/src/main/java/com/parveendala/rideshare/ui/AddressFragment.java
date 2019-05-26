package com.parveendala.rideshare.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.parveendala.rideshare.R;

/************************
 * Parveen Dala
 * 25th May, 2019
 * BikePool - Bengaluru
 */
public class AddressFragment extends Fragment implements View.OnClickListener {

    private TextView tvHome, tvOffice;

    public AddressFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_layout, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        tvHome = view.findViewById(R.id.origin);
        tvOffice = view.findViewById(R.id.destination);
        tvHome.setOnClickListener(this);
        view.findViewById(R.id.origin_title).setOnClickListener(this);
        tvOffice.setOnClickListener(this);
        view.findViewById(R.id.destination_title).setOnClickListener(this);
        setHomeAddress(getString(R.string.default_address));
        setOfficeAddress(getString(R.string.default_address));
    }

    public void setHomeAddress(String address) {
        tvHome.setText(address);
    }

    public void setOfficeAddress(String address) {
        tvOffice.setText(address);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.origin_title:
            case R.id.origin:
                Toast.makeText(getActivity(), getString(R.string.select_home_address), Toast.LENGTH_SHORT).show();
                break;
            case R.id.destination_title:
            case R.id.destination:
                Toast.makeText(getActivity(), getString(R.string.select_office_address), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
