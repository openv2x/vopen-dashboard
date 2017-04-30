package org.vopen.android_sdk.obd_client;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import pt.lighthouselabs.obd.commands.ObdCommand;
import org.vopen.android_sdk.obd_service.ObdGatewayServiceSettings;
import org.vopen.android_sdk.obd_service.ObdGatewayServiceIF;
import org.vopen.android_sdk.obd_service.ObdCommandJob;
import org.vopen.android_sdk.obd_service.MockObdGatewayService;
import org.vopen.android_sdk.obd_service.ObdGatewayService;


/**
 * Created by DBF on 5/31/2015.
 */
public class ObdGatewayClient {

    private static final String TAG = ObdGatewayClient.class.getName();
    private boolean isServiceBound = false;
    private boolean isSrvRunning = false;
    private Messenger mService = null;
    private ObdGatewayClientCallback mCallbacks = null;

    private final Messenger rMessenger = new Messenger(new IncomingHandler());
    private ObdGatewayServiceSettings settings = new ObdGatewayServiceSettings();

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ObdGatewayServiceIF.OBD_GATEWAY_MSG_DATA:
                    if (mCallbacks != null)
                    {
                        mCallbacks.onObdReceiveData((ObdCommandJob) msg.obj);
                    }
                    break;
                case ObdGatewayServiceIF.OBD_GATEWAY_MSG_STATUS:
                    if(msg.arg1 == ObdGatewayServiceIF.OBD_GATEWAY_STAT_SERVICE_RUNNING)
                    {
                        isSrvRunning = true;
                    }
                    else if (msg.arg1 == ObdGatewayServiceIF.OBD_GATEWAY_STAT_SERVICE_STOPPED)
                    {
                        isSrvRunning = false;
                    }
                    else
                    {
                        if (mCallbacks != null)
                        {
                            mCallbacks.onObdReceiveStatus(msg.arg1);
                        }
                    }
                    //In case of BT Error unbind theservice
                    if (msg.arg1 == ObdGatewayServiceIF.OBD_GATEWAY_STAT_TRANSPORT_ERROR)
                    {
                        //TBD decide what to do in case of BT error -> can be handled in wrapping class
                        //doUnbindService();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void serviceSetSettings (String remoteDev, String protList, String wIp, int wPort, boolean useWiFi, ObdGatewayClientCallback callbacks )
    {
        settings.remoteDevice = remoteDev;
        settings.protocolList =  protList;
        settings.wifiIp = wIp;
        settings.wifiPort = wPort;
        settings.connWiFi = useWiFi;
        mCallbacks = callbacks;
    }

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d(TAG, className.toString() + " service is bound");
            isServiceBound = true;
            Log.d(TAG, "Starting live data");

            //Bind service
            mService = new Messenger(binder);

            if (isServiceBound)
            {
                //Configure Service
                Message msg = Message.obtain(null,ObdGatewayServiceIF.OBD_GATEWAY_MSG_SETSETTINGS , 0, 0);
                msg.obj = (Object)settings;
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                //Start Service
                Message msg2 = Message.obtain(null,ObdGatewayServiceIF.OBD_GATEWAY_MSG_STARTSERVICE , 0, 0);
                msg2.replyTo = rMessenger;
                try {
                    mService.send(msg2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        // This method is *only* called when the connection to the service is lost unexpectedly
        // and *not* when the client unbinds (http://developer.android.com/guide/components/bound-services.html)
        // So the isServiceBound attribute should also be set to false when we unbind from the service.
        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, className.toString()  + " service is unbound");
            //SR clean up messenger
            mService = null;
            isServiceBound = false;
        }
    };

    public void doBindObdService(boolean useMockService, Context context) {
        ContextWrapper cw = new ContextWrapper(context);
        if (!isServiceBound) {
            Log.d(TAG, "Binding OBD service..");
            if(useMockService == false) {
                Intent serviceIntent = new Intent(cw, ObdGatewayService.class);
                cw.bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
            } else {
                Intent serviceIntent = new Intent(cw, MockObdGatewayService.class);
                cw.bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
            }
        }
    }

    public void doUnbindObdService(Context context) {
        ContextWrapper cw = new ContextWrapper(context);
        if (isServiceBound)
        {
            //Start Service
            Message msg = Message.obtain(null, ObdGatewayServiceIF.OBD_GATEWAY_MSG_STOPSERVICE, 0, 0);
            try
            {
                mService.send(msg);
            } catch (RemoteException e)
            {
                e.printStackTrace();
            }

            Log.d(TAG, "Unbinding OBD service..");
            cw.unbindService(serviceConn);
            isServiceBound = false;
        }
    }

    public boolean isServiceRunning ()
    {
        return isSrvRunning;
    }

    public boolean isServiceBound ()
    {
        return isServiceBound;
    }

    public void queueCommand(ObdCommand command) {
        if (isServiceBound)
        {
           //Get OBD Data
           Message msg = new Message();
           msg.what = ObdGatewayServiceIF.OBD_GATEWAY_MSG_DATA;
           msg.obj = (Object)command;
           try
           {
               mService.send(msg);
           } catch (RemoteException e)
           {
               e.printStackTrace();
           }

        }
    }
}
