package ch.fhnw.ip6.powerconsumptionmanager.view;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gigamole.library.ArcProgressStackView;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.util.DashboardHelper;

public class NowFragment extends Fragment {
    private DashboardHelper mDashBoardHelper;

    public static NowFragment newInstance() {
        return new NowFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now, container, false);

        mDashBoardHelper = DashboardHelper.getInstance();
        mDashBoardHelper.init(getContext(), (LinearLayout) view.findViewById(R.id.dynamic_components_container));

        if(mDashBoardHelper.getmDynamicLayoutContainerWidth() == 0 && mDashBoardHelper.getmDynamicLayoutContainerHeight() == 0) {
            setupViewTreeObserver(view);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDashBoardHelper.getArcsvIdsMap().clear();

        mDashBoardHelper.generateDynamicComponentsLayout("Boiler", ContextCompat.getColor(getContext(), R.color.colorDynamicConsumer1));
        mDashBoardHelper.generateDynamicComponentsLayout("Wärmepumpe", ContextCompat.getColor(getContext(), R.color.colorDynamicConsumer2));
        mDashBoardHelper.generateDynamicComponentsLayout("Emobil", ContextCompat.getColor(getContext(), R.color.colorDynamicConsumer3));

        mDashBoardHelper.displayAnimated();
    }

    private void setupViewTreeObserver(View view) {
        final LinearLayout dynamicContentContainer = (LinearLayout) view.findViewById(R.id.dynamic_components_container);
        ViewTreeObserver vto = dynamicContentContainer.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // TODO: Seems to get called before real height and width are available --> nothing is visible on attach

                if (Build.VERSION.SDK_INT < 16) {
                    dynamicContentContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    dynamicContentContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                mDashBoardHelper.setmDynamicLayoutContainerWidth(dynamicContentContainer.getWidth());
                mDashBoardHelper.setmDynamicLayoutContainerHeight(dynamicContentContainer.getWidth());

                RelativeLayout.LayoutParams arcsvLayoutParams = new RelativeLayout.LayoutParams(
                        dynamicContentContainer.getWidth(),
                        dynamicContentContainer.getHeight()
                );
                arcsvLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                int margins = (int) mDashBoardHelper.getDensity() * 8;
                arcsvLayoutParams.setMargins(margins, margins, margins, margins);

                for (int id : mDashBoardHelper.getArcsvIdsMap().values()) {
                    ArcProgressStackView arcsv = (ArcProgressStackView) getView().findViewById(id);
                    arcsv.setLayoutParams(arcsvLayoutParams);
                }
            }
        });
    }
}
