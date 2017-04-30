package org.vopen.dashboard;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dpandeli on 18-Jan-16.
 */
@Root(name="layout")
public class LayoutFile extends XmlFile
{
    @ElementList(name="pages")
    private List<LayoutPage> list = new ArrayList<>();

    public List<LayoutPage> getList()
    {
        return list;
    }

    public void setList(List<LayoutPage> list)
    {
        this.list = list;
    }
}
