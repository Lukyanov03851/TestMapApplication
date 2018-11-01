package vyacheslav.lukyanov.com.testmapapplication.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import vyacheslav.lukyanov.com.testmapapplication.R;
import vyacheslav.lukyanov.com.testmapapplication.ReverseGeocodingTextView;

public class CustomDialogFragment extends DialogFragment {

    private static final String ARG_SERVICE = "service";
    private static final String ARG_REVERSE_GEOCODING_RESULT = "reverse_geocoding_result";
    private int service;
    private String reverseGeocodingResult;

    ReverseGeocodingTextView reverseGeocodingTextView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param service Parameter 1.
     * @param reverseGeocodingResult Parameter 2.
     * @return A new instance of fragment CustomDialogFragment.
     */
    public static CustomDialogFragment newInstance(int service, String reverseGeocodingResult) {
        CustomDialogFragment fragment = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SERVICE, service);
        args.putString(ARG_REVERSE_GEOCODING_RESULT, reverseGeocodingResult);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            service = getArguments().getInt(ARG_SERVICE);
            reverseGeocodingResult = getArguments().getString(ARG_REVERSE_GEOCODING_RESULT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_custom_dialog, container, false);
        getDialog().setTitle(getString(R.string.reverse_geocoding_result));

        reverseGeocodingTextView = v.findViewById(R.id.tv_reverse_geocoding);
        reverseGeocodingTextView.setText(service, reverseGeocodingResult);

        Button cancelButton = v.findViewById(R.id.btn_ok);
        cancelButton.setOnClickListener(v1 -> dismiss());
        return v;
    }
}
