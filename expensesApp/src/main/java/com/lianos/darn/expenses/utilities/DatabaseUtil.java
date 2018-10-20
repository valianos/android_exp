package com.lianos.darn.expenses.utilities;

import android.os.Build;
import android.support.annotation.RequiresApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author Vasilis Lianos
 */
public class DatabaseUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // Hidden constructor. This is a utility class.
    private DatabaseUtil() {}

    public static File createDatabase(File root, String name) throws IOException {

        File db = new File(root, name);
        if (!db.exists() && !db.createNewFile()) throw new RuntimeException("Failed to create db:" + name);
        return db;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <T> void readDatabase(File db, Class<T> type, Consumer<T> mapper) throws IOException {

        ObjectReader reader = MAPPER.readerFor(type);
        reader.<T>readValues(db).forEachRemaining(mapper);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <T> void dump(File db, Collection<T> entries) throws IOException {

        try (FileWriter writer = new FileWriter(db)) {

            entries.forEach(c -> {

                try { writer.write(MAPPER.writeValueAsString(c)); }
                catch (IOException e) { throw new RuntimeException("Failed to write to db", e); }

            });

        } catch (IOException e) { throw new IOException("Failed to write to db: " + db, e); }

    }

}
