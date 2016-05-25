package ch.fhnw.ip6.powerconsumptionmanager.view;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.gigamole.library.ArcProgressStackView;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.util.DashboardHelper;

public class OverviewFragment extends Fragment {
    private final static String PRODUCER_NAME = "Photovoltaik";
    private final static String CONSUMER_NAME = "VerbrauchTot";
    private final static String AUTARCHY = "Autarkie";
    private final static String POWERCONSUMPTION = "Eigenverbrauch";
    private final static String RECEIPT = "Bezug";

    private DashboardHelper mDashBoardHelper;
    private Handler mUpdateHandler;

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        setupViewTreeObserver(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int[] graphColors = getContext().getResources().getIntArray(R.array.colorsGraph);

        mDashBoardHelper = new DashboardHelper(getContext(), (LinearLayout) getView().findViewById(R.id.llDynamicContent));
        mDashBoardHelper.addComponentView(PRODUCER_NAME, (ArcProgressStackView) getView().findViewById(R.id.arcProgressProducer));
        mDashBoardHelper.addComponentView(CONSUMER_NAME, (ArcProgressStackView) getView().findViewById(R.id.arcProgressConsumer));
        mDashBoardHelper.addVerticalProgressbar(AUTARCHY, (ProgressBar) getView().findViewById(R.id.vProgressAutarchy));
        mDashBoardHelper.addVerticalProgressbar(RECEIPT, (ProgressBar) getView().findViewById(R.id.vProgressReceipt));
        mDashBoardHelper.addVerticalProgressbar(POWERCONSUMPTION, (ProgressBar) getView().findViewById(R.id.vProgressPowerConsumption));

        //Dynamisch
        mDashBoardHelper.generateDynamicComponentsLayout("Boiler", ContextCompat.getColor(getContext(), R.color.colorDynamicConsumer1));
        mDashBoardHelper.generateDynamicComponentsLayout("Wärmepumpe", ContextCompat.getColor(getContext(), R.color.colorDynamicConsumer2));
        mDashBoardHelper.generateDynamicComponentsLayout("Emobil", ContextCompat.getColor(getContext(), R.color.colorDynamicConsumer3));

        mDashBoardHelper.setPowerForComponent(
            PRODUCER_NAME,
            mDashBoardHelper.generateModelForComponent(
                "",
                (int) Math.floor(Math.random() * 101),
                ContextCompat.getColor(getContext(), R.color.colorArcBackground),
                graphColors[0]
            )
        );

        mDashBoardHelper.setPowerForComponent(
                CONSUMER_NAME,
                mDashBoardHelper.generateModelForComponent(
                        "",
                        (int) Math.floor(Math.random() * 101),
                        ContextCompat.getColor(getContext(), R.color.colorArcBackground),
                        graphColors[1]
                )
        );

        mDashBoardHelper.setVerticalProgress(AUTARCHY, (int) Math.floor(Math.random() * 101));
        mDashBoardHelper.setVerticalProgress(RECEIPT, (int) Math.floor(Math.random() * 41));
        mDashBoardHelper.setVerticalProgress(POWERCONSUMPTION, (int) Math.floor(Math.random() * 101));

        mDashBoardHelper.displayAnimated();

        mUpdateHandler = new Handler();
        mUpdateHandler.postDelayed(updateData, 5000);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Stop data updater if instantiated
        if(mUpdateHandler != null) {
            mUpdateHandler.removeCallbacks(updateData);
        }
    }

    private final Runnable updateData = new Runnable() {
        public void run() {
            mDashBoardHelper.updatePowerForComponent(CONSUMER_NAME, (int) Math.floor(Math.random() * 101));
            mDashBoardHelper.displayAnimated();
            mUpdateHandler.postDelayed(this, 5000);
        }
    };

    private void setupViewTreeObserver(View view) {
        final LinearLayout llDynamicContent = (LinearLayout) view.findViewById(R.id.llDynamicContent);
        ViewTreeObserver vto = llDynamicContent.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    llDynamicContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    llDynamicContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                RelativeLayout.LayoutParams arcsvLayoutParams = new RelativeLayout.LayoutParams(
                        llDynamicContent.getWidth(),
                        llDynamicContent.getHeight()
                );
                arcsvLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                int margins = (int) mDashBoardHelper.getDensity() * 8;
                arcsvLayoutParams.setMargins(margins, margins, margins, margins);

                for (int id: mDashBoardHelper.getArcsvIdsMap().values()) {
                    ArcProgressStackView arcsv = (ArcProgressStackView) getView().findViewById(id);
                    arcsv.setLayoutParams(arcsvLayoutParams);
                }
            }
        });
    }
}
