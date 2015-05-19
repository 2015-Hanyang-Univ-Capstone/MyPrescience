package com.myprescience.ui.artist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.util.Indicator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static com.myprescience.util.Server.ARTIST_API;
import static com.myprescience.util.Server.SELECT_MY_ARTISTS;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.WITH_USER;
import static com.myprescience.util.Server.getStringFromUrl;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class MyArtistListActivity extends ActionBarActivity {

    private UserData userDTO;

    private GridView gridView;
    private MyArtistListAdapter myArtistListAdapter;
    private boolean mLockListView = false;

    private int mListCount;
    private int mListAddCount;
    private int totalListSize;
    private JSONArray mAlbumArray;

    Indicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( R.layout.activity_mysong);
        userDTO = new UserData(getApplicationContext());
        setActionBar(R.string.title_activity_MyArtist);

        mListCount = 0;
        mListAddCount = 5;
        mIndicator = new Indicator(this);

        gridView = (GridView) findViewById(R.id.mysongGridView);

        myArtistListAdapter = new MyArtistListAdapter(this, userDTO.getId());
        gridView.setAdapter(myArtistListAdapter);

        new getSimpleSongTask().execute(SERVER_ADDRESS+ARTIST_API+SELECT_MY_ARTISTS+WITH_USER+userDTO.getId());

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 현재 가장 처음에 보이는 셀번호와 보여지는 셀번호를 더한값이
                // 전체의 숫자와 동일해지면 가장 아래로 스크롤 되었다고 가정
                if ( (totalItemCount+mListAddCount < totalListSize) && ((firstVisibleItem + visibleItemCount) == totalItemCount) && (mLockListView == false) ) {
                    mListCount += mListAddCount;
//                        else if(totalItemCount+10 > totalListSize && !(totalItemCount >= totalListSize))
//                            mListCount = totalListSize - (10+1);
                    new getSimpleSongTask().execute();
                    mLockListView = true;
                } else if(totalItemCount + mListAddCount >= totalListSize && totalListSize > 5) {
                    mListCount += mListAddCount;
                    mListAddCount =  totalListSize - mListCount;
                    new getSimpleSongTask().execute();
                    Toast.makeText(getApplicationContext(), "노래를 전부 가져왔습니다.", Toast.LENGTH_LONG);
                    gridView.setOnScrollListener(null);
                }
            }
        });
    }

    private void setActionBar(int title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        // 뒤로가기 버튼
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView TitleTextView = (TextView) findViewById(R.id.toolbar_title);
        TitleTextView.setText(title);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "Steinerlight.ttf");
        TitleTextView.setTypeface(face);

    }

    class getSimpleSongTask extends AsyncTask<String, String, String> {

        public getSimpleSongTask(){
        }

        @Override
        protected String doInBackground(String... url) {
            if (mAlbumArray == null)
                return getStringFromUrl(url[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String songJSON) {
            super.onPostExecute(songJSON);
//            mLockListView = true;

            try {
                JSONParser jsonParser = new JSONParser();

                if(songJSON != null) {
                    mAlbumArray = (JSONArray) jsonParser.parse(songJSON);
                    totalListSize = mAlbumArray.size();
                }

                if(totalListSize == 0) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MyArtistListActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                            finish();
                        }
                    });
                    alert.setMessage("평가하신 노래가 없습니다.\n평가하기에서 노래를 평가해주세요.");
                    alert.show();
                    return;
                }

                if(totalListSize < 5)
                    mListAddCount = totalListSize;

                for(int i = mListCount; i < mListCount+mListAddCount; i ++) {

                    JSONObject artistJSON = (JSONObject) mAlbumArray.get(i);
                    final String id = (String) artistJSON.get("id");
                    String name = (String) artistJSON.get("name");
                    String artist = (String) artistJSON.get("artist");
                    String genres = (String) artistJSON.get("genres");
                    String image_300 = (String) artistJSON.get("image_300");

                    myArtistListAdapter.addItem(id, name, artist, genres, image_300);

                    if(myArtistListAdapter.getCount() > 0) {
                        myArtistListAdapter.notifyDataSetChanged();

                        if ( mIndicator.isShowing())
                            mIndicator.hide();
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            mLockListView = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if ( !mIndicator.isShowing())
                mIndicator.show();
        }
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