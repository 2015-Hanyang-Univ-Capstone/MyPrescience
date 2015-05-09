package com.myprescience.dto;

import android.graphics.Bitmap;

/**
 * Created by hyeon-seob on 15. 3. 4..
 * 곡 정보를 저장하는 클래스
 */
public class AlbumData {
    public String id;
    public String name;
    public String artist;
    public String release_date;
    public Bitmap albumArt;
    public String image_600;
    public String image_300;
    public String image_64;

    public void setAlbumArt(Bitmap mAlbumArt) { albumArt = mAlbumArt; }
}
