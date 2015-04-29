package com.myprescience.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.myprescience.util.RoundImage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.myprescience.util.Server.FACEBOOK_PROFILE;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.USER_API;
import static com.myprescience.util.Server.USER_ID_WITH_FACEBOOK_ID;
import static com.myprescience.util.Server.WIDTH_150;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class MyPageActivity extends Activity {

    private GridView gridView;
    private int userId;
    private boolean mLockListView = false;

    private LinearLayout linearLayout, songTextView;
    private ImageView facebook_profile;
    private TextView nameTextView, ArtistTextView, AlbumTextView, OptionTextView;

    Indicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_title);

        TextView TitleTextView = (TextView) findViewById(R.id.customActionbarTitle);
        TitleTextView.setText(R.string.title_section4);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "Steinerlight.ttf");
        TitleTextView.setTypeface(face);

        // 뒤로가기 버튼
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_mypage);
        mIndicator = new Indicator(this);

//        facebook_profile = (ImageView) findViewById(R.id.profileImageView);
//        facebook_profile.setImageDrawable(getFACEBOOK_PROFILE_BITMAP());

//        nameTextView = (TextView) findViewById(R.id.nameTextView);
//        nameTextView.setText(getUSER_NAME());

        songTextView = (LinearLayout) findViewById(R.id.mysongButton);
//        ArtistTextView = (TextView) findViewById(R.id.myartistTextView);
//        AlbumTextView = (TextView) findViewById(R.id.myalbumTextView);
//        OptionTextView = (TextView) findViewById(R.id.myoptionTextView);

        songTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MySongListActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.search_menu, menu);
        return true;
    }

    // 뒤로가기 버튼
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };

}