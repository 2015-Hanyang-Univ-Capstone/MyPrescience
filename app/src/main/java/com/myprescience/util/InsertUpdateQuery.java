package com.myprescience.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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