package com.parveendala.rideshare.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parveendala.rideshare.R;
import com.parveendala.rideshare.model.Direction;
import com.parveendala.rideshare.model.Info;
import com.parveendala.rideshare.model.Leg;
import com.parveendala.rideshare.model.Route;
import com.parveendala.rideshare.model.Step;
import com.parveendala.rideshare.model.Waypoint;
import com.parveendala.rideshare.network.GeoCoderAsync;
import com.parveendala.rideshare.network.OnGeoCoderCompleted;
import com.parveendala.rideshare.network.RetrofitClient;
import com.parveendala.rideshare.utils.BitmapUtils;
import com.parveendala.rideshare.utils.DirectionUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/************************
 * Parveen Dala
 * 25th May, 2019
 * BikePool - Bengaluru
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, OnGeoCoderCompleted {

    private GoogleMap mMap;
    private GeoCoderAsync geoCoderAsync;
    ArrayList<Marker> markerPoints = new ArrayList();

    private Route driverRoute;
    private boolean mBackPressed;
    private ResultFragment resultFragment;
    private AddressFragment selectedFragment, driverFragment, passengerFragment;
    private BitmapDescriptor bitmapDescriptorGreen, bitmapDescriptorRed;
    private LatLng driverHomeLocation, driverOfficeLocation, passengerHomeLocation, passengerOfficeLocation;


    //Views
    private TextView tvTitle;
    private ProgressBar progressBar;
    private LinearLayout fragmentLayout;
    private FloatingActionButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void updateFragments(Fragment fragment) {
        if (fragment == driverFragment) {
            tvTitle.setText(getString(R.string.driver_ride));
        } else if (fragment == passengerFragment) {
            tvTitle.setText(getString(R.string.passenger_ride));
        } else if (fragment == resultFragment) {
            tvTitle.setText(getString(R.string.final_route));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(fragmentLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.title);
        progressBar = findViewById(R.id.progress_bar);
        fragmentLayout = findViewById(R.id.fragment_layout);
        btnNext = findViewById(R.id.btn_next);
        progressBar.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);

        driverFragment = new AddressFragment();
        selectedFragment = driverFragment;
        updateFragments(driverFragment);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onNextBtnClicked(View view) {
        if (getPosition() == 1) {
            if (passengerFragment == null) {
                passengerFragment = new AddressFragment();
                passengerFragment.setRetainInstance(true);
            } else {
                passengerFragment.setHomeAddress(getString(R.string.default_address));
                passengerFragment.setOfficeAddress(getString(R.string.default_address));
            }
            setNextBtnVisibility(false);
            selectedFragment = passengerFragment;
            updateFragments(passengerFragment);
        } else if (getPosition() == 2) {
            selectedFragment = null;
            getRoute();
        }
    }

    private void setResultFragment(boolean isValid, int initialDistance, int combinedDistance) {
        resultFragment = ResultFragment.newInstance(isValid, initialDistance, combinedDistance);
        selectedFragment = null;
        updateFragments(resultFragment);
    }

    public void onLeftBtnClicked(View view) {
        onBackPressed();
    }

    public void onRightBtnClicked(View view) {
        resetMap();
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed) {
            super.onBackPressed();
            return;
        }
        this.mBackPressed = true;
        Toast.makeText(this, getString(R.string.tap_again_to_exit), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBackPressed = false;
            }
        }, 2000);
    }

    private void resetMap() {
        markerPoints.clear();
        if (mMap != null) {
            mMap.clear();
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(12.9122866, 77.643372)).zoom(18f).tilt(10).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        driverFragment.setHomeAddress(getString(R.string.default_address));
        driverFragment.setOfficeAddress(getString(R.string.default_address));
        selectedFragment = driverFragment;
        updateFragments(driverFragment);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (markerPoints.size() == 0) {
            float[] hsv = new float[3];
            Color.colorToHSV(getRouteColor(1), hsv);
            bitmapDescriptorGreen = BitmapDescriptorFactory.defaultMarker(hsv[0]);
            Color.colorToHSV(getRouteColor(3), hsv);
            bitmapDescriptorRed = BitmapDescriptorFactory.defaultMarker(hsv[0]);
            resetMap();
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (progressBar.getVisibility() == View.VISIBLE || getPosition() == 3) {
                    return;
                } else if (getPosition() == 1) {
                    Toast.makeText(MainActivity.this, getString(R.string.tap_to_create_passenger_ride), Toast.LENGTH_SHORT).show();
                    return;
                } else if (getPosition() == 2) {
                    Toast.makeText(MainActivity.this, getString(R.string.tap_to_check_final_route), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    MarkerOptions options = new MarkerOptions();
                    options.position(latLng);
                    if ((markerPoints.size() + 1) == 1) {
                        driverHomeLocation = latLng;
                        options.title(getString(R.string.home_address));
                    } else if ((markerPoints.size() + 1) == 2) {
                        driverOfficeLocation = latLng;
                        options.title(getString(R.string.office_address));
                    } else if ((markerPoints.size() + 1) == 3) {
                        passengerHomeLocation = latLng;
                        options.title(getString(R.string.passenger_home_address));
                    } else if ((markerPoints.size() + 1) == 4) {
                        passengerOfficeLocation = latLng;
                        options.title(getString(R.string.passenger_office_address));
                    }
                    options.icon(getBitmapDescriptor(markerPoints.size() + 1));
                    markerPoints.add(mMap.addMarker(options));
                    getLocationAddress(latLng);
                }

            }
        });
    }

    private void getLocationAddress(LatLng latLng) {
        try {
            if (geoCoderAsync != null)
                geoCoderAsync.cancel(true);
            geoCoderAsync = new GeoCoderAsync(MainActivity.this, latLng.latitude, latLng.longitude);
            updateAddress(latLng.latitude + ", " + latLng.longitude);
            progressBar.setVisibility(View.VISIBLE);
            setNextBtnVisibility(false);
            geoCoderAsync.execute();
        } catch (Exception e) {
        }
    }

    @Override
    public void onGeoCoderCompleted(String address) {
        try {
            if (null != address)
                updateAddress(address);
            getRoute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private int getRouteColor(int position) {
        if (position == 1 || position == 2)
            return Color.GREEN;
        else
            return Color.RED;
    }

    private BitmapDescriptor getBitmapDescriptor(int position) {
        if (position == 1 || position == 2)
            return bitmapDescriptorGreen;
        else
            return bitmapDescriptorRed;
    }

    private int getPosition() {
        if (markerPoints.size() == 2 && selectedFragment != null && selectedFragment == driverFragment)
            return 1;
        else if (markerPoints.size() == 4 && selectedFragment != null && selectedFragment == passengerFragment)
            return 2;
        else if (markerPoints.size() == 4 && selectedFragment == null)
            return 3;
        else
            return -1;
    }

    private void setNextBtnVisibility(boolean visibility) {
        if (!visibility) {
            btnNext.setVisibility(View.GONE);
            return;
        }
        if (markerPoints.size() == 1 || markerPoints.size() == 3 || getPosition() == 3) {
            btnNext.setVisibility(View.GONE);
        } else if (markerPoints.size() == 2 || markerPoints.size() == 4) {
            btnNext.setVisibility(View.VISIBLE);
        }
    }

    private void updateAddress(String address) {
        if (markerPoints.size() == 1 || markerPoints.size() == 3) {
            selectedFragment.setHomeAddress(address);
        } else if (markerPoints.size() == 2 || markerPoints.size() == 4) {
            selectedFragment.setOfficeAddress(address);
        }
    }

    private void getRoute() {
        setNextBtnVisibility(false);
        progressBar.setVisibility(View.VISIBLE);

        String origin = null;
        String destination = null;
        String waypoints = null;
        if (getPosition() == 1) {
            origin = driverHomeLocation.latitude + "," + driverHomeLocation.longitude;
            destination = driverOfficeLocation.latitude + "," + driverOfficeLocation.longitude;
        } else if (getPosition() == 2) {
            origin = passengerHomeLocation.latitude + "," + passengerHomeLocation.longitude;
            destination = passengerOfficeLocation.latitude + "," + passengerOfficeLocation.longitude;
        } else if (getPosition() == 3) {
            waypoints = "via:" + passengerHomeLocation.latitude + "," + passengerHomeLocation.longitude + "|via:" + passengerOfficeLocation.latitude + "," + passengerOfficeLocation.longitude;
            origin = driverHomeLocation.latitude + "," + driverHomeLocation.longitude;
            destination = driverOfficeLocation.latitude + "," + driverOfficeLocation.longitude;
        } else {
            progressBar.setVisibility(View.GONE);
            setNextBtnVisibility(true);
            return;
        }
        Call<Direction> direction = RetrofitClient.getInstance().getRetrofitService().getDirection(
                origin, destination, waypoints,
                getResources().getString(R.string.google_maps_key));

        direction.enqueue(new Callback<Direction>() {
            @Override
            public void onResponse(Call<Direction> call, Response<Direction> response) {
                Direction direction = response.body();
                if (direction == null) {
                    Toast.makeText(MainActivity.this, getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
                    onRouteFailed();
                    return;
                }
                if (direction.isOK() && direction.getRouteList().size() > 0) {
                    Route route = direction.getRouteList().get(0);
                    updateMapRoute(route);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.error_unable_to_find_route), Toast.LENGTH_SHORT).show();
                    onRouteFailed();
                }
            }

            @Override
            public void onFailure(Call<Direction> call, Throwable t) {
                Toast.makeText(MainActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                onRouteFailed();
            }
        });
    }

    private void onRouteFailed() {
        if (getPosition() != 3) {
            markerPoints.get(markerPoints.size() - 1).remove();
            markerPoints.remove(markerPoints.size() - 1);
        } else {
            selectedFragment = passengerFragment;
            setNextBtnVisibility(true);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void updateMapRoute(Route route) {
        if (getPosition() == 3) {
            if (mMap != null)
                mMap.clear();
            int driverDistance = getRouteDistance(driverRoute);
            int finalDistance = getRouteDistance(route);
            boolean isValid = ((finalDistance - driverDistance) <= 1000);
            if (!isValid)
                route = driverRoute;
            setResultFragment(isValid, driverDistance, finalDistance);
        }

        progressBar.setVisibility(View.GONE);
        setNextBtnVisibility(true);
        int legCount = route.getLegList().size();
        for (int index = 0; index < legCount; index++) {
            Leg leg = route.getLegList().get(index);
            mMap.addMarker(new MarkerOptions().position(leg.getStartLocation().getCoordination()).title(getString(R.string.home_address)).icon(getBitmapDescriptor(markerPoints.size())));
            if (index == legCount - 1) {
                mMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()).title(getString(R.string.office_address)).icon(getBitmapDescriptor(markerPoints.size())));
            }
            List<Step> stepList = leg.getStepList();
            ArrayList<PolylineOptions> polylineOptionList = DirectionUtils.createTransitPolyline(this, stepList, 4, getRouteColor(markerPoints.size()), 2, Color.BLUE);
            for (PolylineOptions polylineOption : polylineOptionList) {
                mMap.addPolyline(polylineOption);
            }
            List<Waypoint> waypointsList = leg.getViaWaypointList();
            if (waypointsList != null)
                for (int i = 0; i < waypointsList.size(); i++) {
                    if (waypointsList.get(i).getLocation() != null)
                        mMap.addMarker(new MarkerOptions().position(waypointsList.get(i).getLocation().getCoordination())
                                .title(i == 0 ? getString(R.string.passenger_home_address) : getString(R.string.passenger_office_address))
                                .icon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.getBitmap(MainActivity.this, R.drawable.svg_flag))));
                }
        }

        setCameraWithCoordinationBounds(route, mMap);

        if (getPosition() == 1) {
            driverRoute = route;
            markerPoints.get(markerPoints.size() - 1).remove();
            markerPoints.get(markerPoints.size() - 2).remove();
            Toast.makeText(this, getString(R.string.tap_to_create_passenger_ride), Toast.LENGTH_SHORT).show();
        } else if (getPosition() == 2) {
            markerPoints.get(markerPoints.size() - 1).remove();
            markerPoints.get(markerPoints.size() - 2).remove();
            Toast.makeText(this, getString(R.string.tap_to_check_final_route), Toast.LENGTH_SHORT).show();
        } else if (getPosition() == 3) {
            Toast.makeText(this, getString(R.string.tap_to_start_again), Toast.LENGTH_SHORT).show();
        }
    }

    private int getRouteDistance(Route route) {
        if (route.getLegList() == null)
            return 0;
        int totalDistance = 0;
        for (int index = 0; index < route.getLegList().size(); index++) {
            Leg leg = route.getLegList().get(index);
            Info distance = leg.getDistance();
            if (distance != null) {
                String value = distance.getValue();
                if (value != null)
                    totalDistance += Integer.parseInt(value);
            }
        }
        return totalDistance;
    }

    private void setCameraWithCoordinationBounds(Route route, GoogleMap googleMap) {
        try {
            LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
            LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
            LatLngBounds bounds = new LatLngBounds(southwest, northeast);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}