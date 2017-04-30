package org.vopen.dashboard;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import java.io.File;

/** A helper class for outputting an xml file
 *
 */
public class XmlWriter
{
    /** Creates xml file based on the provided object content.
     *
     * This uses org.simpleframework.xml so the content object should be created
     * as specified inside the org.simpleframework.xml documentation
     *
     * @param xmlFileContent  desired xml content
     * @param file    Filename together with the extension (if any)
     * @param folderPath   Folder path that will contain the file.
     */
    public static boolean writeToFile(XmlFile xmlFileContent, String file, String folderPath) throws Exception
    {
        //override the default format of the xml file, with a one containing some additional info
        Serializer serializer = new Persister(new Format("<?xml version=\"1.0\"?>"));
        File outputFolder = new File(folderPath);

        outputFolder.mkdirs();

        File outputFile = new File(folderPath + "/" + file);
        serializer.write(xmlFileContent, outputFile);

        return outputFile.isFile();
    }
}
