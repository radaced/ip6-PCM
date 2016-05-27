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
import android.widget.RelativeLayout;

import com.gigamole.library.ArcProgressStackView;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.activity.MainActivity;
import ch.fhnw.ip6.powerconsumptionmanager.util.DashboardHelper;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import me.itangqi.waveloadingview.WaveLoadingView;

public class OverviewFragment extends Fragment {
    public final static String AUTARCHY = "Autarchy";
    public final static String SELF_CONSUMPTION = "Self consumption";
    public final static String OCCUPATION = "Occupation";
    private final static String PRODUCER = "Producer";
    private final static String CONSUMER = "Consumer";

    private PowerConsumptionManagerAppContext mAppContext;
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
        mAppContext = (PowerConsumptionManagerAppContext) getActivity().getApplicationContext();

        final int[] graphColors = getContext().getResources().getIntArray(R.array.colorsGraph);

        mDashBoardHelper = new DashboardHelper(getContext(), (LinearLayout) getView().findViewById(R.id.llDynamicContent));

        mDashBoardHelper.addSummaryView(AUTARCHY, (WaveLoadingView) getView().findViewById(R.id.wlvAutarchy), mAppContext.UNIT_PERCENTAGE);
        mDashBoardHelper.addSummaryView(SELF_CONSUMPTION, (WaveLoadingView) getView().findViewById(R.id.wlvSelfConsumption), mAppContext.UNIT_PERCENTAGE);
        mDashBoardHelper.addSummaryView(OCCUPATION, (WaveLoadingView) getView().findViewById(R.id.wlvOccupation), mAppContext.UNIT_KW);

        mDashBoardHelper.setSummaryRatio(AUTARCHY, 50);
        mDashBoardHelper.setSummaryRatio(SELF_CONSUMPTION, 20);
        mDashBoardHelper.setSummaryRatio(OCCUPATION, -10);

        //Dynamisch
        mDashBoardHelper.generateDynamicComponentsLayout("Boiler", ContextCompat.getColor(getContext(), R.color.colorDynamicConsumer1));
        mDashBoardHelper.generateDynamicComponentsLayout("Wärmepumpe", ContextCompat.getColor(getContext(), R.color.colorDynamicConsumer2));
        mDashBoardHelper.generateDynamicComponentsLayout("Emobil", ContextCompat.getColor(getContext(), R.color.colorDynamicConsumer3));

        mDashBoardHelper.displayAnimated();

//        mUpdateHandler = new Handler();
//        mUpdateHandler.postDelayed(updateData, 5000);
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
//            mDashBoardHelper.updatePowerForComponent(CONSUMER_NAME, (int) Math.floor(Math.random() * 101));
//            mDashBoardHelper.displayAnimated();
//            mUpdateHandler.postDelayed(this, 5000);
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
