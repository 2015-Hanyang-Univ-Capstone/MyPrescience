package com.myprescience.dto;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.myprescience.R;
import com.myprescience.util.RecommendThread;
import com.myprescience.util.RoundImage;

import static com.myprescience.util.Server.EXEC_RECOMMEND_ALGORITHM;
import static com.myprescience.util.Server.INSERT_SIMILAR_SONG;
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

    public static int RatingSongCount = 0, triger = 15;
    private int RECOMMEND_ALGORITHM = 0, SIMILAR_SONG = 1;
    static public boolean recommRunning = false, similarRunning = false;
    public int getRatingSongCount() { return RatingSongCount; }
    public void setRatingSongCount(int ratingSongCount) { this.RatingSongCount = ratingSongCount; }
    public void addRatingSoungCount(String song_id, int rating) {

        Log.e("rating" , song_id + " " + rating);

        RatingSongCount++;

        if(rating > 8 && !similarRunning) {
            similarRunning = true;
            new RecommendThread(mContext, SERVER_ADDRESS + RECOMMEND_API + INSERT_SIMILAR_SONG
                    + WITH_USER + getId() + "&song_id=" + song_id, SIMILAR_SONG).start();
        } else if(RatingSongCount % triger == 0 && !recommRunning) {
            recommRunning = true;
            Toast.makeText(mContext, mContext.getString(R.string.recommend_start_recom), Toast.LENGTH_LONG);
            Log.e("ExecRecommend", SERVER_ADDRESS + RECOMMEND_API + EXEC_RECOMMEND_ALGORITHM + WITH_USER + USER_ID);
            new RecommendThread(mContext, SERVER_ADDRESS + RECOMMEND_API + EXEC_RECOMMEND_ALGORITHM + WITH_USER + USER_ID,
                    RECOMMEND_ALGORITHM).start();
            triger = (triger * 2) + 10;
        }
    }

}
