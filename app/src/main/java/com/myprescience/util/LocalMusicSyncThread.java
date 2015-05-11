package com.myprescience.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.ui.MyPrescienceActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import static com.myprescience.util.Server.getStringFromUrl;

/**
 * Created by dongjun on 15. 5. 9..
 */
public class LocalMusicSyncThread extends Thread {

    private UserData userDTO;
    private Context mContext;
    private String mUrl, mTitle, mArtist;

    public LocalMusicSyncThread(Context _context, String _url) {
        this.mContext = _context;
        this.mUrl = _url;
        userDTO = new UserData(mContext);
    }

    @Override
    public void run() {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet post = new HttpGet(mUrl);
        try {
            httpclient.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

