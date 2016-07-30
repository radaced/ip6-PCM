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

public class PCMTextInfo extends PCMSetting {
    private LinkedHashMap<String, String> mDescTextPairs;

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
        
        LinearLayout llTextInfo = new LinearLayout(context);
        llTextInfo.setOrientation(LinearLayout.VERTICAL);
        llTextInfo.setLayoutParams(llLayoutParams);

        for (Map.Entry<String, String> entry : mDescTextPairs.entrySet()) {
            LinearLayout.LayoutParams llDescTextPairParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            LinearLayout llDescTextPair = new LinearLayout(context);
            llDescTextPair.setOrientation(LinearLayout.HORIZONTAL);
            llDescTextPair.setLayoutParams(llDescTextPairParams);

            LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            );

            TextView tvDescription = new TextView(context);
            tvDescription.setText(entry.getKey());
            tvDescription.setTextSize(14);
            tvDescription.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
            tvDescription.setLayoutParams(tvLayoutParams);

            llDescTextPair.addView(tvDescription);

            TextView tvText = new TextView(context);
            tvText.setText(entry.getValue());
            tvText.setTextSize(14);
            tvText.setTypeface(null, Typeface.ITALIC);
            tvText.setGravity(Gravity.END);
            tvText.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
            tvText.setLayoutParams(tvLayoutParams);

            llDescTextPair.addView(tvText);

            llTextInfo.addView(llDescTextPair);
        }

        container.addView(llTextInfo);
    }

    @Override
    public String generateSaveJson() {
        return null;
    }
}
