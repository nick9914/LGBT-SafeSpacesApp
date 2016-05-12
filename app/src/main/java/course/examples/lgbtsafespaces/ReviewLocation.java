package course.examples.lgbtsafespaces;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

/**
 * Created by claw on 4/17/16.
 */
public class ReviewLocation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_location);


        //Submit button functionality
        final Button submitButton = (Button) findViewById(R.id.submitButton);
        assert submitButton != null;
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userExit();
            }
        });

        //Rating Bar
        final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        assert ratingBar != null;
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //TODO
                //Rating bar logic here
            }
        });

        //Cancel button functionality
        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        assert cancelButton != null;
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }

    private void userExit() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ReviewLocation.this);
        builder1.setMessage("Thank you for your review!");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "No Problem!",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }
}
