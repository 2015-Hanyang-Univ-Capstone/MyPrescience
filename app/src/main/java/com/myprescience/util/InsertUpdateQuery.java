package com.myprescience.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.myprescience.R;
import com.myprescience.ui.MyPrescienceActivity;

import static com.myprescience.util.Server.getStringFromUrl;

/**
 * Created by dongjun on 15. 4. 30..
 */
public class InsertUpdateQuery extends AsyncTask<String, String, String> {

    private Context mContext;

    public InsertUpdateQuery(Context _context) {
        this.mContext = _context;
    }

    @Override
    protected String doInBackground(String... url) {
        String JSONresult = getStringFromUrl(url[0]);
        Log.e("response", JSONresult);
        return JSONresult;
    }
}