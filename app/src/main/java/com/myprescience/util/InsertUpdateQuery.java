package com.myprescience.util;

import android.os.AsyncTask;
import android.util.Log;

import static com.myprescience.util.Server.getStringFromUrl;

/**
 * Created by dongjun on 15. 4. 30..
 */
public class InsertUpdateQuery extends AsyncTask<String, String, Void> {

    @Override
    protected Void doInBackground(String... url) {
        String userIdJSON = getStringFromUrl(url[0]);
        Log.e("response", userIdJSON);
        return null;
    }
}