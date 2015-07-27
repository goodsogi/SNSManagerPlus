package world.plus.manager.sns4.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by johnny on 15. 7. 2.
 */
public class ProgressDialogManager {

    private static ProgressDialog mProgressDialog;

    public static void showProgessDialog(Context context, String msg) {
        if(mProgressDialog != null) {
            mProgressDialog.setMessage(msg);
            return;
        }


        try {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(msg);
            mProgressDialog.show();
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or loler.unca
            mProgressDialog = null;

        }

    }

    public static void removeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();

            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
        } finally {
            mProgressDialog = null;
        }
    }

    public static void showDownloadProgessDialog(Context context, String msg) {
        try {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.show();
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or loler.unca
            mProgressDialog = null;

        }
    }

    public static void updateDownloadProgressDialog(int progres) {
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progres);
    }
}
