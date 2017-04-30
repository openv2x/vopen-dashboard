package org.vopen.dashboard;

import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dpandeli on 18-Jan-16.
 */
public class LayoutPage
{
    @ElementList
    private List<GraphicalWidget> list = new ArrayList<>();

    public List<GraphicalWidget> getList()
    {
        return list;
    }

    public void setList(List<GraphicalWidget> list)
    {
        this.list = list;
    }
}
