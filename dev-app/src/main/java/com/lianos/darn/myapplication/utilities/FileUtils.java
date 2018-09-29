package com.lianos.darn.myapplication.utilities;

import java.io.*;

/**
 * @author Vasilis Lianos
 */
public class FileUtils {

    // Hidden constructor. This is a utility class.
    private FileUtils() {}

    public static String getStringFromFile (String filePath) throws Exception {

        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);

        //Make sure to close all streams.
        fin.close();
        return ret;

    }

    private static String convertStreamToString(InputStream is) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) sb.append(line).append("\n");

        reader.close();
        return sb.toString();

    }

}
