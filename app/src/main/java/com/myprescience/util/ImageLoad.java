package com.myprescience.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.myprescience.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dongjun on 15. 4. 24..
 */
public class ImageLoad extends AsyncTask<Void, Void, Bitmap> {

    private String mUrl;
    private ImageView mImageView;
    private Indicator mIndicator;
    private Activity mActivity;
//        private Activity mActivity;

    public ImageLoad(Activity _activity, Indicator _indicator, String _url, ImageView _imageView) {
        this.mUrl = _url;
        this.mImageView = _imageView;
        this.mActivity = _activity;
        this.mIndicator = _indicator;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL(mUrl);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if(mIndicator.isShowing())
            mIndicator.hide();
        BitmapDrawable bitmapDrawable = new BitmapDrawable(mActivity.getResources(), result);
        mImageView.setBackgroundDrawable(bitmapDrawable);
        viewFadeIn(mImageView);
        mActivity.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void viewFadeIn(View view) {
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.abc_fade_in);
        view.startAnimation(animation);
    }

//    public Bitmap chopCenterBitmap(Bitmap srcBmp) {
//        Bitmap dstBmp;
//        if (srcBmp.getWidth() >= srcBmp.getHeight()){
//
//            dstBmp = Bitmap.createBitmap(
//                    srcBmp,
//                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
//                    0,
//                    srcBmp.getHeight(),
//                    srcBmp.getHeight()
//            );
//
//        }else{
//
//            dstBmp = Bitmap.createBitmap(
//                    srcBmp,
//                    0,
//                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
//                    srcBmp.getWidth(),
//                    srcBmp.getWidth()
//            );
//        }
//        return dstBmp;
//    }
}