package vyacheslav.lukyanov.com.testmapapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import vyacheslav.lukyanov.com.testmapapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMapBoxReverseGeocoding} interface
 * to handle interaction events.
 */
public class MapBoxFragment extends Fragment implements MapboxMap.OnMapClickListener{

    private MapView mapView;
    private MapboxMap mapboxMap;
    private OnMapBoxReverseGeocoding mListener;

    public MapBoxFragment() {
        // Required empty public constructor
    }

    public static MapBoxFragment newInstance() {
        return new MapBoxFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Mapbox.getInstance(getContext().getApplicationContext(), getString(R.string.mapbox_access_token));
        View view =  inflater.inflate(R.layout.fragment_map_box, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapBoxMap -> {
            MapBoxFragment.this.mapboxMap = mapBoxMap;
            mapboxMap.addOnMapClickListener(this);
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMapBoxReverseGeocoding) {
            mListener = (OnMapBoxReverseGeocoding) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMapBoxReverseGeocoding");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        mListener.onMapBoxReverseGeocoding(point.getLongitude(), point.getLatitude());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnMapBoxReverseGeocoding {
        void onMapBoxReverseGeocoding(double lng, double lat);
    }
}
