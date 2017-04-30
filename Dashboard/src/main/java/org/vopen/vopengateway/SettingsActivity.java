package org.vopen.vopengateway;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.CheckBox;



import java.util.List;

public class SettingsActivity extends PreferenceActivity
{

    Messenger mMessenger = new Messenger(new IncomingHandler());
    Messenger mService;
    private static final int MSG_SUBSCRIBE = 0;
    private static final int MSG_UNSUBSCRIBE = 1;
    private static final int MSG_TOPICUPDATE = 2;
    private static final int MSG_PUBLISH = 3;
    private static final int MSG_ENABLE = 4;
    private static final int MSG_DISABLE = 5;
    private static final int MSG_APIKEYCHANGED = 6;
    private static final int MSG_HOSTCHANGED = 7;
    private static final int MSG_PORTCHANGED = 8;
    private static final int MSG_PUBLIC_SUBSCRIBE = 9;
    private static final int MSG_PUBLIC_UNSUBSCRIBE = 10;
    private String apiKey, host, port;

    private void sendSubscription(String update, boolean enabled)
    {

        Message m = new Message();
        m.replyTo = mMessenger;
        m.what = enabled? MSG_PUBLIC_SUBSCRIBE : MSG_PUBLIC_UNSUBSCRIBE;
        Bundle b = new Bundle();
        b.putString("topic", "gwStatus");
        m.setData(b);
        try
        {
            mService.send(m);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    private void enableService(boolean enabled)
    {
        Message m = new Message();
        m.replyTo = mMessenger;
        m.what = enabled? MSG_ENABLE : MSG_DISABLE;
        try
        {
            mService.send(m);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }


    private void sendConfigUpdate(int what, String key, String value)
    {
        Message m = new Message();
        m.replyTo = mMessenger;
        m.what = what;
        Bundle b = new Bundle();
        b.putString(key,value);
        m.setData(b);
        try
        {
            if (mService != null)
            {
                mService.send(m);
            }
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }






    class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TOPICUPDATE:
                    activation.setSummary(msg.getData().getCharSequence("gwStatus").toString());

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    CheckBoxPreference activation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getApplicationContext().startService(new Intent(getApplicationContext(), MQTTService.class));
        getApplicationContext().bindService(new Intent(getApplicationContext(), MQTTService.class), mConnection, 0);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }


    private void setupSimplePreferencesScreen()
    {

        //addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(findPreference("broker_address"));
        bindPreferenceSummaryToValue(findPreference("broker_port"));
        bindPreferenceSummaryToValue(findPreference("broker_apiKey"));
        activation = (CheckBoxPreference)findPreference("broker_status");
        //activation.setSummary("Unknown");
        activation.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);



    }

    @Override
    public boolean onIsMultiPane()
    {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    private static boolean isXLargeTablet(Context context)
    {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    private static boolean isSimplePreferences(Context context)
    {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    private static class Logger
    {
        public static void log(String text)
        {
            Log.d(SettingsActivity.class.getCanonicalName(), text);
        }
    }


    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener()
    {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value)
        {


            Logger.log(preference.getKey());

            if (preference.getKey().compareTo("broker_status") == 0)
            {

                CheckBoxPreference activation = (CheckBoxPreference)preference;
                if (activation.isChecked())
                {

                    enableService(false);

                }
                else
                {
                    enableService(true);
                }
            }
            else if (preference.getKey().compareTo("broker_apiKey") == 0)
            {
                sendConfigUpdate(MSG_APIKEYCHANGED, "apiKey", value.toString());
                apiKey = value.toString();
                String stringValue = value.toString();
                preference.setSummary(stringValue);
            }
            else if (preference.getKey().compareTo("broker_address") == 0)
            {
                sendConfigUpdate(MSG_HOSTCHANGED, "address", value.toString());
                host = value.toString();
                String stringValue = value.toString();
                preference.setSummary(stringValue);
            }
            else if (preference.getKey().compareTo("broker_port") == 0)
            {
                sendConfigUpdate(MSG_PORTCHANGED, "port", value.toString());
                port = value.toString();
                String stringValue = value.toString();
                preference.setSummary(stringValue);
            }


            return true;
        }
    };

    private ServiceConnection mConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            mService = new Messenger(service);
            sendSubscription("gwStatus",true);
            sendConfigUpdate(MSG_APIKEYCHANGED, "apiKey", apiKey);
            sendConfigUpdate(MSG_HOSTCHANGED, "address", host);
            sendConfigUpdate(MSG_PORTCHANGED, "port", port);

        }

        public void onServiceDisconnected(ComponentName className)
        {
            mService = null;
        }
    };

    private void bindPreferenceSummaryToValue(Preference preference)
    {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        bindPreferenceSummaryToValue(findPreference("broker_apiKey"));
    }
}
