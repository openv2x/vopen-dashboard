package org.vopen.dashboard.events;

/**
 * Created by dpandeli on 05-Nov-16.
 */

public class GatewayEvent
{
    String message;

    public GatewayEvent(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}
