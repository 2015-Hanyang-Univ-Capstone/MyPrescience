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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
                    NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext, MyPrescienceActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

                    Notification.Builder mBuilder = new Notification.Builder(mContext);
                    mBuilder.setSmallIcon(R.drawable.logo_small);
                    mBuilder.setTicker(userDTO.getName() + "님 음악분석이 완료되었습니다.");
                    mBuilder.setWhen(System.currentTimeMillis());
                    mBuilder.setContentTitle(userDTO.getName() + "님 음악분석이 완료되었습니다.");
                    mBuilder.setContentText("지금 바로 My Prescience에서 당신에게 추천해주는 노래를 만나보세요!");
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setContentIntent(pendingIntent);
                    mBuilder.setAutoCancel(true);

                    mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

                    nm.notify(111, mBuilder.build());
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}

