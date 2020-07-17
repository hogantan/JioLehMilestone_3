package com.example.jioleh.location;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jioleh.R;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import timber.log.Timber;

import static androidx.constraintlayout.widget.ConstraintSet.WRAP_CONTENT;
import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;



/**
 * Drop a marker at a specific location and then perform
 * reverse geocoding to retrieve and display the location's address
 */
public class LocationPicker extends AppCompatActivity implements PermissionsListener, OnMapReadyCallback {

    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";
    private static final String MAPBOX_ACCESS_TOKEN = "pk.eyJ1IjoiamlvbGVoIiwiYSI6ImNrYmxvczRndjE4N28ydW5jajNxMGI1ZTkifQ.FI8oIkU05z3-aEHFKsN8fA";
    private MapView mapView;
    private MapboxMap mapboxMap;
    private MapboxGeocoding mapboxGeocodingForward;
    private MapboxGeocoding mapboxGeocodingBackward;
    private Button selectLocationButton;
    private PermissionsManager permissionsManager;
    private ImageView hoveringMarker;
    private Layer droppedMarkerLayer;
    private SearchView searchView;
    private Style style;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this,
                (MAPBOX_ACCESS_TOKEN));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_location_picker);

        // Initialise searchView
        searchView = findViewById(R.id.sv_locationPicker);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                              @Override
                                              public boolean onQueryTextSubmit(String query) {
                                                  search(query);
                                                  return false;
                                              }

                                              @Override
                                              public boolean onQueryTextChange(String newText) {
                                                  return false;
                                              }
                                          });

        // Initialize the mapboxMap view
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        this.mapboxMap.getUiSettings().setCompassEnabled(false); //disable compass
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull final Style style) {


                enableLocationPlugin(style);
                Bitmap icon = BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.find_nearby));
                style.addImage("icon-id", icon);

                SymbolLayer symbolLayer = new SymbolLayer("layer-id", "source-id");
                symbolLayer.setProperties(PropertyFactory.iconImage("icon-id"));

                style.addLayer(symbolLayer);

                // Toast instructing user to tap on the mapboxMap
                Toast.makeText(
                        LocationPicker.this,
                        "Move map to select location for your activity", Toast.LENGTH_SHORT).show();

                // When user is still picking a location, we hover a marker above the mapboxMap in the center.
                // This is done by using an image view with the default marker found in the SDK. You can
                // swap out for your own marker image, just make sure it matches up with the dropped marker.
                hoveringMarker = new ImageView(LocationPicker.this);
                hoveringMarker.setImageResource(R.drawable.map_red_marker);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                hoveringMarker.setLayoutParams(params);
                mapView.addView(hoveringMarker);


                // Initialize, but don't show, a SymbolLayer for the marker icon which will represent a selected location.
                initDroppedMarker(style);

                // Button for user to drop marker or to pick marker back up.
                selectLocationButton = findViewById(R.id.select_location_button);
                selectLocationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (hoveringMarker.getVisibility() == View.VISIBLE) {

                            // Use the map target's coordinates to make a reverse geocoding search
                            final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;

                            // Hide the hovering red hovering ImageView marker
                            hoveringMarker.setVisibility(View.INVISIBLE);

                            // Transform the appearance of the button to become the cancel button
                            selectLocationButton.setBackgroundColor(
                                    ContextCompat.getColor(LocationPicker.this, R.color.red));
                            selectLocationButton.setText("Cancel");


                            // Show the SymbolLayer icon to represent the selected map location
                            if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                                GeoJsonSource source = style.getSourceAs("dropped-marker-source-id");
                                if (source != null) {
                                    source.setGeoJson(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));
                                }
                                droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                                if (droppedMarkerLayer != null) {
                                    droppedMarkerLayer.setProperties(visibility(VISIBLE));
                                }
                            }

                            // Use the map camera target's coordinates to make a reverse geocoding search
                            reverseGeocode(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));

                        } else {

                            // Switch the button appearance back to select a location.
                            selectLocationButton.setBackgroundColor(
                                    ContextCompat.getColor(LocationPicker.this, R.color.colorPrimary));
                            selectLocationButton.setText("Select");

                            // Show the red hovering ImageView marker
                            hoveringMarker.setVisibility(View.VISIBLE);

                            // Hide the selected location SymbolLayer
                            droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                            if (droppedMarkerLayer != null) {
                                droppedMarkerLayer.setProperties(visibility(NONE));
                            }
                        }
                        LocationPicker.this.style = style;
                    }
                });
            }
        });
    }

    private void initDroppedMarker(@NonNull Style loadedMapStyle) {
        // Add the marker image to map
        loadedMapStyle.addImage("dropped-icon-image", BitmapFactory.decodeResource(
                getResources(), R.drawable.blue_marker_27by33));
        loadedMapStyle.addSource(new GeoJsonSource("dropped-marker-source-id"));
        loadedMapStyle.addLayer(new SymbolLayer(DROPPED_MARKER_LAYER_ID,
                "dropped-marker-source-id").withProperties(
                iconImage("dropped-icon-image"),
                visibility(NONE),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        ));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Location permission needed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted && mapboxMap != null) {
            Style style = mapboxMap.getStyle();
            if (style != null) {
                enableLocationPlugin(style);
            }
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

     //This method is used to reverse geocode where the user has dropped the marker.
    private void reverseGeocode(final Point point) {
        try {
            mapboxGeocodingBackward = MapboxGeocoding.builder()
                    .accessToken(("pk.eyJ1IjoiamlvbGVoIiwiYSI6ImNrYmxvczRndjE4N28ydW5jajNxMGI1ZTkifQ.FI8oIkU05z3-aEHFKsN8fA"))
                    .query(Point.fromLngLat(point.longitude(), point.latitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            mapboxGeocodingBackward.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                    if (response.body() != null) {
                        List<CarmenFeature> results = response.body().features();
                        if (results.size() > 0) {
                            final CarmenFeature feature = results.get(0);

                            // If the geocoder returns a result, we take the first in the list and show a Toast with the place name.
                            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {
                                    if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                                        Dialog dlg = buildDialog(feature.placeName(), point);
                                        Window window = dlg.getWindow();
                                        assert window != null;
                                        window.setGravity(Gravity.BOTTOM);
                                        dlg.show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(LocationPicker.this,
                                    ("No result please try selecting another location"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                    //Timber.e("Geocoding Failure: %s", throwable.getMessage());
                }
            });
        } catch (ServicesException servicesException) {
            //Timber.e("Error geocoding: %s", servicesException.toString());
            servicesException.printStackTrace();
        }
    }


    @SuppressWarnings({"MissingPermission"})
    private void enableLocationPlugin(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component. Adding in LocationComponentOptions is also an optional
            // parameter
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(
                    this, loadedMapStyle).build());

            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.NORMAL);


        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    public AlertDialog buildDialog(String place, Point point) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LocationPicker.this);
        builder.setTitle("Confirm Location")
                .setMessage(place)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //populate back to activity listing
                        Intent data = new Intent();
                        //---set the data to pass back---
                        data.putExtra("Address_name",place);
                        data.putExtra("Latitude_double", point.latitude());
                        data.putExtra("Longitude_double", point.longitude());
                        setResult(RESULT_OK, data);
                        //---close the activity---
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel then continue finding location
                        // Switch the button appearance back to select a location.
                        selectLocationButton.setBackgroundColor(
                                ContextCompat.getColor(LocationPicker.this, R.color.colorPrimary));
                        selectLocationButton.setText("Select");

                        //make blue icon disappear
                        droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                        if (droppedMarkerLayer != null) {
                            droppedMarkerLayer.setProperties(visibility(NONE));
                        }

                        //make red icon appear
                        hoveringMarker.setVisibility(View.VISIBLE);
                    }
                });
        return builder.create();

    }

    public void search(String queryLocation) {
        mapboxGeocodingForward = MapboxGeocoding.builder().accessToken(MAPBOX_ACCESS_TOKEN)
                                        .query(queryLocation)
                                        .build();

        mapboxGeocodingForward.enqueueCall(new Callback<GeocodingResponse>() {
                    @Override
                    public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                        List<CarmenFeature> results = response.body().features();

                        if (results.size() > 0) {
                            Point firstResultPoint = results.get(0).center();
                            CameraPosition position = new CameraPosition.Builder()
                                    .target(new LatLng(firstResultPoint.latitude(), firstResultPoint.longitude())) // Sets the new camera position
                                    .zoom(15) // Sets the zoom
                                    .bearing(180) // Rotate the camera
                                    .tilt(30) // Set the camera tilt
                                    .build(); // Creates a CameraPosition from the builder'

                            mapboxMap.animateCamera(com.mapbox.mapboxsdk.camera.CameraUpdateFactory
                                    .newCameraPosition(position), 7000);

                        } else {
                            // No result for your request were found.
                            Toast.makeText(LocationPicker.this, "No result found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    //lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

        // if users search halfway and destroy activity
        // will end the thread
        if (mapboxGeocodingBackward!= null) {
            mapboxGeocodingBackward.cancelCall();
        }
        if (mapboxGeocodingForward!=null) {
            mapboxGeocodingForward.cancelCall();
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}




