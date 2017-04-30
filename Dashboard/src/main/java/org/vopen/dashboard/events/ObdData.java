package org.vopen.dashboard.events;

/**
 * Created by dpandeli on 01-Feb-16.
 */
public class ObdData
{
    public String description;
    public float data;
    public String units;
    public ObdData (String desc, float d, String u )
    {
        description = desc;
        data = d;
        units = u;
    }
}
