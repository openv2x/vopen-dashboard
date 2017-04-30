package org.vopen.dashboard;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;

/**
 * Created by dpandeli on 18-Jan-16.
 */
public class XmlReader
{

    public static LayoutFile readFile(File inputFile)
    {
        Serializer serializer = new Persister();
        LayoutFile retrievedFile = null;
        try
        {
            retrievedFile = serializer.read(LayoutFile.class, inputFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return retrievedFile;
    }
}
