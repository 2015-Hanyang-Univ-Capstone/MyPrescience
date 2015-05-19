package com.myprescience.dto;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.myprescience.util.RecommendThread;
import com.myprescience.util.RoundImage;

import static com.myprescience.util.Server.EXEC_RECOMMEND_ALGORITHM;
import static com.myprescience.util.Server.RECOMMEND_API;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.WITH_USER;

/**
 * Created by dongjun on 15. 4. 28..
 */
public class UserData {

    private Context mContext;

    public UserData(Context _context) {
        this.mContext = _context;
    }

    static private int USER_ID = 0;
    public int getId() { return this.USER_ID; };
    public void setId(int user_id) { USER_ID = user_id; };

    static private RoundImage FACEBOOK_PROFILE_BITMAP = null;
    public RoundImage getFacebook_profile() { return FACEBOOK_PROFILE_BITMAP; };
    public void setFacebook_profile(Bitmap image) { this.FACEBOOK_PROFILE_BITMAP = new RoundImage(image); };

    static private String USER_NAME = "";
    public String getName() { return USER_NAME; };
    public void setName(String user_name) { this.USER_NAME = user_name; };

    static private int RatingSongCount = 0, triger = 15;
    public int getRatingSongCount() { return RatingSongCount; }
    public void setRatingSongCount(int ratingSongCount) { this.RatingSongCount = ratingSongCount; }
    public void addRatingSoungCount() {
        RatingSongCount++;
        if(RatingSongCount % triger == 0) {
            Log.e("ExecRecommend", SERVER_ADDRESS + RECOMMEND_API + EXEC_RECOMMEND_ALGORITHM + WITH_USER + USER_ID);
            new RecommendThread(mContext, SERVER_ADDRESS + RECOMMEND_API + EXEC_RECOMMEND_ALGORITHM + WITH_USER + USER_ID).start();
            triger = (triger * 2) + 10;
        }
    }
}
