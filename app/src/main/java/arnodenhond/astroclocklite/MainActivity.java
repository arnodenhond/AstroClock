package arnodenhond.astroclocklite;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import arnodenhond.astroclocklite.databinding.ActivityMainBinding;

public class MainActivity extends Activity {

    private TextView mTextView;
    private Button mButton;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mTextView = binding.text;
        mButton = binding.button;
        mTextView.setText("AstroClock needs to know your location to show the position of the sun. Background location permission is granted in 2 steps.");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestPermissions( new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},1);
                }
            });
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mTextView.setText("Step 1 completed! Select \"" + getPackageManager().getBackgroundPermissionOptionLabel() +"\" to enable AstroClock");
                mButton.setText("step 2");
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestPermissions( new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION},2);
                    }
                });
            } else {
                mTextView.setText("Permission Granted!\nUse AstroClock by adding the tile");
                mButton.setVisibility(View.GONE);
                getLocation();
            }
        }
    }

    private void getLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d("astroclocklite","got location "+((location==null)?"null":location.toString()));

                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            float flat = (float) location.getLatitude();
                            float flon = (float) location.getLongitude();
                            SharedPreferences prefs = getSharedPreferences("location", MODE_PRIVATE);
                            prefs.edit().putFloat("lat",flat).putFloat("lon",flon).apply();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case 1:
                    mTextView.setText("Step 1 completed! Select \"" + getPackageManager().getBackgroundPermissionOptionLabel() +"\" to enable AstroClock");
                    mButton.setText("step 2");
                    mButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions( new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION},2);
                        }
                    });
                    break;
                case 2:
                    mTextView.setText("Permission Granted!\nUse AstroClock by adding the tile");
                    mButton.setVisibility(View.GONE);
                    getLocation();
                    break;
            }
        }  else {
            // Explain to the user that the feature is unavailable because
            // the feature requires a permission that the user has denied.
            // At the same time, respect the user's decision. Don't link to
            // system settings in an effort to convince the user to change
            // their decision.
        }
    }
}