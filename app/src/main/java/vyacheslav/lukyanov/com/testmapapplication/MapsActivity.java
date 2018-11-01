package vyacheslav.lukyanov.com.testmapapplication;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import vyacheslav.lukyanov.com.testmapapplication.fragments.ChooseMapProviderFragment;
import vyacheslav.lukyanov.com.testmapapplication.fragments.ChooseRevGeocodingServiceFragment;
import vyacheslav.lukyanov.com.testmapapplication.fragments.CustomDialogFragment;
import vyacheslav.lukyanov.com.testmapapplication.fragments.MapBoxFragment;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        ChooseMapProviderFragment.OnChooseMapProviderListener,
        MapBoxFragment.OnMapBoxReverseGeocoding,
        ChooseRevGeocodingServiceFragment.OnChooseServiceListener {

    public static final String TAG = MapsActivity.class.getSimpleName();

    private Toolbar toolbar;
    private GoogleMap googleMap;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private GoogleGeocodingResultReceiver mResultGeocodingReceiver;
    private int currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.maps_drawer_layout);
        navigationView = findViewById(R.id.main_nav_view);
        setUpNavigationView(navigationView, drawer);

        mResultGeocodingReceiver = new GoogleGeocodingResultReceiver(null);

        showMap();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>0) {
            getSupportFragmentManager().popBackStack();
            navigationView.setCheckedItem(R.id.item_map);
        }else {
            super.onBackPressed();
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
        this.googleMap = googleMap;

        this.googleMap.setOnMapClickListener(latLng ->
                fetchReverseGeocoding(latLng.longitude, latLng.latitude));
    }

    protected void setUpNavigationView(NavigationView navigationView, final DrawerLayout drawer) {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            if (currentItem != menuItem.getItemId()) {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                switch (menuItem.getItemId()) {
                    case R.id.item_map:
                        showMap();
                        break;
                    case R.id.item_map_provider:
                        currentItem = R.id.item_map_provider;
                        gotoMapProvidersFragment();
                        break;
                    case R.id.item_reverse_geocoding_service:
                        currentItem = R.id.item_reverse_geocoding_service;
                        gotoReverseGeocodingFragment();
                        break;
                }
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void showMap(){
        navigationView.setCheckedItem(R.id.item_map);
        currentItem = R.id.item_map;
        switch (getCurrentProvider()){
            case Constants.GOOGLE_MAP_PROVIDER:
                gotoGoogleMapFragment();
                break;
            case Constants.MAP_BOX_PROVIDER:
                gotoMapBoxFragment();
                break;
        }
    }

    private void gotoGoogleMapFragment() {
        SupportMapFragment supportMapFragment = new SupportMapFragment();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        supportMapFragment.getMapAsync(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_fragment, supportMapFragment)
                .commit();
    }

    private void gotoMapBoxFragment() {
        MapBoxFragment mapBoxFragment = MapBoxFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_fragment, mapBoxFragment)
                .commit();
    }

    private void gotoMapProvidersFragment() {
        int currentProvider = getCurrentProvider();
        ChooseMapProviderFragment chooseMapProviderFragment = ChooseMapProviderFragment.newInstance(currentProvider);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_fragment, chooseMapProviderFragment)
                .addToBackStack(null)
                .commit();
    }

    private void gotoReverseGeocodingFragment() {
        int currentService = getCurrentReverseGeocodingService();
        ChooseRevGeocodingServiceFragment geocodingServiceFragment = ChooseRevGeocodingServiceFragment.newInstance(currentService);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_fragment, geocodingServiceFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onChooseMapProvider(int provider) {
        getSupportFragmentManager().popBackStack();
        if (getCurrentProvider() != provider){
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(Preferences.MAP_PROVIDER, provider);
            editor.apply();
            showMap();
        }
    }

    @Override
    public void onChooseGeocodingService(int service) {
        getSupportFragmentManager().popBackStack();
        if (getCurrentReverseGeocodingService() != service) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(Preferences.REVERSE_GEOCODING_SERVICE, service);
            editor.apply();
            showMap();
        }
    }

    private int getCurrentProvider(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getInt(Preferences.MAP_PROVIDER, Constants.GOOGLE_MAP_PROVIDER);
    }

    private int getCurrentReverseGeocodingService(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getInt(Preferences.REVERSE_GEOCODING_SERVICE, Constants.GOOGLE_MAP_REV_GEOCODING);
    }

    private void showReverseGeocodingResult(int service, String result){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        CustomDialogFragment fragment = CustomDialogFragment.newInstance(service, result);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialogFragment);
        fragment.show(ft, "dialog");
    }

    @Override
    public void onMapBoxReverseGeocoding(double lng, double lat) {
        fetchReverseGeocoding(lng, lat);
    }

    private void fetchReverseGeocoding(double lng, double lat) {
        switch (getCurrentReverseGeocodingService()){
            case Constants.GOOGLE_MAP_REV_GEOCODING:
                setupGoogleReverseGeocoding(lng, lat);
                break;
            case Constants.MAP_BOX_REV_GEOCODING:
                setupMapBoxReverseGeocoding(lng, lat);
                break;
        }
    }

    private void setupMapBoxReverseGeocoding(double lng, double lat){
        MapboxGeocoding reverseGeocode = MapboxGeocoding.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .query(Point.fromLngLat(lng, lat))
                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS, GeocodingCriteria.TYPE_PLACE)
                .build();

        reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeocodingResponse> call,
                                   @NonNull Response<GeocodingResponse> response) {
                List<CarmenFeature> results = response.body().features();
                if (results.size() > 0) {
                    for (CarmenFeature carmenFeature: results){
                        Timber.d(TAG, "onResponse: %s", carmenFeature.toString());
                    }

                    // Get the first Feature from the successful geocoding response
                    CarmenFeature feature = results.get(0);
//                    Toast.makeText(MapsActivity.this, feature.placeName(),
//                            Toast.LENGTH_SHORT).show();
                    showReverseGeocodingResult(Constants.MAP_BOX_REV_GEOCODING, feature.placeName());
                } else {
                    Toast.makeText(MapsActivity.this, R.string.no_results,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                Timber.e("Geocoding Failure: %s", throwable.getMessage());
            }
        });
    }

    private void setupGoogleReverseGeocoding(double lng, double lat){
        Location targetLocation = new Location("");//provider name is unnecessary
        targetLocation.setLongitude(lng);
        targetLocation.setLatitude(lat);

        Intent intent = new Intent(this, GoogleGeocodingIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultGeocodingReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, targetLocation);
        startService(intent);
    }

    private void displayGoogleGeocoding(final String addressText){
        runOnUiThread(() -> showReverseGeocodingResult(Constants.GOOGLE_MAP_REV_GEOCODING, addressText));
    }

    class GoogleGeocodingResultReceiver extends ResultReceiver {

        GoogleGeocodingResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData == null) {
                return;
            }
            // Display the address string
            // or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(Constants.GEOCODING_RESULT_DATA_KEY);
            if (mAddressOutput == null) {
                mAddressOutput = "";
            }
            displayGoogleGeocoding(mAddressOutput);
        }
    }
}
