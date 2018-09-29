package com.lianos.darn.expenses.utilities;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import com.lianos.darn.expenses.R;

/**
 * @author Vasilis Lianos
 */
public class AlertUtils {

    // Hidden constructor. This is a utility class.
    private AlertUtils() {}

    public static boolean checkCredentials(String user, String pass, Activity caller) {

        if (user == null || pass == null || user.isEmpty() || pass.isEmpty()) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(caller);
            alertDialogBuilder.setMessage(R.string.missing_fields);
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
            return false;

        }

        return true;

    }

}
