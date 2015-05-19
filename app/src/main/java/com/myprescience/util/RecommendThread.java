package com.myprescience.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RemoteViews;

import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.ui.MyPrescienceActivity;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.concurrent.ExecutionException;

import static com.myprescience.util.Server.getStringFromUrl;

/**
 * Created by dongjun on 15. 5. 9..
 */
public class RecommendThread extends Thread {

    private UserData userDTO;
    private Context mContext;
    private String mUrl;

    public RecommendThread(Context _context, String _url) {
        this.mContext = _context;
        this.mUrl = _url;
        userDTO = new UserData(mContext);
    }

    @Override
    public void run() {

        String recommendStr = getStringFromUrl(mUrl);
        Log.e("recommendStr", recommendStr);
        JSONParser jsonParser = new JSONParser();
        JSONObject recommend = null;
        if(recommendStr != null) {
            try {
                recommend = (JSONObject) jsonParser.parse(recommendStr.toString());
                String result = (String) recommend.get("recommend");

                if(result.equals("true")) {

                    String image_300 = (String) recommend.get("image_300");
                    Bitmap bigPicture = null;
                    try {
                        bigPicture = new ImageLoad(image_300).execute().get();
                        bigPicture = Bitmap.createScaledBitmap(bigPicture, 120, 120, false);
//                        int px_dp_256 = dpToPx(256);
//                        bigPicture = Bitmap.createScaledBitmap(bigPicture, px_dp_256, px_dp_256, true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                        Notification myNotification = new Notification();

                        RemoteViews views;
                        views = new RemoteViews(mContext.getPackageName(), R.layout.remote_notifyimage);
                        views.setImageViewBitmap(R.id.big_picture, bigPicture);
                        myNotification.bigContentView = views;

                        NotificationManager notificationManager = (NotificationManager)
                                mContext.getSystemService(Context.NOTIFICATION_SERVICE);

                        notificationManager.notify(111, myNotification);

                    } else {

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
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}

