package ch.fhnw.ip5.powerconsumptionmanager.util;

import android.app.Application;

import com.github.mikephil.charting.charts.LineChart;

import ch.fhnw.ip5.powerconsumptionmanager.model.ConsumptionDataModel;

/**
 * Created by Patrik on 02.12.2015.
 */
public class PowerConsumptionManagerAppContext extends Application{
    private ConsumptionDataModel mConsumptionData;
    private LineChart mConsumptionChart;

    public ConsumptionDataModel getConsumptionData() {
        return mConsumptionData;
    }

    public void setUsageData(ConsumptionDataModel componentData) {
        this.mConsumptionData = componentData;
    }

    public LineChart getConsumptionChart() {
        return mConsumptionChart;
    }

    public void setConsumptionChart(LineChart mConsumptionChart) {
        this.mConsumptionChart = mConsumptionChart;
    }
}
