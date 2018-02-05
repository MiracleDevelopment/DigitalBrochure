package com.ipati.dev.brochure;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ipati.dev.brochure.model.UserInfo;

import org.w3c.dom.Text;

public class RegisterActivity extends BaseApplication implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener
        , GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, View.OnTouchListener {
    private final static int KEY = 1010;
    Toolbar mToolbar;
    Button mSaving;
    GoogleApiClient googleApiClient;
    LatLng lastPosition;
    private static double longitude;
    private static double latitude;
    private static GoogleMap mGoogleMap;
    LocationAvailability locationAvailability;
    EditText edFirstname, edLastname, edGender, edBirth, edMobilePhone, edEmail, edProduct, edAddress, edDistrict, edProvice, edPostcode;
    LinearLayout mLinearlayout;
    String latitudeText, longtitudeText;
    static String keyValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            keyValue = bundle.getString("keyValue");
//            Toast.makeText(getApplicationContext(), keyValue, Toast.LENGTH_SHORT).show();
        }

        mToolbar = (Toolbar) findViewById(R.id.mToolbarRegister);
        mSaving = (Button) findViewById(R.id.bt_saveing);
        edFirstname = (EditText) findViewById(R.id.ed_firstname);
        edLastname = (EditText) findViewById(R.id.ed_lastname);
        edGender = (EditText) findViewById(R.id.ed_gender);
        edEmail = (EditText) findViewById(R.id.ed_email);
        edBirth = (EditText) findViewById(R.id.ed_date);
        edProduct = (EditText) findViewById(R.id.ed_product);
        edAddress = (EditText) findViewById(R.id.ed_Address);
        edMobilePhone = (EditText) findViewById(R.id.ed_mobile);
        edDistrict = (EditText) findViewById(R.id.ed_District);
        edProvice = (EditText) findViewById(R.id.ed_Province);
        edPostcode = (EditText) findViewById(R.id.ed_Postcode);
        mLinearlayout = (LinearLayout) findViewById(R.id.touch_item_view);
        mLinearlayout.setOnTouchListener(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Register");
        mSaving.setOnClickListener(this);

        MapFragment mapFragment = new MapFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.googleMap, mapFragment);
        transaction.commit();
        mapFragment.getMapAsync(this);


        googleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(LocationServices.API)
                .addOnConnectionFailedListener(this).addConnectionCallbacks(this).build();


    }

    private void saveDataInfo() {
        String firstName = edFirstname.getText().toString();
        String lastName = edLastname.getText().toString();
        String gender = edGender.getText().toString();
        String birth = edBirth.getText().toString();
        String mobile = edMobilePhone.getText().toString();
        String email = edEmail.getText().toString();
        String product = edProduct.getText().toString();
        String address = edAddress.getText().toString();
        String district = edDistrict.getText().toString();
        String provice = edProvice.getText().toString();
        String postcode = edPostcode.getText().toString();

        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRef = Ref.child("customer");
        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(gender)
                && !TextUtils.isEmpty(birth) && !TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(product)
                && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(provice) && !TextUtils.isEmpty(postcode) && !TextUtils.isEmpty(district)
                && !TextUtils.isEmpty(longtitudeText) && !TextUtils.isEmpty(latitudeText)) {
            UserInfo user = new UserInfo();
            user.setFistname(firstName);
            user.setLastname(lastName);
            user.setGender(gender);
            user.setBirth(birth);
            user.setMobile(mobile);
            user.setEmail(email);
            user.setProduct(product);
            user.setAddress(address);
            user.setDistrict(district);
            user.setProvice(provice);
            user.setPostcode(postcode);
            user.setLongitude(longtitudeText);
            user.setLatitude(latitudeText);
            mRef.push().setValue(user, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Intent GoPromotion = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(GoPromotion);
                    finish();
                    Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (TextUtils.isEmpty(firstName)) {
            edFirstname.setError("empty");
        }

        if (TextUtils.isEmpty(lastName)) {
            edLastname.setError("empty");
        }

        if (TextUtils.isEmpty(gender)) {
            edGender.setError("empty");
        }

        if (TextUtils.isEmpty(address)) {
            edAddress.setError("empty");
        }

        if (TextUtils.isEmpty(birth)) {
            edBirth.setError("empty");
        }

        if (TextUtils.isEmpty(email)) {
            edEmail.setError("empty");
        }

        if (TextUtils.isEmpty(mobile)) {
            edMobilePhone.setError("empty");
        }

        if (TextUtils.isEmpty(product)) {
            edProduct.setError("empty");
        }

        if (TextUtils.isEmpty(provice)) {
            edProvice.setError("empty");
        }

        if (TextUtils.isEmpty(postcode)) {
            edPostcode.setError("empty");
        }

        if (TextUtils.isEmpty(district)) {
            edDistrict.setError("empty");
        }
        if (TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName) && TextUtils.isEmpty(gender)
                && TextUtils.isEmpty(birth) && TextUtils.isEmpty(mobile) && TextUtils.isEmpty(email) && TextUtils.isEmpty(product)
                && TextUtils.isEmpty(address) && TextUtils.isEmpty(provice) && TextUtils.isEmpty(postcode) && TextUtils.isEmpty(district)
                && TextUtils.isEmpty(longtitudeText) && TextUtils.isEmpty(latitudeText)) {
            Toast.makeText(getApplicationContext(), "Please Add Infomation", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_saveing:
                saveDataInfo();
                if (googleApiClient != null) {
                    googleApiClient.disconnect();
                }

                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startHandler();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "not Connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(getApplicationContext(), "Connecting To Google Map", Toast.LENGTH_SHORT).show();
        LocationService();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getApplicationContext(), "MapReady", Toast.LENGTH_SHORT).show();
        mGoogleMap = googleMap;
        googleMap.setBuildingsEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setMinZoomPreference(15);


    }

    private void LocationService() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, KEY);
            } else {
                locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
                if (locationAvailability.isLocationAvailable()) {
                    LocationRequest locationRequest = new LocationRequest()
                            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY).setInterval(8000);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            mGoogleMap.clear();
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();

                            latitudeText = String.valueOf(latitude);
                            longtitudeText = String.valueOf(latitude);

                            String longitudes = String.valueOf(location.getLongitude());
                            String latitudes = String.valueOf(location.getLatitude());
//                            Toast.makeText(getApplicationContext(), latitudes + ":" + longitudes, Toast.LENGTH_SHORT).show();

                            lastPosition = new LatLng(latitude, longitude);
                            mGoogleMap.addMarker(new MarkerOptions().position(lastPosition).title("You Here"));
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(lastPosition));
                        }
                    });
                }
            }


        } else {
            locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
            if (locationAvailability.isLocationAvailable()) {
                LocationRequest locationrequest = new LocationRequest()
                        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY).setInterval(8000);
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationrequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mGoogleMap.clear();
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                        latitudeText = String.valueOf(latitude);
                        longtitudeText = String.valueOf(longitude);

                        String longitudes = String.valueOf(location.getLongitude());
                        String latitudes = String.valueOf(location.getLatitude());
//                        Toast.makeText(getApplicationContext(), latitudes + ":" + longitudes, Toast.LENGTH_SHORT).show();

                        lastPosition = new LatLng(latitude, longitude);
                        mGoogleMap.addMarker(new MarkerOptions().position(lastPosition).title("You Here"));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(lastPosition));
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Not Avliable Google Location", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case KEY:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationService();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meu_register, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_back:
                if (googleApiClient != null) {
                    googleApiClient.disconnect();
                }
                Intent main = new Intent(this, MainActivity.class);
                startActivity(main);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setmMotionEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                setmMotionEvent(null);
                break;
            case MotionEvent.ACTION_MOVE:
                setmMotionEvent(event);
                break;
        }
        return true;
    }
}
