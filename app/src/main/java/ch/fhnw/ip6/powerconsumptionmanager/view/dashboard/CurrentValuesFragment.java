package ch.fhnw.ip6.powerconsumptionmanager.view.dashboard;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gigamole.library.ArcProgressStackView;

import java.util.LinkedHashMap;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.PCMComponent;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip6.powerconsumptionmanager.util.helper.DashboardHelper;

/**
 * Displays an arc progress view to every component that shows its current state.
 */
public class CurrentValuesFragment extends Fragment {
    private DashboardHelper mDashboardHelper;

    public static CurrentValuesFragment newInstance() {
        return new CurrentValuesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now, container, false);

        // Setup helper instance
        mDashboardHelper = DashboardHelper.getInstance();
        mDashboardHelper.initCurrentValuesContext(getContext());
        mDashboardHelper.setDynamicLayoutContainer((LinearLayout) view.findViewById(R.id.dynamic_components_container));
        mDashboardHelper.getComponentViews().clear();

        /* On first display of current values fragment setup a view tree observer because maximum height
         * and width of dynamic content container are not available yet
         */
        if(mDashboardHelper.getDynamicLayoutContainerWidth() == 0 && mDashboardHelper.getDynamicLayoutContainerHeight() == 0) {
            setupViewTreeObserver(view);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PowerConsumptionManagerAppContext appContext = (PowerConsumptionManagerAppContext) getActivity().getApplicationContext();
        LinkedHashMap<String, PCMComponent> componentData = appContext.getPCMData().getComponentData();

        // Generate the UI element to show current data for every component that needs to be displayed in the dashboard
        for (String key : componentData.keySet()) {
            if(componentData.get(key).isDisplayedOnDashboard()) {
                mDashboardHelper.generateComponentUIElement(
                        key,
                        ContextCompat.getColor(getContext(), R.color.colorArcProgress)
                );
            }
        }

        // Display the UI elements
        mDashboardHelper.displayAnimated();
    }

    private void setupViewTreeObserver(View view) {
        final LinearLayout dynamicContentContainer = (LinearLayout) view.findViewById(R.id.dynamic_components_container);
        ViewTreeObserver vto = dynamicContentContainer.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    dynamicContentContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    dynamicContentContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                // Set the width and height of the dynamic layout container in the dashboard helper instance
                mDashboardHelper.setDynamicLayoutContainerWidth(dynamicContentContainer.getWidth());
                mDashboardHelper.setDynamicLayoutContainerHeight(dynamicContentContainer.getWidth());

                RelativeLayout.LayoutParams arcsvLayoutParams = new RelativeLayout.LayoutParams(
                        dynamicContentContainer.getWidth(),
                        dynamicContentContainer.getHeight()
                );
                arcsvLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                int margins = (int) mDashboardHelper.getDensity() * 8;
                arcsvLayoutParams.setMargins(margins, margins, margins, margins);

                // Update the width and height of the arc progress views to fill out all available space
                for (ArcProgressStackView arcsv : mDashboardHelper.getComponentViews().values()) {
                    arcsv.setLayoutParams(arcsvLayoutParams);
                }
            }
        });
    }
}
