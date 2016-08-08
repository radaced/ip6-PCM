package ch.fhnw.ip6.powerconsumptionmanager.view.startup;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

/**
 * Fragment shows when the application is started for the first time (initial setting of IP).
 */
public class InitFragment extends Fragment {

    public static InitFragment newInstance() {
        return new InitFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_init, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button continueButton = (Button) view.findViewById(R.id.buttonContinue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean correct = true;
                EditText ip1 = (EditText) getActivity().findViewById(R.id.etIP1);
                EditText ip2 = (EditText) getActivity().findViewById(R.id.etIP2);
                EditText ip3 = (EditText) getActivity().findViewById(R.id.etIP3);
                EditText ip4 = (EditText) getActivity().findViewById(R.id.etIP4);

                // Error detection in case IP is invalid
                if (!isValidIPNumber(ip1)) {
                    ip1.setError(getString(R.string.text_init_ip_error));
                    correct = false;
                }
                if (!isValidIPNumber(ip2)) {
                    ip2.setError(getString(R.string.text_init_ip_error));
                    correct = false;
                }
                if (!isValidIPNumber(ip3)) {
                    ip3.setError(getString(R.string.text_init_ip_error));
                    correct = false;
                }
                if (!isValidIPNumber(ip4)) {
                    ip4.setError(getString(R.string.text_init_ip_error));
                    correct = false;
                }

                if (correct) {
                    // When input was correct load preference file and set the key-value pair for IP and other settings
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = settings.edit();
                    String ip = ip1.getText().toString() + "." + ip2.getText().toString() + "." + ip3.getText().toString() + "." + ip4.getText().toString();
                    editor.putString("IP", ip);
                    editor.putBoolean("googleCalendar", false);
                    editor.putBoolean("updateAutomatically", false);
                    editor.putInt("updateInterval", 10);
                    editor.putInt("costStatisticsPeriod", 7);
                    editor.apply();

                    // Set settings in the application context for easier access
                    PowerConsumptionManagerAppContext context = (PowerConsumptionManagerAppContext) getActivity().getApplicationContext();
                    context.setIPAdress(ip);
                    context.setGoogleCalendar(false);
                    context.setUpdatingAutomatically(false);
                    context.setUpdateInterval(10);
                    context.setCostStatisticsPeriod(7);
                    context.setIPAdress(settings.getString("IP", "192.168.0.1"));

                    // Change to loading fragment
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    SplashFragment fragment = new SplashFragment();
                    transaction.replace(R.id.flStartupContentContainer, fragment);
                    transaction.commit();
                }
            }
        });
    }

    /**
     * Error detection for IP.
     * @param ip The textfield where the content needs to be checked.
     * @return True when IP number is valid, false otherwise.
     */
    private boolean isValidIPNumber(EditText ip) {
        // IP field can't be empty
        if(ip.getText().length() <= 0) {
            return false;
        }

        // Entered number needs to be between 0 and 255
        int ipNumber = Integer.parseInt(ip.getText().toString());
        return !(ipNumber < 0 || ipNumber > 255);
    }
}
