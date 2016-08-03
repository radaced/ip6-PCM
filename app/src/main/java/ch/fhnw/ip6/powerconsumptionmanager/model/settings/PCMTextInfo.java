package ch.fhnw.ip6.powerconsumptionmanager.model.settings;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.Map;

import ch.fhnw.ip6.powerconsumptionmanager.R;

/**
 * Represents a various amount of key (string) value (string) pairs. Displayed next to each other.
 */
public class PCMTextInfo extends PCMSetting {
    private LinkedHashMap<String, String> mDescTextPairs;

    /**
     * Constructor to create new text info.
     * @param infoTitle Title that should be displayed above the key value pairs of information.
     * @param descTextPairs A hash map that holds the key value pairs to be displayed.
     */
    public PCMTextInfo(String infoTitle, LinkedHashMap<String, String> descTextPairs) {
        super(infoTitle);
        mDescTextPairs = descTextPairs;
    }

    @Override
    public void inflateLayout(Context context, LinearLayout container) {
        super.inflateLayout(context, container);

        float density = context.getResources().getDisplayMetrics().density;

        LinearLayout.LayoutParams llLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        llLayoutParams.setMargins(0, 0, 0, (int) (MARGIN_BOTTOM * density));

        // Vertical container layout to display the key value pairs under each other (1)
        LinearLayout llTextInfo = new LinearLayout(context);
        llTextInfo.setOrientation(LinearLayout.VERTICAL);
        llTextInfo.setLayoutParams(llLayoutParams);

        // For every key value pair in the hash map...
        for (Map.Entry<String, String> entry : mDescTextPairs.entrySet()) {
            LinearLayout.LayoutParams llDescTextPairParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            // ... create a horizontal layout container to display the key and value next to each other (2)
            LinearLayout llDescTextPair = new LinearLayout(context);
            llDescTextPair.setOrientation(LinearLayout.HORIZONTAL);
            llDescTextPair.setLayoutParams(llDescTextPairParams);

            LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            );

            // ... create a label for one key of the map (description)
            TextView tvDescription = new TextView(context);
            tvDescription.setText(entry.getKey());
            tvDescription.setTextSize(14);
            tvDescription.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
            tvDescription.setLayoutParams(tvLayoutParams);

            // ... add the label to (2)
            llDescTextPair.addView(tvDescription);

            // ... create a label for the value to the key of the map (text/value)
            TextView tvText = new TextView(context);
            tvText.setText(entry.getValue());
            tvText.setTextSize(14);
            tvText.setTypeface(null, Typeface.ITALIC);
            tvText.setGravity(Gravity.END);
            tvText.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
            tvText.setLayoutParams(tvLayoutParams);

            // ... add the text/value to (2)
            llDescTextPair.addView(tvText);

            // ... add the horizontal layout container to (1)
            llTextInfo.addView(llDescTextPair);
        }

        // Add the whole text info container to the main container which holds all settings
        container.addView(llTextInfo);
    }

    @Override
    public String executeSaveOrGenerateJson(Context context) {
        // Save action is not needed because this type of setting can't be modified
        return "";
    }
}
