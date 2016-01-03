package ch.fhnw.ip5.powerconsumptionmanager.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ch.fhnw.ip5.powerconsumptionmanager.R;

/**
 * Created by Patrik on 02.01.2016.
 */
public class TeslaLoginFragment extends Fragment {

    public static TeslaLoginFragment newInstance() {
        TeslaLoginFragment fragment = new TeslaLoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tesla_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        final Tesla t = new Tesla();

        Button connectButton = (Button) view.findViewById(R.id.button_connect_tesla);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) v.findViewById(R.id.edit_login_username);
                EditText password = (EditText) v.findViewById(R.id.edit_login_password);
                TextView status = (TextView) v.findViewById(R.id.text_login_status);

//                if(t.connect(username.getText().toString(), password.getText().toString())) {
//                    status.setText("TESLA CALL SUCCESSFULL!");
//                } else {
//                    status.setText("TESLA CALL FAILED!");
//                }
            }
        });
    }
}
