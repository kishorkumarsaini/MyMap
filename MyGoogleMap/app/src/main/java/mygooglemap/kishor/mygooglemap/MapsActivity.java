package mygooglemap.kishor.mygooglemap;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private  GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private  Location lastLocatoion;
    private Marker CurrentMaker;
    public static final int REQUEST_LOCATION_CODE=99;
    private  Button myButton;
    private  DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        dbHelper =new DbHelper(this);
        myButton=(Button)findViewById(R.id.button2);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    //permission granted
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                    {
                        if(googleApiClient==null)
                        {
                            buildGoogleApi();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else //permision denied
                {
                    Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
                }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {

            buildGoogleApi();
            mMap.setMyLocationEnabled(true);
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }



    }

    public  void  Onclick(View view)
    {
        ContentValues contentValues = new ContentValues();

        if(view.getId()==R.id.button2)
        {
            EditText tf_location=(EditText)findViewById(R.id.textView4);
            String locations=tf_location.getText().toString();
            List<Address>addresses = null;
            MarkerOptions mo=new MarkerOptions();
            if(!locations.equals(""))
            {
                Geocoder geocoder=new Geocoder(this);

                try
                {
                    addresses=geocoder.getFromLocationName(locations,5);

                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                for(int i=0;i<addresses.size();i++)
                {
                      Address  myaddress = addresses.get(i);
                    LatLng latLng = new LatLng(myaddress.getLatitude(), myaddress.getLongitude());
                    mo.position(latLng);
                    mo.title("Your Search Position");
                    mMap.addMarker(mo);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    ContentValues contentValues1=new ContentValues();
                    contentValues.put(dbHelper.LAT,myaddress.getLatitude());
                    contentValues.put(dbHelper.LNG,myaddress.getLongitude());
                    contentValues.put(dbHelper.ZOOM, mMap.getCameraPosition().zoom);
                    long row=dbHelper.insert(contentValues);
                    if(row>0)
                    {
                        Toast.makeText(getApplicationContext(),"value is inserted",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"somthing wrong",Toast.LENGTH_SHORT).show();
                    }


                }

            }

        }

    }
    protected synchronized  void buildGoogleApi()
    {
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        locationRequest=new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public  boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
            return true;
    }




    @Override
    public void onLocationChanged(Location location)
    {

      lastLocatoion=location;
        if(CurrentMaker!=null)
        {
            CurrentMaker.remove();
        }

        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        CurrentMaker =mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


        if(googleApiClient!=null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

        }



    }
}
