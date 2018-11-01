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
 * {@link OnChooseMapProviderListener} interface
 * to handle interaction events.
 * Use the {@link ChooseMapProviderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseMapProviderFragment extends Fragment {

    private static final String ARG_CURRENT_PROVIDER = "current_provider";

    private int currentProvider;

    private OnChooseMapProviderListener mListener;
    RadioButton googleRadioButton;
    RadioButton mapBoxRadioButton;

    public ChooseMapProviderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currentProvider Parameter 1.
     * @return A new instance of fragment ChooseMapProviderFragment.
     */
    public static ChooseMapProviderFragment newInstance(int currentProvider) {
        ChooseMapProviderFragment fragment = new ChooseMapProviderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CURRENT_PROVIDER, currentProvider);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            currentProvider = getArguments().getInt(ARG_CURRENT_PROVIDER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_choose_map, container, false);
        googleRadioButton = v.findViewById(R.id.radio_google);
        mapBoxRadioButton = v.findViewById(R.id.radio_map_box);

        switch (currentProvider){
            case Constants.GOOGLE_MAP_PROVIDER:
                googleRadioButton.setChecked(true);
                break;
            case Constants.MAP_BOX_PROVIDER:
                mapBoxRadioButton.setChecked(true);
                break;
        }

        RadioGroup radioGroup = v.findViewById(R.id.radio_group_maps_provider);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radio_google:
                    currentProvider = Constants.GOOGLE_MAP_PROVIDER;
                    break;
                case R.id.radio_map_box:
                    currentProvider = Constants.MAP_BOX_PROVIDER;
                    break;
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChooseMapProviderListener) {
            mListener = (OnChooseMapProviderListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChooseMapProviderListener");
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
            mListener.onChooseMapProvider(currentProvider);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnChooseMapProviderListener {
        void onChooseMapProvider(int provider);
    }
}
