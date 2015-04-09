package com.myprescience.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.myprescience.R;
import com.myprescience.util.Indicator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.myprescience.util.JSON.FACEBOOK_PROFILE;
import static com.myprescience.util.JSON.SERVER_ADDRESS;
import static com.myprescience.util.JSON.USER_API;
import static com.myprescience.util.JSON.USER_ID_WITH_FACEBOOK_ID;
import static com.myprescience.util.JSON.WIDTH_150;
import static com.myprescience.util.JSON.getStringFromUrl;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class MyPageActivity extends ActionBarActivity {

    private GridView gridView;
    private int userId;
    private boolean mLockListView = false;

    private LinearLayout linearLayout;
    private ImageView facebook_profile;
    private TextView nameTextView, songTextView, ArtistTextView, AlbumTextView, OptionTextView;

    Indicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        mIndicator = new Indicator(this);

        facebook_profile = (ImageView) findViewById(R.id.profileImageView);

        Session session = Session.getActiveSession();
        if (session != null) {
            if (session.isOpened()) {
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                    // callback after Graph API response with user object
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        new searchUserTask().execute(SERVER_ADDRESS + USER_API + USER_ID_WITH_FACEBOOK_ID + user.getId());
                        new LoadProfileImage().execute(FACEBOOK_PROFILE+user.getId()+WIDTH_150);
                        nameTextView.setText(user.getName());
                    }
                });
            }
        }

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        songTextView = (TextView) findViewById(R.id.mysongTextView);
        ArtistTextView = (TextView) findViewById(R.id.myartistTextView);
        AlbumTextView = (TextView) findViewById(R.id.myalbumTextView);
        OptionTextView = (TextView) findViewById(R.id.myoptionTextView);

        songTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MySongListActivity.class);
                startActivity(intent);
            }
        });

    }

    class searchUserTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... parameter) {
            String userIdJSON = getStringFromUrl(parameter[0]);
            JSONParser jsonParser = new JSONParser();
            JSONArray users = null;
            try {
                users = (JSONArray) jsonParser.parse(userIdJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(users != null) {
                JSONObject user = (JSONObject) users.get(0);
                userId = Integer.parseInt((String)user.get("user_id"));
            }
            return null;
        }
    }

    class LoadProfileImage extends AsyncTask<String, String, Bitmap> {

        public LoadProfileImage(){
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            Bitmap myBitmap = null;
            try {
                URL urlConnection = new URL(url[0]);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getCircularBitmapFrom(myBitmap);
        }

        @Override
        protected void onPostExecute(Bitmap albumArt) {
            super.onPostExecute(albumArt);
            facebook_profile.setImageBitmap(albumArt);
            if(mIndicator.isShowing())
                mIndicator.hide();
        }

        @Override
        protected void onPreExecute() {
            if(!mIndicator.isShowing())
                mIndicator.show();
        }

    }

    public Bitmap getCircularBitmapFrom(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        float radius = bitmap.getWidth() > bitmap.getHeight() ? ((float) bitmap
                .getHeight()) / 2f : ((float) bitmap.getWidth()) / 2f;
        Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);

        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                radius, paint);

        return canvasBitmap;
    }
}