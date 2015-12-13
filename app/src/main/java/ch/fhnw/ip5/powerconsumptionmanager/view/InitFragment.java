package ch.fhnw.ip5.powerconsumptionmanager.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

public class InitFragment extends Fragment {
    private static final String CONNECTION_SETTINGS = "connection_settings";

    public static InitFragment newInstance() {
        InitFragment fragment = new InitFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_init, container, false);

        Button continueButton = (Button) view.findViewById(R.id.buttonContinue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean correct = true;

                EditText ip1 = (EditText) getActivity().findViewById(R.id.editIP1);
                EditText ip2 = (EditText) getActivity().findViewById(R.id.editIP2);
                EditText ip3 = (EditText) getActivity().findViewById(R.id.editIP3);
                EditText ip4 = (EditText) getActivity().findViewById(R.id.editIP4);

                if (!isValidIPNumber(ip1)) {
                    ip1.setError(getString(R.string.init_text_ip_error));
                    correct = false;
                }
                if (!isValidIPNumber(ip2)) {
                    ip2.setError(getString(R.string.init_text_ip_error));
                    correct = false;
                }
                if (!isValidIPNumber(ip3)) {
                    ip3.setError(getString(R.string.init_text_ip_error));
                    correct = false;
                }
                if (!isValidIPNumber(ip4)) {
                    ip4.setError(getString(R.string.init_text_ip_error));
                    correct = false;
                }

                if(correct) {
                    SharedPreferences settings = getActivity().getSharedPreferences(CONNECTION_SETTINGS, getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    String ip = ip1.getText().toString()+"."+ip2.getText().toString()+"."+ip3.getText().toString()+"."+ip4.getText().toString();
                    editor.putString("IP", ip);
                    editor.commit();

                    PowerConsumptionManagerAppContext context = (PowerConsumptionManagerAppContext) getActivity().getApplicationContext();
                    context.setIPAdress(ip);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    SplashFragment fragment = new SplashFragment();
                    transaction.replace(R.id.splash_fragment, fragment);
                    transaction.commit();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private boolean isValidIPNumber(EditText ip) {
        if(ip.getText().length() <= 0) {
            return false;
        }

        int ipNumber = Integer.parseInt(ip.getText().toString());
        if(ipNumber < 0 || ipNumber > 255) {
            return false;
        }

        return true;
    }
}
