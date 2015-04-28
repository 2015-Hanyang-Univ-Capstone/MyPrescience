package com.myprescience.dto;

import android.graphics.Bitmap;

import com.myprescience.util.RoundImage;

/**
 * Created by dongjun on 15. 4. 28..
 */
public class UserData {

    static private int USER_ID = 0;
    public int getId() { return this.USER_ID; };
    public void setId(int user_id) { USER_ID = user_id; };

    static private RoundImage FACEBOOK_PROFILE_BITMAP = null;
    public RoundImage getFacebook_profile() { return FACEBOOK_PROFILE_BITMAP; };
    public void setFacebook_profile(Bitmap image) { this.FACEBOOK_PROFILE_BITMAP = new RoundImage(image); };

    static private String USER_NAME = "";
    public String getName() { return USER_NAME; };
    public void setName(String user_name) { this.USER_NAME = user_name; };
}
