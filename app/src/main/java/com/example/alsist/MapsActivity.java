package com.example.alsist;

import android.Manifest;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import com.github.clans.fab.FloatingActionMenu;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener,
        FloatingActionButton.OnClickListener{

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    JSONArray jArr = null; // 역들의 위치 및 정보가 담긴 json 형태의 배열
    JSONObject json = null;
    int length = 0;
    String nexttrain;
    String temp; // 마커 클릭커에서 잠시 필요한 변수
    Marker[] markerarraysub; // 지하철 위치가 들어갈 마커 배열
    ImageView batteryimage;
    private FloatingActionMenu fam;
    private FloatingActionButton menu1, menu2, menu3;   // 1폴딩, 2자율주행, 3수동 조작
    public static ImageView battery;
    private BluetoothAdapter mBluetoothAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        com.github.clans.fab.FloatingActionButton menu1 = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.menu1);
        menu1.setOnClickListener(this);
        com.github.clans.fab.FloatingActionButton menu2 = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.menu2);
        menu2.setOnClickListener(this);
        com.github.clans.fab.FloatingActionButton menu3 = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.menu3);
        menu3.setOnClickListener(this);


        final Thread threadsub = new Thread() {
            public void run() {
                String subwaylist = Networkcon.getsubwaylist();
                Message msg = handlersub.obtainMessage();
                Bundle bun = new Bundle();
                bun.putString("mysub", subwaylist);
                msg.setData(bun);
                handlersub.sendMessage(msg);
            }
        };
        Thread threadshop = new Thread() {
            public void run() {
                String shoplist = Networkcon.getshoplist();
                Message msg = handlershop.obtainMessage();
                Bundle bun = new Bundle();
                bun.putString("myshop", shoplist);
                msg.setData(bun);
                handlershop.sendMessage(msg);
            }
        };
        Thread threadAS = new Thread() {
            public void run() {
                String ASlist = Networkcon.getASlist();
                Message msg = handlerAS.obtainMessage();
                Bundle bun = new Bundle();
                bun.putString("myAS", ASlist);
                msg.setData(bun);
                handlerAS.sendMessage(msg);
            }
        };

        // bluetooth thread
        /*
        final Thread threadBlue = new Thread() {
            public void run() {

            }
        }*/

        threadshop.start();
        threadsub.start();
        threadAS.start();
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.subway_icon2);
        final Bitmap subpic = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), 52, 72, false);
        CheckBox checkboxsub = (CheckBox) findViewById(R.id.checkBoxsub);

        checkboxsub.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        threadsub.join(); // 쓰레드가 종료되면 그 다음 진행
                    } catch(Exception e){
                    }
                    for (int i = 0 ; i < length;i++) {
                        markerarraysub[i] = mMap.addMarker(new MarkerOptions()
                                .position(markerarraysub[i].getPosition())
                                .title(markerarraysub[i].getTitle())
                                .icon(BitmapDescriptorFactory.fromBitmap(subpic))
                                .zIndex(0.05f)
                        );
                    }
                } else {
                    for (int j = 0; j < length; j++) {
                        markerarraysub[j].remove();
                    }
                }
            }
        });
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                nexttrain = Networkcon.getnexttrains();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 15000);

        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 폴딩
            }
        });

        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // yolo
                Intent intent = new Intent(MapsActivity.this, yolo.class);
                startActivity(intent);
            }
        });
        menu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수동 조작
                if (!mBluetoothAdapter.isEnabled()) {
                    Toast.makeText(MapsActivity.this, "Please turn on the Bluetooth mode", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MapsActivity.this, ManualActivity.class);
                    startActivity(intent);
                }
            }
        });
        battery = (ImageView) findViewById(R.id.imageView2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu1:
                break;
            case R.id.menu2:
                break;
            case R.id.menu3:
                break;
            default:
                break;
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
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        UiSettings mapSettings; //ui세팅 불러오기 위해 생성
        mapSettings = mMap.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMarkerClickListener(this);
    }

    public boolean onMarkerClick(final Marker marker) {
        if (marker.getZIndex() == 0.05f) {
            LatLng latlng = marker.getPosition();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            temp = marker.getTitle();
            Intent intent = new Intent(this, SubwayPopupActivity.class);
            intent.putExtra("markertitle", temp);
            intent.putExtra("json", json.toString());
            intent.putExtra("nexttrains", nexttrain);
            startActivity(intent);
        } else if (marker.getZIndex() == 0.1f) {
            Object temp = marker.getTag();
            int scode = Integer.parseInt(temp.toString());
            if (((MyApplication) MapsActivity.this.getApplication()).getUserRent() == false) {
                Intent intent = new Intent(this, shop_popup.class);
                intent.putExtra("name", marker.getTitle());
                intent.putExtra("shopcode", scode);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, shop_return.class);
                intent.putExtra("name1", marker.getTitle());
                startActivity(intent);
            }
        } else if (marker.getZIndex() == 0.07f) {
            Intent intent = new Intent(this, AS_popup.class);
            intent.putExtra("telephonenum", marker.getTag().toString());
            intent.putExtra("ASname", marker.getTitle());
            startActivity(intent);
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        // save location of user
        ((MyApplication) MapsActivity.this.getApplication()).setUserLocation(latLng);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    // 지하철 역정보를 다뤄주고 마커를 찍어주는 핸들러
    Handler handlersub = new Handler(Looper.getMainLooper()) {
        // @Override
        public void handleMessage(Message msg1) {
            Bundle bun = msg1.getData();
            String subwaylist = bun.getString("mysub");

            try {
                json = new JSONObject(subwaylist);
                jArr = json.getJSONArray("Stations");
                markerarraysub = new Marker[jArr.length()];

                length = jArr.length();
                for (int i = 0; i < length; i++) {
                    JSONObject temp = jArr.getJSONObject(i);

                    double Lattemp = temp.getDouble("Lat");
                    double Lontemp = temp.getDouble("Lon");
                    String Nametemp = temp.getString("Name");
                    LatLng subwaytemp = new LatLng(Lattemp, Lontemp);
                    Marker subway = mMap.addMarker(new MarkerOptions()
                            .position(subwaytemp)
                            .title(Nametemp)
                    );
                    markerarraysub[i]=subway;
                    subway.remove();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // shop 정보를 다뤄주고 마커를 찍어주는 핸들러
    Handler handlershop = new Handler(Looper.getMainLooper()) {
        // @Override
        public void handleMessage(Message msg1) {
            Bundle bun = msg1.getData();
            String shoplist = bun.getString("myshop");
            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.shop_mark);
            final Bitmap shoppic = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), 84, 87, false);
            try {
                JSONObject jsonshop = new JSONObject(shoplist);
                JSONArray jArrshop = jsonshop.getJSONArray("smarker");
                for (int i = 0; i < jArrshop.length(); i++) {
                    JSONObject temp = jArrshop.getJSONObject(i);

                    double Lattemp = temp.getDouble("latitude");
                    double Lontemp = temp.getDouble("longitude");
                    String Nametemp = temp.getString("name");
                    LatLng shoptemp = new LatLng(Lattemp, Lontemp);
                    Marker shop = mMap.addMarker(new MarkerOptions()
                            .position(shoptemp)
                            .title(Nametemp)
                            .icon(BitmapDescriptorFactory.fromBitmap(shoppic))
                            .zIndex(0.1f)
                    );
                    shop.setTag(temp.getInt("shopcode"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Handler handlerAS = new Handler(Looper.getMainLooper()) {
        // @Override
        public void handleMessage(Message msg1) {
            Bundle bun = msg1.getData();
            String ASlist = bun.getString("myAS");
            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.ascenter);
            final Bitmap shoppic = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), 84, 87, false);
            try {
                JSONObject jsonshop = new JSONObject(ASlist);
                JSONArray jArrshop = jsonshop.getJSONArray("asmarker");
                for (int i = 0; i < jArrshop.length(); i++) {
                    JSONObject temp = jArrshop.getJSONObject(i);

                    double Lattemp = temp.getDouble("latitude");
                    double Lontemp = temp.getDouble("longitude");
                    String Nametemp = temp.getString("name");
                    LatLng shoptemp = new LatLng(Lattemp, Lontemp);
                    Marker shop = mMap.addMarker(new MarkerOptions()
                            .position(shoptemp)
                            .title(Nametemp)
                            .icon(BitmapDescriptorFactory.fromBitmap(shoppic))
                            .zIndex(0.07f)
                    );
                    shop.setTag(temp.getString("tel"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
