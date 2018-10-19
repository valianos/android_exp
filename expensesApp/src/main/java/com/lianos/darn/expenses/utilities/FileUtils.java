package com.lianos.darn.expenses.utilities;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vasilis Lianos
 */
public final class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    // Hidden constructor. This is a utility class.
    private FileUtils() {}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getStringFromFile(String filePath) throws Exception {

        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        return new String(encoded, Charset.defaultCharset());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<String> getlinesFromFile(String filePath) throws Exception {
        return Files.readAllLines(Paths.get(filePath));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean dumpToFile(Context ctx, File file, String contents) {

        log.debug("Creating new file: [{}]", file.getPath());

        try {

            FileOutputStream outputStream = new FileOutputStream(file, true);
            if (file.length() != 0) outputStream.write("\n".getBytes()); //Separate from previous entry.
            outputStream.write(contents.getBytes());
            outputStream.close();

        } catch (Exception e) {

            log.error("Error handling file.", e);
            AlertUtils.fileCreationFailure(ctx);
            return false;

        }

        return true;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<String> getOrCreate(Context ctx, String filePath) {

        // Check if file exists, create it otherwise (persistent).
        File file = new File(filePath);
        List<String> contents = new ArrayList<>();

        if (file.exists()) {

            try { contents = getlinesFromFile(file.getPath()); }
            catch (Exception e) {

                log.error("Failed to read the file.", e);

                AlertUtils.fileCreationFailure(ctx);

                // Sanity check.
                log.debug("Deleting [{}] file: [{}]", filePath, file.delete());
                return null;

            }

        } else {

            log.debug("Creating new file: [{}]", file.getPath());

            try {

                // Hopefully this will create an empty file.
                FileOutputStream outPutStream = ctx.openFileOutput(file.getName(), Context.MODE_PRIVATE);
                outPutStream.close();

            } catch (IOException e) {

                log.error("Failed to create the file.", e);

                AlertUtils.fileCreationFailure(ctx);

                // Sanity check.
                log.debug("Deleting [{}] file: [{}]", filePath, file.delete());
                return null;

            }

        }

        return contents;

    }

    public static void deleteFiles(File... files) {
        for (File file : files)
            log.debug("[{}] file deleted: [{}]", file.getPath(), file.delete());
    }

}
