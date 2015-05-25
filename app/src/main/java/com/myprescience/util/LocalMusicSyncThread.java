package com.myprescience.util;

import android.content.Context;

import com.myprescience.dto.UserData;

import org.apache.http.NameValuePair;

import java.util.List;

import static com.myprescience.util.Server.callByArrayParameters;

/**
 * Created by dongjun on 15. 5. 9..
 */
public class LocalMusicSyncThread extends Thread {

    private UserData userDTO;
    private Context mContext;
    private String mUrl, mTitle, mArtist;
    private List<NameValuePair> mParameters;
    private List<NameValuePair> mArtistParameters;

    public LocalMusicSyncThread(Context _context, String _url, List<NameValuePair> _parameters) {
        this.mContext = _context;
        this.mUrl = _url;
        this.mParameters = _parameters;
        userDTO = new UserData(mContext);
    }

    @Override
    public void run() {

        callByArrayParameters(mUrl, mParameters);

//        DefaultHttpClient httpclient = new DefaultHttpClient();
//        HttpGet post = new HttpGet(mUrl);
//        try {
//            httpclient.execute(post);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}

