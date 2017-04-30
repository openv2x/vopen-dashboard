package org.vopen.vopengateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootReceiver extends BroadcastReceiver
{
    public BootReceiver()
    {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // start the VOpenGateway service on boot
        context.startService(new Intent(context, MQTTService.class));
    }
}

