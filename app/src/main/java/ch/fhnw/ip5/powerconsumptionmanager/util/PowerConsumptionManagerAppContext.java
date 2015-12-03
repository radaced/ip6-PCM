package ch.fhnw.ip5.powerconsumptionmanager.util;

import android.app.Application;

import ch.fhnw.ip5.powerconsumptionmanager.model.ConsumptionDataModel;

/**
 * Created by Patrik on 02.12.2015.
 */
public class PowerConsumptionManagerAppContext extends Application{
    private ConsumptionDataModel consumptionData;

    public ConsumptionDataModel getConsumptionData() {
        return consumptionData;
    }

    public void setUsageData(ConsumptionDataModel componentData) {
        this.consumptionData = componentData;
    }
}
