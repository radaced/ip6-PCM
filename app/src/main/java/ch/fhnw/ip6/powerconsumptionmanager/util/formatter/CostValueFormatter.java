package ch.fhnw.ip6.powerconsumptionmanager.util.formatter;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Formats the cost labels in the bar chart that shows the daily values in the overview section.
 */
public class CostValueFormatter implements ValueFormatter {

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(value) + " CHF";
    }
}
