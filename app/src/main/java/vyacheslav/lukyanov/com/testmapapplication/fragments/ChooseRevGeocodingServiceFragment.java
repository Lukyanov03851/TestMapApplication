package vyacheslav.lukyanov.com.testmapapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import vyacheslav.lukyanov.com.testmapapplication.Constants;
import vyacheslav.lukyanov.com.testmapapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnChooseServiceListener} interface
 * to handle interaction events.
 * Use the {@link ChooseRevGeocodingServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseRevGeocodingServiceFragment extends Fragment {

    private static final String ARG_CURRENT_SERVICE = "current_reverse_geocoding_service";

    private int currentReverseGeocodingService;

    RadioButton googleRadioButton;
    RadioButton mapBoxRadioButton;

    private OnChooseServiceListener mListener;

    public ChooseRevGeocodingServiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currentService Parameter 1.
     * @return A new instance of fragment ChooseRevGeocodingServiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChooseRevGeocodingServiceFragment newInstance(int currentService) {
        ChooseRevGeocodingServiceFragment fragment = new ChooseRevGeocodingServiceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CURRENT_SERVICE, currentService);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            currentReverseGeocodingService = getArguments().getInt(ARG_CURRENT_SERVICE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_choose_reverse_geocoding, container, false);
        googleRadioButton = v.findViewById(R.id.radio_google_service);
        mapBoxRadioButton = v.findViewById(R.id.radio_map_box_service);

        switch (currentReverseGeocodingService){
            case Constants.GOOGLE_MAP_REV_GEOCODING:
                googleRadioButton.setChecked(true);
                break;
            case Constants.MAP_BOX_REV_GEOCODING:
                mapBoxRadioButton.setChecked(true);
                break;
        }

        RadioGroup radioGroup = v.findViewById(R.id.radio_group_reverse_geocoding_services);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radio_google_service:
                    currentReverseGeocodingService = Constants.GOOGLE_MAP_REV_GEOCODING;
                    break;
                case R.id.radio_map_box_service:
                    currentReverseGeocodingService = Constants.MAP_BOX_REV_GEOCODING;
                    break;
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChooseServiceListener) {
            mListener = (OnChooseServiceListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChooseServiceListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_done, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(item.getItemId() == R.id.menu_done){
            mListener.onChooseGeocodingService(currentReverseGeocodingService);
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnChooseServiceListener {
        void onChooseGeocodingService(int service);
    }
}
