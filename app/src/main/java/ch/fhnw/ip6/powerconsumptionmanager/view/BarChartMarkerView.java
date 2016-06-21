package ch.fhnw.ip6.powerconsumptionmanager.view;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import ch.fhnw.ip6.powerconsumptionmanager.R;

public class BarChartMarkerView extends MarkerView {

    private TextView tvContent;

    public BarChartMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        String value = "" + Math.round(e.getVal());
        tvContent.setText(value);
    }

    @Override
    public int getXOffset(float xpos) {
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        return -getHeight();
    }
}
