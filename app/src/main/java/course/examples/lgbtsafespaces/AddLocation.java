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
 * Created by claw on 4/30/16.
 *
 * Address to Latitude / Longitude function taken/adapted from:
 * http://stackoverflow.com/questions/22909756/how-to-get-latitude-and-longitude-from-a-given-address-in-android-google-map-v2
 */
public class AddLocation extends AppCompatActivity {
    private Double lat = 0.0;
    private Double lng = 0.0;
    TextView latValue;
    TextView longValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        //Get the lattitue and longitude from the previous activity

        TextView latValue = (TextView) findViewById(R.id.latValue);
        TextView longValue = (TextView) findViewById(R.id.longValue);
        final TextView streetText = (TextView) findViewById(R.id.streetText);
        final TextView stateText = (TextView) findViewById(R.id.stateText);
        final TextView cityText = (TextView) findViewById(R.id.cityText);
        final TextView zipCodeText = (TextView) findViewById(R.id.zipCodeText);

        final CheckBox safeSpaceCheck = (CheckBox) findViewById(R.id.safeSpaceCheck);
        if (safeSpaceCheck.isChecked()) {
            //TODO
            //write to the json file that this option is checked for this location
        }
        ;

        //TODO
        //find way to loop through all the checkboxes on the screen?

        Button doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLatLongFromPlace(streetText.getText() + ", " + cityText.getText()
                    + ", " + stateText.getText() + ", " + zipCodeText.getText() );
            }
        });
    }

    public void getLatLongFromPlace(String place) {
        try {
            Geocoder selected_place_geocoder = new Geocoder(AddLocation.this);
            List<Address> address;
            address = selected_place_geocoder.getFromLocationName(place, 5);

            if (address == null) {
                Toast.makeText(AddLocation.this, "That Location is invalid!", Toast.LENGTH_LONG).show();
            } else {
                Address location = address.get(0);
                Double lat= location.getLatitude();
                Double lng = location.getLongitude();

                Log.d("CLAW", lat.toString());
                Log.d("CLAW", lng.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fetchLatLongFromService fetch_latlng_from_service_abc = new fetchLatLongFromService(
                    place.replaceAll("\\s+", ""));
            fetch_latlng_from_service_abc.execute();

            //Warn user
            Toast.makeText(AddLocation.this, "That location is invalid!", Toast.LENGTH_LONG).show();
        }
    }

    //Sometimes happens that device gives location = null
    public class fetchLatLongFromService extends
            AsyncTask<Void, Void, StringBuilder> {
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

                // Extract the Place descriptions from the results
                // resultList = new ArrayList<String>(resultJsonArray.length());

                JSONObject before_geometry_jsonObj = resultJsonArray
                        .getJSONObject(0);

                JSONObject geometry_jsonObj = before_geometry_jsonObj
                        .getJSONObject("geometry");

                JSONObject location_jsonObj = geometry_jsonObj
                        .getJSONObject("location");

                String lat_helper = location_jsonObj.getString("lat");
                double lat = Double.valueOf(lat_helper);


                String lng_helper = location_jsonObj.getString("lng");
                double lng = Double.valueOf(lng_helper);


                LatLng point = new LatLng(lat, lng);


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
    }
}


