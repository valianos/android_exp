package com.lianos.darn.expenses.utilities;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Vasilis Lianos
 */
public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    // Hidden constructor. This is a utility class.
    private FileUtils() {}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getStringFromFile(String filePath) throws Exception {

        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        return new String(encoded, Charset.defaultCharset());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean dumpToFile(Context ctx, File file, String contents) {

        log.debug("Creating new file: [{}]", file.getPath());

        try {

            FileOutputStream outputStream;
            outputStream = ctx.openFileOutput(file.getName(), Context.MODE_PRIVATE);
            outputStream.write(contents.getBytes());
            outputStream.close();

        } catch (Exception e) {

            log.error("Error handling file.", e);
            AlertUtils.fileCreationFailure(ctx);
            return false;

        }

        return true;

    }

}
