package course.examples.lgbtsafespaces;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Street Address to Latitude / Longitude function taken/adapted from:
 * http://stackoverflow.com/questions/22909756/how-to-get-latitude-and-longitude-from-a-given-address-in-android-google-map-v2
 */
public class AddLocation extends AppCompatActivity {
    private Double lat = 0.0;
    private Double lng = 0.0;
    private LatLng latlng;
    TextView latValue;
    TextView longValue;
    /**
    //TODO
    CLAW: I know this is a terrible way to do this
     Final version should have something better in place
     */
    private boolean isSafeSpace;
    private boolean isGenderNeutralBathroom;
    private boolean isShelter;
    private boolean isCrisisCenter;
    private boolean isFriendlyBusiness;
    private boolean isValidLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        final TextView streetText = (TextView) findViewById(R.id.streetText);
        final TextView stateText = (TextView) findViewById(R.id.stateText);
        final TextView cityText = (TextView) findViewById(R.id.cityText);
        final TextView zipCodeText = (TextView) findViewById(R.id.zipCodeText);
        final TextView locationNameText = (TextView) findViewById(R.id.locationNameText);

        final CheckBox safeSpaceCheck = (CheckBox) findViewById(R.id.safeSpaceCheck);
        safeSpaceCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (safeSpaceCheck.isChecked()) {
                    isSafeSpace = true;
                } else {
                    isSafeSpace = false;
                }
            }
        });

        final CheckBox genderNeutralBathroomCheck = (CheckBox) findViewById(R.id.genNeutBathCheck);
        safeSpaceCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (genderNeutralBathroomCheck.isChecked()) {
                    isGenderNeutralBathroom = true;
                } else {
                    isGenderNeutralBathroom = false;
                }
            }
        });

        final CheckBox shelterCheck = (CheckBox) findViewById(R.id.shelterCheck);
        safeSpaceCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shelterCheck.isChecked()) {
                    isShelter = true;
                } else {
                    isShelter = false;
                }
            }
        });

        final CheckBox crisisCenteCheck = (CheckBox) findViewById(R.id.crisisCenterCheck);
        safeSpaceCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (crisisCenteCheck.isChecked()) {
                    isCrisisCenter = true;
                } else {
                    isCrisisCenter = false;
                }
            }
        });

        final CheckBox friendlyBusinessCheck = (CheckBox) findViewById(R.id.friendlyBusinessCheck);
        safeSpaceCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendlyBusinessCheck.isChecked()) {
                    isFriendlyBusiness = true;
                } else {
                    isFriendlyBusiness = false;
                }
            }
        });

        Button addLocationButton = (Button) findViewById(R.id.addLocationButton);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latlng = getLatLongFromPlace(streetText.getText() + ", " + cityText.getText()
                    + ", " + stateText.getText() + ", " + zipCodeText.getText() );

                //write to the underlying JSON
                writeLocationToJSON(latlng, locationNameText.getText().toString(),
                    isCrisisCenter, isGenderNeutralBathroom,
                        isShelter, isSafeSpace, isFriendlyBusiness);


                //TODO
                //Check to make sure the write to json was successful

                if (isValidLocation) {
                    Toast.makeText(AddLocation.this, "Thank you for adding a location!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AddLocation.this, "Please enter a valid location first!", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public LatLng getLatLongFromPlace(String place) {
        LatLng latlng = new LatLng(0.0, 0.0);

        try {
            Geocoder selected_place_geocoder = new Geocoder(AddLocation.this);
            List<Address> address;
            address = selected_place_geocoder.getFromLocationName(place, 5);

            if (address == null) {
                isValidLocation = false;
            } else {
                Address location = address.get(0);
                Double lat= location.getLatitude();
                Double lng = location.getLongitude();

                Log.d("CLAW", lat.toString());
                Log.d("CLAW", lng.toString());

                isValidLocation = true;
                return new LatLng(lat, lng);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fetchLatLongFromService fetch_latlng_from_service_abc = new fetchLatLongFromService(
                    place.replaceAll("\\s+", ""));
            fetch_latlng_from_service_abc.execute();

            isValidLocation = false;
        }

        return latlng;
    }

    public void writeLocationToJSON(LatLng latlng, String locationName, boolean isFriendlyBusiness,
                                    boolean isCrisisCenter, boolean isGenderNeutralBathroom,
                                    boolean isSafeSpace, boolean isShelter) {

        //TODO
        //things to write the location name and checkbox statuses to the json
    }

    /**
     * Functions to translate a street address to latitude and longitude coordinates
     */
    public class fetchLatLongFromService extends AsyncTask<Void, Void, StringBuilder> {
        String place;

        public fetchLatLongFromService(String place) {
            super();
            this.place = place;
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
            this.cancel(true);
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                HttpURLConnection conn = null;
                StringBuilder jsonResults = new StringBuilder();
                String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?address="
                        + this.place + "&sensor=false";

                URL url = new URL(googleMapUrl);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(
                        conn.getInputStream());
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
                String a = "";
                return jsonResults;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                JSONArray resultJsonArray = jsonObj.getJSONArray("results");

                JSONObject before_geometry_jsonObj = resultJsonArray.getJSONObject(0);
                JSONObject geometry_jsonObj = before_geometry_jsonObj.getJSONObject("geometry");
                JSONObject location_jsonObj = geometry_jsonObj.getJSONObject("location");

                String lat_helper = location_jsonObj.getString("lat");
                double lat = Double.valueOf(lat_helper);

                String lng_helper = location_jsonObj.getString("lng");
                double lng = Double.valueOf(lng_helper);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
    }
}


