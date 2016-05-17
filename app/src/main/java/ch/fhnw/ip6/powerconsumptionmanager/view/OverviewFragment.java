package ch.fhnw.ip6.powerconsumptionmanager.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.gigamole.library.ArcProgressStackView;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.util.DashboardHelper;

public class OverviewFragment extends Fragment {
    private final static String PRODUCER_NAME = "Photovoltaik";
    private final static String CONSUMER_NAME = "VerbrauchTot";

    private DashboardHelper mDashBoardHelper;

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int[] graphColors = getContext().getResources().getIntArray(R.array.colorsGraph);

        mDashBoardHelper = new DashboardHelper();
        mDashBoardHelper.addArcProgressStackView(PRODUCER_NAME, (ArcProgressStackView) getView().findViewById(R.id.arcProgressProducer));
        mDashBoardHelper.addArcProgressStackView(CONSUMER_NAME, (ArcProgressStackView) getView().findViewById(R.id.arcProgressConsumer));

        ProgressBar pb = (ProgressBar) getView().findViewById(R.id.vProgressReceipt);
        int progress = (int) Math.floor(Math.random() * 41);
        if(progress <= 17) {
            pb.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.progressbar_vertical_positive));
        } else if(progress > 17 && progress < 23) {
            pb.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.progressbar_vertical_neutral));
        } else {
            pb.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.progressbar_vertical_negative));
        }
        pb.setProgress(progress);

//        FrameLayout container = (FrameLayout) getView().findViewById(R.id.frame);
//        ArcProgressStackView arcsv = new ArcProgressStackView(getContext());
//        arcsv.setStartAngle(135);
//        arcsv.setSweepAngle(270);
//        mDashBoardHelper.addArcProgressStackView("Test", arcsv);
//        mDashBoardHelper.setModel(
//            "Test",
//            mDashBoardHelper.generateModel(
//                "",
//                100,
//                ContextCompat.getColor(getContext(), R.color.colorArcBackground),
//                graphColors[2]
//            )
//        );
//        container.addView(arcsv);


        mDashBoardHelper.setModel(
            PRODUCER_NAME,
            mDashBoardHelper.generateModel(
                "",
                (int) Math.floor(Math.random() * 101),
                ContextCompat.getColor(getContext(), R.color.colorArcBackground),
                graphColors[0]
            )
        );

        mDashBoardHelper.setModel(
                CONSUMER_NAME,
                mDashBoardHelper.generateModel(
                        "",
                        (int) Math.floor(Math.random() * 101),
                        ContextCompat.getColor(getContext(), R.color.colorArcBackground),
                        graphColors[1]
                )
        );

        mDashBoardHelper.displayAnimated();
    }
}
