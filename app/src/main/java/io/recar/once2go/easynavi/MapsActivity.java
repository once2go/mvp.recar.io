package io.recar.once2go.easynavi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.recar.once2go.easynavi.framents.MusicPlayerFragment;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener , View.OnSystemUiVisibilityChangeListener{

    private GoogleMap mMap;
    private LocationManager mlocationManager;
    private MarkerOptions mCarLocationMarker;
    private Bitmap mCarMarker;
    private static final int AUTO_HIDE_DELAY_MILLIS = 5000;
    private View mDecorView;
    private final Handler mHideHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.louncher_container);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mlocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1,
                    5, this);
        mCarMarker = BitmapFactory.decodeResource(getResources(),
                R.drawable.car_icon);
        mCarMarker = Bitmap.createScaledBitmap(mCarMarker, 48, 48, false);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.player_container, new MusicPlayerFragment())
                .commit();
        hideSystemPanels();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setTrafficEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        mCarLocationMarker = new MarkerOptions();
        mCarLocationMarker.position(position);
        mCarLocationMarker.draggable(false);

        mCarLocationMarker.icon(BitmapDescriptorFactory.fromBitmap(mCarMarker));
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(mCarLocationMarker);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void hideSystemPanels() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDecorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            mDecorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int i) {
                    if (i == View.SYSTEM_UI_FLAG_VISIBLE) {
                        mHideHandler.postDelayed(hideSystemNavigationBarRunnable, AUTO_HIDE_DELAY_MILLIS);
                    }
                }
            });
        }
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == View.INVISIBLE) {
            hideSystemPanels();
        }
    }
    /**
     hideSystemNavigationBarRunnable - hiding system navigation bar on elder Android Api, up to 18
     **/
    private final Runnable hideSystemNavigationBarRunnable = new Runnable() {
        @Override
        public void run() {
            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

}
