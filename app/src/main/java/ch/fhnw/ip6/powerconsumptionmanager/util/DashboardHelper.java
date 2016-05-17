package ch.fhnw.ip6.powerconsumptionmanager.util;

import com.gigamole.library.ArcProgressStackView;
import com.gigamole.library.ArcProgressStackView.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Patrik on 15.05.2016.
 */
public class DashboardHelper {
    private HashMap<String, ArcProgressStackView> mArcProgressViews;

    public DashboardHelper() {
        mArcProgressViews = new HashMap<>();
    }

    public void addArcProgressStackView(String key, ArcProgressStackView apsv) {
        mArcProgressViews.put(key, apsv);
    }

    public void setModel(String key, ArrayList<Model> apsvModels) {
        mArcProgressViews.get(key).setModels(apsvModels);
    }

    public ArrayList<Model> generateModel(String description, int progress, int bgProgressColor, int progressColor) {
        Model m = new Model(description, progress, bgProgressColor, progressColor);
        ArrayList<Model> models = new ArrayList<>();
        models.add(m);
        return models;
    }

    public void displayNonAnimated() {
        for (ArcProgressStackView apsv : mArcProgressViews.values()) {
            apsv.invalidate();
        }
    }

    public void displayAnimated() {
        for (ArcProgressStackView apsv : mArcProgressViews.values()) {
            apsv.animateProgress();
        }
    }
}
