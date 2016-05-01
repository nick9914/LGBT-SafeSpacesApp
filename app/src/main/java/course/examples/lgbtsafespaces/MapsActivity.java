package course.examples.lgbtsafespaces;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for {@link android.Manifest.permission#ACCESS_FINE_LOCATION} is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */
public class MapsActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnInfoWindowClickListener {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private Marker userClick;
    private String userClickId;
    private Map<String, Location> locationMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationMap = new HashMap<>();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        mMap.setOnMapClickListener(this);
        enableMyLocation();
        setMarkers();
        //Move Camera to current Location
        /*LatLng latLng = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        mMap.animateCamera(cameraUpdate);*/

        //TODO
        mMap.setOnInfoWindowClickListener(this);
    }

    private void setMarkers() {
        InputStream inputStream = this.getResources().openRawResource(R.raw.locations);
        String jsonString = readJsonFile(inputStream);
        Type collectionType = new TypeToken<List<Location>>(){}.getType();
        Gson gson = new Gson();
        List<Location> listOfLocations = gson.fromJson(jsonString, collectionType);
        for(Location loc : listOfLocations) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLat(), loc.getLng()))
            .title(loc.getLocationName())
            .icon(BitmapDescriptorFactory.defaultMarker(150f)));
            locationMap.put(loc.getLocationName(), loc);
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (userClick != null) {
            userClick.setPosition(latLng);
        } else {
            userClick = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Add Location"));
        }

        userClickId = userClick.getId();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private String readJsonFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte bufferByte[] = new byte[1024];
        int length;
        try {
            while ((length = inputStream.read(bufferByte)) != -1) {
                outputStream.write(bufferByte, 0, length);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d("READ JSON FILE", "could not read JSON File");
        }
        return outputStream.toString();
    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View mContents;

        CustomInfoWindowAdapter() {
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleText.length(), 0);
                titleUi.setText(titleText);
                Location loc = locationMap.get(title);

                if (loc != null) {
                    if (!loc.genderNeutralBathroom) {
                        view.findViewById(R.id.gender_neutral_bathroom_icon).setVisibility(View.GONE);
                    } else {
                        view.findViewById(R.id.gender_neutral_bathroom_icon).setVisibility(View.VISIBLE);
                    }

                    if (!loc.verifiedSafeSpace) {
                        view.findViewById(R.id.verified_safe_space_icon).setVisibility(View.GONE);
                    } else {
                        view.findViewById(R.id.verified_safe_space_icon).setVisibility(View.VISIBLE);
                    }

                    if (!loc.friendlyBusiness) {
                        view.findViewById(R.id.friendly_business_icon).setVisibility(View.GONE);
                    } else {
                        view.findViewById(R.id.friendly_business_icon).setVisibility(View.VISIBLE);
                    }

                    if (!loc.shelter) {
                        view.findViewById(R.id.shelter_icon).setVisibility(View.GONE);
                    } else {
                        view.findViewById(R.id.shelter_icon).setVisibility(View.VISIBLE);
                    }
                } else {
                    //Turn off all icons
                    view.findViewById(R.id.gender_neutral_bathroom_icon).setVisibility(View.GONE);
                    view.findViewById(R.id.verified_safe_space_icon).setVisibility(View.GONE);
                    view.findViewById(R.id.friendly_business_icon).setVisibility(View.GONE);
                    view.findViewById(R.id.shelter_icon).setVisibility(View.GONE);
                }
            } else {
                titleUi.setText("");
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (marker.getId().equals(userClickId)) {
            //Pass the location of the user's marker to the AddLocation class while starting it
            Bundle args = new Bundle();
            args.putParcelable("latlng", marker.getPosition());

            Intent intent = new Intent(getApplicationContext(), AddLocation.class);
            intent.putExtra("bundle", args);

            startActivity(intent);
        } else {
            //TODO
            //Pass the name of the location into the LocationInfo class so that the proper
            //information can be displayed about the location

            Intent intent = new Intent(getApplicationContext(), DummyLocation.class);
            startActivity(intent);
        }
    }

    public void mapButtonChecked(View view) {
        boolean checked = ((ToggleImageButton)view).isChecked();
        if(checked) {
            findViewById(R.id.map_legend).setVisibility(View.VISIBLE);
            findViewById(R.id.info_button_linear_layout).setBackgroundResource(R.drawable.mapbutton_pressed);
            findViewById(R.id.map_legend).invalidate();
            findViewById(R.id.info_button_linear_layout).invalidate();
        } else {
            findViewById(R.id.info_button_linear_layout).setBackgroundResource(R.drawable.mapbutton_normal);
            findViewById(R.id.map_legend).setVisibility(View.GONE);
            findViewById(R.id.map_legend).invalidate();
            findViewById(R.id.info_button_linear_layout).invalidate();
        }
    }
}
