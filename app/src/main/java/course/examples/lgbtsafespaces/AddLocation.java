package course.examples.lgbtsafespaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

/**
 * Created by claw on 4/30/16.
 */
public class AddLocation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        //Get the lattitue and longitude from the previous activity
        Bundle bundle = getIntent().getParcelableExtra("bundle");
        LatLng latlng = bundle.getParcelable("latlng");

        Log.d("CLAW", latlng.toString());

        final TextView latValue = (TextView) findViewById(R.id.latValue);
        final TextView longValue = (TextView) findViewById(R.id.longValue);

        latValue.setText(((Double) latlng.latitude).toString());
        longValue.setText(((Double) latlng.longitude).toString());

        final CheckBox safeSpaceCheck = (CheckBox) findViewById(R.id.safeSpaceCheck);
        if (safeSpaceCheck.isChecked()) {
            //TODO
            //write to the json file that this option is checked for this location
        };

        //TODO
        //find way to loop through all the checkboxes on the screen?


    }
}
