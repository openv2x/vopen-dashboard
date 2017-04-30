package org.vopen.dashboard;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by dpandeli on 18-Jan-16.
 */
@Root
public class GraphicalWidget
{
    @Element
    private int columnStart;
    @Element
    private int rowStart;
    @Element
    private int columnEnd;
    @Element
    private int rowEnd;
    @Element
    private int id;

    public int getColumnStart()
    {
        return columnStart;
    }

    public int getRowStart()
    {
        return rowStart;
    }

    public int getColumnEnd()
    {
        return columnEnd;
    }

    public int getRowEnd()
    {
        return rowEnd;
    }

    public void setColumnStart(int columnStart)
    {
        this.columnStart = columnStart;
    }

    public void setRowStart(int rowStart)
    {
        this.rowStart = rowStart;
    }

    public void setColumnEnd(int columnEnd)
    {
        this.columnEnd = columnEnd;
    }

    public void setRowEnd(int rowEnd)
    {
        this.rowEnd = rowEnd;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}
