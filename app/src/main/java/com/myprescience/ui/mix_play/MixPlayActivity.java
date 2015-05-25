package com.myprescience.ui.mix_play;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.ui.song.SongListActivity;
import com.myprescience.util.Indicator;
import com.myprescience.util.InsertUpdateQuery;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import static com.myprescience.dto.UserData.triger;
import static com.myprescience.util.Server.LUCENE_API;
import static com.myprescience.util.Server.RESET_DATE;
import static com.myprescience.util.Server.SEARCH_SONGID;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.SYNC_MODE;
import static com.myprescience.util.Server.USER_API;
import static com.myprescience.util.Server.WITH_USER;
import static com.myprescience.util.Server.callByArrayParameters;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class MixPlayActivity extends ActionBarActivity {

    private UserData userDTO;

    private LinearLayout mMyInfoButton, mMp3SyncButton, mAnalizeButton,
                        mMySongButton, mMyAlbumButton, mMyArtistButton;

    Indicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_play);
        setActionBar(R.string.title_activity_MixPlay);
        userDTO = new UserData(getApplicationContext());

        mIndicator = new Indicator(this);

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

    private void syncLocalMusicFile() {

        new syncLocalMP3File(MixPlayActivity.this).execute(100);

//        ProgressDialog progressDialog = new ProgressDialog(MyPageActivity.this);
//
//        new syncLocalMP3File(progressDialog).execute();

//        AlertDialog.Builder alert = new AlertDialog.Builder(MyPageActivity.this);
//        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {


//                try {
//                    new LocalMusicSyncThread(getApplicationContext(), SERVER_ADDRESS+RATING_API+INSERT_LOCAL_FILE_RATING
//                            +WITH_USER+userDTO.getId()+"&title="+URLEncoder.encode(title,"utf-8")+"&artist="+URLEncoder.encode(artist,"utf-8") ).start();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }

                //                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        public void run() {
//                            new RecommendThread(getApplicationContext(), SERVER_ADDRESS + RECOMMEND_API + EXEC_RECOMMEND_ALGORITHM + WITH_USER + userDTO.getId()).start();
//                        }
//                    }, 5000);
//                dialog.dismiss();     //닫기
//            }
//        });
//        alert.setMessage("보유하고 계신 음악을 동기화합니다.");
//        alert.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
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
            case R.id.action_reset:
                // NavUtils.navigateUpFromSameTask(this);
                new InsertUpdateQuery(getApplicationContext()).execute(SERVER_ADDRESS + USER_API + RESET_DATE + WITH_USER + userDTO.getId());
                userDTO.setRatingSongCount(0);
                triger = 15;
                return true;
        }
        return super.onOptionsItemSelected(item);
    };

    public class syncLocalMP3File extends AsyncTask< Integer//excute()실행시 넘겨줄 데이터타입
            , String//진행정보 데이터 타입 publishProgress(), onProgressUpdate()의 인수
            , Integer//doInBackground() 종료시 리턴될 데이터 타입 onPostExecute()의 인수
            > {
        private ProgressDialog mDlg;
        private Context mContext;
        private Cursor mMusiccursor;
        private int count;

        public syncLocalMP3File(Context context) {
            mContext = context;

            String[] songFile = {
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Video.Media.ARTIST };
            mMusiccursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    songFile, null, null, null);

            count = 0;
        }

        @Override
        protected void onPreExecute() {
            mDlg = new ProgressDialog(mContext);
            mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDlg.setMessage("음악 동기화 시작.");
            publishProgress("max", Integer.toString(mMusiccursor.getCount()));
            mDlg.show();
            Toast.makeText(mContext, "노래정보를 읽는 중 입니다.", Toast.LENGTH_LONG).show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... params) {

            int taskCnt = mMusiccursor.getCount();

            if(taskCnt < 30) {

                final List<NameValuePair> parameters = new ArrayList<>();
                parameters.add(new BasicNameValuePair("user_id", userDTO.getId() + ""));
                boolean delay = false;

                while (!delay && mMusiccursor.moveToNext()) {
                    delay = true;
                    count++;
                    int music_column_index = mMusiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    final String title = mMusiccursor.getString(music_column_index);

                    music_column_index = mMusiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                    final String artist = mMusiccursor.getString(music_column_index);

                    if (artist.equals("<unknown>") || artist.equals("FaceBook") || title.indexOf("Hangouts") != -1) {

                    } else {
                        parameters.add(new BasicNameValuePair("title[]", title));
                        parameters.add(new BasicNameValuePair("artist[]", artist));
                    }

                    publishProgress("progress", Integer.toString(count), title + "\n    " + artist);

                    try {
                        Thread.sleep(100);
                        delay = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                new getSearchResult(getApplicationContext(), parameters, taskCnt).execute(
                        SERVER_ADDRESS + LUCENE_API + SEARCH_SONGID + WITH_USER + userDTO.getId());

            }

            return taskCnt;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            if (progress[0].equals("progress")) {
                mDlg.setProgress(Integer.parseInt(progress[1]));
                mDlg.setMessage(progress[2]);

                Log.e("background", progress[2]);
            }
            else if (progress[0].equals("max")) {
                mDlg.setMax(Integer.parseInt(progress[1]));
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result < 30) {
                mDlg.dismiss();
                mIndicator.show();
            } else {
                mDlg.dismiss();
                Toast.makeText(mContext, "노래의 수가 많습니다. 아직 최적화가 안 되어 오래걸립니다. 다음에 업데이트 할 예정입니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    class getSearchResult extends AsyncTask<String, String, String> {

        private Context mContext;
        private List<NameValuePair> mParameters;
        private int syncCount;

        public getSearchResult(Context _context, List<NameValuePair> _parameters, int _count){
            this.mContext = _context;
            this.mParameters = _parameters;
            this.syncCount = _count;
        }

        @Override
        protected String doInBackground(String... url) {
            return callByArrayParameters(url[0], mParameters);
        }

        @Override
        protected void onPostExecute(String syncJSON) {
            super.onPostExecute(syncJSON);

            mIndicator.hide();
            Intent intent = new Intent(MixPlayActivity.this, SongListActivity.class);
            intent.putExtra("syncJSON", syncJSON);
            intent.putExtra("mode", SYNC_MODE);
            startActivity(intent);

//            try {
//                JSONParser jsonParser = new JSONParser();
//                JSONObject resultJSON = (JSONObject) jsonParser.parse(songJSON);
//                String sync = (String) resultJSON.get("sync");
//                if(sync.equals("true"))
//                    Toast.makeText(mContext, syncCount + "개의 노래 동기화 완료", Toast.LENGTH_SHORT).show();
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

}
