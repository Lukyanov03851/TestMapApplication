package vyacheslav.lukyanov.com.testmapapplication;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.util.AttributeSet;

public class ReverseGeocodingTextView extends AppCompatTextView {

    public ReverseGeocodingTextView(Context context) {
        super(context);
    }

    public ReverseGeocodingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setText(int service, String reverseGeocodingResult) {
        setText(Html.fromHtml(getGeocodingResultText(service, reverseGeocodingResult)));
    }

    private String getGeocodingResultText(int service, String reverseGeocodingResult){
        String resultText = "<html><body><b>";
        resultText += getServiceLine(service);
        resultText += "<font color=\"" + String.format("#%06X", (0x392129)) + "\">" + getContext().getString(R.string.result) + "</font>" + " " + reverseGeocodingResult + ", <br>";
        resultText += "</b></body></html>";
        return resultText;
    }

    private String getServiceLine(int service){
        String serviceLine = "<font color=\"" + String.format("#%06X", (0x171718)) + "\">" + getContext().getString(R.string.reverse_geocoding_service);

        switch (service){
            case Constants.GOOGLE_MAP_REV_GEOCODING:
                serviceLine += "</font>" + " " + getContext().getString(R.string.google) + ", <br>";
                break;
            case Constants.MAP_BOX_REV_GEOCODING:
                serviceLine += "</font>" + " " + getContext().getString(R.string.mapbox) + ", <br>";
                break;
        }
        return serviceLine;
    }
}
