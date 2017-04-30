package org.vopen.vopengateway;

//import org.eclipse.paho.client.mqttv3.IMqttActionListener;
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.IMqttToken;
//import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
//import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MQTTService extends Service implements VOpenGatewayService
        //, IMqttActionListener, MqttCallback
        {
    private static final int MSG_PRIVATE_SUBSCRIBE = 0;
    private static final int MSG_PRIVATE_UNSUBSCRIBE = 1;
    private static final int MSG_TOPICUPDATE = 2;
    private static final int MSG_PUBLISH = 3;
    private static final int MSG_ENABLE = 4;
    private static final int MSG_DISABLE = 5;
    private static final int MSG_APIKEYCHANGED = 6;
    private static final int MSG_HOSTCHANGED = 7;
    private static final int MSG_PORTCHANGED = 8;
    private static final int MSG_PUBLIC_SUBSCRIBE = 9;
    private static final int MSG_PUBLIC_UNSUBSCRIBE = 10;
    private static final int MSG_REST_API_CALL = 11;
    private static final int MSG_REST_API_RESP = 12;



    private ApiAuth broker_apiKey = new ApiAuth("ChangeMe","ChangeMe");
    private String broker_address;
    private int broker_port;
    private boolean broker_enabled;
   // private IMqttToken token;
    private SubscriptionGraph mSubscriptions = new SubscriptionGraph();
    private static final String TAG = "MQTTService";
    private String connectivityStatus;
    final Messenger mMessenger = new Messenger(new IncomingHandler(this));
   // private MqttAsyncClient client;
    private Handler reconnectionHandler = new Handler();
    private Runnable mReconnector = new Runnable() {

        @Override
        public void run() {
            reconnectionHandler.removeCallbacks(this);

            if (broker_enabled) {
                    doConnect();
            }

        }

    };


    @Override
    public void onCreate() {

        super.onCreate();


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);


        broker_address = preferences.getString("broker_address", "");
        broker_port = Integer.parseInt(preferences.getString("broker_port", "0"));
        broker_enabled = preferences.getBoolean("broker_status", false);

        try
        {
            broker_apiKey = ApiAuth.fromString(preferences.getString("broker_apiKey", "ChangeMe:ChangeMe"));
        }
        catch (MalformedApiKeyException e)
        {
            connectivityStatus = "MALFORMED API KEY";
            sendConnectivityUpdate();
            return;
        }

        connectivityStatus = "DISCONNECTED";
        sendConnectivityUpdate();
        if (broker_enabled) doConnect();

    }


    @Override
    public void doConnect() {
        if (!broker_enabled)
            return;


//        try {
//
//            MqttConnectOptions options = new MqttConnectOptions();
//            options.setUserName(broker_apiKey.getPrivateKey());
//            options.setServerURIs(new String[]{"tcp://" + broker_address + ":" + broker_port});
//            options.setCleanSession(true);
//
//
//            try
//            {
//                if (client != null && client.isConnected()) client.close();
//                client = new MqttAsyncClient("tcp://" + broker_address + ":" + broker_port,
//                        MqttClient.generateClientId(), new MemoryPersistence());
//            } catch (MqttException e) {
//                e.printStackTrace();
//            }
//
//            client.setCallback(this);
//            client.connect(options, null, this);
//
//            connectivityStatus = "CONNECTING";
//            sendConnectivityUpdate();
//
//            Log.v(TAG, "CONNECTING to " + "tcp://" + broker_address + ":" + broker_port + " clientId: " + client.getClientId());
//
//        }
//        catch (MqttException e)
//        {
//            e.printStackTrace();
//        }
//        catch (MalformedApiKeyException e)
//        {
//            connectivityStatus = "MALFORMED API KEY";
//            sendConnectivityUpdate();
//            return;
//        }
    }

    @Override
    public void doDisconnect()
    {
        connectivityStatus = "DISCONNECTED";
        sendConnectivityUpdate();

//        if (client == null) return;
//
//        try
//        {
//            if (client.isConnected())
//            {
//                client.setCallback(null);
//                client.disconnect();
//            }
//        }
//        catch (MqttException e)
//        {
//            e.printStackTrace();
//        }
    }

//    @Override
//    public void onSuccess(IMqttToken asyncActionToken)
//    {
//        Log.v(TAG, "onSuccess");
//        connectivityStatus = "CONNECTED";
//        // since we used options.setCleanSession(true);
//        // we should clean the graph for anything != gwStatus
//        mSubscriptions.externalClear();
//        sendConnectivityUpdate();
//        reconnectionHandler.removeCallbacks(mReconnector);
//    }
//
//    @Override
//    public void onFailure(IMqttToken asyncActionToken, Throwable exception)
//    {
//        Log.v(TAG, "onFailure");
//        connectivityStatus = "CONNECTION ERROR (" + exception.getMessage() + ")";
//        sendConnectivityUpdate();
//        reconnectionHandler.removeCallbacks(mReconnector);
//        reconnectionHandler.postDelayed(mReconnector, 5000);
//    }

    @Override
    public void sendConnectivityUpdate(Messenger client)
    {

        Message m = new Message();
        m.what = MSG_TOPICUPDATE;
        Bundle b = new Bundle();
        b.putString("gwStatus", connectivityStatus);
        m.setData(b);
        try
        {
            client.send(m);
        }
        catch (RemoteException e)
        {
            e.printStackTrace(); // TODO catch the other dead exception
        }

    }

    private void sendConnectivityUpdate()
    {
        Log.v(TAG, "sendConnectivityUpdate " + connectivityStatus);

        for (Messenger client : mSubscriptions.getClientsForTopic("public/gwStatus"))
        {
            Log.v(TAG, "sendConnectivityUpdate to " + getUserId());
            sendConnectivityUpdate(client);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.v(TAG, "onStartCommand()");
        return START_STICKY;
    }

//    @Override
//    public void connectionLost(Throwable arg0)
//    {
//        connectivityStatus = "DISCONNECTED";
//        sendConnectivityUpdate();
//        reconnectionHandler.removeCallbacks(mReconnector);
//        reconnectionHandler.postDelayed(mReconnector, 5000);
//    }

//    @Override
//    public void messageArrived(String topic, final MqttMessage msg) throws Exception
//    {
//        Log.i(TAG, "Message arrived from topic " + topic);
//        final String finalTopic = topic;
//        Handler h = new Handler(getMainLooper());
//        h.post(new Runnable() {
//            @Override
//            public void run() {
//                ArrayList<Messenger> clients = new ArrayList<>(mSubscriptions.getClientsForTopic(finalTopic));
//
//                for (Messenger client : clients)
//                {
//
//                    Message m = new Message();
//                    m.what = MSG_TOPICUPDATE;
//                    Bundle b = new Bundle();
//                    b.putString("topic",finalTopic);
//                    b.putString("payload", msg.toString());
//                    m.setData(b);
//
//
//                    try
//                    {
//                        client.send(m);
//                    }
//                    catch (DeadObjectException e)
//                    {
//                        ArrayList<String> topics = new ArrayList(mSubscriptions.getTopicsForClient(client));
//                        for (String topic : topics)
//                            mSubscriptions.removeSubscription(topic,client);
//
//                    }
//                    catch (RemoteException e)
//                    {
//                        e.printStackTrace();
//                    }
//
//                }
//
//            }
//        });
//    }
//
//    @Override
//    public void deliveryComplete(IMqttDeliveryToken token) {
//
//    }
//

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.i(TAG, "onBind called");
        sendConnectivityUpdate();
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        mSubscriptions.clear();
//        if (client != null && client.isConnected())
//        {
//            try
//            {
//                client.disconnect();
//            }
//            catch (MqttException e)
//            {
//                e.printStackTrace();
//            }
//        }

        return super.onUnbind(intent);
    }


    @Override
    public void publish(String topic, String value)
    {
        Log.i(TAG, "publish(" + topic + "," + value + ")");

//        try
//        {
//            final String finalTopic = "private/" + broker_apiKey.getPublicKey() + "/" + topic;
//            Log.d(TAG, "publishing " + finalTopic);
//            client.publish(finalTopic, new MqttMessage(value.getBytes()));
//        }
//        catch (MqttException e)
//        {
//            e.printStackTrace();
//        }
//        catch (MalformedApiKeyException e)
//        {
//            e.printStackTrace();
//        }
    }

    @Override
    public void enable() {
        broker_enabled = true;
    }

    @Override
    public void disable() {
        broker_enabled = false;
    }

    @Override
    public boolean isEnabled() {
        return broker_enabled;
    }

    @Override
    public SubscriptionGraph getSubscriptionGraph() {
        return mSubscriptions;
    }

    @Override
    public void setBrokerApiKey(String apiKey) throws MalformedApiKeyException
    {
        broker_apiKey = ApiAuth.fromString(apiKey);
    }

    @Override
    public void setBrokerAddress(String address) {
        broker_address = address;
    }

    @Override
    public void setBrokerPort(int port) {
        broker_port = port;
    }

    @Override
    public String getBrokerApiKey()
    {
        return broker_apiKey.toString();
    }

    @Override
    public String getBrokerAddress() {
        return broker_address;
    }

    @Override
    public int getBrokerPort() {
        return broker_port;
    }

    @Override
    public String getUserId()
    {
        try
        {
            return broker_apiKey.getPublicKey();
        }
        catch (NullPointerException e)
        {
            return "";
        }
        catch (MalformedApiKeyException e)
        {
            return "";
        }
    }

    @Override
    public void setSubscription(String topic, boolean enabled)
    {
        try
        {
//            if (client != null && client.isConnected() && enabled)
//            {
//                Log.i(TAG, "subscribe(" + topic + ")");
//                try
//                {
//                    client.subscribe(topic, 0);
//                }
//                catch (MqttException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//            else if (client != null && client.isConnected() && !enabled)
//            {
//                Log.i(TAG, "unsubscribe(" + topic + ")");
//                try
//                {
//                    client.unsubscribe(topic);
//                }
//                catch (MqttException e)
//                {
//                    e.printStackTrace();
//                }
//            }
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void restApiCall(final String method, final String params, final Messenger replyTo)
    {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground( Void... voids ) {
                restApiCallImpl( method,  params,  replyTo);
                return null;
            }
        }.execute();
    }


    public void restApiCallImpl(String method, String params, Messenger replyTo)
    {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {

                try {
                    String username = broker_apiKey.getPublicKey();
                    char[] passwd = broker_apiKey.getPrivateKey().toCharArray();
                    return new PasswordAuthentication(username,passwd);
                }
                catch (MalformedApiKeyException e)
                {
                    char[] c = {};
                    return new PasswordAuthentication("",c);
                }

            }
        });

        try
        {
            URL url = new URL("http://api.vopen.org/" + params);

            int responseCode = 0;
            String content = "";

            HttpURLConnection urlConnection = null;
            try
            {

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(method);

                responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    content = readStream(in);
                }

                sendHttpResponseToClient(responseCode,content,replyTo);
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (urlConnection != null) urlConnection.disconnect();
            }

        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }


    }

    String readStream(InputStream inputStream)
    {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();

        try {
            Reader in = new InputStreamReader(inputStream, "UTF-8");
            for (; ; ) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    void sendHttpResponseToClient(int status, String response, Messenger client)
    {
        Message m = new Message();
        m.what = MSG_REST_API_RESP;
        Bundle b = new Bundle();
        b.putInt("status", status);
        b.putString("content", response);
        m.setData(b);

        Log.v("content",response);

        try {
            client.send(m);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}