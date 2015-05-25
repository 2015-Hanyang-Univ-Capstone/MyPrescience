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

                    if (result.equals("true") && (String) recommend.get("image_300") != null) {

                        Bitmap bigPicture = null;
                        try {
                            bigPicture = new ImageLoad(image_300).execute().get();
//                        bigPicture = Bitmap.createScaledBitmap(bigPicture, 120, 120, false);
//                        int px_dp_256 = dpToPx(256);
                            bigPicture = Bitmap.createScaledBitmap(bigPicture, 600, 300, true);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }


//                    NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext, MyPrescienceActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
//
//                    RemoteViews views;
//                    views = new RemoteViews(mContext.getPackageName(), R.layout.remote_notifyimage);
//                    views.setImageViewBitmap(R.id.big_picture, bigPicture);
//
//                    Notification notification = new Notification(R.drawable.icon_recom_complete,
//                            userDTO.getName() + "님 음악분석이 완료되었습니다.", System.currentTimeMillis());
//
//                    // These flags should be self explanatory
//                    notification.flags |= Notification.FLAG_NO_CLEAR;
//                    notification.flags |= Notification.FLAG_ONGOING_EVENT;
//
//                    // This is where you select the xml for you custm view
//
//                    notification.contentView   = views;
//                    notification.contentIntent = pendingIntent;
//
////                    mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
//                    nm.notify(111, notification);

//                    NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext, MyPrescienceActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
//
//                    Notification myNotification = new Notification();
//
//                    RemoteViews views;
//                    views = new RemoteViews(mContext.getPackageName(), R.layout.remote_notifyimage);
//                    views.setImageViewBitmap(R.id.big_picture, bigPicture);
//                    views.setOnClickPendingIntent(R.id.big_picture, pendingIntent);
//                    myNotification.bigContentView = views;
//
//                    nm.notify(111, myNotification);
//
//                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//
//
//
//                    } else {
//
                        NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext, MyPrescienceActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

                        Notification.Builder mBuilder = new Notification.Builder(mContext);
                        mBuilder.setSmallIcon(R.drawable.logo_small);
                        mBuilder.setTicker(userDTO.getName() + "님 음악분석이 완료되었습니다.");
                        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                        mBuilder.setContentIntent(pendingIntent);
                        mBuilder.setAutoCancel(true);

                        Notification.BigPictureStyle bigStyle = new Notification.BigPictureStyle(mBuilder);
                        bigStyle.setBigContentTitle("음악취향 분석이 완료되었습니다.");
                        bigStyle.setSummaryText("지금 바로 My Prescience에서 당신에게 추천해주는 노래를 만나보세요!");
                        if (bigPicture != null)
                            bigStyle.bigPicture(bigPicture);

                        mBuilder.setStyle(bigStyle);
                        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
                        nm.notify(111, mBuilder.build());
//                    }
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

