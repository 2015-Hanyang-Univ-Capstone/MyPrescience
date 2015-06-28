package com.myprescience.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.ui.main.MyPrescienceActivity;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.concurrent.ExecutionException;

import static com.myprescience.dto.UserData.similarRunning;
import static com.myprescience.util.Server.getStringFromUrl;
import static com.myprescience.dto.UserData.recommRunning;

/**
 * Created by dongjun on 15. 5. 9..
 */
public class RecommendThread extends Thread {

    private UserData userDTO;
    private Context mContext;
    private String mUrl;
    private int mMode;

    private int RECOMMEND_ALGOLITHM = 0, SIMILAR_SONG = 1;

    public RecommendThread(Context _context, String _url, int _mode) {
        this.mContext = _context;
        this.mUrl = _url;
        this.mMode = _mode;
        userDTO = new UserData(mContext);
    }

    @Override
    public void run() {

        if(mMode == RECOMMEND_ALGOLITHM) {

            String recommendStr = getStringFromUrl(mUrl);
            Log.e("recommendStr", recommendStr);
            JSONParser jsonParser = new JSONParser();
            JSONObject recommend = null;
            if (recommendStr != null) {
                try {
                    recommend = (JSONObject) jsonParser.parse(recommendStr.toString());
                    String result = (String) recommend.get("recommend");
                    String image_300 = (String) recommend.get("image_300");

                    if (result.equals("true") && image_300 != null) {

                        Bitmap bigPicture = null;
                        try {
                            bigPicture = new ImageLoad(image_300).execute().get();
                            bigPicture = Bitmap.createScaledBitmap(bigPicture, 600, 300, true);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext, MyPrescienceActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

                        Notification.Builder mBuilder = new Notification.Builder(mContext);
                        mBuilder.setSmallIcon(R.drawable.logo_small);
                        mBuilder.setTicker(userDTO.getName() + mContext.getString(R.string.recommend_finish_recom));
                        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                        mBuilder.setContentIntent(pendingIntent);
                        mBuilder.setAutoCancel(true);

                        Notification.BigPictureStyle bigStyle = new Notification.BigPictureStyle(mBuilder);
                        bigStyle.setBigContentTitle(mContext.getString(R.string.recommend_noti_recom1));
                        bigStyle.setSummaryText(mContext.getString(R.string.recommend_noti_recom2));
                        if (bigPicture != null)
                            bigStyle.bigPicture(bigPicture);

                        mBuilder.setStyle(bigStyle);
                        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
                        nm.notify(111, mBuilder.build());
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            recommRunning = false;

        } else if (mMode == SIMILAR_SONG) {

            String similarStr = getStringFromUrl(mUrl);
            Log.e("similarStr", similarStr);

            similarRunning = false;

        }


    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}

