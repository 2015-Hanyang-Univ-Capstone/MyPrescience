package com.myprescience.util;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by hyeon-seob on 15. 3. 4..
 * 곡 정보를 리스트로 출력하기 위한 클래스
 */
public class ViewHolder {
    public ImageView albumImageView;
    public TextView titleTextView;
    public TextView artistTextView;
    public Button albumDetailButton;
    public RatingBar ratingBar;
    public int position;
    public TextView localTextView;
    public LinearLayout resultLayout;
}
