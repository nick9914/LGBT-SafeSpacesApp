package course.examples.lgbtsafespaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by claw on 4/18/16.
 *
 * This is a temporary placeholder class used only in prototyping v1.
 */
public class DummyLocation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dummy_location);

        final Button reviewButton = (Button) findViewById(R.id.reviewButton);
        assert reviewButton != null;
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DummyLocation.this, ReviewLocation.class);
                startActivity(intent);
            }
        });

        //Home button functionality
        final Button homeButton = (Button) findViewById(R.id.homeButton);
        assert homeButton != null;
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DummyLocation.this, MapsActivity.class);
                startActivity(intent);
            }
        });

    }
}
