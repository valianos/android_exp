package com.lianos.darn.expenses.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import com.lianos.darn.expenses.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vasilis Lianos
 */
public class AlertUtils {

    private static final Logger log = LoggerFactory.getLogger(AlertUtils.class);

    // Hidden constructor. This is a utility class.
    private AlertUtils() {}

    public static boolean checkFields(Activity caller, String... fields) {

        for (String field : fields) {

            if (field == null || field.isEmpty()) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(caller);
                alertDialogBuilder.setMessage(R.string.missing_fields);
                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
                return false;

            }

        }

        return true;

    }

    public static void fileCreationFailure(Context ctx) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(R.string.file_failure);
        builder.setPositiveButton(R.string.try_again,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        log.debug("Clicked to try again");

                    }

                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * Just a tinny tiny alert box for real-time debugging.
     */
    public static void debugAlert(Context ctx, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        log.debug("Clicked OK");

                    }

                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
