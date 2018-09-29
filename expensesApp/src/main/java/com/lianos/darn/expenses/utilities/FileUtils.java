package com.lianos.darn.expenses.utilities;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Vasilis Lianos
 */
public class FileUtils {

    // Hidden constructor. This is a utility class.
    private FileUtils() {}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getStringFromFile (String filePath) throws Exception {

        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        return new String(encoded, Charset.defaultCharset());

    }

}
