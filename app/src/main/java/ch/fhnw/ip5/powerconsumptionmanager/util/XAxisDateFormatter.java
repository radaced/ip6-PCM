package ch.fhnw.ip5.powerconsumptionmanager.util;

import android.text.format.DateFormat;

import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Patrik on 06.12.2015.
 */
public class XAxisDateFormatter implements XAxisValueFormatter {
    @Override
    public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
        Calendar cal = Calendar.getInstance(Locale.GERMAN);
        cal.setTimeInMillis(Long.parseLong(original) * 1000);
        return DateFormat.format("HH:mm", cal).toString();
    }
}
